/* ============================================================
 * CatalogueServiceModule.java
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

package ca.ualberta.physics.cssdp.catalogue;

import ca.ualberta.physics.cssdp.catalogue.dao.DataProductDao;
import ca.ualberta.physics.cssdp.catalogue.dao.InstrumentTypeDao;
import ca.ualberta.physics.cssdp.catalogue.dao.ObservatoryDao;
import ca.ualberta.physics.cssdp.catalogue.dao.ObservatoryGroupDao;
import ca.ualberta.physics.cssdp.catalogue.dao.ProjectDao;
import ca.ualberta.physics.cssdp.catalogue.dao.UrlDataProductDao;
import ca.ualberta.physics.cssdp.catalogue.service.CatalogueService;
import ca.ualberta.physics.cssdp.configuration.CommonModule;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configured various infrastructure items needed for the application to work
 */
public class CatalogueServerModule extends AbstractModule {

	@Override
	protected void configure() {

		install(new CommonModule());

		bind(DataProductDao.class).in(Scopes.SINGLETON);
		bind(InstrumentTypeDao.class).in(Scopes.SINGLETON);
		bind(ObservatoryDao.class).in(Scopes.SINGLETON);
		bind(ObservatoryGroupDao.class).in(Scopes.SINGLETON);
		bind(ProjectDao.class).in(Scopes.SINGLETON);
		bind(UrlDataProductDao.class).in(Scopes.SINGLETON);

		bind(CatalogueService.class).in(Scopes.SINGLETON);

	}

}
