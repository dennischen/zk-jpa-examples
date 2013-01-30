package org.zkoss.jpa.examples.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.entity.Item;

@Repository
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class CommonDao extends DaoBase{

	
	@Transactional(readOnly=true)
    public List<Item> getItemList() {
        Query query = em.createQuery("from Item");
        List<Item> result = query.getResultList();
        return result;
    }
	
	@Transactional(readOnly=true)
    public List<Category> getCategoryList() {
        Query query = em.createQuery("from Category");
        List<Category> result = query.getResultList();
        return result;
    }
}
