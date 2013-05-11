package ca.ualberta.physics.cicstart.macro.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.ualberta.physics.cssdp.service.ServiceResponse;

public class LogBufferTests {

	private MacroService ms;

	@Before
	public void setupService() {
		ms = new MacroService() {
			@Override
			public ServiceResponse<String> run(String cmlScript,
					String sessionToken) {
				setupLogBuffer("12345");
				return null;
			}
		};
		// setup the buffer.
		ms.run(null, null);
	}

	@Test
	public void testWriteAndRead_nonconcurrent() {

		final AtomicInteger logCount = new AtomicInteger(0);

		final OutputStream os = new OutputStream() {

			@Override
			public void write(byte[] b) throws IOException {
				logCount.incrementAndGet();
				System.out.println("I just read this: " + new String(b)
						+ ", hooraay!");
			}

			@Override
			public void write(int b) throws IOException {

			}
		};

		Thread reader = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("connecting to log buffer");
				ms.connectToLogStream("12345", os);
			}

		});
		reader.start();

		final Object wait = new Object();
		Thread writer = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("writing to log buffer");
				for (int i = 0; i < 200; i++) {
					ms.writeToLogBuffer("12345", "writing " + i + " to buffer");
					// throttle the writes slightly
					if (i % 100 == 0) {
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
						}
					}

				}
				// let the reader finish all entries.
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				synchronized (wait) {
					wait.notify();
				}
			}

		});
		writer.start();

		synchronized (wait) {
			try {
				wait.wait();
			} catch (InterruptedException e) {
			}
		}

		// reader.interrupt();
		try {
			// be a good citizen
			os.close();
		} catch (IOException ignore) {
		}

		// we sent 200 log messages, 200 should have been printed.
		Assert.assertEquals(200, logCount.get());

	}

}
