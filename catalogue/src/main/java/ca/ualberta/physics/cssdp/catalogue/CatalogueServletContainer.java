package ca.ualberta.physics.cssdp.catalogue;

import javax.servlet.ServletException;

import ca.ualberta.physics.cssdp.configuration.CommonServletContainer;

/**
 * Binds the File Server Resources to the JerseryServlet and maps them to the
 * appropriate url mapping
 */
public class CatalogueServletContainer extends CommonServletContainer {

	private static final long serialVersionUID = 1L;

	@Override
	protected void touchComponentProperties() {
		CatalogueServer.properties();
	}

	@Override
	public void init() throws ServletException {
		// touch the guice module so that things like the custom JSON Object
		// Mapper is registered with Jackson
		InjectorHolder.get();
		super.init();
	}
}
