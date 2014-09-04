package net.smart4life.hibernateplay.controller;

import net.smart4life.hibernateplay.model.OneToOneManyToOneA;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.ServletRequest;

@RequestScoped
@Named
@Stateful
public class OneToOneManyToOneDetailsController {
	
	@Inject
	private EntityManager em;
	
	private OneToOneManyToOneA data;
	
	@PostConstruct
	private void init(){
		ServletRequest request = (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String paramVal = request.getParameter("id");
		if(paramVal != null && !paramVal.isEmpty()){
			Long id = Long.parseLong(paramVal);
			data = em.find(OneToOneManyToOneA.class, id);
			System.out.println(data.getB().toString());
//			data = em.createQuery("select a from OneToOneManyToOneA a left join fetch a.bees bees where a.id = :id", OneToOneManyToOneA.class)
//					.setParameter("id", id)
//					.getSingleResult();
		}
	}

	public OneToOneManyToOneA getData() {
		return data;
	}
	
	

}
