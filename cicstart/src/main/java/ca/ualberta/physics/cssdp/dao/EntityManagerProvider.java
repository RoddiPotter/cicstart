/* ============================================================
 * EntityManagerProvider.java
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
package ca.ualberta.physics.cssdp.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class EntityManagerProvider implements EntityManagerStore,
		Provider<EntityManager> {

	private static final Logger log = LoggerFactory.getLogger(EntityManagerProvider.class);
	
	private static final ThreadLocal<EntityManager> ems = new ThreadLocal<EntityManager>();

	private EntityManagerFactory emf;

	@Inject
	public EntityManagerProvider(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Override
	public EntityManager get() {

		EntityManager em = null;
		em = ems.get();
		if (em == null) {
			em = emf.createEntityManager();
			/*
			 * this ensures that changes to attached objects are not persisted
			 * to the database unless a commit has been issued. The default of
			 * "auto" will commit changes to persistent objects sometimes
			 * without a commit. This can happen when there is a web form that
			 * issues an ajax request -- if the object has changes, the closing
			 * of the EntityManager will flush those changes to the database.
			 * Setting this to FlushModeType.COMMIT will stop this behaviour
			 * from happening.
			 */
			em.setFlushMode(FlushModeType.COMMIT);
			ems.set(em);
		}
		log.info("returning EntityManager: " + em);
		return em;
	}

	@Override
	public void remove() {
		EntityManager em = ems.get();
		if (em.isOpen()) {
			try {
				em.close();
			} catch (Exception ignore) {
				// interceptor may have already close me.
			}
		}
		ems.remove();
	}
}
