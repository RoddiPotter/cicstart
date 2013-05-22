package ca.ualberta.physics.cicstart.macro.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.cml.CMLLexer;
import ca.ualberta.physics.cicstart.cml.CMLParser;
import ca.ualberta.physics.cicstart.cml.command.CMLRuntime;
import ca.ualberta.physics.cicstart.cml.command.Macro;
import ca.ualberta.physics.cicstart.cml.command.PutVFS;
import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.MacroServer;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.util.FileUtil;

import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class MacroService {

	private static final Logger logger = LoggerFactory
			.getLogger(MacroService.class);

	public static enum JobStatus {
		PENDING, RUNNING, STOPPED
	}

//	private final ConcurrentHashMap<String, Future<String>> jobs = new ConcurrentHashMap<String, Future<String>>();
	private final CopyOnWriteArraySet<String> active = new CopyOnWriteArraySet<String>();

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

	/*
	 * This method is called inside the client binary JVM
	 */
	public ServiceResponse<String> run(String cmlScript,
			final String sessionToken) {

		ServiceResponse<String> sr = new ServiceResponse<String>();

		ANTLRInputStream input;
		input = new ANTLRInputStream(cmlScript);

		CMLLexer lexer = new CMLLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		CMLParser parser = new CMLParser(tokens);

		ParseTreeWalker walker = new ParseTreeWalker();

		final Macro macro = new Macro();

		ParseTree tree = parser.macro();

		walker.walk(macro, tree);

		String requestId = sessionToken + CMLRuntime.newJobId();
		final CMLRuntime runtime = new CMLRuntime(requestId, sessionToken);

		sr.setPayload(runtime.getRequestId());

		// Future<String> future = executor.submit(new Runnable() {
		//
		// @Override
		// public void run() {
		// String requestId = runtime.getRequestId();
		try {
			setupLogBuffer(requestId);
			active.add(requestId);
			runtime.run(macro.getCommands());
			new PutVFS(sessionToken, runtime.getRequestId(), "macro.log")
					.execute(runtime);
		} finally {
			active.remove(requestId);
		}
		// }

		// }, runtime.getRequestId());
		// jobs.put(runtime.getRequestId(), future);

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
				logEntry = (String) buffer.remove();
				try {
					if (logEntry != null) {
						output.write(logEntry.getBytes());
						output.flush();
					}
				} catch (IOException e) {
					logger.debug("Ok, you probably closed the outputstream, "
							+ "not sending you anymore data.", e);
				} finally {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ServiceResponse<Void> writeToLogBuffer(String requestId,
			String message) {
		ServiceResponse<Void> sr = new ServiceResponse<Void>();
		Buffer buffer = logBuffers.get(requestId);
		if (buffer != null) {
			buffer.add(message);
		} else {
			sr.error("No buffer for request Id " + requestId);
		}
		return sr;
	}

	void setupLogBuffer(String requestId) {
		logBuffers.put(requestId,
				BlockingBuffer.decorate(new CircularFifoBuffer(100)));
	}

	public ServiceResponse<File> assembleClient(String cmlScript,
			String sessionToken, boolean includeJre) {

		ServiceResponse<File> sr = new ServiceResponse<File>();

		File clientTemplateDir = new File(MacroServer.properties().getString(
				"macro.client.dir"));

		// cp the macroClientDirectory to a scratch area
		File buildDirectory = new File(new File(MacroServer.properties()
				.getString("macro.client.build.dir")), sessionToken);

		logger.debug("building client in " + buildDirectory.getAbsolutePath());

		FileUtil.copy(clientTemplateDir).to(buildDirectory);

		try {
			// write the cmlScript to bin folder
			File macro = new File(new File(buildDirectory, "bin"), "macro.cml");
			macro.createNewFile();
			Files.write(cmlScript.getBytes(), macro);
			macro.setExecutable(true, false);

			// create a logback.xml
			File logbackConfig = new File(new File(buildDirectory, "bin"),
					"logback.xml");
			logbackConfig.createNewFile();
			Files.copy(
					new File(MacroService.class.getResource("logback.template")
							.toURI()), logbackConfig);

			// write an application.properties to the bin folder, only needed
			// because the client requires it.
			
			// could do this more elegantly, but this works for now.
			File appProperties = new File(new File(buildDirectory, "bin"),
					"application.properties");
			appProperties.createNewFile();
			ApplicationProperties.dumpToFile(appProperties);

			String overrides = Files.toString(appProperties,
					Charset.forName("UTF-8"));
			overrides = overrides
					.replaceAll(
							"logback.configuration=.*$",
							"logback.configuration=logback.xml");
			Files.write(overrides, appProperties, Charset.forName("UTF-8"));

			// write a run.sh with all req. params to the bin folder, set it to
			// executable
			File run = new File(new File(buildDirectory, "bin"), "run");
			run.createNewFile();
			Files.append("#!/usr/bin/env bash\n", run, Charset.forName("UTF-8"));
			Files.append(
					"./macro " + appProperties.getName() + " "
							+ macro.getName() + " " + sessionToken, run,
					Charset.forName("UTF-8"));
			run.setExecutable(true, false);

		} catch (IOException e) {
			Throwables.propagate(e);
		} catch (URISyntaxException e) {
			Throwables.propagate(e);
		}

		// if include jre, copy jre to bin folder and modify macro, adding
		// JAVA_HOME to second line with appropriate path

		// tar.gz the macro client
		File tarball = FileUtil.gzip(FileUtil.tar(buildDirectory,
				buildDirectory.getPath()));

		// set the payload and return
		sr.setPayload(tarball);

		// cleanup
		buildDirectory.delete();
		
		return sr;
	}

}
