/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.live;

import java.io.Serializable;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.service.CommonDao;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

@VariableResolver(DelegatingVariableResolver.class)
public class ManualPagingCatViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CommonDao commonDao;
	
	Category selectedCategory;
	ListModelList<Category> availableCategories;
	
	
	int pageSize = 10;
	int activePage = 0;
	int totalSize;
	
	public ListModel<Category> getAvailableCategories() {
		return availableCategories;
	}
	
	public Category getSelectedCategory(){
		return selectedCategory;
	}

	public int getActivePage() {
		return activePage;
	}

	public void setActivePage(int activePage) {
		this.activePage = activePage;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setSelectedCategory(Category selectedCategory) {
		if(selectedCategory!=null){
			//refresh it to avoid org.hibernate.LazyInitializationException
			this.selectedCategory = commonDao.reload(selectedCategory);
		}else{
			this.selectedCategory = null;
		}
	}

	@Init
	public void init(){
		totalSize = commonDao.sizeOf(Category.class);
		availableCategories = new ListModelList(commonDao.list(Category.class,activePage*pageSize,pageSize));
	}

	@Command 
	@NotifyChange({"totalSize","availableCategories"}) 
	public void paging(){
		totalSize = commonDao.sizeOf(Category.class);
		if(activePage*pageSize>=totalSize){//the data size was change since last paging, reysnc it.
			activePage = 0;//simply to first page
		}
		availableCategories = new ListModelList(commonDao.list(Category.class,activePage*pageSize,pageSize));
	}
}
