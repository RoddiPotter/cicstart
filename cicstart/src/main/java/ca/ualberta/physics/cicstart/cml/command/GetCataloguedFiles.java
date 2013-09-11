package ca.ualberta.physics.cicstart.cml.command;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchRequest;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchResponse;
import ca.ualberta.physics.cssdp.util.UrlParser;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class GetCataloguedFiles implements Command {

	private static final Logger jobLogger = LoggerFactory
			.getLogger("JOBLOGGER");

	private final CatalogueSearchRequest searchRequest;

	private final List<File> downloadedFiles = new ArrayList<File>();

	public GetCataloguedFiles(CatalogueSearchRequest searchRequest) {
		this.searchRequest = searchRequest;
	}

	@Override
	public void execute(CMLRuntime runtime) {

//		String catalogueResource = Common.properties().getString(
//				"api.url") + "/catalogue";

//		String fileResource = Common.properties().getString("api.url") + "/file";

		String findUrl = ResourceUrls.PROJECT + "/find";
		jobLogger.info("GetCatalogueFiles: finding data at " + findUrl);
		jobLogger.info("GetCatalogueFiles: search request is " + searchRequest);

		Response res = given().content(searchRequest).and()
				.contentType(ContentType.JSON).post(findUrl);

		List<URI> urisToFetch = res.as(CatalogueSearchResponse.class).getUris();
		Set<URI> urisFetched = new HashSet<URI>();

		// pass 1 - asynchronous request
		for (URI uri : urisToFetch) {
			jobLogger.info("GetCatalogueFiles: found url "
					+ uri.toASCIIString() + ", requesting data download");
			res = given().queryParam("url", uri.toASCIIString()).and()
					.contentType(ContentType.URLENC).when()
					.get(ResourceUrls.CACHE);
			if (res.statusCode() == 202) {
				jobLogger
						.info("GetCatalogueFiles: data not in cache, trying next one");
				continue;
			} else {

				// TODO test for local file MD5 and skip download if already
				// exists (restart)

				jobLogger
						.info("GetCatalogueFiles: cache hit! Downloading it now.");
				File downloadedFile = Commands.streamToFile(
						res.asInputStream(),
						new File(UrlParser.getLeaf(uri.toASCIIString())));
				downloadedFiles.add(downloadedFile);
				urisFetched.add(uri);
				jobLogger.info("GetCatalogueFiles: file data saved to "
						+ downloadedFile.getAbsolutePath());
			}
		}

		// pass 2 - download data
		for (URI uri : urisToFetch) {

			// we've already downloaded the file
			if (urisFetched.contains(uri)) {
				continue;
			}

			File file = new File(UrlParser.getLeaf(uri.toASCIIString()));

			res = given().queryParam("url", uri.toASCIIString()).and()
					.contentType(ContentType.URLENC).when()
					.get(ResourceUrls.CACHE);

			int i = 0;
			while (res.statusCode() == 202) {
				i++;
				if (i % 100 == 0) {
					jobLogger
							.debug("GetCatalogueFiles: data not in cache, trying again");
				}
				try {
					Thread.sleep(1000);
					res = get(res.getHeader("location"));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			File downloadedFile = Commands.streamToFile(res.asInputStream(),
					file);
			downloadedFiles.add(downloadedFile);
			jobLogger.info("GetCatalogueFiles: file data saved to "
					+ downloadedFile.getAbsolutePath());
		}

	}

	@Override
	public Object getResult() {
		return downloadedFiles;
	}

}
