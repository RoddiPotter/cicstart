package ca.ualberta.physics.cssdp.auth;

import ca.ualberta.physics.cssdp.configuration.ComponentProperties;


public class AuthServer extends ComponentProperties {

	public static AuthServer properties() {
		return (AuthServer) ComponentProperties.properties(AuthServer.class);
	}
	
}
