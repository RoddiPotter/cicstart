package ca.ualberta.physics.cicstart.macro.service;

import java.io.IOException;
import java.io.OutputStream;
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
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.cml.CML2Lexer;
import ca.ualberta.physics.cicstart.cml.CMLParser;
import ca.ualberta.physics.cicstart.cml.command.CMLRuntime;
import ca.ualberta.physics.cicstart.cml.command.Macro;
import ca.ualberta.physics.cicstart.cml.command.PutVFS;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

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

	public ServiceResponse<String> run(String cmlScript,
			final String sessionToken) {

		ServiceResponse<String> sr = new ServiceResponse<String>();

		ANTLRInputStream input;
		input = new ANTLRInputStream(cmlScript);

		CML2Lexer lexer = new CML2Lexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		CMLParser parser = new CMLParser(tokens);

		ParseTreeWalker walker = new ParseTreeWalker();

		final Macro macro = new Macro();

		ParseTree tree = parser.macro();

		walker.walk(macro, tree);

		final CMLRuntime runtime = new CMLRuntime(sessionToken);

		sr.setPayload(runtime.getJobId());

		Future<String> future = executor.submit(new Runnable() {

			@Override
			public void run() {
				String jobId = runtime.getJobId();
				try {
					setupLogBuffer(jobId);
					active.add(jobId);
					runtime.run(macro.getCommands());
					new PutVFS(sessionToken, runtime.getJobId(), "macro.log")
							.execute(runtime);
				} finally {
					active.remove(jobId);
					jobs.remove(jobId);
				}
			}

		}, runtime.getJobId());
		jobs.put(runtime.getJobId(), future);

		return sr;

	}

	public ServiceResponse<JobStatus> getStatus(String requestId) {

		ServiceResponse<JobStatus> sr = new ServiceResponse<JobStatus>();
		if (active.contains(requestId)) {
			sr.setPayload(JobStatus.RUNNING);
		} else if (jobs.contains(requestId)) {
			sr.setPayload(JobStatus.PENDING);
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

}
