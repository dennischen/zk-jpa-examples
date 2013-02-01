/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.tree;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jpa.examples.entity.Unit;
import org.zkoss.jpa.examples.service.UnitDao;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class UnitTreeViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	UnitDao unitDao;

	UnitTreeModel unitTreeModel;
	
	Unit selectedUnit;

	@Init
	public void init(){
		unitTreeModel = new UnitTreeModel(unitDao.getRoot(),unitDao);
	}

	public UnitTreeModel getUnitTreeModel() {
		return unitTreeModel;
	}

	public Unit getSelectedUnit() {
		return selectedUnit;
	}

	public void setSelectedUnit(Unit selectedUnit) {
		if(selectedUnit!=null){
			//refresh it to avoid org.hibernate.LazyInitializationException if it has other lazy relation
			this.selectedUnit = unitDao.reload(selectedUnit);
		}else{
			this.selectedUnit = null;
		}
	}
	
	@Command @NotifyChange("selectedUnit")
	public void selectRandom(){
		List<Unit> all = unitDao.list(Unit.class);
		
		int s = all.size();
		s = new Random(System.currentTimeMillis()).nextInt(s);
		
		selectedUnit = all.get(s);
		
		
		Unit p = selectedUnit;
		while(p!=null){
			unitTreeModel.addOpenObject(p);
			p = p.getParent();
		}
	}
	
	@Command
	public void collapse(){
		Unit root = unitTreeModel.getRoot();
		int s = unitTreeModel.getChildCount(root);
		for(int i=0;i<s;i++){
			unitTreeModel.removeOpenObject(unitTreeModel.getChild(root, i));
		}
	}

	@Command
	public void update(){
		
	}
	
	@Command
	public void reload(){
		
	}
}
