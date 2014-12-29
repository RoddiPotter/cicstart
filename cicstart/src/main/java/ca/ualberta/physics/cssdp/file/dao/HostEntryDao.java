/* ============================================================
 * HostEntryDao.java
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

		Query query = emp.get().createQuery(qlString);
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
		List<Host> list = emp.get().createQuery(
				"select h from Host h order by h.hostname").getResultList();
		return list;
	}

}
