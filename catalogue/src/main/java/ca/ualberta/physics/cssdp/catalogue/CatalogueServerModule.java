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
