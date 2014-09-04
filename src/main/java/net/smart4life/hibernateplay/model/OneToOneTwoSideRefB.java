package net.smart4life.hibernateplay.model;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;

@Entity
public class OneToOneTwoSideRefB extends BaseEntity {
	
	@Column
	private String name;
	
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private OneToOneTwoSideRefA a;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OneToOneTwoSideRefA getA() {
		return a;
	}

	public void setA(OneToOneTwoSideRefA a) {
		this.a = a;
	}
}
