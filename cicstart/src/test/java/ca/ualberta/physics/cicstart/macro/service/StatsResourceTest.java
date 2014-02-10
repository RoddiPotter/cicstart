package ca.ualberta.physics.cicstart.macro.service;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

import com.google.common.io.Files;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class StatsResourceTest extends IntegrationTestScaffolding {

	private static final Logger logger = LoggerFactory
			.getLogger(StatsResourceTest.class);

	@Override
	protected String getComponetContext() {
		return "vfs";
	}

	@Test
	public void shouldAlwaysReturnContentType() throws InterruptedException,
			ExecutionException, IOException {

		ExecutorService executor = Executors.newFixedThreadPool(100);

		List<String> baseUrls = Arrays.asList(ResourceUrls.AUTH,
				ResourceUrls.FILE, ResourceUrls._MACRO, ResourceUrls.CATALOGUE,
				ResourceUrls.VFS);

		Random r = new Random(System.currentTimeMillis());

		final File to = new File(
				"/home/rpotter/workspaces/cicstart/soak_test_results.csv");
		to.createNewFile();
		Files.write(
				"THREAD,URL,SENT,RECEIVED,DURATION,STATUS,ACCEPT,CONTENT-TYPE,SUCCESS\n",
				to, Charset.forName("UTF-8"));

		for (int i = 0; i < 50000; i++) {

			Thread.sleep((long) (r.nextFloat() * 150));

			final String url = baseUrls.get(r.nextInt(baseUrls.size()));

			executor.execute(new Runnable() {

				@Override
				public void run() {
					ResponseData rd = doRequest(url, "application/json");
					try {
						Files.append(rd.toString() + "\n", to,
								Charset.forName("UTF-8"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			executor.execute(new Runnable() {

				@Override
				public void run() {
					ResponseData rd = doRequest(url, "text/html");
					try {
						Files.append(rd.toString() + "\n", to,
								Charset.forName("UTF-8"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.MINUTES);

	}

	private ResponseData doRequest(String url, String acceptHeader) {
		RequestSpecification request = given().header("Accept", acceptHeader);
		LocalDateTime sent = new LocalDateTime();
		String fullUrl = url + "/service/stats";
		Response res = request.get(fullUrl);
		LocalDateTime received = new LocalDateTime();
		int statusCode = res.getStatusCode();
		String contentType = res.getHeader("Content-Type");
		boolean success = contentType.equals(acceptHeader);

		ResponseData rd = new ResponseData(Thread.currentThread().getName(), fullUrl, sent, received, statusCode,
				acceptHeader, contentType, success);
		logger.info(rd.toString());
		return rd;
	}

	static class ResponseData {

		private final String thread;
		private final String url;
		private final LocalDateTime requestSent;
		private final LocalDateTime responseReceived;
		private final int statusCode;
		private final String acceptHeader;
		private final String contentType;
		private final boolean success;

		public ResponseData(String thread, String url, LocalDateTime requestSent,
				LocalDateTime responseReceived, int statusCode,
				String acceptHeader, String contentType, boolean success) {
			this.thread = thread;
			this.url = url;
			this.requestSent = requestSent;
			this.responseReceived = responseReceived;
			this.statusCode = statusCode;
			this.acceptHeader = acceptHeader;
			this.contentType = contentType;
			this.success = success;
		}

		@Override
		public String toString() {
			return thread + "," + url + "," + requestSent.toString() + ","
					+ responseReceived.toString() + "," + getDuration() + ","
					+ statusCode + "," + acceptHeader + "," + contentType + ","
					+ success;
		}

		long getDuration() {
			long duration = Math.abs(requestSent.toDate().getTime()
					- responseReceived.toDate().getTime());
			return duration;
		}

		boolean isSuccess() {
			return success;
		}

	}
}
