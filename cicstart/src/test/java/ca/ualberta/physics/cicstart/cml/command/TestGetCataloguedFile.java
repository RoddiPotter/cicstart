package ca.ualberta.physics.cicstart.cml.command;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.ualberta.physics.cssdp.auth.service.AuthClient;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchRequest;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchResponse;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Discriminator;
import ca.ualberta.physics.cssdp.domain.catalogue.MetadataParserConfig;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.Host.Protocol;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class TestGetCataloguedFile extends IntegrationTestScaffolding {

	@Inject
	protected ObjectMapper mapper;

	@Inject
	protected AuthClient authClient;

	private String sessionToken;

	@Override
	protected String getComponetContext() {
		return "unsure";
	}

	public TestGetCataloguedFile() {
		InjectorHolder.inject(this);
	}

	@Before
	public void setupCatalogue() throws JsonProcessingException {

		// this is a copy & paste from a ProjectResourceTest
		// TODO dry this.
		Project apache = new Project();
		apache.setExternalKey(Mnemonic.of("APACHE1"));
		apache.setHost("sunsite.ualberta.ca");
		apache.setName("Apache data on sunsite at ualberta.ca");
		apache.setScanDirectories(Arrays.asList("/pub/Mirror/apache/commons"));

		Discriminator d = new Discriminator();
		d.setDescription("commons/daemon");
		d.setExternalKey(Mnemonic.of("COMMONS/DAEMON1"));
		d.setProject(apache);
		apache.getDiscriminators().add(d);

		DataProduct commonsDaemon = new DataProduct();
		commonsDaemon.setExternalKey(Mnemonic.of("COMMONS/DAEMON1"));
		commonsDaemon.setDescription("Apache Commons Daemon files");
		commonsDaemon.setDiscriminator(d);
		commonsDaemon.setProject(apache);

		MetadataParserConfig metadataParserConfig = new MetadataParserConfig();
		metadataParserConfig.setIncludesRegex(".*jar$");
		commonsDaemon.setMetadataParserConfig(metadataParserConfig);

		apache.getDataProducts().add(commonsDaemon);

		String apacheJSON = mapper.writeValueAsString(apache);

//		String catalogueUrl = Common.properties()
//				.getString("api.url") + "/catalogue";
		
		Response res = given().body(apacheJSON).and()
				.contentType("application/json")
				.post(ResourceUrls.PROJECT);

		User dataManager = setupDataManager();
		sessionToken = login(dataManager.getEmail(), "password");

		Host host = new Host();
		host.setHostname("sunsite.ualberta.ca");
		host.setProtocol(Protocol.ftp);
		host.setUsername("anonymous");
		host.setPassword("anonymous");

//		String fileUrl = Common.properties().getString("api.url") + "/file";
		given().content(host).and().contentType(ContentType.JSON).and()
				.headers("CICSTART.session", sessionToken)
				.post(ResourceUrls.HOST);

		// scan the host first
		res = given().header("CICSTART.session", sessionToken).put(
				ResourceUrls.PROJECT + "/APACHE1/scan");

		System.out.println(res.asString());

		CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();
		searchRequest.setProjectKey(Mnemonic.of("APACHE1"));

		res = given().content(searchRequest).and()
				.contentType(ContentType.JSON)
				.post(ResourceUrls.PROJECT + "/find");

		Assert.assertEquals(4, res.as(CatalogueSearchResponse.class).getUris()
				.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetCatalogueFile() {

		CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();
		searchRequest.setProjectKey(Mnemonic.of("APACHE1"));

		GetCataloguedFiles getCataloguedFiles = new GetCataloguedFiles(
				searchRequest);
		getCataloguedFiles.execute(new CMLRuntime("testJob", sessionToken));
		List<File> downloadedFiles = (List<File>) getCataloguedFiles
				.getResult();

		System.out.println("Downloaded "
				+ Joiner.on(", ").join(downloadedFiles));

		Assert.assertEquals(4, downloadedFiles.size());

		for (File file : downloadedFiles) {
			file.delete();
		}
	}

	/*
	 * This verifies that the cataloguing in the @Before doesn't duplicate
	 * entries in the database and also doesn't download multiple files of the
	 * same name
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void doItAgain() {

		CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();
		searchRequest.setProjectKey(Mnemonic.of("APACHE1"));

		GetCataloguedFiles getCataloguedFiles = new GetCataloguedFiles(
				searchRequest);
		getCataloguedFiles.execute(new CMLRuntime("testJob", sessionToken));
		List<File> downloadedFiles = (List<File>) getCataloguedFiles
				.getResult();

		System.out.println("Downloaded "
				+ Joiner.on(", ").join(downloadedFiles));

		Assert.assertEquals(4, downloadedFiles.size());

		for (File file : downloadedFiles) {
			file.delete();
		}
	}
}
