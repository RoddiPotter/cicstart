package ca.ualberta.physics.cssdp.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;

public class ServiceStatsDao extends AbstractJpaDao<ServiceStats> {

	public ServiceStats find(ServiceName serviceName) {
		
		String qlString = "select ss from ServiceStats ss where ss.serviceName = :serviceName";
		Query q = em.createQuery(qlString);
		q.setParameter("serviceName", serviceName);

		ServiceStats stats = null;

		try {
			stats = (ServiceStats) q.getSingleResult();
		} catch (NoResultException nre) {
			// ignored
		}

		return stats;

	}
		
}
