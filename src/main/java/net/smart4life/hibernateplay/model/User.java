package net.smart4life.hibernateplay.model;

import javax.persistence.*;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

@Entity
public class User {
	
	@Column
	private String username;
	
	@Column
	private String password;
	
	@OneToOne(mappedBy="user", fetch=FetchType.LAZY, cascade=CascadeType.ALL, optional=false)
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private Person person;

	@Id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
		person.setUser(this);
	}
	
	

}
