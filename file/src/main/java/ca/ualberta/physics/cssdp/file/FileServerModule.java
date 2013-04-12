/* ============================================================
 * FileServerModule.java
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
