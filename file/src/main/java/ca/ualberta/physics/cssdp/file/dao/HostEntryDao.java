package ca.ualberta.physics.cssdp.file.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.domain.file.Host;

public class HostEntryDao extends AbstractJpaDao<Host> {

	public boolean exists(String hostname) {
		return find(hostname) != null;
	}

	public Host find(String hostname) {
		String qlString = "select h from Host h where h.hostname = :hostname";

		Query query = em.createQuery(qlString);
		query.setParameter("hostname", hostname);
		Host he = null;
		try {
			he = (Host) query.getSingleResult();
		} catch (NoResultException e) {
			// ignore
		}

		return he;
	}

	@SuppressWarnings("unchecked")
	public List<Host> list() {
		List<Host> list = em.createQuery(
				"select h from Host h order by h.hostname").getResultList();
		return list;
	}

}
