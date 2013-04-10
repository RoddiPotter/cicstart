package ca.ualberta.physics.cssdp.configuration;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;

public class Slf4jLoggerInitializer extends AbstractModule {

	@Override
	protected void configure() {

		String logbackXmlConfigurationFile = Common.properties().getString(
				"logback.configuration.xml");
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
			throw Throwables.propagate(e);
		}
		
	}


}
