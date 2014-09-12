package net.smart4life.hibernateplay.model;

import javax.persistence.*;

@Entity
public class OneToOneOneSideRefA extends BaseEntity {
	
	@Column
	private String name;
	
	@OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL, mappedBy = "a", optional = false)
	private OneToOneOneSideRefB b;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OneToOneOneSideRefB getB() {
		return b;
	}

	public void setB(OneToOneOneSideRefB b) {
		this.b = b;
		if(b != null){
			b.setA(this);
		}
	}
}
