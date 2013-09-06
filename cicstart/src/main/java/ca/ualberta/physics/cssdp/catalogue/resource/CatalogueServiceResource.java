package ca.ualberta.physics.cssdp.catalogue.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Path;

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
@Path("/catalogue/service")
public class CatalogueServiceResource extends AbstractServiceResource {

	@Inject
	private StatsService statsService;

	public CatalogueServiceResource() {
		InjectorHolder.inject(this);
	}

	@Override
	protected ServiceInfo buildInfo() {
		ServiceInfo info = new ServiceInfo();
		info.setName(StatsService.ServiceName.CATALOGUE);
		info.setSynopsis("A generic catalogue service used for mapping urls to metadata.  Metadata can then be searched to find those urls.");
		return info;
	}

	@Override
	protected ServiceStats buildStats() {
		return statsService.find(StatsService.ServiceName.CATALOGUE).getPayload();
	}

	@Override
	protected URI getDocURI() throws URISyntaxException {
		return new URI("https://github.com/RoddiPotter/cicstart/wiki/Catalogue");
	}

}
