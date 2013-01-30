package org.zkoss.jpa.examples.entity;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Entity
 */
@Entity
@Table(name = "a_item")
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@Column(nullable = false, length = 128)
	String name;

	//mark this Set is mapped by Category.items, so Category is the main-role to maintain the relation ship
	@ManyToMany(mappedBy = "items") 
	private Set<Category> categories;

	public Item() {
		categories = new LinkedHashSet<Category>();
	}

	public Item(String subject) {
		this();
		this.name = subject;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public String getSubject() {
		return name;
	}

	public void setSubject(String subject) {
		this.name = subject;
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
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
