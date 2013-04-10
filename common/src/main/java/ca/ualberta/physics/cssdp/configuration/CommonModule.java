package ca.ualberta.physics.cssdp.configuration;

import javax.persistence.EntityManager;

import ca.ualberta.physics.cssdp.dao.EntityManagerProvider;
import ca.ualberta.physics.cssdp.dao.EntityManagerStore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CommonModule extends AbstractModule {

	@Override
	protected void configure() {

		install(new Slf4jLoggerInitializer());
		install(new JpaModule());
		install(new TransactionalModule());

		// The EntityManagerProvider is also the EntityManagerStore ...
		bind(EntityManagerStore.class).to(EntityManagerProvider.class).in(
				Scopes.SINGLETON);
		bind(EntityManager.class).toProvider(EntityManagerProvider.class).in(
				Scopes.SINGLETON);

		bind(ObjectMapper.class).toProvider(new JSONObjectMapperProvider());

	}

}
