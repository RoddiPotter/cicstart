/* ============================================================
 * MacroServerModule.java
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
package ca.ualberta.physics.cicstart.macro;

import ca.ualberta.physics.cicstart.macro.service.CloudService;
import ca.ualberta.physics.cicstart.macro.service.MacroService;
import ca.ualberta.physics.cssdp.configuration.JSONObjectMapperProvider;
import ca.ualberta.physics.cssdp.configuration.Slf4jLoggerInitializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configured various infrastructure items needed for the application to work
 */
public class MacroServerModule extends AbstractModule {

	@Override
	protected void configure() {

		// install(new CommonModule());
		// macro doesn't use any JPA stuff and we especially don't want to load
		// that up on the binary client

		install(new Slf4jLoggerInitializer());
		bind(ObjectMapper.class).toProvider(new JSONObjectMapperProvider());

		bind(MacroService.class).in(Scopes.SINGLETON);
		bind(CloudService.class).in(Scopes.SINGLETON);
	}

}
