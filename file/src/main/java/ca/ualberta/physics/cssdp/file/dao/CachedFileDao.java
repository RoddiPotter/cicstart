package ca.ualberta.physics.cssdp.file.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.common.collect.Sets;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.domain.file.CachedFile;

public class CachedFileDao extends AbstractJpaDao<CachedFile> {

	public CachedFile find(String externalKey) {

		String qlString = "select cf from CachedFile cf, in(cf.externalKeys) extkey where extkey = :externalKey";

		Query q = em.createQuery(qlString);
		q.setParameter("externalKey", Sets.newHashSet(externalKey));

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

		Query q = em.createQuery(qlString);
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
