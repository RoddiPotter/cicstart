package ca.ualberta.physics.cssdp.catalogue.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.dao.Dao;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.model.Mnemonic;

public class ProjectDao extends AbstractJpaDao<Project> implements Dao<Project> {

	@SuppressWarnings("unchecked")
	public List<Project> findAll() {

		String qlString = "select p from Project p order by p.externalKey";
		Query q = em.createQuery(qlString);
		List<Project> projects = q.getResultList();

		return projects;
	}

	public Project find(Mnemonic externalKey) {

		String qlString = "select p from Project p where p.externalKey = :externalKey";
		Query q = em.createQuery(qlString);
		q.setParameter("externalKey", externalKey);

		Project project = null;
		try {
			project = (Project) q.getSingleResult();
		} catch (NoResultException nre) {
			// ignore
		}

		return project;
	}

}
