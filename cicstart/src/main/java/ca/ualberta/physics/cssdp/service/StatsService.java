package ca.ualberta.physics.cssdp.service;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.dao.ServiceStatsDao;
import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;

import com.google.inject.Inject;

/**
 * 
 * Provides facilities for finding and updating the ServiceStats of each service
 * 
 * @author rpotter
 * 
 */
public class StatsService {

	private static final Logger logger = LoggerFactory
			.getLogger(StatsService.class);

	@Inject
	private ServiceStatsDao dao;

	@Inject
	private EntityManager em;

	public ServiceResponse<ServiceStats> find(final ServiceName serviceName) {
		final ServiceResponse<ServiceStats> sr = new ServiceResponse<ServiceStats>();

		new ManualTransaction(sr, em) {

			@Override
			public void onError(Exception e, ServiceResponse<?> sr) {
				sr.error(e.getMessage());
				logger.error("transaction failed due to " + e.getMessage());
			}

			@Override
			public void doInTransaction() {
				logger.debug("about to find stats for service " + serviceName);
				ServiceStats stats = dao.find(serviceName);
				logger.debug("found stats: " + (stats != null ? stats.toString()
						: "null"));
				if (stats == null) {
					stats = new ServiceStats();
					stats.setInvocations(0);
					stats.setLastReset(DateTime.now());
					stats.setServiceName(serviceName);
					dao.save(stats);
					logger.debug("saved new stats: " + stats);
				}
				sr.setPayload(stats);
			}
		};
		return sr;

	}

	/*
	 * We synchronize here to avoid optimistic lock exceptions when many
	 * simultaneous requests come in. It might slow things down a bit... we'll
	 * wait and see how it does.
	 */
	public synchronized ServiceResponse<ServiceStats> incrementInvocationCount(
			final ServiceName serviceName) {

		final ServiceResponse<ServiceStats> sr = new ServiceResponse<ServiceStats>();

		new ManualTransaction(sr, em) {

			@Override
			public void onError(Exception e, ServiceResponse<?> srForError) {
				// we often see optimistic lock exceptions when many get
				// requests come in simultaneously
				sr.equals(e.getMessage());
				sr.setPayload(null);
			}

			@Override
			public void doInTransaction() {
				ServiceStats stats = dao.find(serviceName);
				if (stats == null) {
					ServiceStats newStats = new ServiceStats();
					newStats.setInvocations(1);
					newStats.setLastReset(DateTime.now());
					newStats.setServiceName(serviceName);
					dao.save(newStats);
					sr.setPayload(newStats);
				} else {
					stats.incrementInvocations();
					dao.update(stats);
					sr.setPayload(stats);
				}

			}
		};
		return sr;
	}

}
