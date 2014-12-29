package ca.ualberta.physics.cssdp.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.service.StatsService;

public class ServiceStatsDao extends AbstractJpaDao<ServiceStats> {

	private static final Logger logger = LoggerFactory
			.getLogger(ServiceStatsDao.class);

	public ServiceStats find(StatsService.ServiceName serviceName) {

		logger.debug("Finding a stats record for service " + serviceName);

		String qlString = "select ss from ServiceStats ss where ss.serviceName = :serviceName";
		Query q = emp.get().createQuery(qlString);
		q.setParameter("serviceName", serviceName);

		ServiceStats stats = null;

		try {
			stats = (ServiceStats) q.getSingleResult();
			logger.debug("Found a stats record " + stats.getId());
		} catch (NoResultException nre) {
			// ignored
		}

		return stats;

	}

}
