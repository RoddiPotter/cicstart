package ca.ualberta.physics.cssdp.dao;

import javax.persistence.EntityManager;

/**
 * A simple interface which allows us to gain access to the EntityManager (via a
 * ThreadLocal) and remove it from that ThreadLocal when the unit of work is
 * done. Typcially, a call to get() will be made at the start of an http request
 * and a call to remove() at the end of an HTTP request.
 */
public interface EntityManagerStore {

	public void remove();

	public EntityManager get();

}
