package ca.ualberta.physics.cssdp.file.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ca.ualberta.physics.cssdp.domain.ServiceInfo;
import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;
import ca.ualberta.physics.cssdp.file.InjectorHolder;
import ca.ualberta.physics.cssdp.resource.AbstractServiceResource;
import ca.ualberta.physics.cssdp.service.StatsService;

import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;

/*
 * Normally paths at class levels end with .json or .xml so the auto-api documentation
 * works properly.  This won't work but we are constrainted due to CANARIE's requirements
 * for this service.
 */
@Path("/service")
@Api(value = "/service", description = "Generic info about this service")
@Produces({ MediaType.APPLICATION_JSON })
public class FileServiceResource extends AbstractServiceResource {

	@Inject
	private StatsService statsService;

	public FileServiceResource() {
		InjectorHolder.inject(this);
	}

	@Override
	protected ServiceInfo buildInfo() {
		ServiceInfo info = new ServiceInfo();
		info.name = ServiceName.FILE;
		info.synopsis = "A generic file transfer and caching service.  Give it a URL and get the file data.  Useful for downloading and caching file data resulting from Catalogue search results.";
		return info;
	}

	@Override
	protected ServiceStats buildStats() {
		return statsService.find(ServiceName.FILE).getPayload();
	}

	@Override
	protected URI getDocURI() throws URISyntaxException {
		return new URI("https://github.com/RoddiPotter/cicstart/wiki/File");
	}

}
