package ca.ualberta.physics.cssdp.file.service;

import javax.persistence.EntityManager;

import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.file.InjectorHolder;
import ca.ualberta.physics.cssdp.file.dao.HostEntryDao;
import ca.ualberta.physics.cssdp.service.ManualTransaction;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.inject.Inject;

public class HostService {

	@Inject
	private HostEntryDao hostEntryDao;

	@Inject
	private EntityManager em;

	public HostService() {
		InjectorHolder.inject(this);
	}

	public ServiceResponse<Host> getHostEntry(String hostname) {
		return new ServiceResponse<Host>(hostEntryDao.find(hostname));
	}

	public ServiceResponse<Host> deleteHostEntry(final String hostname) {

		final ServiceResponse<Host> sr = new ServiceResponse<Host>();
		new ManualTransaction(sr, em) {

			@Override
			public void onError(Exception e, ServiceResponse<?> sr) {
				sr.equals(e.getMessage());
				sr.setPayload(null);
			}

			@Override
			public void doInTransaction() {
				Host host = hostEntryDao.find(hostname);
				if (host != null) {
					hostEntryDao.delete(host);
					sr.setPayload(host);
				} else {
					sr.error("host " + hostname + " does not exit");
				}
			}
		};
		return sr;

	}

	public ServiceResponse<Void> addHost(final Host hostEntry) {

		final ServiceResponse<Void> sr = new ServiceResponse<Void>();
		new ManualTransaction(sr, em) {

			@Override
			public void onError(Exception e, ServiceResponse<?> sr) {
				sr.equals(e.getMessage());
			}

			@Override
			public void doInTransaction() {
				if (!hostEntryDao.exists(hostEntry.getHostname())) {
					hostEntryDao.save(hostEntry);
				} else {
					sr.error("host " + hostEntry.getHostname()
							+ " already exits");
				}
			}
		};
		return sr;
	}

}
