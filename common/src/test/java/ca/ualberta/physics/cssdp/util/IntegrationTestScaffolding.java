/* ============================================================
 * IntegrationTestScaffolding.java
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
package ca.ualberta.physics.cssdp.util;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.RestAssuredConfig.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;

import ca.ualberta.physics.cssdp.configuration.JSONObjectMapperProvider;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import com.jayway.restassured.response.Response;

public abstract class IntegrationTestScaffolding extends TestSupport {

	private static Server server;

	@Before
	public void setupEnvironment() {

		config().objectMapperConfig(
				new ObjectMapperConfig()
						.jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {

							@Override
							public ObjectMapper create(
									@SuppressWarnings("rawtypes") Class cls,
									String charset) {
								return new JSONObjectMapperProvider().get();
							}
						}));

		if (server == null || !server.isStarted()) {
			// configure Jetty as an embedded web application server
			server = new Server();
			server.setStopAtShutdown(true);
			server.setGracefulShutdown(1000);

			SocketConnector connector = new SocketConnector();
			connector.setPort(8080);
			server.addConnector(connector);

			WebAppContext context = new WebAppContext();
			context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
			context.setResourceBase("src/main/webapp");
			context.setContextPath(getComponetContext());
			context.setParentLoaderPriority(true);
			server.setHandler(context);

			try {
				server.start();
			} catch (Exception e) {
				Throwables.propagate(e);
			}

		}
	}

	protected abstract String getComponetContext();

	protected User setupDataManager() {

		User newDataManager = new User();
		newDataManager.setName("Data Manager");
		newDataManager.setDeleted(false);
		newDataManager.setEmail("datamanager@nowhere.com");
		newDataManager.setInstitution("institution");
		newDataManager.setPassword("password");
		newDataManager.setRole(Role.DATA_MANAGER);

		Response res = given().content(newDataManager).and()
				.contentType("application/json")
				.post("http://localhost:8081/auth/user.json");

		String location = res.getHeader("location");
		User dataManager = given().contentType(ContentType.JSON).get(location)
				.as(User.class);

		return dataManager;
	}

	/**
	 * Login into the system. Can't use cached users in this class directly
	 * because the response of getting the user info masks the password, so you
	 * need prior knowledge of the passwords (by looking at the setup users
	 * method to find them). Assumes Auth is running.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	protected String login(String username, String password) {
		return given().formParam("username", username)
				.formParam("password", password)
				.post("http://localhost:8081/auth/session.json").asString();
	}
}
