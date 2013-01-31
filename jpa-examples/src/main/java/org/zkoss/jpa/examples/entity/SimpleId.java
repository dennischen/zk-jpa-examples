package org.zkoss.jpa.examples.entity;

import java.io.Serializable;

public interface SimpleId<T> extends Serializable{
	
	public T getId();

}
