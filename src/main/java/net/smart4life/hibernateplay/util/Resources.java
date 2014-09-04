/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smart4life.hibernateplay.util;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 * 
 * <p>
 * Example injection on a managed bean field:
 * </p>
 * 
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
public class Resources {
    // use @SuppressWarnings to tell IDE to ignore warnings about field not being referenced directly
    @SuppressWarnings("unused")
    @Produces
    @PersistenceContext
    private EntityManager em;

//	public void closeEntityManagerFactory(@Disposes @Any EntityManagerFactory entityManagerFactory) {
//		entityManagerFactory.close();
//	}
//
//	public void closeEntityManager(@Disposes @Any EntityManager entityManager) {
//		if(entityManager != null && entityManager.isOpen()){
//			try{
//				entityManager.close();
//			}catch(Exception e){
//				//proxy handle is no longer valid
//				if(e.getMessage() != null && e.getClass().getName().equals("org.hibernate.HibernateException")
//						&& e.getMessage().equals("proxy handle is no longer valid")){
//					e.printStackTrace();
//				} else {
//					throw e;
//				}
//
//			}
//		}
//	}
//
//	@Produces
//	@ApplicationScoped
//	public EntityManagerFactory createMMnetEntityManagerFactory(){
//		return Persistence.createEntityManagerFactory("primary");
//	}
//
//	@Produces
//	@RequestScoped
//	public EntityManager createMMnetEntityManager(EntityManagerFactory entityManagerFactory){
//		return entityManagerFactory.createEntityManager();
//	}



    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Produces
    @RequestScoped
    public FacesContext produceFacesContext() {
        return FacesContext.getCurrentInstance();
    }

}
