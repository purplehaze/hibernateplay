package net.smart4life.hibernateplay.controller;

import net.smart4life.hibernateplay.model.OneToOneOneSideRefA;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.List;

@RequestScoped
@Named
public class OneToOneOneSideRefController {
	
	@Inject
	private EntityManager em;
	
	private List<OneToOneOneSideRefA> data;
	
	@PostConstruct
	private void init(){
		data = em.createQuery("select o from OneToOneOneSideRefA o", OneToOneOneSideRefA.class).getResultList();
	}

	public List<OneToOneOneSideRefA> getData() {
		return data;
	}
	
	

}
