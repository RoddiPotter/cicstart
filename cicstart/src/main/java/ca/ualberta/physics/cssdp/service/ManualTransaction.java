/* ============================================================
 * ManualTransaction.java
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
package ca.ualberta.physics.cssdp.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ManualTransaction {

	private static final Logger logger = LoggerFactory
			.getLogger(ManualTransaction.class);

	public ManualTransaction(ServiceResponse<?> sr, EntityManager em) {

		EntityTransaction tx = em.getTransaction();
		if (tx == null || tx.isActive() == false) {
			tx.begin();
			logger.debug("Started Transaction");
		}
		try {

			doInTransaction();
			// only commit the transaction if the request is ok still.
			// Validation errors from the concrete class may invalidate the
			// transaction.
			if (tx != null && tx.isActive() == true) {
				if (sr.isRequestOk()) {
					tx.commit();
					logger.debug("Committed Transaction");
				} else {
					tx.rollback();
					logger.debug("Rolled back transaction due to business error");
				}
			}
		} catch (Exception e) {
			sr.setOk(false);

			if (tx != null && tx.isActive() == true) {
				tx.rollback();
			}
			em.clear();
			onError(e, sr);
			logger.warn("Rolled back Transaction: ", e);
		} finally {

		}

	}

	abstract public void doInTransaction();

	abstract public void onError(Exception e, ServiceResponse<?> sr);

}
