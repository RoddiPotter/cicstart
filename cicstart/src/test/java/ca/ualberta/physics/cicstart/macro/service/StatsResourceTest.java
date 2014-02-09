package ca.ualberta.physics.cicstart.macro.service;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

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
			ExecutionException {

		ExecutorService executor = Executors.newFixedThreadPool(25);

		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();

		List<String> baseUrls = Arrays.asList(ResourceUrls.AUTH,
				ResourceUrls.FILE, ResourceUrls._MACRO, ResourceUrls.CATALOGUE,
				ResourceUrls.VFS);

		Random r = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < 5000; i++) {

			final String url1 = baseUrls.get(r.nextInt(baseUrls.size() - 1));
			final String url2 = baseUrls.get(r.nextInt(baseUrls.size() - 1));

			Callable<Boolean> callable = new Callable<Boolean>() {

				@Override
				public Boolean call() {
					RequestSpecification request = given().header("Accept",
							"application/json");
					// request.log().all();
					Response res = request.get(url1 + "/service/stats");
					Boolean worked;
					if (!"application/json".equals(res
							.getHeader("Content-Type"))) {
						logger.error("Expecting application/json but was: "
								+ res.getHeader("Content-Type"));
						worked = false;
					} else {
						worked = true;
					}
					logger.info(res.getStatusLine() + " " + res.getHeaders());
					if (res.getStatusCode() != 200) {
						logger.error(res.asString());
					}
					return worked;
				}

			};
			Future<Boolean> f1 = executor.submit(callable);
			futures.add(f1);

			Callable<Boolean> callable1 = new Callable<Boolean>() {

				@Override
				public Boolean call() {
					Response res = given().header("Accept", "text/html").get(
							url2 + "/service/stats");
					Boolean worked;
					if (!"text/html".equals(res.getHeader("Content-Type"))) {
						logger.error("Expecting text/html but was "
								+ res.getHeader("Content-Type"));
						worked = false;
					} else {
						worked = true;
					}
					logger.info(res.getStatusLine() + " " + res.getHeaders());
					if (res.getStatusCode() != 200) {
						logger.error(res.asString());
					}
					return worked;

				}

			};
			Future<Boolean> f2 = executor.submit(callable1);
			futures.add(f2);
		}

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.MINUTES);

		for (Future<Boolean> f : futures) {
			Assert.assertTrue(f.get());
		}
	}

}
