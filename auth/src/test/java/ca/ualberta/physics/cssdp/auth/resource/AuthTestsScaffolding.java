/* ============================================================
 * AuthTestScaffolding.java
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

import org.junit.Before;

import com.jayway.restassured.http.ContentType;

import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;
import ca.ualberta.physics.cssdp.util.IntegrationTestScaffolding;

public class AuthTestsScaffolding extends IntegrationTestScaffolding {

	protected User dataUser;

	@Override
	protected String getComponetContext() {
		return "/auth";
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
				.post(baseUrl() + "/user.json");

		dataUser = given().contentType(ContentType.JSON)
				.get(baseUrl() + "/user.json/datauser@nowhere.com")
				.as(User.class);
	}

}
