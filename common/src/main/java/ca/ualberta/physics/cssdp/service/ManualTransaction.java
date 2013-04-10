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
				tx.commit();
				logger.debug("Committed Transaction");
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
