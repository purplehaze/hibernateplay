package net.smart4life.hibernateplay.controller;

import net.smart4life.hibernateplay.model.OneToOneTwoSideRefA;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.List;

@RequestScoped
@Named
public class OneToOneTwoSideRefController {
	
	@Inject
	private EntityManager em;
	
	private List<OneToOneTwoSideRefA> data;
	
	@PostConstruct
	public void init(){
		data = em.createQuery("select o from OneToOneTwoSideRefA o", OneToOneTwoSideRefA.class).getResultList();
	}

	public List<OneToOneTwoSideRefA> getData() {
		return data;
	}
	
	

}
