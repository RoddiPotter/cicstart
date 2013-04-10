package ca.ualberta.physics.cssdp.file;

import ca.ualberta.physics.cssdp.configuration.ComponentProperties;


public class FileServer extends ComponentProperties {

	public static FileServer properties() {
		return (FileServer) ComponentProperties.properties(FileServer.class);
	}
	
}
