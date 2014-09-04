package net.smart4life.hibernateplay.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import net.smart4life.hibernateplay.model.User;

@RequestScoped
@Named
public class UserController {
	
	@Inject
	private EntityManager em;
	
	private List<User> users;
	
	@PostConstruct
	private void init(){
		users = em.createQuery("select o from User o", User.class).getResultList();
	}

	public List<User> getUsers() {
		return users;
	}
	
	

}
