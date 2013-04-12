/* ============================================================
 * TransactionInterceptor.java
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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.inject.Inject;

public class TransactionInterceptor implements MethodInterceptor {

	private static final Logger logger = LoggerFactory
			.getLogger(TransactionInterceptor.class);

	@Inject
	private EntityManager em;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		Object obj = null;

		EntityTransaction tx = em.getTransaction();
		try {
			boolean shouldCommit = true;
			if (tx != null && tx.isActive() == false) {
				tx.begin();
				logger.debug("Started Transaction");
			} else {
				shouldCommit = false;
			}

			obj = invocation.proceed();

			if (tx != null && tx.isActive() == true && shouldCommit) {

				tx.commit();
				logger.debug("Committed Transaction");

			}

		} catch (Exception e) {

			if (tx != null && tx.isActive() == true) {
				tx.rollback();
			}
			em.clear();
			logger.error("Rolled back Transaction: ", e);
			Throwables.propagate(Throwables.getRootCause(e));
		} finally {
			
		}

		return obj;
	}

}
