package ca.ualberta.physics.cssdp.vfs;

import ca.ualberta.physics.cssdp.configuration.JSONObjectMapperProvider;
import ca.ualberta.physics.cssdp.vfs.service.FileSystemService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configured various infrastructure items needed for the application to work
 */
public class VfsServerModule extends AbstractModule {

	@Override
	protected void configure() {

		/*
		 * VFS is so simple (no database) that it doesn't need to install the
		 * common modules, but it does need the object mapper for json mapping
		 */
		bind(ObjectMapper.class).toProvider(new JSONObjectMapperProvider());

		bind(FileSystemService.class).in(Scopes.SINGLETON);

	}

}
