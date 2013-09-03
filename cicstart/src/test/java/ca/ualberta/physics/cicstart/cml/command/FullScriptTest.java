package ca.ualberta.physics.cicstart.cml.command;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

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
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
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
//		// this is a copy & paste from a ProjectResourceTest
//		// TODO dry this.
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

		String catalogueUrl = Common.properties()
				.getString("api.url") + "/catalogue";
		Response res = given().body(apacheJSON).and()
				.contentType("application/json").expect().statusCode(201)
				.when().post(catalogueUrl + "/project.json");

		dataManager = setupDataManager();
		sessionToken = login(dataManager.getEmail(), "password");

		Host host = new Host();
		host.setHostname("sunsite.ualberta.ca");
		host.setProtocol(Protocol.ftp);
		host.setUsername("anonymous");
		host.setPassword("anonymous");

		String fileUrl = Common.properties().getString("api.url") + "/file";
		expect().statusCode(201).when().given().content(host).and()
				.contentType(ContentType.JSON).and()
				.headers("CICSTART.session", sessionToken)
				.post(fileUrl + "/host.json");

		// scan the host first
		res = given().header("CICSTART.session", sessionToken).expect()
				.statusCode(202).when()
				.put(catalogueUrl + "/project.json/APACHE3/scan");

		System.out.println(res.asString());

		CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();
		searchRequest.setProjectKey(Mnemonic.of("APACHE3"));

		res = given().content(searchRequest).and()
				.contentType(ContentType.JSON)
				.post(catalogueUrl + "/project.json/find");

		Assert.assertEquals(4, res.as(CatalogueSearchResponse.class).getUris()
				.size());

	}

	@Test
	public void testFullScriptRun() throws Exception {

		// cleanup left over cruft
		String vfsRoot = VfsServer.properties().getString("vfs_root");
		File extractedJarRoot = new File(new File(vfsRoot, dataManager.getId()
				.toString()), "testJob");

		for (File file : extractedJarRoot.listFiles()) {
			file.delete();
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
		Assert.assertEquals(5, extractedJarRoot.listFiles().length);

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

		CMLRuntime runtime = new CMLRuntime("testJob", sessionToken);
		runtime.run(macro.getCommands());

	}

}
