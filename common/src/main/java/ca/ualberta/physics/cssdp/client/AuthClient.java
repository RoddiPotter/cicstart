package ca.ualberta.physics.cssdp.client;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import javax.ws.rs.WebApplicationException;

import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class AuthClient {

	private String authUrl = Common.properties().getString("auth.url");

	public ServiceResponse<String> login(String username, String password) {

		Response res = given().auth().preemptive().basic(username, password)
				.post(authUrl + "/session.json");

		ServiceResponse<String> sr = new ServiceResponse<String>();
		if (res.getStatusCode() == 200) {
			sr.setPayload(res.asString());
		} else {
			sr.error("Invalid login credentials");
		}
		return sr;
	}

	public void validate(String sessionToken) {

		Response res = RestAssured.get(authUrl + "/session.json/"
				+ sessionToken + "/whois");

		if (res.statusCode() != 200) {
			throw new WebApplicationException(404);
		}
	}

	public User addUser(User newUser) {

		String location = given().content(newUser).and()
				.contentType("application/json").post(authUrl + "/user.json")
				.getHeader("location");

		User user = get(location).as(User.class);
		user.setPassword(newUser.getPassword());
		return user;

	}

	public User whois(String sessionToken) {
		Response res = RestAssured.get(authUrl + "/session.json/"
				+ sessionToken + "/whois");
		if (res.getStatusCode() == 200) {
			return res.as(User.class);
		} else {
			throw new WebApplicationException(404);
		}
	}
}
