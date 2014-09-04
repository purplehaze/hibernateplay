package net.smart4life.hibernateplay.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class OneToOneManyToOneA extends BaseEntity {
	
	@Column
	private String name;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "a")
	@Column(unique = true)
	private Set<OneToOneManyToOneB> bees;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<OneToOneManyToOneB> getBees() {
		return bees;
	}

	public void setBees(Set<OneToOneManyToOneB> bees) {
		this.bees = bees;
	}

	@Transient
	public OneToOneManyToOneB getB(){
		OneToOneManyToOneB result = null;
		if(getBees() != null && getBees().size() > 0){
			result = getBees().iterator().next();
		}
		return result;
	}

	@Transient
	public void setB(OneToOneManyToOneB b){
		if(getBees() == null){
			setBees(new HashSet<OneToOneManyToOneB>());
		} else {
			getBees().clear();
		}
		if(b != null) {
			getBees().add(b);
		}
	}
}
