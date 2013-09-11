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

import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.domain.auth.User;

import com.jayway.restassured.response.Response;

public class UserTests extends AuthTestsScaffolding {

	@Test
	public void testCreateAndUpdateUser() {

		// use default role and deleted properties
		User user = new User();
		user.setCountry("Canada");
		user.setEmail("testuser1@nowhere.com");
		user.setInstitution("none");
		user.setName("Test User");
		user.setPassword("password");

		Response res = given()
				.content(user)
				.and()
				.contentType("application/json")
				.expect()
				.statusCode(201)
				.and()
				.header("location",
						ResourceUrls.USER + "/testuser1@nowhere.com").when()
				.post(ResourceUrls.USER);

		String findUserUrl = res.getHeader("location");
		User created = get(findUserUrl).as(User.class);

		Assert.assertNotNull(created.getId());
		Assert.assertTrue(created.getId() > 0);
		Assert.assertEquals("Canada", created.getCountry());
		Assert.assertEquals("testuser1@nowhere.com", created.getEmail());
		Assert.assertEquals("none", created.getInstitution());
		Assert.assertEquals("Test User", created.getName());
		Assert.assertEquals("****", created.getPassword());
		Assert.assertNull(created.getPasswordDigest());
		Assert.assertNull(created.getPasswordSalt());

		String sessionToken = login(created.getEmail(), "password");

		created.setEmail("new_email@nowhere.com");
		created.setInstitution("UofA");

		res = given().content(created).and()
				.header("CICSTART.session", sessionToken).and()
				.contentType("application/json").expect().statusCode(200)
				.when().put(ResourceUrls.USER);

		User updated = get(res.getHeader("location")).as(User.class);
		Assert.assertEquals("new_email@nowhere.com", updated.getEmail());
		Assert.assertEquals("UofA", updated.getInstitution());
		Assert.assertEquals(created.getPasswordDigest(),
				updated.getPasswordDigest());
		Assert.assertEquals(created.getPasswordSalt(),
				updated.getPasswordSalt());

	}

}
