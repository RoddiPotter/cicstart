package ca.ualberta.physics.cicstart.macro.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.cml.CMLLexer;
import ca.ualberta.physics.cicstart.cml.CMLParser;
import ca.ualberta.physics.cicstart.cml.command.CMLRuntime;
import ca.ualberta.physics.cicstart.cml.command.Macro;
import ca.ualberta.physics.cicstart.cml.command.PutVFS;
import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.util.FileUtil;
import ca.ualberta.physics.cssdp.util.NetworkUtil;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class MacroService {

	private static final Logger logger = LoggerFactory
			.getLogger(MacroService.class);

	public static enum JobStatus {
		PENDING, RUNNING, STOPPED
	}

	private final ConcurrentHashMap<String, Future<String>> jobs = new ConcurrentHashMap<String, Future<String>>();
	private final CopyOnWriteArraySet<String> active = new CopyOnWriteArraySet<String>();

	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	/*
	 * A circular buffer that overwrites (non-blocking) on writes, but blocks
	 * when the buffer is empty and read is attempted. These are contained in a
	 * map for each request that is currently active.
	 * 
	 * @see
	 * http://commons.apache.org/proper/commons-collections/apidocs/org/apache
	 * /commons/collections/buffer/BlockingBuffer.html
	 */
	private final ConcurrentHashMap<String, Buffer> logBuffers = new ConcurrentHashMap<String, Buffer>();

	public ServiceResponse<String> run(String cmlScript, String sessionToken) {
		return run(cmlScript, sessionToken, null);
	}

	/*
	 * This method is called inside the client binary JVM
	 */
	public ServiceResponse<String> run(String cmlScript,
			final String sessionToken, String jobId) {

		ServiceResponse<String> sr = new ServiceResponse<String>();

		ANTLRInputStream input;
		input = new ANTLRInputStream(cmlScript);

		CMLLexer lexer = new CMLLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		CMLParser parser = new CMLParser(tokens);

		ParseTreeWalker walker = new ParseTreeWalker();

		final Macro macro = new Macro(cmlScript);

		ParseTree tree = parser.macro();

		walker.walk(macro, tree);

		String requestId = Strings.isNullOrEmpty(jobId) ? CMLRuntime.newJobId()
				: jobId;

		final CMLRuntime runtime = new CMLRuntime(requestId, sessionToken);

		sr.setPayload(runtime.getRequestId());

		// set job to active so that MainClass can wait
		active.add(requestId);
		setupLogBuffer(requestId);

		Future<String> future = executor.submit(new Runnable() {

			@Override
			public void run() {
				String requestId = runtime.getRequestId();
				try {
					runtime.run(macro.getCommands());
					String cicstartHost = MacroServer.properties().getString(
							"cicstart.host.internal");
					// only post the macro.log to the VFS from spawned VMs
					if (!NetworkUtil.currentlyRunningOn(cicstartHost)) {
						new PutVFS(sessionToken, runtime.getRequestId(),
								"macro.log").execute(runtime);
					}
				} finally {
					active.remove(requestId);
					jobs.remove(requestId);
					writeToLogBuffer(requestId, "CML_STOP");
				}
			}

		}, runtime.getRequestId());

		jobs.put(runtime.getRequestId(), future);

		return sr;

	}

	public ServiceResponse<JobStatus> getStatus(String requestId) {

		ServiceResponse<JobStatus> sr = new ServiceResponse<JobStatus>();
		if (active.contains(requestId)) {
			sr.setPayload(JobStatus.RUNNING);
		} else {
			sr.setPayload(JobStatus.STOPPED);
		}

		return sr;
	}

	public void connectToLogStream(String requestId, OutputStream output) {
		BlockingBuffer buffer = (BlockingBuffer) logBuffers.get(requestId);
		if (buffer != null) {
			while (true) {
				String logEntry = null;
				try {
					logEntry = (String) buffer.remove();
					if (logEntry != null) {
						if (logEntry.equals("CML_STOP")) {
							output.write("done.".getBytes());
							output.flush();
							return;
						}
						output.write(logEntry.getBytes());
						output.flush();
					}
				} catch (IOException e) {
					logger.debug("Ok, you probably closed the outputstream, "
							+ "not sending you anymore data.", e);
				} catch (BufferUnderflowException bue) {
					logger.debug("No log data, waiting...");
				} finally {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ServiceResponse<Void> writeToLogBuffer(String requestId,
			String message) {
		ServiceResponse<Void> sr = new ServiceResponse<Void>();
		if (!Strings.isNullOrEmpty(message)) {

			Buffer buffer = logBuffers.get(requestId);
			if (buffer != null) {
				buffer.add(message);
			} else {
				sr.error("No buffer for request Id " + requestId);
			}
		} else {
			logger.warn("log message was null for request Id " + requestId);
		}
		return sr;
	}

	void setupLogBuffer(String requestId) {
		logBuffers.put(requestId,
				BlockingBuffer.decorate(new CircularFifoBuffer(100)));
	}

	public ServiceResponse<File> assembleClient(String cmlScript,
			String sessionToken, boolean includeJre,
			boolean useInternalNetwork, String jobId) {

		logger.debug("Got script to assemble: \n" + cmlScript);

		ServiceResponse<File> sr = new ServiceResponse<File>();

		File clientTemplateDir = new File(MacroServer.properties().getString(
				"macro.client.dir"));

		// cp the macroClientDirectory to a scratch area
		File buildDirectory = new File(new File(MacroServer.properties()
				.getString("macro.client.build.dir")), sessionToken);
		buildDirectory.mkdirs();
		logger.debug("building client in " + buildDirectory.getAbsolutePath());

		FileUtil.copy(clientTemplateDir).to(buildDirectory);
		FileOutputStream os = null;

		try {
			// write the cmlScript to bin folder
			File macroFile = new File(new File(buildDirectory, "bin"),
					"macro.cml");
			macroFile.createNewFile();
			Files.write(cmlScript, macroFile, Charset.forName("UTF-8"));
			macroFile.setExecutable(true, false);

			// create a logback.xml
			File logbackConfig = new File(new File(buildDirectory, "bin"),
					"logback.xml");
			logbackConfig.createNewFile();
			Files.copy(
					new File(MacroService.class.getResource("logback.template")
							.toURI()), logbackConfig);

			// write an application.properties to the bin folder, only needed
			// because the client requires it.

			File appProperties = new File(new File(buildDirectory, "bin"),
					"application.properties");
			appProperties.createNewFile();

			Properties overrides = ApplicationProperties.dump();

			// logback will be in the bin folder
			overrides
					.setProperty("common.logback.configuration", "logback.xml");

			// hide passwords and other sensitive info
			overrides
					.setProperty("common.hibernate.connection.url", "-hidden-");
			overrides.setProperty("common.hibernate.connection.username",
					"-hidden-");
			overrides.setProperty("common.hibernate.connection.password",
					"-hidden-");
			overrides.setProperty(
					"macroserver.cicstart.test.openstack.username", "-hidden-");
			overrides.setProperty(
					"macroserver.cicstart.test.openstack.password", "-hidden-");
			overrides.setProperty("macroserver.cicstart.pemfile", "-hidden-");
			overrides.setProperty("macroserver.isServer", "false");

			/*
			 * Internal IP or External IP? Determine that before decided how to
			 * override the properties when building the overrides. This is
			 * required because when we run the client from outside the cloud
			 * network, we need to use the external IP.... but if the binary is
			 * being run on a VM on the same cloud as CICSTART, then we can not
			 * use the external IP (unless the cloud is set to allow access....
			 * DAIR is not)
			 */

			if (useInternalNetwork) {
				// no overrides necessary and internal 10.0.whatever network is
				// required
			} else {
				// the client will be run on an external device, so the external
				// url's are needed for accessing CICSTART resources
				overrides.setProperty("common.api.url", Common.properties()
						.getString("external.api.url"));
			}

			os = new FileOutputStream(appProperties);
			overrides.store(os,
					"binary client properties generated in session "
							+ sessionToken);
			os.flush();

			/*
			 * write a run.sh with all required params to the bin folder, set it
			 * to executable
			 */
			File run = new File(new File(buildDirectory, "bin"), "run");
			run.createNewFile();
			Files.append("#!/usr/bin/env bash\n", run, Charset.forName("UTF-8"));
			Files.append("./cicstart " + appProperties.getName() + " "
					+ macroFile.getName() + " " + sessionToken + " " + jobId,
					run, Charset.forName("UTF-8"));
			run.setExecutable(true, false);

		} catch (Exception e) {
			Throwables.propagate(e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}

		// if include jre, copy jre to bin folder and modify macro, adding
		// JAVA_HOME to second line with appropriate path

		// tar.gz the macro client
		File tarball = FileUtil.gzip(FileUtil.tar(buildDirectory,
				buildDirectory.getPath()));

		// set the payload and return
		sr.setPayload(tarball);

		// cleanup
		for (File file : FileUtil.walk(buildDirectory)) {
			file.delete();
		}
		buildDirectory.delete();

		return sr;
	}

}
