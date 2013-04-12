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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class EntityManagerProvider implements EntityManagerStore,
		Provider<EntityManager> {

	private Map<Thread, EntityManager> ems = new HashMap<Thread, EntityManager>();

	private EntityManagerFactory emf;

	@Inject
	public EntityManagerProvider(EntityManagerFactory emf) {
		this.emf = emf;
	}

	/*
	 * This is synchronized because we don't want to overwrite an existing map
	 * entry with a new EntityManager if one already exists from different call.
	 * 
	 * @see ca.ualberta.eas.environet.configuration.EntityManagerStore#get()
	 */
	@Override
	public synchronized EntityManager get() {
		Thread currentThread = Thread.currentThread();
		EntityManager em = ems.get(currentThread);
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
			ems.put(currentThread, em);
		}
		return em;
	}

	/*
	 * This is synchronized because we modify the internal hashmap with this
	 * call. We only ever want one thread to modify the map at a time.
	 * 
	 * @see ca.ualberta.eas.environet.configuration.EntityManagerStore#remove()
	 */
	@Override
	public synchronized void remove() {
		Thread currentThread = Thread.currentThread();
		EntityManager em = ems.remove(currentThread);
		if (em.getTransaction().isActive()) {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}
