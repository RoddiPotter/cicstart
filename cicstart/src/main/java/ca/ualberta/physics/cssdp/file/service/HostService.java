/* ============================================================
 * HostService.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.file.service;

import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.dao.EntityManagerProvider;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.file.dao.HostEntryDao;
import ca.ualberta.physics.cssdp.service.ManualTransaction;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.inject.Inject;

public class HostService {

	@Inject
	private HostEntryDao hostEntryDao;

	@Inject
	private EntityManagerProvider emp;

	public HostService() {
		InjectorHolder.inject(this);
	}

	public ServiceResponse<Host> getHostEntry(String hostname) {
		return new ServiceResponse<Host>(hostEntryDao.find(hostname));
	}

	public ServiceResponse<Host> deleteHostEntry(final String hostname) {

		final ServiceResponse<Host> sr = new ServiceResponse<Host>();
		new ManualTransaction(sr, emp.get()) {

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
		new ManualTransaction(sr, emp.get()) {

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
