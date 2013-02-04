package org.zkoss.jpa.examples.tree;

import java.util.LinkedList;

import org.zkoss.jpa.examples.entity.Unit;
import org.zkoss.zul.DefaultTreeNode;

public class UnitNode extends DefaultTreeNode<Unit>{
	private static final long serialVersionUID = 1L;
	
	//flag for loaded
	boolean loaded;
	
	public UnitNode(Unit unit) {
		super(unit,new LinkedList<UnitNode>());
	}
	
	//equals getData, 
	public Unit getUnit(){
		return getData();
	}
	
	//NOTE, set unit that has different hashcode and equals will cause identification problem.
	public void setUnit(Unit unit){
		setData(unit);
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getData() == null) ? 0 : getData().hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitNode other = (UnitNode) obj;
		if (getData() == null) {
			if (other.getData() != null)
				return false;
		} else if (!getData().equals(other.getData()))
			return false;
		return true;
	}
}
