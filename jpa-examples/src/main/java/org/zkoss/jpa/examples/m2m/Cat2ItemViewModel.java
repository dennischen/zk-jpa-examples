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
import org.zkoss.jpa.examples.service.CommonDao;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class Cat2ItemViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CommonDao commonDao;
	
	Category selectedCategory;
	List<Item> availableItems;
	List<Category> availableCategories;

	
	//getter & setter for the binding of the view
	public List<Item> getAvailableItems() {
		return availableItems;
	}
	
	public List<Category> getAvailableCategories() {
		return availableCategories;
	}
	
	
	public Category getSelectedCategory(){
		return selectedCategory;
	}
	
	

	
	public void setSelectedCategory(Category selectedCategory) {
		if(selectedCategory!=null){
			//refresh it to avoid org.hibernate.LazyInitializationException
			this.selectedCategory = commonDao.reload(Category.class,selectedCategory.getId());
		}else{
			this.selectedCategory = null;
		}
	}

	@Init
	public void init(){
		availableItems = commonDao.list(Item.class);
		availableCategories = commonDao.list(Category.class);
	}

	@Command 
	@NotifyChange({"selectedCategory"}) 
	public void update(){
		for(Item item:selectedCategory.getItems()){
			//we can ignore this block to enhance performance if we don't care the obj-bi-direction in current page.
			item = commonDao.reload(Item.class,item.getId());//the item is possible detached, reload it.
			item.getCategories().add(selectedCategory);//bi-direction relation-ship;
			commonDao.update(item);
		}
		selectedCategory = commonDao.update(selectedCategory);
	}
	
	@Command 
	@NotifyChange({"selectedCategory"}) 
	public void reload(){
		selectedCategory = commonDao.reload(Category.class,selectedCategory.getId());
	}
}
