/* ============================================================
 * VfsServerModule.java
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
