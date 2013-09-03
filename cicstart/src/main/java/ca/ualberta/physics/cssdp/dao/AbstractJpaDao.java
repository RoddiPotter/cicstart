/* ============================================================
 * AbstractJpaDao.java
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

import java.io.Serializable;

import javax.persistence.EntityManager;

import com.google.inject.Inject;

/**
 * Provides basic CRUD operations that use an EntityManager to do their work.
 * 
 * @param <T>
 */
public abstract class AbstractJpaDao<T extends Persistent> implements Dao<T> {

	@Inject
	protected EntityManager em;

	@Override
	@SuppressWarnings("unchecked")
	public void delete(T t) {
		T toDelete = (T) em.getReference(t.getClass(), t.getId());
		em.remove(toDelete);
	}

	@Override
	public T load(Class<T> clazz, Serializable uid) {
		return (T) em.find(clazz, uid);
	}

	@Override
	public void save(T t) {
		em.persist(t);
	}

	@Override
	public void update(T t) {
		em.merge(t);
	}

	@Override
	public void refresh(T t) {
		em.refresh(t);
	}
}
