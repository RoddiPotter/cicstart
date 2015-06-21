package ca.ualberta.physics.cssdp.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cssdp.auth.configuration.AuthServer;
import ca.ualberta.physics.cssdp.catalogue.configuration.CatalogueServer;
import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.configuration.ComponentProperties;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.file.configuration.FileServer;
import ca.ualberta.physics.cssdp.service.StatsService;
import ca.ualberta.physics.cssdp.vfs.configuration.VfsServer;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

public class EntityManagerInterceptor implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(EntityManagerInterceptor.class);

	public static final ThreadLocal<Boolean> readWebXml = new ThreadLocal<Boolean>();

	@Inject
	private EntityManagerProvider emProvider;

	@Inject
	private StatsService statsService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// this needs to happen before any other initialized code runs.

		String applicationPropertiesFile = filterConfig.getServletContext()
				.getInitParameter("application.properties");

		Boolean useWebXmlOverrides = readWebXml.get();

		if (useWebXmlOverrides == null || useWebXmlOverrides) {

			if (!Strings.isNullOrEmpty(applicationPropertiesFile)) {

				// must load components first.
				Common.properties();
				AuthServer.properties();
				CatalogueServer.properties();
				FileServer.properties();
				VfsServer.properties();
				MacroServer.properties();

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

		InjectorHolder.inject(this);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		try {
			logger.debug("Creating Entity Manager");
			emProvider.get();

			if (request instanceof HttpServletRequest) {
				
				logger.debug("got an http servlet request here...");
				
				String url = ((HttpServletRequest) request).getRequestURL()
						.toString();

				logger.debug("and the request is for " + url);

				if (url.contains("api-docs") || url.contains("service")) {
					// don't count stats on these.
					
				} else {

					if (url.contains("auth")) {
						logger.debug("Incrementing Auth count");
						statsService
								.incrementInvocationCount(StatsService.ServiceName.AUTH);
					}
					if (url.contains("catalogue")) {
						logger.debug("Incrementing Catalogue count");
						statsService
								.incrementInvocationCount(StatsService.ServiceName.CATALOGUE);
					}
					if (url.contains("file")) {
						logger.debug("Incrementing File count");
						statsService
								.incrementInvocationCount(StatsService.ServiceName.FILE);
					}
					if (url.contains("macro")) {
						logger.debug("Incrementing Macro count");
						statsService
								.incrementInvocationCount(StatsService.ServiceName.MACRO);
					}
					if (url.contains("vfs")) {
						logger.debug("Incrementing VFS count");
						statsService
								.incrementInvocationCount(StatsService.ServiceName.VFS);
					}

				}
			}

			logger.debug("going down filter chain now...");
			chain.doFilter(request, response);
			logger.debug("Hello, I'm back up the filter chain now");
			
		} catch (RuntimeException e) {
			logger.error("uh oh", e.getMessage(), e);
		} finally {
			emProvider.remove();
			logger.debug("Entity Manager destroyed");
		}

	}

	@Override
	public void destroy() {

	}

}
