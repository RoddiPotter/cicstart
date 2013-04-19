package ca.ualberta.physics.cssdp.configuration;

import javax.servlet.ServletConfig;

import com.wordnik.swagger.jaxrs.ConfigReader;

public class SwaggerConfigReader extends ConfigReader {

	private final ServletConfig sc;

	public SwaggerConfigReader(ServletConfig sc) {
		this.sc = sc;
	}

	@Override
	public String basePath() {
		return Common.properties().getString(
				sc.getInitParameter("common.properties_url.key"));
	}

	@Override
	public String swaggerVersion() {
		return "1.1";
	}

	@Override
	public String apiVersion() {
		return Common.properties().getString(
				sc.getInitParameter("common.properties_url.key"));
	}

	@Override
	public String modelPackages() {
		return null;
	}

	@Override
	public String apiFilterClassName() {
		return "ca.ualberta.physics.cssdp.util.ApiAuthorizationFilterImpl";
	}

}
