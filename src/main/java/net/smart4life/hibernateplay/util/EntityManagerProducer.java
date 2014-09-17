package net.smart4life.hibernateplay.util;

import net.smart4life.hibernateplay.cdi.qualifier.NotRequestScoped;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerProducer implements Serializable {
	
//	@Inject
//	private Logger logger;

	@PersistenceUnit
	private EntityManagerFactory emf;

	public void closeEntityManager(@Disposes EntityManager entityManager) {
		if(entityManager != null && entityManager.isOpen()){
			try{
				entityManager.close();
//				logger.info("EntityManager geschlossen.");
			}catch(Exception e){
				//proxy handle is no longer valid
				if(e.getMessage() != null && e.getClass().getName().equals("org.hibernate.HibernateException")
						&& e.getMessage().equals("proxy handle is no longer valid")){
//					logger.warning(e.getMessage());
				} else {
					throw e;
				}

			}
		}
	}

	@Produces
	@RequestScoped
	public EntityManager createRequestScopedEntityManager(){
//		logger.info("Standard mmnet EntityManager erstellt.");
		System.out.println("!!!!!!!!!!!!!! createRequestScopedEntityManager()");
		return emf.createEntityManager();
	}

	@Produces
	@NotRequestScoped
	public EntityManager createDependentEntityManager(){
//		logger.info("Standard mmnet EntityManager erstellt.");
		System.out.println("!!!!!!!!!!!!!! createDependentEntityManager() ");
		return emf.createEntityManager();
	}

}
