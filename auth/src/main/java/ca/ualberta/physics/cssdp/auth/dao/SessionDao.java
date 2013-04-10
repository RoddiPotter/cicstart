package ca.ualberta.physics.cssdp.auth.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.domain.auth.Session;

public class SessionDao extends AbstractJpaDao<Session> {

	public Session find(String token) {

		String qlString = "select s from Session s where s.token = :token";
		Query q = em.createQuery(qlString);
		q.setParameter("token", token);

		Session session = null;

		try {
			session = (Session) q.getSingleResult();
		} catch (NoResultException nre) {
			// ignored
		}

		return session;
	}

	
}
