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
