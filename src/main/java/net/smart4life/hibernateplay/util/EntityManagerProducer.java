package net.smart4life.hibernateplay.util;

import net.smart4life.hibernateplay.cdi.qualifier.NotRequestScoped;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.Serializable;

public class EntityManagerProducer implements Serializable {
	
	@Inject
	private Logger logger;

	@PersistenceUnit
	private EntityManagerFactory emf;

	public void closeEntityManager(@Disposes EntityManager entityManager) {
		if(entityManager != null && entityManager.isOpen()){
			try{
				entityManager.close();
				logger.info("EntityManager geschlossen.");
			}catch(Exception e){
				//proxy handle is no longer valid
				if(e.getMessage() != null && e.getClass().getName().equals("org.hibernate.HibernateException")
						&& e.getMessage().equals("proxy handle is no longer valid")){
					logger.warn(e.getMessage());
				} else {
					throw e;
				}

			}
		}
	}

	@Produces
	@RequestScoped
	public EntityManager createRequestScopedEntityManager(){
		logger.info("RequestScoped EntityManager erstellt.");
		return emf.createEntityManager();
	}

	@Produces
	@NotRequestScoped
	public EntityManager createDependentEntityManager(){
		logger.info("DependentScoped EntityManager erstellt.");
		return emf.createEntityManager();
	}

}
