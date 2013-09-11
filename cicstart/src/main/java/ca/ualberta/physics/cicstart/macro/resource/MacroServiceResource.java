package ca.ualberta.physics.cicstart.macro.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Path;

import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.ServiceInfo;
import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.resource.AbstractServiceResource;
import ca.ualberta.physics.cssdp.service.StatsService;

import com.google.inject.Inject;


@Path("/macro/service")
public class MacroServiceResource extends AbstractServiceResource {

	@Inject
	private StatsService statsService;

	public MacroServiceResource() {
		InjectorHolder.inject(this);
	}

	@Override
	protected ServiceInfo buildInfo() {
		ServiceInfo info = new ServiceInfo();
		info.setName(StatsService.ServiceName.MACRO);
		info.setSynopsis("A facility to script cloud actions and run arbitrary commands on a spawned VM.  "
				+ "The scripting functions also allow for interaction with all other CICSTART services, providing "
				+ "a mechanism for you to build fast, custom CICSTART clients to suite your needs.");
		return info;
	}

	@Override
	protected ServiceStats buildStats() {
		return statsService.find(StatsService.ServiceName.MACRO).getPayload();
	}

	@Override
	protected URI getDocURI() throws URISyntaxException {
		return new URI("https://github.com/RoddiPotter/cicstart/wiki/Macro");
	}

}
