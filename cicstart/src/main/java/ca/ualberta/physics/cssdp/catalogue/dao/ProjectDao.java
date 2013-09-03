/* ============================================================
 * ProjectDao.java
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
