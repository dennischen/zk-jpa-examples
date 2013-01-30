/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.m2m;

import java.io.Serializable;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.entity.Item;
import org.zkoss.jpa.examples.service.CategoryService;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class M2MAssociation implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CategoryService categoryService;
	
	Category category;
	List<Item> availableItems;

	
	//getter & setter for the binding of the view
	public List<Item> getAvailableItems() {
		return availableItems;
	}
	
	public Category getCategory(){
		return category;
	}

	
	@Init // @Init annotates a initial method
	public void init(){
		categoryService.init();
		//get data from service and wrap it to model for the view
		availableItems = categoryService.getItemList();
		//you can use List directly, however use ListModelList provide efficient control in MVVM 
		category = categoryService.getCategoryList().get(0);
	}

	@Command //@Command annotates a command method 
	@NotifyChange({"category"}) //@NotifyChange annotates data changed notification after calling this method 
	public void update(){
		categoryService.update(category);
	}
}
