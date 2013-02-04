package org.zkoss.jpa.examples.m2m.live;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.service.CommonDao;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.AbstractListModel;

public class LiveCatListModel extends AbstractListModel<Category> {

	private static final long serialVersionUID = 1L;
	CommonDao dao;
	
	private int size = -1;
	private int pageSize = 30;
	private static final String CACHE_KEY = LiveCatListModel.class + "_cache";

	public LiveCatListModel(CommonDao dao) {
		this.dao = dao;
	}

	public Category getElementAt(int index) {
		Map<Integer, Category> cache = getCache();

		Category cat = cache.get(index);
		if (cat == null) {
			// if cache doesn't contain target object, query a page size of data
			// starting from the index
			List<Category> pageResult = dao.list(Category.class, index, pageSize);
			int indexKey = index;
			for (Category o : pageResult) {
				cache.put(indexKey, o);
				indexKey++;
			}
		} else {
			return cat;
		}

		// get the target after query from database
		cat = cache.get(index);
		if (cat == null) {
			// if we still cannot find the target object from database, there is
			// inconsistency between memory and the database
			throw new RuntimeException("Element at index " + index + " cannot be found in the database.");
		} else {
			return cat;
		}
	}

	public int getSize() {
		if(size>=0) return size;
		return size = dao.sizeOf(Category.class);
	}

	private Map<Integer, Category> getCache() {
		Execution execution = Executions.getCurrent();
		// we only reuse this cache in one execution to avoid accessing detached
		// objects.
		// our filter opens a session during a HTTP request
		Map<Integer, Category> cache = (Map) execution.getAttribute(CACHE_KEY);
		if (cache == null) {
			cache = new HashMap<Integer, Category>();
			execution.setAttribute(CACHE_KEY, cache);
		}
		return cache;
	}
}
