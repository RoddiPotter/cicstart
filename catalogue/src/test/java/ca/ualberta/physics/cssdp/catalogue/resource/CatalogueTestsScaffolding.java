package ca.ualberta.physics.cssdp.catalogue.resource;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.util.Arrays;

import org.junit.Before;

import ca.ualberta.physics.cssdp.catalogue.InjectorHolder;
import ca.ualberta.physics.cssdp.client.AuthClient;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Discriminator;
import ca.ualberta.physics.cssdp.domain.catalogue.InstrumentType;
import ca.ualberta.physics.cssdp.domain.catalogue.MetadataParserConfig;
import ca.ualberta.physics.cssdp.domain.catalogue.Observatory;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.Host.Protocol;
import ca.ualberta.physics.cssdp.model.Mnemonic;
import ca.ualberta.physics.cssdp.model.Point;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class CatalogueTestsScaffolding extends IntegrationTestScaffolding {

	@Inject
	protected ObjectMapper mapper;

	@Inject
	protected AuthClient authClient;

	protected Project PROJ1;
	protected Project APACHE;

	public CatalogueTestsScaffolding() {
		InjectorHolder.inject(this);
	}

	@Override
	protected String getComponetContext() {
		return "";
	}

	@Before
	public void setupProjects() throws Exception {

		Project project = new Project();
		project.setExternalKey(Mnemonic.of("PROJECT1"));
		project.setHost("localhost");
		project.setName("Test Project 1");

		Observatory o = new Observatory();
		o.setExternalKey(Mnemonic.of("O1"));
		o.setDescription("Test Observatory 1");
		o.setProject(project);
		o.setLocation(new Point(10, 10));
		project.getObservatories().add(o);

		InstrumentType i = new InstrumentType();
		i.setExternalKey(Mnemonic.of("IT1"));
		i.setDescription("Test Instrument Type 1");
		i.setProject(project);
		project.getInstrumentTypes().add(i);

		DataProduct dp = new DataProduct();
		dp.setExternalKey(Mnemonic.of("DP1"));
		dp.setDescription("Test Data Product 1");
		dp.addInstrumentTypes(project.getInstrumentTypes());
		dp.addObservatories(project.getObservatories());
		dp.setProject(project);
		project.getDataProducts().add(dp);

		String projectJSON = mapper.writeValueAsString(project);

		Response res = given().body(projectJSON).and()
				.contentType("application/json").expect().statusCode(201)
				.when().post("/catalogue/project.json");

		PROJ1 = get(res.getHeader("location")).as(Project.class);

		Project apache = new Project();
		apache.setExternalKey(Mnemonic.of("APACHE"));
		apache.setHost("sunsite.ualberta.ca");
		apache.setName("Apache data on sunsite at ualberta.ca");
		apache.setScanDirectories(Arrays
				.asList("/pub/Mirror/apache/commons/daemon/"));

		Discriminator d = new Discriminator();
		d.setDescription("commons/daemon");
		d.setExternalKey(Mnemonic.of("COMMONS/DAEMON"));
		d.setProject(project);
		apache.getDiscriminators().add(d);

		DataProduct commonsDaemon = new DataProduct();
		commonsDaemon.setExternalKey(Mnemonic.of("COMMONS/DAEMON"));
		commonsDaemon.setDescription("Apache Commons Daemon files");
		commonsDaemon.setDiscriminator(d);
		commonsDaemon.setProject(apache);

		MetadataParserConfig metadataParserConfig = new MetadataParserConfig();
		metadataParserConfig.setIncludesRegex(".*jar$");
		commonsDaemon.setMetadataParserConfig(metadataParserConfig);

		apache.getDataProducts().add(commonsDaemon);

		String apacheJSON = mapper.writeValueAsString(apache);

		res = given().body(apacheJSON).and().contentType("application/json")
				.expect().statusCode(201).when()
				.post("/catalogue/project.json");

		APACHE = get(res.getHeader("location")).as(Project.class);

		User dataManager = setupDataManager();
		String sessionToken = login(dataManager.getEmail(), "password");

		Host host = new Host();
		host.setHostname("sunsite.ualberta.ca");
		host.setProtocol(Protocol.ftp);
		host.setUsername("anonymous");
		host.setPassword("anonymous");

		given().content(host).and().contentType(ContentType.JSON).and()
				.headers("cicstart_session", sessionToken)
				.post("http://localhost:8083/file/host.json");

	}

}
