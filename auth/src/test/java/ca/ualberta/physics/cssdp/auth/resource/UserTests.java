/* ============================================================
 * UserTests.java
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

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.domain.auth.User;

public class UserTests extends AuthTestsScaffolding {

	@Test
	public void testCreateUser() {

		// use default role and deleted properties
		User user = new User();
		user.setCountry("Canada");
		user.setEmail("testuser1@nowhere.com");
		user.setInstitution("none");
		user.setName("Test User");
		user.setPassword("password");

		given().content(user)
				.and()
				.contentType("application/json")
				.expect()
				.statusCode(201)
				.and()
				.header("location",
						"http://localhost:8080/auth/user.json/testuser1@nowhere.com")
				.when().post("/auth/user.json");

		User created = get(
				"http://localhost:8080/auth/user.json/testuser1@nowhere.com")
				.as(User.class);
		
		Assert.assertTrue(created.getId() > 0);
		Assert.assertEquals("Canada", created.getCountry());
		Assert.assertEquals("testuser1@nowhere.com", created.getEmail());
		Assert.assertEquals("none", created.getInstitution());
		Assert.assertEquals("Test User", created.getName());
		Assert.assertEquals("******", created.getPassword());
		Assert.assertNull(created.getPasswordDigest());
		Assert.assertNull(created.getPasswordSalt());
		
	}

}
