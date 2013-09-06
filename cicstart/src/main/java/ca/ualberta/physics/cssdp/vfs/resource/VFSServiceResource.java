package ca.ualberta.physics.cssdp.vfs.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.ServiceInfo;
import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.resource.AbstractServiceResource;
import ca.ualberta.physics.cssdp.service.StatsService;
import ca.ualberta.physics.cssdp.service.StatsService.ServiceName;

import com.google.inject.Inject;

/*
 * Normally paths at class levels end with .json or .xml so the auto-api documentation
 * works properly.  This won't work but we are constrainted due to CANARIE's requirements
 * for this service.
 */
@Path("/vfs/service")
public class VFSServiceResource extends AbstractServiceResource {

	private static final Logger logger = LoggerFactory.getLogger(VFSServiceResource.class);
	
	@Inject
	private StatsService statsService;

	public VFSServiceResource() {
		InjectorHolder.inject(this);
	}

	@Override
	protected ServiceInfo buildInfo() {
		ServiceInfo info = new ServiceInfo();
		info.setName(StatsService.ServiceName.VFS);
		info.setSynopsis("Long term persistent storage that can interact with other CICSTART services.  "
				+ "Useful for accessing input files required by Macros running on spawned VMs.  Also useful "
				+ "for storing output from Macros.");
		return info;
	}

	@Override
	protected ServiceStats buildStats() {
		logger.debug("Have a stats service " + statsService);
		return statsService.find(StatsService.ServiceName.VFS).getPayload();
	}

	@Override
	protected URI getDocURI() throws URISyntaxException {
		return new URI("https://github.com/RoddiPotter/cicstart/wiki/VFS");
	}

}
