package net.smart4life.hibernateplay.cdi.transaction;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.*;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


public abstract class TransactionalInterceptor implements Serializable {

	@Inject
	private Logger logger;
	
//	@Resource(lookup="java:jboss/UserTransaction")
	private UserTransaction userTransaction;

	@AroundInvoke
	public Object applyTransaction(InvocationContext context) throws Exception {

		Method method = context.getMethod();
		String params = formatMethodParams(context.getParameters());
		logger.info("start aroundInvoke of " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(" + params + ")");

		long startTime = System.currentTimeMillis();
		boolean transactionStarted = beginTransactionIfNeeded(context);
		try {
			Object result = context.proceed();
			return result;
		} catch (Exception e) {
			rollbackTransactionIfNeeded(transactionStarted, e);
			throw e;
		} finally {
			handleTransactionEnd(transactionStarted);

			// log critical transaction times if any
			long executionTime = System.currentTimeMillis() - startTime;
			boolean isWebCall = Thread.currentThread().getName().startsWith("http");
			long criticalTime = 10;
			
			Transactional transactional = context.getMethod().getAnnotation(Transactional.class);
			if (transactional != null && transactional.timeout() > 0) {
				criticalTime = transactional.timeout();
			}
			
			criticalTime *= 1000;
			if(executionTime > criticalTime){
				Method method1 = context.getMethod();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
				String executionTimeFormated = sdf.format(new Date(executionTime));
				String criticalTimeFormated = sdf.format(new Date(criticalTime));
				StringBuilder msg = new StringBuilder();
				msg.append("Kritische Transaktionszeit groesser "+criticalTimeFormated+" ");
				msg.append("Ausgefuehrt in "+executionTimeFormated+". ");
				String params1 = formatMethodParams(context.getParameters());
				msg.append("In "+ method1.getDeclaringClass().getSimpleName() + "." + method1.getName() + "("+params1+") ");
				logger.warning(msg.toString());
			}

			Method method2 = context.getMethod();
			logger.info("end aroundInvoke of " + method2.getDeclaringClass().getSimpleName() + "." + method2.getName() + "(...)");

		}
	}
	
	private String formatMethodParams(Object[] paramArr){
		StringBuilder result = new StringBuilder("");
		if(paramArr != null && paramArr.length > 0){
			for(Object param : paramArr){
				if(param != null && param instanceof String){
					result.append("'"+param+"'");
				} else {
					result.append(""+param);
				}
				result.append(", ");
			}
			result.setLength(result.length() - 2);
		}
		
		return result.toString();
	}

	protected boolean beginTransactionIfNeeded(InvocationContext context) 
			throws TransactionRequiredException, NotSupportedException, SystemException, NamingException {
		if (!isTransactionActive()) {
			beginTransaction(context);
			return true;
		}
		return false;
	}

	protected boolean isTransactionActive() throws SystemException, NamingException {
		return getUserTransaction().getStatus() == Status.STATUS_ACTIVE;
	}

	protected boolean isTransactionMarkedRollback() throws SystemException, NamingException {
		return getUserTransaction().getStatus() == Status.STATUS_MARKED_ROLLBACK;
	}

	protected abstract void beginTransaction(InvocationContext context) throws NotSupportedException, SystemException, NamingException;

	protected void rollbackTransactionIfNeeded(boolean responsible, Exception exception) throws SystemException, IllegalStateException, NamingException {
		if (isTransactionActive()) {
			getUserTransaction().setRollbackOnly();
		}
	}

	protected void handleTransactionEnd(boolean responsible) 
	throws SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, SecurityException, NamingException {
		if (responsible) {
			if (isTransactionMarkedRollback()) {
				rollbackTransaction();
			} else {
				commitTransaction();
			}
		}
	}

	protected void commitTransaction() 
	throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException, SecurityException, IllegalStateException, NamingException {
		getUserTransaction().commit();
		logger.info("userTransaction.commit()");
	}

	protected void rollbackTransaction() throws SystemException, IllegalStateException, SecurityException, NamingException {
		getUserTransaction().rollback();
		logger.info("userTransaction.rollback()");
	}

	protected UserTransaction getUserTransaction() throws NamingException {
		if(userTransaction == null){
			userTransaction = lookupUserTransaction();
		}
		return userTransaction;
	}
	
	protected UserTransaction lookupUserTransaction() throws NamingException {
		Context context = new InitialContext();
		try {
			// that is standard JBoss lookup for UserTransaction which searches for normal EE threads
			return (UserTransaction) context.lookup("java:comp/UserTransaction");
		} catch (NameNotFoundException nnfe) {
			try {
				// if our thread is not normal EE thread, for ex. Quartz thread then lookup here
				UserTransaction ut = (UserTransaction) context.lookup("java:jboss/UserTransaction");
				return ut;
			} catch (Exception e) {
				throw nnfe;
			}
		}
	}
	
}
