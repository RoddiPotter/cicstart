/* ============================================================
 * Slf4jLoggerInitializer.java
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

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.google.inject.AbstractModule;

public class Slf4jLoggerInitializer extends AbstractModule {

	@Override
	protected void configure() {

		String logbackXmlConfigurationFile = Common.properties().getString(
				"logback.configuration");

		File logbackXmlConfiguration = new File(logbackXmlConfigurationFile);

		LoggerContext context = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset(); // override default configuration

		try {
			System.out.println("Overriding logback.xml found on "
					+ "classpath and configuring logback from file "
					+ logbackXmlConfiguration.getAbsolutePath());
			jc.doConfigure(logbackXmlConfiguration.getAbsolutePath());
		} catch (JoranException e) {
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(context);

	}

}
