/* ============================================================
 * DataProductDao.java
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

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.dao.Dao;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Discriminator;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.model.Mnemonic;

public class DataProductDao extends AbstractJpaDao<DataProduct> implements
		Dao<DataProduct> {

	@SuppressWarnings("unchecked")
	public List<DataProduct> find(Project project,
			Collection<Mnemonic> observatories,
			Collection<Mnemonic> instrumentTypes, Discriminator discriminator) {

		StringBuffer qlString = new StringBuffer("select dp from DataProduct dp ");
		
		if(observatories != null && !observatories.isEmpty()) {
			qlString.append(" left outer join dp.observatories o ");
		}
		if(instrumentTypes != null && !instrumentTypes.isEmpty()) {
			qlString.append(" left outer join dp.instrumentTypes it ");
		}

		qlString.append(" where dp.project = :project ");
		
		if(observatories != null && !observatories.isEmpty()) {
			qlString.append(" and o.externalKey in (:observatories)");
		}
		if(instrumentTypes != null && !instrumentTypes.isEmpty()) {
			qlString.append(" and it.externalKey in (:instrumentTypes)");
		}		
		if(discriminator != null) {
			qlString.append(" and dp.discriminator = :discriminator");
		}
	
		Query q = emp.get().createQuery(qlString.toString());
		q.setParameter("project", project);
		if(observatories != null && !observatories.isEmpty()) {
			q.setParameter("observatories", observatories);
		}
		if(instrumentTypes != null && !instrumentTypes.isEmpty()) {
			q.setParameter("instrumentTypes", instrumentTypes);
		}
		if(discriminator != null) {
			q.setParameter("discriminator", discriminator);
		}

		List<DataProduct> result = q.getResultList();
		
		return result;
	}

}
