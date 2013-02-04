package org.zkoss.jpa.examples.entity;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Entity
 */
@Entity
@Table(name = "a_unit")
public class Unit implements SimpleId<Integer> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;
	public Integer getId() {
		return id;
	}
	
	@Column(nullable = false, length = 128)
	String name;

	//mark this Set is mapped by Student.department, so Student is the relationship maintainer
	@OneToMany(mappedBy="unit")
	private Set<Member> members;
	
	@ManyToOne
	Unit parent;
	
	@OneToMany(mappedBy="parent")
	@OrderBy("name DESC")
	List<Unit> subUnits; 
	

	public Unit() {
		members = new LinkedHashSet<Member>();
		subUnits = new LinkedList<Unit>();
	}

	public Unit(String name) {
		this();
		this.name = name;
	}

	

	public Unit getParent() {
		return parent;
	}

	public void setParent(Unit parent) {
		this.parent = parent;
	}

	public List<Unit> getSubUnits() {
		return subUnits;
	}

	public void setSubUnits(List<Unit> subUnits) {
		this.subUnits = subUnits;
	}

	public Set<Member> getMembers() {
		return members;
	}

	public void setMembers(Set<Member> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Unit other = (Unit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
