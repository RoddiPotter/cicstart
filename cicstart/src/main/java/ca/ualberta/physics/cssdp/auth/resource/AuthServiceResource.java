package ca.ualberta.physics.cssdp.auth.resource;

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
@Path("/auth/service")
public class AuthServiceResource extends AbstractServiceResource {

	@Inject
	private StatsService statsService;

	public AuthServiceResource() {
		InjectorHolder.inject(this);
	}

	@Override
	protected ServiceInfo buildInfo() {
		ServiceInfo info = new ServiceInfo();
		info.setName(StatsService.ServiceName.AUTH);
		info.setSynopsis("A generic authentication service used for user and session management.");
		return info;
	}

	@Override
	protected ServiceStats buildStats() {
		return statsService.find(StatsService.ServiceName.AUTH).getPayload();
	}

	@Override
	protected URI getDocURI() throws URISyntaxException {
		return new URI("https://github.com/RoddiPotter/cicstart/wiki/Auth");
	}

}
