package ca.ualberta.physics.cssdp.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;

import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.Common;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Binds the Authorization Server Resources to the JerseryServlet and maps them
 * to the appropriate url mapping
 */
public abstract class CommonServletContainer extends ServletContainer {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {

		// this needs to happen before any other initialized code runs.

		String applicationPropertiesFile = getServletContext()
				.getInitParameter("application.properties");

		if (!Strings.isNullOrEmpty(applicationPropertiesFile)) {

			// must load components first.
			Common.properties();
			touchComponentProperties();

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

			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		} else {
			System.out
					.println("No application.properties init parameter found, using default properties.");
		}

		super.init();
	}

	protected abstract void touchComponentProperties();

}
