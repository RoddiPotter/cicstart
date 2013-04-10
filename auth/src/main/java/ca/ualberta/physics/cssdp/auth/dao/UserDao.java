package ca.ualberta.physics.cssdp.auth.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.domain.auth.User;

public class UserDao extends AbstractJpaDao<User> {

	public User find(String email) {

		String qlString = "select u from User u where u.email = :email";
		Query q = em.createQuery(qlString);
		q.setParameter("email", email);

		User user = null;

		try {
			user = (User) q.getSingleResult();
		} catch (NoResultException nre) {
			// ignored
		}

		return user;
	}

	
}
