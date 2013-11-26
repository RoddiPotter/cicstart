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

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.BeforeClass;

import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.JSONObjectMapperProvider;
import ca.ualberta.physics.cssdp.configuration.ResourceUrls;
import ca.ualberta.physics.cssdp.dao.EntityManagerInterceptor;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;

import com.coderod.db.migrations.Migrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import com.jayway.restassured.response.Response;

public abstract class IntegrationTestScaffolding {

	private static Server server;

	@BeforeClass
	public static void setupDb() {
		
		ApplicationProperties.dropOverrides();

		// initialize default properties
		Common.properties();

		// override the database connection url to point to an in-memory
		// database
		Properties overrides = new Properties();
		overrides.put("common.logback.configuration",
				"src/test/resources/logback-test.xml");
		overrides
				.setProperty(
						"common.hibernate.connection.url",
						"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;TRACE_LEVEL_FILE=0;");
		ApplicationProperties.overrideDefaults(overrides);
		
		String url = Common.properties().getString("hibernate.connection.url");
		String driver = Common.properties().getString(
				"hibernate.connection.driver_class");
		String user = Common.properties().getString(
				"hibernate.connection.username");
		String password = Common.properties().getString(
				"hibernate.connection.password");
		String scriptsDir = "../database/migrations";

		Migrator migrator = new Migrator(url, driver, user, password,
				scriptsDir);
		migrator.initDb();
		migrator.migrateUpAll();

	}
	
	// start an auth server for tests that depend on the auth resources
	@Before
	public void setup() throws Exception {


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

			// override some common setup needed during actual runtime on servers
			EntityManagerInterceptor.readWebXml.set(Boolean.FALSE);

			// configure Jetty as an embedded web application server
			server = new Server(8080);
//			server.setStopAtShutdown(true);
//			server.setGracefulShutdown(1000);
//
//			SocketConnector connector = new SocketConnector();
//			connector.setPort(8080);
//			server.addConnector(connector);

			// this one is relative to the project we are testing
			WebAppContext context = new WebAppContext();
			context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
			context.setResourceBase("src/main/webapp");
			context.setContextPath("/cicstart");
			context.setParentLoaderPriority(true);

			server.setHandler(context);

			try {
				server.start();
			} catch (Exception e) {
				Throwables.propagate(e);
			}

		}
	}

	protected Server getServer() {
		return server;
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
		newDataManager.setOpenStackUsername(MacroServer.properties().getString("cicstart.test.openstack.username"));
		newDataManager.setOpenStackPassword(MacroServer.properties().getString("cicstart.test.openstack.password"));

		Response res = given().content(newDataManager).and()
				.contentType("application/json").post(ResourceUrls.USER);

		String location = res.getHeader("location");
		User dataManager = given().contentType(ContentType.JSON).get(location)
				.as(User.class);

		// bootstrap so tests will work.  cicstart masks automatically.
		dataManager.setOpenStackUsername(MacroServer.properties().getString("cicstart.test.openstack.username"));
		dataManager.setOpenStackPassword(MacroServer.properties().getString("cicstart.test.openstack.password"));

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
				.post(ResourceUrls.SESSION).asString();
	}
}
