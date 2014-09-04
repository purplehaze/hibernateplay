package net.smart4life.hibernateplay.model;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;

@Entity
public class OneToOneTwoSideRefA extends BaseEntity {
	
	@Column
	private String name;
	
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//	@Column(unique = true)
	private OneToOneTwoSideRefB b;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OneToOneTwoSideRefB getB() {
		return b;
	}

	public void setB(OneToOneTwoSideRefB b) {
		this.b = b;
	}
}
