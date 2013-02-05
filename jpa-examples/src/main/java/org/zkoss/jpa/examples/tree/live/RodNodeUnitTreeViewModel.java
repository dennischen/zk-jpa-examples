/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.tree.live;

import java.io.Serializable;
import java.util.LinkedList;
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
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

@VariableResolver(DelegatingVariableResolver.class)
public class RodNodeUnitTreeViewModel implements Serializable{
private static final long serialVersionUID = 1L;
	
	@WireVariable
	UnitDao unitDao;

	DefaultTreeModel<Unit> unitTreeModel;

	UnitNode selectedUnit;

	@Init
	public void init(){
		UnitNode root = new UnitNode(unitDao.getRoot());
		loadFurther(root,2);//in this case, we always load further (+2 layer) for a node
		unitTreeModel = new DefaultTreeModel<Unit>(root);
	}
	
	
	private void loadFurther(UnitNode node,int more){
		if(more<0) return;

		if(node.isLoaded()){
			//check children load state
			for(TreeNode<Unit> n:node.getChildren()){
				loadFurther((UnitNode)n,more-1);
			}
		}else{
			node.setLoaded(true);
			Unit u = unitDao.reload(node.getUnit());//might be detached
			for(Unit sub:u.getSubUnits()){
				UnitNode n = new UnitNode(sub);
				node.add(n);
				loadFurther(n,more-1);
			}
		}
	}

	public TreeModel<TreeNode<Unit>> getUnitTreeModel() {
		return unitTreeModel;
	}

	public UnitNode getSelectedUnit() {
		return selectedUnit;
	}

	public void setSelectedUnit(UnitNode selectedUnitNode) {
		this.selectedUnit = selectedUnitNode;
	}
	
	@Command
	public void load(@BindingParam("node") UnitNode node,@BindingParam("open") boolean open){
		if(open){
			loadFurther(node,2);//in this case, we always load further (+2 layer) for a node
		}
	}
	
	@Command
	public void collapse(){
		unitTreeModel.clearOpen();
	}
	
	
	@Command
	@NotifyChange({ "selectedUnit" })
	public void move(@BindingParam("target") UnitNode target,
			@BindingParam("parent") UnitNode parent){
		//TODO, need to re-confirm the implementation unit bug http://tracker.zkoss.org/browse/ZK-1608 fix
		
		UnitNode oldparent = (UnitNode)target.getParent();
		if(parent.equals(oldparent)) return;
		if(parent.equals(target) || isLoop(target,parent)) {
			//message?
			return;
		}
		
		//database
		Unit t = target.getData();
		Unit p = parent.getData();
		t.setParent(p);
		t = unitDao.update(t);
		
		//sync ui
		oldparent.remove(target);
		oldparent.setData(unitDao.reload(oldparent.getData()));//could remove in this case
		
		target.setData(t);
		if(target.equals(selectedUnit)){//in this case, target is always the selectedUnit.
			selectedUnit.setData(t);
		}
		
		parent.add(target);
		parent.setData(unitDao.reload(parent.getData()));//could remove in this case
		
	}

	private boolean isLoop(UnitNode target, UnitNode parent) {
		if(parent==null) return false;
		if(parent.equals(target)){
			return true;
		}
		return isLoop(target,(UnitNode)parent.getParent());
	}

	@Command
	@NotifyChange({ "selectedUnit" })
	public void update(){
		Unit unit = unitDao.update(selectedUnit.getUnit());
		selectedUnit.setData(unit);
	}
	
	@Command
	@NotifyChange({ "selectedUnit" })
	public void reload() {
		Unit unit = unitDao.reload(selectedUnit.getUnit());
		selectedUnit.setData(unit);
	}
	
	@Command
	public void show() {
		UnitNode unit = (UnitNode)selectedUnit.getParent();
		
		while(unit!=null){
			unitTreeModel.addOpenObject(unit);
			unit = (UnitNode)unit.getParent();
		}
	}
}
