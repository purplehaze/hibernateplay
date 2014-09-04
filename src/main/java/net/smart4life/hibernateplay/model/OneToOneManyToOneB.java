package net.smart4life.hibernateplay.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class OneToOneManyToOneB extends BaseEntity {
	
	@Column
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL, optional = false)
	private OneToOneManyToOneA a;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OneToOneManyToOneA getA() {
		return a;
	}

	public void setA(OneToOneManyToOneA a) {
		this.a = a;
	}
}
