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

import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.file.DirectoryListing;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.Host.Protocol;
import ca.ualberta.physics.cssdp.domain.file.RemoteFile;

import com.google.common.base.Throwables;
import com.jayway.restassured.response.Response;

public class HostResourceTest extends FileTestsScaffolding {

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

		given().content(host)
				.and()
				.header("CICSTART.session", session)
				.contentType("application/json")
				.expect()
				.statusCode(201)
				.and()
				.header("location",
						"http://localhost:8080/file/host.json/hostname").when()
				.post("/file/host.json");

		Host created = get("http://localhost:8080/file/host.json/hostname").as(
				Host.class);

		Assert.assertTrue(created.getId() > 0);
		Assert.assertEquals("hostname", created.getHostname());
		Assert.assertEquals(Protocol.file, created.getProtocol());
		Assert.assertEquals("******", created.getPassword());
		Assert.assertEquals("******", created.getUsername());

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

		given().content(host)
				.and()
				.header("CICSTART.session", session)
				.contentType("application/json")
				.expect()
				.statusCode(201)
				.and()
				.header("location",
						"http://localhost:8080/file/host.json/delete").when()
				.post("/file/host.json");

		given().header("CICSTART.session", session).expect().statusCode(200)
				.when().delete("http://localhost:8080/file/host.json/delete");

		given().header("CICSTART.session", session).expect().statusCode(404)
				.when().get("http://localhost:8080/file/host.json/delete");

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
		host.setHostname("localhost");
		host.setProtocol(Protocol.file);
		host.setPassword("password");
		host.setUsername("username");

		given().content(host)
				.and()
				.header("CICSTART.session", session)
				.contentType("application/json")
				.expect()
				.statusCode(201)
				.and()
				.header("location",
						"http://localhost:8080/file/host.json/localhost")
				.when().post("/file/host.json");

		Response res = given()
				.header("CICSTART.session", session)
				.expect()
				.statusCode(200)
				.when()
				.get("http://localhost:8080/file/host.json/localhost/ls?path=/home/rpotter&depth=2");

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
			int numSlashes = file.getUrl().split("/").length - 1;
			Assert.assertTrue(file.getUrl() + ":" + numSlashes, numSlashes >= 5
					&& numSlashes <= 7);
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

		given().content(host)
				.and()
				.header("CICSTART.session", session)
				.contentType("application/json")
				.expect()
				.statusCode(201)
				.and()
				.header("location",
						"http://localhost:8080/file/host.json/sunsite.ualberta.ca")
				.when().post("/file/host.json");

		Response res = given()
				.header("CICSTART.session", session)
				.expect()
				.statusCode(200)
				.when()
				.get("http://localhost:8080/file/host.json/sunsite.ualberta.ca/ls?path=/pub/Mirror/apache/commons/daemon/&depth=5");

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

		for (RemoteFile file : ls.getRemoteFiles()) {
			int numSlashes = file.getUrl().split("/").length - 1;
			Assert.assertTrue(file.getUrl() + ":" + numSlashes, numSlashes >= 6
					&& numSlashes <= 10);
			System.out.println(file.getUrl());
			if (file.getUrl().contains("windows")) {
				Assert.assertTrue(

				file.getUrl()
						.startsWith(
								"ftp://sunsite.ualberta.ca/pub/Mirror/apache/commons/daemon/binaries/windows/"));
			}
		}

	}

	/**
	 * Requires Auth to be running on 8080
	 */
	@Test
	public void ls_depth0() {

		String session = login(dataManager.getEmail(),
				dataManager.getPassword());

		// use default role and deleted properties
		Host host = new Host();
		host.setHostname("localhost");
		host.setProtocol(Protocol.file);
		host.setPassword("password");
		host.setUsername("username");

		given().content(host)
				.and()
				.header("CICSTART.session", session)
				.contentType("application/json")
				.expect()
				.statusCode(201)
				.and()
				.header("location",
						"http://localhost:8080/file/host.json/localhost")
				.when().post("/file/host.json");

		Response res = given()
				.header("CICSTART.session", session)
				.expect()
				.statusCode(200)
				.when()
				.get("http://localhost:8080/file/host.json/localhost/ls?path=/home/rpotter&depth=0");

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
			int numSlashes = file.getUrl().split("/").length - 1;
			Assert.assertTrue(file.getUrl() + ":" + numSlashes, numSlashes == 5);
		}

	}

}
