package ca.ualberta.physics.cicstart.macro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cicstart.macro.service.MacroService;
import ca.ualberta.physics.cssdp.auth.configuration.AuthServer;
import ca.ualberta.physics.cssdp.catalogue.configuration.CatalogueServer;
import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.ComponentProperties;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.file.configuration.FileServer;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.vfs.configuration.VfsServer;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class MainClass {

	private static final Logger logger = LoggerFactory
			.getLogger(MainClass.class);

	public static void main(String args[]) {

		if (args.length < 3) {
			logger.error("Must specify application.properties file, cml script, and session Token.");
			System.exit(1);
		}

		File applicationProperties = new File(args[0]);
		if (applicationProperties.exists()) {
			// must load components first.
			Common.properties();

			// TODO refactor Component properties to handle single overrides
			AuthServer.properties();
			CatalogueServer.properties();
			FileServer.properties();
			VfsServer.properties();
			MacroServer.properties();

			Properties overrides = new Properties();
			try {
				logger.info("Loading property overrides from "
						+ applicationProperties);
				overrides.load(new FileInputStream(applicationProperties));

				// on the client, we always run in server mode.
				overrides.setProperty("macroserver.isServer", "false");

				ApplicationProperties.overrideDefaults(overrides);

			} catch (FileNotFoundException e) {

				logger.warn("No override file found at "
						+ applicationProperties
						+ ", reverting to defaults. (this is probably going to be a problem for you)");
				ComponentProperties.printAll();

			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		} else {
			System.out
					.println("No application.properties init parameter found, things may not work as expected.");
		}

		// does the same thing as calling MacroResource.run(Macro macro) would
		// do.

		MainClass mc = new MainClass();

		String cmlScript = null;
		try {
			cmlScript = Files.toString(new File(args[1]),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			logger.error("Could not load CML script " + args[1] + "\n"
					+ Throwables.getStackTraceAsString(e));
			System.exit(1);
		}

		String sessionToken = args[2];
		String jobId = args[3];

		ServiceResponse<String> sr = mc.runCmlScript(cmlScript, sessionToken,
				jobId);

		if (sr.isRequestOk()) {
			logger.info("All done, program exiting with normal error code 0");
			System.exit(0);
		} else {
			logger.error("Exiting with error code 1: "
					+ sr.getMessagesAsStrings());
			System.exit(1);
		}
	}

	@Inject
	private MacroService macroService;

	public MainClass() {
		InjectorHolder.inject(this);
	}

	public ServiceResponse<String> runCmlScript(String cmlScript,
			String sessionToken, String jobId) {
		logger.info("about to run script: " + cmlScript);
		ServiceResponse<String> sr = macroService.run(cmlScript, sessionToken,
				jobId);
		if (sr.isRequestOk()) {
			String requestId = sr.getPayload();
			logger.info("Tailing logs now");
			macroService.connectToLogStream(requestId, System.out);
		}
		return sr;
	}

}
