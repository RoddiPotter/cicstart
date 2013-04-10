package ca.ualberta.physics.cssdp.file;

import ca.ualberta.physics.cssdp.configuration.CommonModule;
import ca.ualberta.physics.cssdp.file.dao.CachedFileDao;
import ca.ualberta.physics.cssdp.file.dao.HostEntryDao;
import ca.ualberta.physics.cssdp.file.remote.RemoteServers;
import ca.ualberta.physics.cssdp.file.remote.RemoteServersImpl;
import ca.ualberta.physics.cssdp.file.service.CacheService;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configured various infrastructure items needed for the application to work
 */
public class FileServerModule extends AbstractModule {

	@Override
	protected void configure() {

		install(new CommonModule());

		// daos & services
		bind(CachedFileDao.class).in(Scopes.SINGLETON);
		bind(HostEntryDao.class).in(Scopes.SINGLETON);

		bind(RemoteServers.class).to(RemoteServersImpl.class).in(
				Scopes.SINGLETON);
		bind(CacheService.class).in(Scopes.SINGLETON);

	}

}
