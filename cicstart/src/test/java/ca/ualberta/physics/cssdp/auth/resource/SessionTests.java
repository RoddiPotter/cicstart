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

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.Session;
import ca.ualberta.physics.cssdp.domain.auth.User;

import com.jayway.restassured.http.ContentType;

public class SessionTests extends AuthTestsScaffolding {

	@Test
	public void testAuthenticate_BASIC() {
		/*
		 * curl --user does@exist.com:anything -X POST
		 * http://localhost:8080/cicstart/api/auth/session
		 */

		// preemptive() is required to always send authentication header since
		// our server won't request it.

		// no user
		given().auth().preemptive().basic("doesnot@exist.com", "anything")
				.contentType(ContentType.JSON).expect().statusCode(404).when()
				.post(ResourceUrls.SESSION);
		given().auth().preemptive().basic("doesnot@exist.com", "anything")
				.contentType(ContentType.XML).expect().statusCode(404).when()
				.post(ResourceUrls.SESSION);

		// success
		Session session = given().auth().preemptive()
				.basic("datauser@nowhere.com", "password")
				.contentType(ContentType.JSON).expect().statusCode(200)
				.post(ResourceUrls.SESSION).as(Session.class);
		Assert.assertEquals(36, session.getToken().length());

		session = given().auth().preemptive()
				.basic("datauser@nowhere.com", "password")
				.contentType(ContentType.XML).expect().statusCode(200)
				.post(ResourceUrls.SESSION).as(Session.class);
		Assert.assertEquals(36, session.getToken().length());

		// fail
		given().auth().preemptive()
				.basic("datauser@nowhere.com", "wrongpassword")
				.contentType(ContentType.JSON).expect().statusCode(404)
				.post(ResourceUrls.SESSION);

		given().auth().preemptive()
				.basic("datauser@nowhere.com", "wrongpassword")
				.contentType(ContentType.XML).expect().statusCode(404)
				.post(ResourceUrls.SESSION);

	}

	@Test
	public void testAuthentication_form() {
		/*
		 * curl --data "username=datauser@nowhere.com" --data
		 * "password=password" -X POST
		 * http://localhost:8080/cicstart/api/auth/session
		 */

		// no user
		given().formParam("username", "doesnot@exist.com")
				.formParam("password", "anything")
				.contentType(ContentType.JSON).expect().statusCode(404).when()
				.post(ResourceUrls.SESSION);

		given().formParam("username", "doesnot@exist.com")
				.formParam("password", "anything").contentType(ContentType.XML)
				.expect().statusCode(404).when().post(ResourceUrls.SESSION);

		// success
		String sessionToken = given()
				.formParam("username", "datauser@nowhere.com")
				.formParam("password", "password")
				.contentType(ContentType.JSON).expect().statusCode(200).when()
				.post(ResourceUrls.SESSION).as(Session.class).getToken();
		Assert.assertEquals(36, sessionToken.length());

		sessionToken = given().formParam("username", "datauser@nowhere.com")
				.formParam("password", "password").expect().statusCode(200)
				.when().contentType(ContentType.XML).post(ResourceUrls.SESSION)
				.as(Session.class).getToken();
		Assert.assertEquals(36, sessionToken.length());

		// fail
		given().formParam("username", "datauser@nowhere.com")
				.formParam("password", "wrongpassword")
				.contentType(ContentType.JSON).expect().statusCode(404).when()
				.post(ResourceUrls.SESSION);

		given().formParam("username", "datauser@nowhere.com")
				.formParam("password", "wrongpassword")
				.contentType(ContentType.XML).expect().statusCode(404).when()
				.post(ResourceUrls.SESSION);

	}

	@Test
	public void testWhoIs() {

		/*
		 * curl http://localhost:8080/cicstart/api/auth/session/
		 * asdfasdfas234234q23asdfasdfas /whois
		 */

		// token not valid
		expect().statusCode(404)
				.when()
				.get(ResourceUrls.SESSION + "/{token}/whois",
						"not-a-valid-token");

		// get a token to validate lookup
		String token = given().auth().preemptive()
				.basic("datauser@nowhere.com", "password")
				.post(ResourceUrls.SESSION).as(Session.class).getToken();
		String email = get(ResourceUrls.SESSION + "/{token}/whois", token).as(
				User.class).getEmail();
		Assert.assertEquals(dataUser.getEmail(), email);
		Assert.assertEquals(dataUser.getPassword(), "****");
		Assert.assertEquals(dataUser.getOpenStackUsername(), "****");
		Assert.assertEquals(dataUser.getOpenStackUsername(), "****");

	}
}
