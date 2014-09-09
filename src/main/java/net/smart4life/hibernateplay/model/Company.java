package net.smart4life.hibernateplay.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ILIN02 on 08.09.2014.
 */
@Entity
public class Company {

	@Column
	private String name;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "company")
	private List<Person> persons = new ArrayList<>();

	@Id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	public void addPerson(Person person){
		person.setCompany(this);
		persons.add(person);
	}
}
