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

import com.google.inject.Inject;

/**
 * Provides basic CRUD operations that use an EntityManager to do their work.
 * 
 * @param <T>
 */
public abstract class AbstractJpaDao<T extends Persistent> implements Dao<T> {

	@Inject
	protected EntityManagerProvider emp;

	@Override
	@SuppressWarnings("unchecked")
	public void delete(T t) {
		T toDelete = (T) emp.get().getReference(t.getClass(), t.getId());
		emp.get().remove(toDelete);
	}

	@Override
	public T load(Class<T> clazz, Serializable uid) {
		return (T) emp.get().find(clazz, uid);
	}

	@Override
	public void save(T t) {
		emp.get().persist(t);
	}

	@Override
	public void update(T t) {
		emp.get().merge(t);
	}

	@Override
	public void refresh(T t) {
		emp.get().refresh(t);
	}
}
