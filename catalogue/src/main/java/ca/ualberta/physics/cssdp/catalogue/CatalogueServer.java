package ca.ualberta.physics.cssdp.catalogue;

import ca.ualberta.physics.cssdp.configuration.ComponentProperties;


public class CatalogueServer extends ComponentProperties {

	public static CatalogueServer properties() {
		return (CatalogueServer) ComponentProperties.properties(CatalogueServer.class);
	}
	
}
