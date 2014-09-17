package net.smart4life.hibernateplay.controller;

import net.smart4life.hibernateplay.model.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.List;

@RequestScoped
@Named
public class UserController {
	
	@Inject
	private EntityManager em;
	
	private List<User> users;
	
	@PostConstruct
	public void init(){
		users = em.createQuery("select o from User o", User.class).getResultList();
	}

	public List<User> getUsers() {
		return users;
	}
	
	

}
