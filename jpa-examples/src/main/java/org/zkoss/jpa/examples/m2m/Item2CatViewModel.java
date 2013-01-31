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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

@VariableResolver(DelegatingVariableResolver.class)
public class Item2CatViewModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@WireVariable
	CommonDao commonDao;

	Item selectedItem;
	ListModelList<Category> availableCategories;
	ListModelList<Item> availableItems;

	Set<Category> selectedItemCategories;

	// getter & setter for the binding of the view
	public ListModel<Category> getAvailableCategories() {
		return availableCategories;
	}

	public ListModel<Item> getAvailableItems() {
		return availableItems;
	}

	public Item getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Item selectedItem) {
		if (selectedItem != null) {
			// refresh it to avoid org.hibernate.LazyInitializationException
			// when getCategories
			this.selectedItem = commonDao.reload(selectedItem);

			// to keep the categories of the item for remove/add relationship
			// when updating.
			// wrap a set to force the delegate set being loaded to avoid lazy
			// init when further using in update
			this.selectedItemCategories = new LinkedHashSet<Category>(this.selectedItem.getCategories());
		} else {
			this.selectedItem = null;
			this.selectedItemCategories = null;
		}
	}

	@Init
	public void init() {
		availableItems = new ListModelList<Item>(commonDao.list(Item.class));
		
		availableCategories = new ListModelList<Category>(commonDao.list(Category.class));
		availableCategories.setMultiple(true);
	}

	@Command
	@NotifyChange({ "selectedItem" })
	public void update() {
		/**
		 * At here, We have to maintain the bi-direction, because 1. The
		 * relationship maintainer is Category, so if you just update Item, the
		 * relationship will not change in DB. 2. No matter you need
		 * object-bi-direction in this page or not, you have to you have to sync
		 * the Category bi-direction relationship to update the relationship in
		 * DB.
		 **/

		// NOTE, for non-maintainer side, if you update it, will cause jpa
		// reload the relation-ship form db.
		// selectedItem = commonDao.update(selectedItem);

		Set<Category> currCategories = selectedItem.getCategories();
		// remove the relationship
		for (Category cat : selectedItemCategories) {
			if (!currCategories.contains(cat)) {
				cat = commonDao.reload(cat);// the cat is probably detached,
											// reload it.
				cat.getItems().remove(selectedItem);// remove bi-direction
													// relation-ship;

				// would be fine if you don't call this, jpa will update
				// automatically.
				// but I will still call it to enhance the code logic for
				// reading
				commonDao.update(cat);
			}
		}

		// add the relationship
		for (Category cat : currCategories) {
			if (!selectedItemCategories.contains(cat)) {
				cat = commonDao.reload(cat);// the cat is probably detached,
											// reload it.
				cat.getItems().add(selectedItem);// add bi-direction
													// relation-ship;

				// would be fine if you don't call this, jpa will update
				// automatically.
				// but I will still call it to enhance the code logic for
				// reading
				commonDao.update(cat);
			}
		}

		// update the selectedItem and refresh it.
		selectedItem = commonDao.update(selectedItem);
		// sync current selected categories
		selectedItemCategories = new LinkedHashSet<Category>(selectedItem.getCategories());

	}

	@Command
	@NotifyChange({ "selectedItem" })
	public void reload() {
		int index = availableItems.indexOf(selectedItem);
		selectedItem = commonDao.reload(selectedItem);

		// resets the model object too
		availableItems.set(index, selectedItem);
	}
}
