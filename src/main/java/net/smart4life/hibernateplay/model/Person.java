package net.smart4life.hibernateplay.model;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

@Entity
public class Person {
	
	@Column
	private String firstname;
	
	@Column
	private String secondname;
	
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@ForeignKey(name="userId")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "COMPANY_ID", nullable = false)
	private Company company;

	@Id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}


}
