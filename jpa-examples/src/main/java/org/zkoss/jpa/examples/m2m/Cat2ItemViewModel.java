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
public class Cat2ItemViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CommonDao commonDao;
	
	Category selectedCategory;
	List<Item> availableItems;
	List<Category> availableCategories;

	Set<Item> selectedCategoryItems;
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
			this.selectedCategory = commonDao.reload(selectedCategory);
			
			//to keep the items of the category for remove/add relationship when updating.
			//wrap a set to force the delegate set being loaded to avoid lazy init when further using in update
			this.selectedCategoryItems = new LinkedHashSet<Item>(this.selectedCategory.getItems());
		}else{
			this.selectedCategory = null;
			this.selectedCategoryItems = null;
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
		/**
		 * At here
		 * 1. The relationship maintainer is Category, so it is ok to just update the Category. 
		 * 2. if you need use both Category,Item relation in the page, then you have to sync the object bi-direction relationship.
		 * (in general case, we always reload the entity before accessing related entity to prevent LazyInitializationException,
		 * so maintain the bi-direction in non-maintainer side (e.g. Item) is useless.
		 **/ 
		
		//you could chose to ignore object-bi-direction (depends on your case)
		//in this example, we don't care the item->category relation in current page,
		//so we can just ignore it to enhance performance (jdbc-query of items)
		
		boolean maintainObjBidirection = true;
		if(maintainObjBidirection){
			
			Set<Item> currItems = selectedCategory.getItems();
			
			//remove the relationship
			for(Item item:selectedCategoryItems){
				if(!currItems.contains(item)){
					item = commonDao.reload(item);//the cat is probably detached, reload it.
					item.getCategories().remove(selectedCategory);//remove bi-direction relation-ship;
					
					//the change of item is only the relationship which is maintained by Category
					//it would be fine if you don't call this 
					//jpa will handle update automatically.
					//but I will still call it to enhance the code logic for reading
					commonDao.update(item);
				}
			}
	
			//add the relationship
			for(Item item:currItems){
				if(!selectedCategoryItems.contains(item)){
					item = commonDao.reload(item);//the cat is probably detached, reload it.
					item.getCategories().add(selectedCategory);//add bi-direction relation-ship;
					
					//the change of item is only the relationship which is maintained by Category
					//it would be fine if you don't call this, 
					//jpa will handle update automatically. 
					//but I will still call it to enhance the code logic for reading
					commonDao.update(item);
				}
			}
			
			//update the selectedCategory and refresh it.
			selectedCategory = commonDao.update(selectedCategory);
			//sync current selected items
			selectedCategoryItems = new LinkedHashSet<Item>(currItems);
		}
	}
	
	@Command 
	@NotifyChange({"selectedCategory"}) 
	public void reload(){
//		int index = availableItems.indexOf(selectedItem);
		selectedCategory = commonDao.reload(selectedCategory);

		//TODO a MVVM bug for notify the item inside left available item list
		
		//if you are not using form binding, you have to also update the one inside model
//		availableItems.set(index, selectedItem);
//		BindUtils.postNotifyChange(null, null, selectedItem, "*");
		
//		//if you are using ListModel for availableItems, you could ignore the notifyChange of availableItems
//		BindUtils.postNotifyChange(null, null, this, "availableItems");
	}
}
