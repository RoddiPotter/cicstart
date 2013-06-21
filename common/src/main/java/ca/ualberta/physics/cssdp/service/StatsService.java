package ca.ualberta.physics.cssdp.service;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

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
			}

			@Override
			public void doInTransaction() {
				ServiceStats stats = dao.find(serviceName);
				if (stats == null) {
					stats = new ServiceStats();
					stats.setInvocations(0);
					stats.setLastReset(DateTime.now());
					stats.setServiceName(serviceName);
					dao.save(stats);
				}
				sr.setPayload(stats);
			}
		};
		return sr;

	}

	public ServiceResponse<ServiceStats> incrementInvocationCount(
			final ServiceName serviceName) {

		final ServiceResponse<ServiceStats> sr = new ServiceResponse<ServiceStats>();

		new ManualTransaction(sr, em) {

			@Override
			public void onError(Exception e, ServiceResponse<?> sr) {
				sr.error(e.getMessage());
			}

			@Override
			public void doInTransaction() {
				ServiceStats stats = dao.find(serviceName);
				if (stats == null) {
					stats = new ServiceStats();
					stats.setInvocations(1);
					stats.setLastReset(DateTime.now());
					stats.setServiceName(serviceName);
					dao.save(stats);
				} else {
					stats.incrementInvocations();
					dao.update(stats);
				}
				sr.setPayload(stats);
			}
		};
		return sr;
	}

}
