package net.smart4life.hibernateplay.cdi.transaction;


import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A replacement for {@link javax.ejb.TransactionAttribute}.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Transactional {
//	@Nonbinding Class<? extends Annotation>[] value() default {Mmnet.class};
	/**
	 * int timeOut in seconds
	 * -1 = Default-JBoss-Transaction-Timeouot
	 */
	@Nonbinding public int timeout() default -1;
}