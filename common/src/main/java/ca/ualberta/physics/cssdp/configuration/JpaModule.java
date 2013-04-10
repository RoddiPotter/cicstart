package ca.ualberta.physics.cssdp.configuration;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Configures the Hibernate JPA provider for database ORM mapping.
 */
public class JpaModule extends AbstractModule {

	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	public EntityManagerFactory provideEntityManagerFactory() {

		Map<String, Object> hibernateProps = Common.properties().getMap(
				"hibernate");
	
		// persistence.xml is in /common/src/main/resources/META-INF
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				"manager1", hibernateProps);

		
		
		return emf;
	}

}
