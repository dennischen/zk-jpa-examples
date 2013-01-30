package org.zkoss.jpa.examples.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.transaction.annotation.Transactional;

public class DaoBase{

	@PersistenceContext
	protected EntityManager em;
	
	
	@Transactional(readOnly=true)
	public <T> List<T> list(Class<T> clz) {
		CriteriaQuery<T> cri = em.getCriteriaBuilder().createQuery(clz);
		cri.from(clz);
		Query query = em.createQuery(cri);
		return query.getResultList();
	}
	 
    @Transactional
    public <T> T update(T item){
    	item = em.merge(item);
        return item;
    }
    
    @Transactional
    public <T> T persist(T item){
    	em.persist(item);
        return item;
    }
}