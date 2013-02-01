package org.zkoss.jpa.examples.service;

import java.util.List;

import javax.persistence.Query;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.jpa.examples.entity.Unit;

@Repository
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class UnitDao extends DaoBase{

	
	@Transactional(readOnly=true)
    public List<Unit> getRoots() {
        Query query = em.createQuery("SELECT u FROM Unit u WHERE u.parent IS NULL");
        List<Unit> result = query.getResultList();
        return result;
    }
	
	@Transactional(readOnly=true)
    public Unit getRoot() {
        List<Unit> result = getRoots();
        return result.size()>0?result.get(0):null;
    }
}
