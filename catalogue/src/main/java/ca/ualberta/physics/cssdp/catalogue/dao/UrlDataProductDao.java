/* ============================================================
 * UrlDataProductDao.java
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

import java.net.URI;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.dao.AbstractJpaDao;
import ca.ualberta.physics.cssdp.dao.Dao;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.UrlDataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.UrlDataProductUpdateMap;

import com.google.common.collect.Lists;

public class UrlDataProductDao extends AbstractJpaDao<UrlDataProduct> implements
		Dao<UrlDataProduct> {

	private static final Logger logger = LoggerFactory
			.getLogger(UrlDataProductDao.class);

	public void process(UrlDataProductUpdateMap urlDataProductUpdateMap) {

		if (urlDataProductUpdateMap.getUrls().size() == 0) {
			return;
		}

		/*
		 * The size of scannedUrlDataProducts should be <= jdbc batch size
		 * configured.
		 */

		// we have to resort to hibernate directly because JPA does not have
		// scrolling capability
		Session session = em.unwrap(Session.class).getSessionFactory()
				.openSession();

		Transaction tx = session.beginTransaction();

		// "in" clause limit is 2^16 on Postgresql, it might be different on
		// other dbs
		String hqlString = "from UrlDataProduct urldp where urldp.url in (:urls)";

		// the fastest way to scroll through the existing data
		Query q = session.createQuery(hqlString);
		q.setParameterList("urls", urlDataProductUpdateMap.getUrls());
		q.setCacheMode(CacheMode.IGNORE);
		ScrollableResults existingData = q.scroll(ScrollMode.FORWARD_ONLY);

		while (existingData.next()) {

			UrlDataProduct existing = (UrlDataProduct) existingData.get(0);
			UrlDataProduct updated = urlDataProductUpdateMap.get(existing
					.getUrl());

			if (updated != null) {

				/*
				 * Only bother to update the record if it's actually changed.
				 * Note that the scan timestamp is ignored in the check because
				 * that isn't something the provider changed. A change can also
				 * mean the url was deleted, and now it's back.
				 */
				if (existing.hasChanged(updated)) {
					// existing.setDataProduct(updated.getDataProduct());
					existing.setUrl(updated.getUrl());
					existing.setStartTimestamp(updated.getStartTimestamp());
					existing.setEndTimestamp(updated.getEndTimestamp());
					existing.setScanTimestamp(updated.getScanTimestamp());
					existing.setDeleted(false);
					urlDataProductUpdateMap.remove(updated.getUrl());
					session.update(existing);
				}

			} else {

				// if we get here it means the existing url has been removed
				// from the server, set "delete" it from the catalogue
				existing.setDeleted(true);
				existing.setScanTimestamp(new LocalDateTime());

			}

		}

		// persist the new url mappings
		for (String newUrl : urlDataProductUpdateMap.getUrls()) {
			UrlDataProduct newUrlDataProduct = urlDataProductUpdateMap
					.get(newUrl);
			session.save(newUrlDataProduct);
			logger.debug("saved a mapping: " + newUrlDataProduct.getUrl());
		}

		session.flush();
		session.clear();

		tx.commit();
		session.close();

	}

	@SuppressWarnings("unchecked")
	public List<URI> findUrls(List<DataProduct> dataProducts,
			LocalDateTime start, LocalDateTime end) {

		List<URI> result;

		if (dataProducts != null && dataProducts.size() > 0) {

			String qlString = "select new java.net.URI(ud.url) from UrlDataProduct ud where ud.dataProduct in (:dataProducts) "
					+ (start != null ? " and ud.startTimestamp <= :end " : "")
					+ (end != null ? " and ud.endTimestamp >= :start" : "");

			javax.persistence.Query q = em.createQuery(qlString);
			q.setParameter("dataProducts", dataProducts);
			if (start != null) {
				q.setParameter("start", start);
			}
			if (end != null) {
				q.setParameter("end", end);
			}

			result = q.getResultList();

		} else {
			result = Lists.newArrayList();
		}

		return result;
	}

}
