package ca.ualberta.physics.cssdp.dao;

import java.io.Serializable;

/**
 * Defines some basic database operations that all concrete Daos will implement.
 * 
 * @param <T>
 */
public interface Dao<T> {

	public void save(T t);

	public T load(Class<T> clazz, Serializable uid);

	public void delete(T t);

	public void update(T t);

	public void refresh(T t);

}
