package ca.ualberta.physics.cssdp.auth;

import ca.ualberta.physics.cssdp.auth.service.UserService;
import ca.ualberta.physics.cssdp.configuration.CommonModule;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configured various infrastructure items needed for the application to work
 */
public class AuthServerModule extends AbstractModule {

	@Override
	protected void configure() {

		install(new CommonModule());
		
		bind(UserService.class).in(Scopes.SINGLETON);
				
	}

}
