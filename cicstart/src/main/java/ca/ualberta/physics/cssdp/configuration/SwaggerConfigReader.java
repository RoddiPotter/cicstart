package ca.ualberta.physics.cssdp.configuration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.config.WebXMLReader;
import com.wordnik.swagger.jersey.JerseyApiReader;
import com.wordnik.swagger.reader.ClassReaders;

public class SwaggerConfigReader extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(final ServletConfig servletConfig) {
		try {
			super.init(servletConfig);
			ConfigFactory.setConfig(new WebXMLReader(servletConfig) {
				@Override
				public String getApiPath() {
					return Common
							.properties()
							.getString(
									servletConfig
											.getInitParameter("common.properties_url.key"));
				}

				@Override
				public String getBasePath() {
					return Common
							.properties()
							.getString(
									servletConfig
											.getInitParameter("common.properties_url.key"));
				}
			});
			ScannerFactory.setScanner(new DefaultJaxrsScanner());
			ClassReaders.setReader(new JerseyApiReader());

		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @Override
	// public String basePath() {
	// return Common.properties().getString(
	// sc.getInitParameter("common.properties_url.key"));
	// }
	//
	// @Override
	// public String apiPath() {
	// return Common.properties().getString(
	// sc.getInitParameter("common.properties_url.key"));
	// }
	//
	// @Override
	// public String modelPackages() {
	// return null;
	// }
	//
	// @Override
	// public String apiFilterClassName() {
	// return "ca.ualberta.physics.cssdp.util.ApiAuthorizationFilterImpl";
	// }

}
