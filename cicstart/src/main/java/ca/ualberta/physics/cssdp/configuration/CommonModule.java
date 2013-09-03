/* ============================================================
 * CommonModule.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.configuration;

import javax.persistence.EntityManager;

import ca.ualberta.physics.cicstart.macro.configuration.MacroServer;
import ca.ualberta.physics.cicstart.macro.service.CloudService;
import ca.ualberta.physics.cicstart.macro.service.MacroService;
import ca.ualberta.physics.cssdp.auth.service.EmailService;
import ca.ualberta.physics.cssdp.auth.service.EmailServiceImpl;
import ca.ualberta.physics.cssdp.auth.service.UserService;
import ca.ualberta.physics.cssdp.catalogue.dao.DataProductDao;
import ca.ualberta.physics.cssdp.catalogue.dao.InstrumentTypeDao;
import ca.ualberta.physics.cssdp.catalogue.dao.ObservatoryDao;
import ca.ualberta.physics.cssdp.catalogue.dao.ObservatoryGroupDao;
import ca.ualberta.physics.cssdp.catalogue.dao.ProjectDao;
import ca.ualberta.physics.cssdp.catalogue.dao.UrlDataProductDao;
import ca.ualberta.physics.cssdp.catalogue.service.CatalogueService;
import ca.ualberta.physics.cssdp.dao.EntityManagerProvider;
import ca.ualberta.physics.cssdp.file.dao.CachedFileDao;
import ca.ualberta.physics.cssdp.file.dao.HostEntryDao;
import ca.ualberta.physics.cssdp.file.remote.RemoteServers;
import ca.ualberta.physics.cssdp.file.remote.RemoteServersImpl;
import ca.ualberta.physics.cssdp.file.service.CacheService;
import ca.ualberta.physics.cssdp.vfs.service.FileSystemService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CommonModule extends AbstractModule {

	@Override
	protected void configure() {

		install(new Slf4jLoggerInitializer());
		bind(ObjectMapper.class).toProvider(new JSONObjectMapperProvider());

		/*
		 * Because Macro clients (and VFS clients) run outside of the server, we
		 * don't want any of this stuff to load on the client.
		 */
		if (MacroServer.properties().getBoolean("isServer")) {

			install(new JpaModule());
			install(new TransactionalModule());

			bind(EntityManager.class).toProvider(EntityManagerProvider.class)
					.asEagerSingleton();

			// Auth server stuff
			bind(UserService.class).in(Scopes.SINGLETON);
			bind(EmailService.class).to(EmailServiceImpl.class).in(
					Scopes.SINGLETON);

			// Catalogue server stuff
			bind(DataProductDao.class).in(Scopes.SINGLETON);
			bind(InstrumentTypeDao.class).in(Scopes.SINGLETON);
			bind(ObservatoryDao.class).in(Scopes.SINGLETON);
			bind(ObservatoryGroupDao.class).in(Scopes.SINGLETON);
			bind(ProjectDao.class).in(Scopes.SINGLETON);
			bind(UrlDataProductDao.class).in(Scopes.SINGLETON);
			bind(CatalogueService.class).in(Scopes.SINGLETON);

			// File server stuff
			bind(CachedFileDao.class).in(Scopes.SINGLETON);
			bind(HostEntryDao.class).in(Scopes.SINGLETON);
			bind(RemoteServers.class).to(RemoteServersImpl.class).in(
					Scopes.SINGLETON);
			bind(CacheService.class).in(Scopes.SINGLETON);

		}

		// Macro stuff
		bind(MacroService.class).in(Scopes.SINGLETON);
		bind(CloudService.class).in(Scopes.SINGLETON);

		// VFS stuff
		bind(FileSystemService.class).in(Scopes.SINGLETON);

	}

}
