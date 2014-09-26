/* ============================================================
 * HostResourceTest.java
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
package ca.ualberta.physics.cssdp.file.resource;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.file.DirectoryListing;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.Host.Protocol;
import ca.ualberta.physics.cssdp.domain.file.RemoteFile;

import com.google.common.base.Throwables;
import com.jayway.restassured.response.Response;

public class HostResourceTest extends FileTestsScaffolding {

	private static final Logger logger = LoggerFactory
			.getLogger(HostResourceTest.class);

	/**
	 * Requires Auth to be running on 8080
	 */
	@Test
	public void addHost() {

		User dataManager = setupDataManager();
		String session = login(dataManager.getEmail(), "password");

		// use default role and deleted properties
		Host host = new Host();
		host.setHostname("hostname");
		host.setProtocol(Protocol.file);
		host.setPassword("password");
		host.setUsername("username");

		given().content(host).and().header("CICSTART.session", session)
				.contentType("application/json").expect().statusCode(201).and()
				.header("location", ResourceUrls.HOST + "/hostname").when()
				.post(ResourceUrls.HOST);

		Host created = get(ResourceUrls.HOST + "/hostname").as(Host.class);

		Assert.assertTrue(created.getId() > 0);
		Assert.assertEquals("hostname", created.getHostname());
		Assert.assertEquals(Protocol.file, created.getProtocol());
		Assert.assertEquals("password", created.getPassword());
		Assert.assertEquals("username", created.getUsername());

	}

	@Test
	public void deleteHost() {

		User dataManager = setupDataManager();
		String session = login(dataManager.getEmail(), "password");

		// use default role and deleted properties
		Host host = new Host();
		host.setHostname("delete");
		host.setProtocol(Protocol.file);
		host.setPassword("password");
		host.setUsername("username");

		given().content(host).and().header("CICSTART.session", session)
				.contentType("application/json").expect().statusCode(201).and()
				.header("location", ResourceUrls.HOST + "/delete").when()
				.post(ResourceUrls.HOST);

		given().header("CICSTART.session", session).expect().statusCode(200)
				.when().delete(ResourceUrls.HOST + "/delete");

		given().header("CICSTART.session", session).expect().statusCode(404)
				.when().get(ResourceUrls.HOST + "/delete");

	}

	/**
	 * Requires Auth to be running on 8080
	 */
	@Test
	public void ls() {

		User dataManager = setupDataManager();
		String session = login(dataManager.getEmail(), "password");

		// use default role and deleted properties
		Host host = new Host();
		host.setHostname("localhost2");
		host.setProtocol(Protocol.file);
		host.setPassword("password");
		host.setUsername("username");

		Response res = given().content(host).and()
				.header("CICSTART.session", session)
				.contentType("application/json").expect().statusCode(201).and()
				.header("location", ResourceUrls.HOST + "/localhost2").when()
				.post(ResourceUrls.HOST);

		System.out.println(res.asString());

		res = given()
				.header("CICSTART.session", session)
				.expect()
				.statusCode(200)
				.when()
				.get(ResourceUrls.HOST
						+ "/localhost2/ls?path=/home/rpotter&depth=2");

		/*
		 * RestAssured sucks at deserializing json... so it's best to access the
		 * Jackson Object Mapper directly
		 */

		DirectoryListing ls = null;
		try {
			ls = mapper.readValue(res.asString(), DirectoryListing.class);
		} catch (Exception e) {
			Throwables.propagate(e);
		}

		Assert.assertNotNull(ls);
		Assert.assertTrue(ls.getRemoteFiles().size() > 50);

		for (RemoteFile file : ls.getRemoteFiles()) {
			if (file != null) {
				int numSlashes = file.getUrl().split("/").length - 1;
				Assert.assertTrue(file.getUrl() + ":" + numSlashes,
						numSlashes >= 5 && numSlashes <= 7);
			}
		}

	}

	/**
	 * Requires Auth to be running on 8080
	 */
	@Test
	public void lsFTP() {

		User dataManager = setupDataManager();
		String session = login(dataManager.getEmail(), "password");

		// use default role and deleted properties
		Host host = new Host();
		host.setHostname("sunsite.ualberta.ca");
		host.setProtocol(Protocol.ftp);
		host.setUsername("anonymous");
		host.setPassword("anonymous");

		// if CacheResourceTest.rquest() runs first, this may return status 500,
		// but that's OK since sunsite.ualberta.ca will have already been added.
		given().content(host).and().header("CICSTART.session", session)
				.contentType("application/json").and()
				.header("location", ResourceUrls.HOST + "/sunsite.ualberta.ca")
				.when().post(ResourceUrls.HOST);

		Response res = given()
				.header("CICSTART.session", session)
				.expect()
				.statusCode(200)
				.when()
				.get(ResourceUrls.HOST
						+ "/sunsite.ualberta.ca/ls?path=/pub/Mirror/apache/commons/daemon&depth=5");

		/*
		 * RestAssured sucks at deserializing json... so it's best to access the
		 * Jackson Object Mapper directly
		 */

		DirectoryListing ls = null;
		String responseBody = res.asString();
		try {
			ls = mapper.readValue(responseBody, DirectoryListing.class);
		} catch (Exception e) {
			logger.error(responseBody);
			Throwables.propagate(e);
		}

		Assert.assertNotNull(ls);

		for (RemoteFile file : ls.getRemoteFiles()) {
			int numSlashes = file.getUrl().split("/").length - 1;
			Assert.assertTrue(file.getUrl() + ":" + numSlashes, numSlashes >= 6
					&& numSlashes <= 11);
			System.out.println(file.getUrl());
			if (file.getUrl().contains("windows")) {
				Assert.assertTrue(

				file.getUrl()
						.startsWith(
								"ftp://sunsite.ualberta.ca/pub/Mirror/apache/commons/daemon/binaries/windows/"));
			}
		}

	}

	@Test
	public void ls_depth0() {

		User dataManager = setupDataManager();
		String session = login(dataManager.getEmail(),
				dataManager.getPassword());

		// use default role and deleted properties
		Host host = new Host();
		host.setHostname("localhost1");
		host.setProtocol(Protocol.file);
		host.setPassword("password");
		host.setUsername("username");

		given().content(host).and().header("CICSTART.session", session)
				.contentType("application/json").expect().statusCode(201).and()
				.header("location", ResourceUrls.HOST + "/localhost1").when()
				.post(ResourceUrls.HOST);

		Response res = given()
				.header("CICSTART.session", session)
				.expect()
				.statusCode(200)
				.when()
				.get(ResourceUrls.HOST
						+ "/localhost1/ls?path=/home/rpotter&depth=0");

		/*
		 * RestAssured sucks at deserializing json... so it's best to access the
		 * Jackson Object Mapper directly
		 */

		DirectoryListing ls = null;
		try {
			ls = mapper.readValue(res.asString(), DirectoryListing.class);
		} catch (Exception e) {
			Throwables.propagate(e);
		}

		Assert.assertNotNull(ls);
		Assert.assertTrue(ls.getRemoteFiles().size() > 10);
		for (RemoteFile file : ls.getRemoteFiles()) {
			int numSlashes = file.getUrl().split("/").length - 1;
			Assert.assertTrue(file.getUrl() + ":" + numSlashes, numSlashes == 5);
		}

	}

}
