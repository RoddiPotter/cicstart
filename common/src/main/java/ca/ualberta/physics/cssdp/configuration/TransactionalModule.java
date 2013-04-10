package ca.ualberta.physics.cssdp.configuration;

import ca.ualberta.physics.cssdp.service.TransactionInterceptor;
import ca.ualberta.physics.cssdp.service.Transactional;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class TransactionalModule extends AbstractModule {

	@Override
	protected void configure() {
		TransactionInterceptor transactional = new TransactionInterceptor();
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class),
				transactional);
		requestInjection(transactional);
	}

}
