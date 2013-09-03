package ca.ualberta.physics.cicstart.cml.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

public class Run implements Command {

	private static final Logger jobLogger = LoggerFactory
			.getLogger("JOBLOGGER");

	private final String commandLine;
	private Process process;
	private CountDownLatch timeoutSignal = new CountDownLatch(1);
	private int exitValue;
	private final int timeoutInMinutes;
	private static final AtomicInteger idGenerator = new AtomicInteger(0);
	private final int id;

	// not accessible via CML but used for testing. Make a constructor to allow
	// access via CML.
	private File workingDirectory = new File(".");

	public Run(String commandLine) {
		this(commandLine, 1);
	}

	public Run(String commandLine, int timeoutInMinutes) {
		this.commandLine = commandLine;
		this.timeoutInMinutes = timeoutInMinutes;
		id = idGenerator.incrementAndGet();
	}

	@Override
	public void execute(CMLRuntime runtime) {

		if (Strings.isNullOrEmpty(commandLine)) {
			throw new IllegalStateException(
					"Can't run empty or null command line");
		}

		// ProcessBuilder likes each parameter as a separate item passed to it
		// so we split the command into parameters here. This regex from
		// stackoverflow:
		// http://stackoverflow.com/questions/7804335/split-string-on-spaces-except-if-between-quotes-i-e-treat-hello-world-as
		List<String> list = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(
				commandLine);
		while (m.find()) {
			// strip all " due to possible jvm bug:
			// http://bugs.sun.com/view_bug.do?bug_id=7032109
			// plus, " are for bash interactive shell, not for passing directly
			// to process.
			list.add(m.group(1).replaceAll("\"", ""));
		}
		jobLogger.debug("Run: parsed command line is "
				+ Joiner.on(" ").join(list));
		final ProcessBuilder pb = new ProcessBuilder(list);
		pb.directory(getWorkingDirectory());
		pb.redirectErrorStream(true); // combine stderr and stdout

		// run the external process in a separate thread to capture input stream
		Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					process = pb.start();
					jobLogger.info("Run: '" + commandLine
							+ "' output logged with reference " + id
							+ ", timeout set to " + timeoutInMinutes
							+ " minutes");
					jobLogger.info("Run: working directory is: "
							+ pb.directory().getAbsolutePath());

					// copy the input stream to output stream, and make the
					// output stream write to the CML logger
					pipe(process.getInputStream(), new OutputStream() {

						public void write(byte[] b, int off, int len)
								throws IOException {

							String stringToLog = new String(b, off, len);
							stringToLog = stringToLog.replaceAll("[\n\r]$", "")
									.replaceAll("^\\s*$", "");
							if (!Strings.isNullOrEmpty(stringToLog)) {
								jobLogger.info("     " + id + "| "
										+ stringToLog);
							}
						};

						@Override
						public void write(int arg0) throws IOException {
							// unused
						}
					});

				} catch (IOException e) {
					if (timeoutSignal.getCount() > 0) {
						timeoutSignal.countDown();
					}
					jobLogger.error("Run: IOException occured: "
							+ Throwables.getStackTraceAsString(e));
					Throwables.propagate(e);
				} finally {
					try {
						// process streams require closing even if not used.
						process.getOutputStream().flush();
						process.getOutputStream().close();
						process.getInputStream().close();
						process.getErrorStream().close();
						timeoutSignal.countDown();
					} catch (Exception ignore) {
					}
				}
			}
		});
		t.start();
		try {
			if (timeoutSignal.await(timeoutInMinutes, TimeUnit.MINUTES)) {
				exitValue = process.waitFor();
				t.join();
				jobLogger
						.info("Run: " + id + " exited with value " + exitValue);
			} else {
				process.destroy();
				jobLogger.error("Run: " + id
						+ " TIMED OUT! The process was destroyed.");
			}
		} catch (InterruptedException e) {
			jobLogger.error("Run: " + id
					+ " Process was interrupted by someone.");
			Thread.currentThread().interrupt();
		}
	}

	public void pipe(InputStream is, OutputStream os) throws IOException {
		int n;
		byte[] buffer = new byte[1024];
		// this read call blocks if waiting for user input... need a timeout on
		// it.
		while ((n = is.read(buffer)) > -1) {
			os.write(buffer, 0, n); // Don't allow any extra bytes to creep in,
									// final write
		}
	}

	@Override
	public Object getResult() {
		return exitValue;
	}

	public String getCommandLine() {
		return commandLine;
	}

	@Override
	public String toString() {
		return commandLine;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
}
