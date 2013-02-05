/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.tree.live;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jpa.examples.entity.Unit;
import org.zkoss.jpa.examples.service.UnitDao;
import org.zkoss.jpa.examples.tree.UnitNode;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class RodModelUnitTreeViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	UnitDao unitDao;

	RodUnitTreeModel unitTreeModel;
	
	Unit selectedUnit;

	@Init
	public void init(){
		//here, we use a live tree to prevent load unnecessary tree-node into memory.
		unitTreeModel = new RodUnitTreeModel(unitDao.getRoot(),unitDao);
	}

	public RodUnitTreeModel getUnitTreeModel() {
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
		unitTreeModel.clearOpen();
	}
	
	@Command
	@NotifyChange({ "selectedUnit" })
	public void move(@BindingParam("target") Unit target,
			@BindingParam("parent") Unit parent){
		//TODO, need to re-confirm the implementation unit bug http://tracker.zkoss.org/browse/ZK-1608 fix
		
		target = unitDao.reload(target);
		parent = unitDao.reload(parent);
		
		Unit oldparent = (Unit)target.getParent();
		if(parent.equals(oldparent)) return;
		if(parent.equals(target) || isLoop(target,parent)) {
			//message?
			return;
		}
		
		//database
		oldparent.getSubUnits().remove(target);//remove bi-direction
		target.setParent(parent);
		parent.getSubUnits().add(target);//bi-direction
		
		
		if(target.equals(selectedUnit)){//in this case, target is always the selectedUnit.
			selectedUnit = target;
		}
		
		unitTreeModel.notifyStructureChange(oldparent);
		unitTreeModel.notifyDataChange(parent);
		
	}
	

	private boolean isLoop(Unit target, Unit parent) {
		if(parent==null) return false;
		if(parent.equals(target)){
			return true;
		}
		return isLoop(target,(Unit)parent.getParent());
	}

	@Command
	@NotifyChange({ "selectedUnit" })
	public void update(){
		selectedUnit = unitDao.update(selectedUnit);
		unitTreeModel.notifyDataChange(selectedUnit);
	}
	
	@Command
	@NotifyChange({ "selectedUnit" })
	public void reload() {
		selectedUnit = unitDao.reload(selectedUnit);
		unitTreeModel.notifyDataChange(selectedUnit);		
	}
}
