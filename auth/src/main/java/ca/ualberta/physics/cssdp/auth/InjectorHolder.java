package ca.ualberta.physics.cssdp.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectorHolder {

	private static Injector injector = Guice.createInjector(new AuthServerModule());

	public static void inject(Object instance) {
		injector.injectMembers(instance);
	}

}
