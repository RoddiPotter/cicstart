/* ============================================================
 * CachedFileDao.java
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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.domain.file.CachedFile;

public class CachedFileDao extends AbstractJpaDao<CachedFile> {

	public CachedFile find(String externalKey) {

		String qlString = "select cf from CachedFile cf, in(cf.externalKeys) extkey where extkey = :externalKey";

		Query q = emp.get().createQuery(qlString);
		q.setParameter("externalKey", externalKey);

		CachedFile cf = null;
		try {
			cf = (CachedFile) q.getSingleResult();
		} catch (NoResultException e) {
			// ignored.
		}

		return cf;
	}

	public CachedFile get(String md5) {

		String qlString = "select cf from CachedFile cf where cf.md5 = :md5";

		Query q = emp.get().createQuery(qlString);
		q.setParameter("md5", md5);

		CachedFile cf = null;
		try {
			cf = (CachedFile) q.getSingleResult();
		} catch (NoResultException e) {
			// ignored.
		}

		return cf;
	}

}
