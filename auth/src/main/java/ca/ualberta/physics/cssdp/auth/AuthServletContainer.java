package ca.ualberta.physics.cssdp.auth;

import ca.ualberta.physics.cssdp.configuration.CommonServletContainer;

/**
 * Binds the Authorization Server Resources to the JerseryServlet and maps them
 * to the appropriate url mapping
 */
public class AuthServletContainer extends CommonServletContainer {

	private static final long serialVersionUID = 1L;

	@Override
	protected void touchComponentProperties() {
		AuthServer.properties();
	}


}
