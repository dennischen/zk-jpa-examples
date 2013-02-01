package org.zkoss.jpa.examples.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.jpa.examples.entity.Unit;
import org.zkoss.jpa.examples.service.UnitDao;
import org.zkoss.zul.AbstractTreeModel;

public class UnitTreeModel extends AbstractTreeModel<Unit> {
	private static final long serialVersionUID = 1L;
	UnitDao unitDao;
	
	public UnitTreeModel(Unit root, UnitDao unitDao) {
		super(root);
		this.unitDao = unitDao;
	}

	public boolean isLeaf(Unit node) {
		return unitDao.reload(node).getSubUnit().size()==0;//might be detached, reload it back
	}

	public Unit getChild(Unit parent, int index) {
		List<Unit> subUnits = unitDao.reload(parent).getSubUnit();//might be detached, reload it back
		subUnits = sort(subUnits);
		return subUnits.get(index);
	}

	public int getChildCount(Unit parent) {
		return unitDao.reload(parent).getSubUnit().size();//might be detached, reload it back
	}
	
	private List<Unit> sort(List<Unit> units){
		//Hint, you can sort again here for UI effect
		//Don't sort on units directly, it might cause JPA persist the order
		return units;
	}

	@Override
	public int getIndexOfChild(Unit parent, Unit child) {
		List<Unit> subUnits =  unitDao.reload(parent).getSubUnit();//might be detached, reload it back
		subUnits = sort(subUnits);
		return subUnits.indexOf(child);
	}

	@Override
	public Unit getChild(int[] path) {
		
		Unit unit = getPathCache(path);
		if(unit!=null){
			return unitDao.reload(unit);//might be detached, reload it back
		}
		
		unit = unitDao.reload(getRoot());//might be detached, reload it back
		List<Unit> subUnits;
		for(int i=0;i<path.length;i++){
			subUnits = sort(unit.getSubUnit());
			unit = subUnits.get(path[i]);
		}
		
		
		setPathCache(unit, path);
		return unit;
	}


	@Override
	public int[] getPath(Unit child) {
		//path cache to enhance performance
		int[] cache = getPathCache(child);
		if(cache!=null) return cache;
		
		
		child = unitDao.reload(child);//might be detached
		Unit root = getRoot();//might be detached, but we don't care here
		if(child.equals(root)){
			return new int[0];
		}
		
		List<Integer> path = new LinkedList<Integer>();
		Unit parent = child.getParent();
		while(parent!=null){
			path.add(0,getIndexOfChild(parent,child));
			
			if(parent.equals(root)){
				break;
			}
			
			//path cache
			cache = getPathCache(parent);
			if(cache!=null){
				for(int i=cache.length-1;i>=0;i--){
					path.add(0,cache[i]);
				}
				break;
			}
			
			
			child = parent;
			parent = child.getParent();
			if(parent==null){
				throw new RuntimeException("Can't find the root ancestor");
			}
		}
		final int[] ipath = new int[path.size()];
		for (int i = 0; i < ipath.length; i++)
			ipath[i] = path.get(i);
		
		
		//put 
		setPathCache(child,ipath);
		
		return ipath;
	}
	
	//a simple read-only implementation, need a good algorithm to handle it.
	Map<Unit,int[]> unit2Path = new HashMap<Unit, int[]>();
	Map<String,Unit> path2Unit = new HashMap<String,Unit>();
	
	public void clearPathCache(Unit unit){
		int[] path = unit2Path.remove(unit);
		if(path==null) return;
		String p = toPathString(path);
		path2Unit.remove(p);
		p = p+".";
		for(String cp:new ArrayList<String>(path2Unit.keySet())){//to prevent comodification
			if(cp.startsWith(p)){
				unit2Path.remove(path2Unit.remove(cp));
			}
		}
	}
	
	private int[] getPathCache(Unit unit){
		return unit2Path.get(unit);
	}
	
	private Unit getPathCache(int[] path){
		return path2Unit.get(toPathString(path));
	}
	
	private void setPathCache(Unit unit,int[] path){
		String p = toPathString(path);
		unit2Path.put(unit, path);
		path2Unit.put(p, unit);
	}
	
	private String toPathString(int[] path){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<path.length;i++){
			if(i>0){
				sb.append(".");
			}
			sb.append(path[i]);
		}
		return sb.toString();
	}
}
