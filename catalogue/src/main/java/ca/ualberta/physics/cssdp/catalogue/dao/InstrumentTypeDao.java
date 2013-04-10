package ca.ualberta.physics.cssdp.catalogue.dao;

import java.util.List;

import javax.persistence.Query;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.dao.Dao;
import ca.ualberta.physics.cssdp.domain.catalogue.InstrumentType;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.model.Mnemonic;

public class InstrumentTypeDao extends AbstractJpaDao<InstrumentType> implements
		Dao<InstrumentType> {

	@SuppressWarnings("unchecked")
	public List<InstrumentType> find(Project project, List<Mnemonic> externalKeys) {

		StringBuffer qlString = new StringBuffer(
				"select it from InstrumentType it where it.project = :project");
		if (externalKeys != null && !externalKeys.isEmpty()) {
			qlString.append(" and it.externalKey in (:externalKeys)");
		}

		Query q = em.createQuery(qlString.toString());
		q.setParameter("project", project);
		if (externalKeys != null && !externalKeys.isEmpty()) {
			q.setParameter("externalKeys", externalKeys);
		}

		List<InstrumentType> result = q.getResultList();

		return result;
	}

}
