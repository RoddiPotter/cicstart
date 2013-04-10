package ca.ualberta.physics.cssdp.catalogue;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectorHolder {

	private static Injector injector = Guice.createInjector(new CatalogueServerModule());

	public static void inject(Object instance) {
		injector.injectMembers(instance);
	}

	public static Injector get() {
		return injector;
	}

}
