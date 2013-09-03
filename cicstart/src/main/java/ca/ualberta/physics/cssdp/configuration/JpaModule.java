/* ============================================================
 * JpaModule.java
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
