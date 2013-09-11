package ca.ualberta.physics.cssdp.file.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Path;

import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.ServiceInfo;
import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.resource.AbstractServiceResource;
import ca.ualberta.physics.cssdp.service.StatsService;

import com.google.inject.Inject;


@Path("/file/service")
public class FileServiceResource extends AbstractServiceResource {

	@Inject
	private StatsService statsService;

	public FileServiceResource() {
		InjectorHolder.inject(this);
	}

	@Override
	protected ServiceInfo buildInfo() {
		ServiceInfo info = new ServiceInfo();
		info.setName(StatsService.ServiceName.FILE);
		info.setSynopsis("A generic file transfer and caching service.  Give it a URL and get the file data.  Useful for downloading and caching file data resulting from Catalogue search results.");
		return info;
	}

	@Override
	protected ServiceStats buildStats() {
		return statsService.find(StatsService.ServiceName.FILE).getPayload();
	}

	@Override
	protected URI getDocURI() throws URISyntaxException {
		return new URI("https://github.com/RoddiPotter/cicstart/wiki/File");
	}

}
