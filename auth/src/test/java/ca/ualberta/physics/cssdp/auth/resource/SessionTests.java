/* ============================================================
 * SessionTests.java
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
package ca.ualberta.physics.cssdp.auth.resource;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;

import org.junit.Assert;
import org.junit.Test;

public class SessionTests extends AuthTestsScaffolding {

	@Test
	public void testAuthenticate_BASIC() {
		/*
		 * curl --user does@exist.com:anything -X POST
		 * http://localhost:8080/auth/session.json
		 */

		// preemptive() is required to always send authentication header since
		// our server won't request it.

		// no user
		given().auth().preemptive().basic("doesnot@exist.com", "anything")
				.expect().statusCode(401).when().post("/auth/session.json");
		given().auth().preemptive().basic("doesnot@exist.com", "anything")
				.expect().statusCode(401).when().post("/auth/session.xml");

		// success
		String sessionToken = given().auth().preemptive()
				.basic("datauser@nowhere.com", "password").expect()
				.statusCode(200).post("/auth/session.json").asString();
		Assert.assertEquals(36, sessionToken.length());

		sessionToken = given().auth().preemptive()
				.basic("datauser@nowhere.com", "password").expect()
				.statusCode(200).post("/auth/session.xml").asString();
		Assert.assertEquals(36, sessionToken.length());

		// fail
		given().auth().preemptive()
				.basic("datauser@nowhere.com", "wrongpassword").expect()
				.statusCode(401).post("/auth/session.json");

		given().auth().preemptive()
				.basic("datauser@nowhere.com", "wrongpassword").expect()
				.statusCode(401).post("/auth/session.xml");

	}

	@Test
	public void testAuthentication_form() {
		/*
		 * curl --data "username=datauser@nowhere.com" --data
		 * "password=password" -X POST http://localhost:8080/auth/session.json
		 */

		// no user
		given().formParam("username", "doesnot@exist.com")
				.formParam("password", "anything").expect().statusCode(401)
				.when().post("/auth/session.json");

		given().formParam("username", "doesnot@exist.com")
				.formParam("password", "anything").expect().statusCode(401)
				.when().post("/auth/session.xml");

		// success
		String sessionToken = given()
				.formParam("username", "datauser@nowhere.com")
				.formParam("password", "password").expect().statusCode(200)
				.when().post("/auth/session.json").asString();
		Assert.assertEquals(36, sessionToken.length());

		sessionToken = given().formParam("username", "datauser@nowhere.com")
				.formParam("password", "password").expect().statusCode(200)
				.when().post("/auth/session.xml").asString();
		Assert.assertEquals(36, sessionToken.length());

		// fail
		given().formParam("username", "datauser@nowhere.com")
				.formParam("password", "wrongpassword").expect()
				.statusCode(401).when().post("/auth/session.json").asString();

		given().formParam("username", "datauser@nowhere.com")
				.formParam("password", "wrongpassword").expect()
				.statusCode(401).when().post("/auth/session.xml").asString();

	}

	@Test
	public void testWhoIs() {

		/*
		 * curl
		 * http://localhost:8080/auth/session.json/asdfasdfas234234q23asdfasdfas/whois
		 */

		// token not valid
		expect().statusCode(404).when()
				.get("/auth/session.json/{token}/whois", "not-a-valid-token");
		
		// get a token to validate lookup
		String token = login(dataUser.getEmail(), "password");
		String email = get("/auth/session.json/{token}/whois", token).asString();
		Assert.assertEquals(dataUser.getEmail(), email);
		
	}
}
