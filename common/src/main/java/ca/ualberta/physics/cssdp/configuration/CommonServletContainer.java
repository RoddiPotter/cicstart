/* ============================================================
 * CommonServletContainer.java
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
package ca.ualberta.physics.cssdp.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Binds the Authorization Server Resources to the JerseryServlet and maps them
 * to the appropriate url mapping
 */
public abstract class CommonServletContainer extends ServletContainer {

	private static final Logger logger = LoggerFactory
			.getLogger(CommonServletContainer.class);

	private static final long serialVersionUID = 1L;

	public static final ThreadLocal<Boolean> readWebXml = new ThreadLocal<Boolean>();

	@Override
	public void init() throws ServletException {

		// this needs to happen before any other initialized code runs.

		String applicationPropertiesFile = getServletContext()
				.getInitParameter("application.properties");

		Boolean useWebXmlOverrides = readWebXml.get();

		if (useWebXmlOverrides == null || useWebXmlOverrides) {

			if (!Strings.isNullOrEmpty(applicationPropertiesFile)) {

				// must load components first.
				Common.properties();

				// ideally these would be in each sub-project, but since we
				// want to reduce the number of configuration, we must
				// initialize these here and so they must be in the common
				// project.
				// TODO refactor Component properties to handle single overrides
				AuthServer.properties();
				CatalogueServer.properties();
				FileServer.properties();
				VfsServer.properties();

				Properties overrides = new Properties();
				try {
					System.out.println("Loading property overrides from "
							+ applicationPropertiesFile);
					overrides.load(new FileInputStream(new File(
							applicationPropertiesFile)));

					ApplicationProperties.overrideDefaults(overrides);

				} catch (FileNotFoundException e) {

					System.out.println("No override file found at "
							+ applicationPropertiesFile
							+ ", reverting to defaults.");
					ComponentProperties.printAll();

				} catch (IOException e) {
					throw Throwables.propagate(e);
				}
			} else {
				logger.warn("No application.properties init parameter found, things may not work as expected.");
			}
		} else {
			logger.info("Not reading application.properties from web.xml because we are testing.");
		}

		super.init();
	}

}
