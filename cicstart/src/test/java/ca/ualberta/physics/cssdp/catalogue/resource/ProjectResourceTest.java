/* ============================================================
 * ProjectResourceTest.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.catalogue.resource;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchRequest;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchResponse;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Discriminator;
import ca.ualberta.physics.cssdp.domain.catalogue.InstrumentType;
import ca.ualberta.physics.cssdp.domain.catalogue.MetadataParserConfig;
import ca.ualberta.physics.cssdp.domain.catalogue.Observatory;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.Host.Protocol;
import ca.ualberta.physics.cssdp.model.Mnemonic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class ProjectResourceTest extends CatalogueTestsScaffolding {

	@Inject
	private ObjectMapper objectMapper;

	@Test
	public void testCreateGetAndDeleteWithJSONAndXML() throws Exception {

		Project project = new Project();
		project.setExternalKey(Mnemonic.of("TEST"));
		project.setHost("localhost");
		project.setName("Test Project");

		Observatory o = new Observatory();
		o.setExternalKey(Mnemonic.of("TEST-OBSERV"));
		o.setDescription("Test Observatory");
		o.setProject(project);
		o.setLocation(10d, 10d);
		project.getObservatories().add(o);

		InstrumentType i = new InstrumentType();
		i.setExternalKey(Mnemonic.of("TEST-INST"));
		i.setDescription("Test Instrument Type");
		i.setProject(project);
		project.getInstrumentTypes().add(i);

		DataProduct dp = new DataProduct();
		dp.setExternalKey(Mnemonic.of("TEST-DP"));
		dp.setDescription("Test Data Product");
		dp.setInstrumentTypes(project.getInstrumentTypes());
		dp.addObservatories(project.getObservatories());
		dp.setProject(project);
		project.getDataProducts().add(dp);

		String projectJSON = mapper.writeValueAsString(project);
		System.out.println(projectJSON);
		Response res = given().body(projectJSON).and()
				.contentType("application/json").expect().statusCode(201)
				.when().post(ResourceUrls.PROJECT);

		Project createdProject = get(res.getHeader("location")).as(
				Project.class);

		Assert.assertNotNull(createdProject.getId());
		for (Observatory obs : createdProject.getObservatories()) {
			Assert.assertNotNull(obs.getId());
			Assert.assertTrue(obs.getId().toString(), obs.getId() > 0);
		}
		for (InstrumentType it : createdProject.getInstrumentTypes()) {
			Assert.assertNotNull(it.getId());
			Assert.assertTrue(it.getId().toString(), it.getId() > 0);
		}
		for (DataProduct dataProduct : createdProject.getDataProducts()) {
			Assert.assertNotNull(dataProduct.getId());
			Assert.assertTrue(dataProduct.getId().toString(),
					dataProduct.getId() > 0);
			for (Observatory o2 : dataProduct.getObservatories()) {
				Assert.assertNotNull(o2.getId());
				Assert.assertTrue(o2.getId().toString(), o2.getId() > 0);
			}
			for (InstrumentType it2 : dataProduct.getInstrumentTypes()) {
				Assert.assertNotNull(it2.getId());
				Assert.assertTrue(it2.getId().toString(), it2.getId() > 0);
			}

		}

		expect().statusCode(200).when().delete(res.getHeader("location"));
		expect().statusCode(404).when().get(res.getHeader("location"));

		// same test with XML now
		project.setExternalKey(Mnemonic.of("TEST2"));

		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(Project.class);
		Marshaller m = context.createMarshaller();
		m.marshal(project, writer);

		String xml = writer.toString();
		System.out.println(xml);
		res = given().content(xml).and().contentType("application/xml")
				.expect().statusCode(201).when().contentType(ContentType.XML)
				.post(ResourceUrls.PROJECT);

		createdProject = get(res.getHeader("location")).as(Project.class);

		Assert.assertNotNull(createdProject.getId());
		for (Observatory obs : createdProject.getObservatories()) {
			Assert.assertNotNull(obs.getId());
			Assert.assertTrue(obs.getId().toString(), obs.getId() > 0);
		}
		for (InstrumentType it : createdProject.getInstrumentTypes()) {
			Assert.assertNotNull(it.getId());
			Assert.assertTrue(it.getId().toString(), it.getId() > 0);
		}
		for (DataProduct dataProduct : createdProject.getDataProducts()) {
			Assert.assertNotNull(dataProduct.getId());
			Assert.assertTrue(dataProduct.getId().toString(),
					dataProduct.getId() > 0);
			for (Observatory o2 : dataProduct.getObservatories()) {
				Assert.assertNotNull(o2.getId());
				Assert.assertTrue(o2.getId().toString(), o2.getId() > 0);
			}
			for (InstrumentType it2 : dataProduct.getInstrumentTypes()) {
				Assert.assertNotNull(it2.getId());
				Assert.assertTrue(it2.getId().toString(), it2.getId() > 0);
			}

		}

		expect().statusCode(200).when().delete(res.getHeader("location"));
		expect().statusCode(404).when().get(res.getHeader("location"));

	}

	@Test
	public void testScanAndFind() throws InterruptedException, IOException {

		Project apache = new Project();
		apache.setExternalKey(Mnemonic.of("APACHE-2"));
		apache.setHost("sunsite.ualberta.ca");
		apache.setName("Apache data on sunsite at ualberta.ca");
		apache.setScanDirectories(Arrays.asList("/pub/Mirror/apache/commons"));

		Discriminator d = new Discriminator();
		d.setDescription("commons/daemon");
		d.setExternalKey(Mnemonic.of("COMMONS/DAEMON2-disc"));
		d.setProject(apache);
		apache.getDiscriminators().add(d);

		DataProduct commonsDaemon = new DataProduct();
		commonsDaemon.setExternalKey(Mnemonic.of("COMMONS/DAEMON2-dp"));
		commonsDaemon.setDescription("Apache Commons Daemon files");
		commonsDaemon.setDiscriminator(d);
		commonsDaemon.setProject(apache);

		MetadataParserConfig metadataParserConfig = new MetadataParserConfig();
		metadataParserConfig.setIncludesRegex(".*jar$");
		commonsDaemon.setMetadataParserConfig(metadataParserConfig);

		apache.getDataProducts().add(commonsDaemon);

		String apacheJSON = mapper.writeValueAsString(apache);

		Response res = given().body(apacheJSON).and()
				.contentType("application/json").when()
				.post(ResourceUrls.PROJECT);

		if (res.getStatusCode() != 201) {
			System.out.println(res.asString());
		}

		Assert.assertEquals("expected 201, not " + res.getStatusCode(), 201,
				res.getStatusCode());

		User dataManager = setupDataManager();
		String sessionToken = login(dataManager.getEmail(), "password");

		Host host = new Host();
		host.setHostname("sunsite.ualberta.ca");
		host.setProtocol(Protocol.ftp);
		host.setUsername("anonymous");
		host.setPassword("anonymous");

		// String fileUrl = Common.properties().getString("api.url") + "/file";
		Response findHostRes = get(ResourceUrls.HOST + "/sunsite.ualberta.ca");
		if (findHostRes.getStatusCode() == 404) {

			expect().statusCode(201).when().given().content(host).and()
					.contentType(ContentType.JSON).and()
					.headers("CICSTART.session", sessionToken)
					.post(ResourceUrls.HOST);
		}

		// scan the host first
		res = given().header("CICSTART.session", sessionToken).expect()
				.statusCode(202).when()
				.put(ResourceUrls.PROJECT + "/APACHE-2/scan");

		System.out.println(">>>>>>>>>>>>>>>>>>>> SCAN RESPONSE " + res.asString());

		CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();
		searchRequest.setProjectKey(Mnemonic.of("APACHE-2"));

		String searchRequestJson = objectMapper
				.writeValueAsString(searchRequest);
		System.out.println(">>>>>>>>>>>>>>>>>>>> SEARCH REQUEST " + searchRequestJson);

		res = given().content(searchRequest).and()
				.contentType(ContentType.JSON)
				.post(ResourceUrls.PROJECT + "/find");

		System.out.println(">>>>>>>>>>>>>>>>>>>> SEARCH RESPONSE " + res.asString());

		// Assert.assertEquals(1,
		// res.as(CatalogueSearchResponse.class).getUris()
		// .size());

	}

}
