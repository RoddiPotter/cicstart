/* ============================================================
 * AuthClient.java
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
package ca.ualberta.physics.cssdp.auth.service;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.Session;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class AuthClient {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthClient.class);

	// private String authUrl = Common.properties().getString("api.url") +
	// "/auth";

	public ServiceResponse<String> login(String username, String password) {

		logger.debug("Authenticating at " + ResourceUrls.SESSION);

		Response res = given().auth().preemptive().basic(username, password)
				.post(ResourceUrls.SESSION);

		ServiceResponse<String> sr = new ServiceResponse<String>();
		if (res.getStatusCode() == 200) {
			sr.setPayload(res.as(Session.class).getToken());
		} else {
			sr.error("Invalid login credentials");
		}
		return sr;
	}

	public void validate(String sessionToken) {

		Response res = RestAssured.get(ResourceUrls.SESSION + "/"
				+ sessionToken + "/whois");

		if (res.statusCode() != 200) {
			throw new WebApplicationException(404);
		}
	}

	public User addUser(User newUser) {

		String location = given().content(newUser).and()
				.contentType("application/json").post(ResourceUrls.USER)
				.getHeader("location");

		User user = get(location).as(User.class);
		user.setPassword(newUser.getPassword());
		return user;

	}

	public User whois(String sessionToken) {
		Response res = RestAssured.get(ResourceUrls.SESSION + "/"
				+ sessionToken + "/whois");
		if (res.getStatusCode() == 200) {
			return res.as(User.class);
		} else {
			throw new WebApplicationException(404);
		}
	}
}
