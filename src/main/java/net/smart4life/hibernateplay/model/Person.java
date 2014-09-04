package net.smart4life.hibernateplay.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

@Entity
public class Person extends BaseEntity {
	
	@Column
	private String firstname;
	
	@Column
	private String secondname;
	
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@ForeignKey(name="userId")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private User user;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSecondname() {
		return secondname;
	}

	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
