package org.zkoss.jpa.examples.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.entity.Item;

@Repository
public class CategoryService {

	@PersistenceContext
	private EntityManager em;
	
	@Transactional()
    public void init() {
		if(getItemList().size()==0){
			for(int i=0;i<10;i++){
				em.persist(new Item("Item "+i));
			}
		}
		if(getCategoryList().size()==0){
			em.persist(new Category("Category 0"));
		}
	}
	
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
    
    @Transactional
    public Category update(Category cat){
    	cat = em.merge(cat);
        return cat;
    }
}
