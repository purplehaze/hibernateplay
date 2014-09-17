package net.smart4life.hibernateplay.controller;

import net.smart4life.hibernateplay.model.Company;
import net.smart4life.hibernateplay.model.Person;
import org.slf4j.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.List;

@RequestScoped
@Named
@Stateful
public class PersonCompanyController {
	@Inject
	private Logger logger;
	
	@Inject
	private EntityManager em;


	public void loadPersonById(){
		Person p = em.find(Person.class, 1L);
		logger.info(p.toString());
	}

	public void loadPersons(){
		List<Person> persons = em.createQuery("select p from Person p", Person.class).getResultList();
		logger.info("loaded "+persons.size()+" persons");
	}

	public void loadCompanyById(){
		Company c = em.find(Company.class, 1L);
		logger.info(c.toString());
	}

	public void loadCompanies(){
		List<Company> companies = em.createQuery("select c from Company c left join c.persons p", Company.class).getResultList();
//		logger.info("loaded "+companies.size()+" companies");
	}
	
	

}
