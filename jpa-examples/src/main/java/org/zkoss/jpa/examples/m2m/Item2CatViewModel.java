/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.m2m;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.entity.Item;
import org.zkoss.jpa.examples.service.CommonDao;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class Item2CatViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CommonDao commonDao;
	
	Item selectedItem;
	List<Category> availableCategories;
	List<Item> availableItems;

	Set<Category> selectedItemCategories;
	//getter & setter for the binding of the view
	public List<Category> getAvailableCategories() {
		return availableCategories;
	}
	
	public List<Item> getAvailableItems() {
		return availableItems;
	}
	
	
	public Item getSelectedItem(){
		return selectedItem;
	}
	
	

	
	public void setSelectedItem(Item selectedItem) {
		if(selectedItem!=null){
			//refresh it to avoid org.hibernate.LazyInitializationException when getCategories
			this.selectedItem = commonDao.reload(Item.class,selectedItem.getId());
			this.selectedItemCategories = new LinkedHashSet<Category>(this.selectedItem.getCategories());// to avoid lazy init
		}else{
			this.selectedItem = null;
			this.selectedItemCategories = null;
		}
	}

	@Init
	public void init(){
		availableCategories = commonDao.list(Category.class);
		availableItems = commonDao.list(Item.class);
	}

	@Command 
	@NotifyChange({"selectedItem"}) 
	public void update(){
		
		//remove the relationship
		Set<Category> curr = selectedItem.getCategories();
		for(Category cat:selectedItemCategories){
			if(!curr.contains(cat)){
				cat = commonDao.reload(Category.class,cat.getId());//the cat is possible detached, reload it.
				cat.getItems().remove(selectedItem);//bi-direction relation-ship;
				commonDao.update(cat);
			}
		}

		//add the relationship
		for(Category cat:curr){
			if(!selectedItemCategories.contains(cat)){
				cat = commonDao.reload(Category.class,cat.getId());//the cat is possible detached, reload it.
				cat.getItems().add(selectedItem);//bi-direction relation-ship;
				commonDao.update(cat);
			}
		}
		selectedItem = commonDao.update(selectedItem);
		
		
	}
	
	@Command 
	@NotifyChange({"selectedItem"}) 
	public void reload(){
		selectedItem = commonDao.reload(Item.class,selectedItem.getId());
	}
}
