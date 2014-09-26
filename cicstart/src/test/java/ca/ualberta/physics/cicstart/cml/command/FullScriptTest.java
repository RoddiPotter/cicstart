package ca.ualberta.physics.cicstart.cml.command;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.ualberta.physics.cicstart.cml.CMLLexer;
import ca.ualberta.physics.cicstart.cml.CMLParser;
import ca.ualberta.physics.cicstart.cml.ParsingAndBuildingTests;
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
import ca.ualberta.physics.cssdp.domain.macro.Instance;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;
import ca.ualberta.physics.cssdp.vfs.configuration.VfsServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class FullScriptTest extends IntegrationTestScaffolding {

	@Inject
	protected ObjectMapper mapper;

	@Inject
	protected AuthClient authClient;

	private String sessionToken;

	private User dataManager;

	public FullScriptTest() {
		InjectorHolder.inject(this);
	}

	@Override
	protected String getComponetContext() {
		return "unused";
	}

	@Before
	public void setupData() throws Exception {

		// Given a project definition
		Project apache = new Project();
		apache.setExternalKey(Mnemonic.of("APACHE3"));
		apache.setHost("sunsite.ualberta.ca");
		apache.setName("Apache data on sunsite at ualberta.ca");
		apache.setScanDirectories(Arrays.asList("/pub/Mirror/apache/commons"));

		Discriminator d = new Discriminator();
		d.setDescription("commons/daemon");
		d.setExternalKey(Mnemonic.of("COMMONS/DAEMON3"));
		d.setProject(apache);
		apache.getDiscriminators().add(d);

		DataProduct commonsDaemon = new DataProduct();
		commonsDaemon.setExternalKey(Mnemonic.of("COMMONS/DAEMON3"));
		commonsDaemon.setDescription("Apache Commons Daemon files");
		commonsDaemon.setDiscriminator(d);
		commonsDaemon.setProject(apache);

		MetadataParserConfig metadataParserConfig = new MetadataParserConfig();
		metadataParserConfig.setIncludesRegex(".*jar$");
		commonsDaemon.setMetadataParserConfig(metadataParserConfig);

		apache.getDataProducts().add(commonsDaemon);

		String apacheJSON = mapper.writeValueAsString(apache);

		Response findProjRes = get(ResourceUrls.PROJECT + "/APACHE3");
		if (findProjRes.getStatusCode() == 404) {
			given().body(apacheJSON).and().contentType("application/json")
					.expect().statusCode(201).when().post(ResourceUrls.PROJECT);
		}

		dataManager = setupDataManager();
		sessionToken = login(dataManager.getEmail(), "password");

		Host host = new Host();
		host.setHostname("sunsite.ualberta.ca");
		host.setProtocol(Protocol.ftp);
		host.setUsername("anonymous");
		host.setPassword("anonymous");

		Response findHostRes = get(ResourceUrls.HOST + "/sunsite.ualberta.ca");
		if (findHostRes.getStatusCode() == 404) {

			expect().statusCode(201).when().given().content(host).and()
					.contentType(ContentType.JSON).and()
					.headers("CICSTART.session", sessionToken)
					.post(ResourceUrls.HOST);
		}

		// WHEN the project host is fully scanned for files using the project's
		// metadata configuration
		Response res = given().header("CICSTART.session", sessionToken)
				.expect().statusCode(202).when()
				.put(ResourceUrls.PROJECT + "/APACHE3/scan");

		System.out.println(res.asString());

		CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();
		searchRequest.setProjectKey(Mnemonic.of("APACHE3"));

		res = given().content(searchRequest).and()
				.contentType(ContentType.JSON)
				.post(ResourceUrls.PROJECT + "/find");

		// THEN a search request/response will contain a some files that we just
		// scanned
		CatalogueSearchResponse response = res
				.as(CatalogueSearchResponse.class);
		Assert.assertTrue(
				"There should be at least 1 apache jar on file on sunsite",
				response.getUris().size() > 0);
		Assert.assertTrue("Expecting any path to contain apache", response
				.getUris().get(0).getPath().contains("apache"));
		Assert.assertTrue("Expecting any path to contain jar", response
				.getUris().get(0).getPath().contains("jar"));

	}

	@Test
	public void testFullScriptRun() throws Exception {

		// cleanup left over cruft
		String vfsRoot = VfsServer.properties().getString("vfs_root");
		File extractedJarRoot = new File(new File(vfsRoot, dataManager.getId()
				.toString()), "testJob");

		if (extractedJarRoot.exists()) {
			for (File file : extractedJarRoot.listFiles()) {
				file.delete();
			}
		}

		String script = Files.toString(new File(ParsingAndBuildingTests.class
				.getResource("/test2.cml").toURI()), Charset.forName("UTF-8"));
		ANTLRInputStream input = new ANTLRInputStream(script);

		CMLLexer lexer = new CMLLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		CMLParser parser = new CMLParser(tokens);

		ParseTreeWalker walker = new ParseTreeWalker();

		Macro macro = new Macro(script);

		ParseTree tree = parser.macro();

		walker.walk(macro, tree);

		CMLRuntime runtime = new CMLRuntime("testJob", sessionToken);
		runtime.run(macro.getCommands());

		new PutVFS(sessionToken, runtime.getRequestId(), "macro.log")
				.execute(runtime);

		vfsRoot = VfsServer.properties().getString("vfs_root");
		extractedJarRoot = new File(new File(vfsRoot, dataManager.getId()
				.toString()), "testJob");
		Assert.assertTrue(extractedJarRoot.listFiles().length > 0);
		Assert.assertEquals(1, extractedJarRoot.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.equals("macro.log");
			}
		}).length);

		for (File file : extractedJarRoot.listFiles()) {
			file.delete();
		}

	}

	@Test
	public void testStartVMAndRunStuffOnIt() throws Exception {

		String script = Files.toString(new File(ParsingAndBuildingTests.class
				.getResource("/test3.cml").toURI()), Charset.forName("UTF-8"));
		ANTLRInputStream input = new ANTLRInputStream(script);

		CMLLexer lexer = new CMLLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		CMLParser parser = new CMLParser(tokens);

		ParseTreeWalker walker = new ParseTreeWalker();

		Macro macro = new Macro(script);

		ParseTree tree = parser.macro();

		walker.walk(macro, tree);

		CMLRuntime runtime = new CMLRuntime("testStartVMAndRunStuffOnIt",
				sessionToken);
		runtime.run(macro.getCommands());

		List<Instance> instances = runtime.getInstances();
		if (instances.size() > 0) {
			Instance instance = instances.get(0);

			// cleanup, stop the instance
			expect().statusCode(200).when().given().content(instance).and()
					.contentType(ContentType.JSON).and()
					.headers("CICSTART.session", sessionToken)
					.delete(ResourceUrls.MACRO + "/vm");
			// TODO release floating ips
		}

	}

}
