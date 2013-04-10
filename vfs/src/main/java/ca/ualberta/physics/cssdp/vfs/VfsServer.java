package ca.ualberta.physics.cssdp.vfs;

import ca.ualberta.physics.cssdp.configuration.ComponentProperties;


public class VfsServer extends ComponentProperties {

	public static VfsServer properties() {
		return (VfsServer) ComponentProperties.properties(VfsServer.class);
	}
	
}
