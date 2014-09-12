package net.smart4life.hibernateplay.model;

import javax.persistence.*;

@Entity
public class OneToOneOneSideRefB extends BaseEntity {
	
	@Column
	private String name;
	
	@OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL, optional = false)
	@JoinColumn(name="AAA_ID")
	private OneToOneOneSideRefA a;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OneToOneOneSideRefA getA() {
		return a;
	}

	public void setA(OneToOneOneSideRefA a) {
		this.a = a;
	}
}
