package net.smart4life.hibernateplay.cdi.transaction;

import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.Serializable;

@Transactional
@Interceptor
public class XaTransactionalInterceptor extends TransactionalInterceptor implements Serializable {

	@Inject
	private EntityManager entityManager;

	protected void beginTransaction(InvocationContext context) throws NotSupportedException, SystemException, NamingException {
		Transactional transactional = context.getMethod().getAnnotation(Transactional.class);
		if (transactional == null) {
			transactional = context.getTarget().getClass().getAnnotation(Transactional.class);
			if (transactional == null) {
				throw new IllegalStateException("No @Transactional annotation found");
			}
		}
		if (transactional.timeout() > 0) {
			getUserTransaction().setTransactionTimeout(transactional.timeout());
		}
		getUserTransaction().begin();
		entityManager.joinTransaction();
	}

}
