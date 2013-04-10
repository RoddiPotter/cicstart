package ca.ualberta.physics.cssdp.auth.resource;

import static com.jayway.restassured.RestAssured.given;

import org.junit.Before;

import com.jayway.restassured.http.ContentType;

import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

public class AuthTestsScaffolding extends IntegrationTestScaffolding {

	protected User dataUser;

	@Override
	protected String getComponetContext() {
		return "";
	}

	@Before
	public void setupTestUsers() {

		User newDataUser = new User();
		newDataUser.setName("Data User");
		newDataUser.setDeleted(false);
		newDataUser.setEmail("datauser@nowhere.com");
		newDataUser.setInstitution("institution");
		newDataUser.setPassword("password");
		newDataUser.setRole(Role.DATA_USER);

		given().content(newDataUser).and().contentType("application/json")
				.post("/auth/user.json");

		dataUser = given().contentType(ContentType.JSON)
				.get("/auth/user.json/datauser@nowhere.com").as(User.class);
	}

}
