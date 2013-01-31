/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.datatype;

import java.io.Serializable;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jpa.examples.entity.DataType;
import org.zkoss.jpa.examples.service.CommonDao;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class SimpleDataTypeViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CommonDao commonDao;
	
	DataType selectedDataType;
	List<DataType> availableDataTypes;

	public DataType getSelectedDataType() {
		return selectedDataType;
	}

	public void setSelectedDataType(DataType selectedDataType) {
		this.selectedDataType = selectedDataType;
	}

	public List<DataType> getAvailableDataTypes() {
		return availableDataTypes;
	}

	@Init
	public void init(){
		availableDataTypes = commonDao.list(DataType.class); 
	}
	
	@Command 
	@NotifyChange({"category"}) 
	public void update(){
		if(selectedDataType!=null){
			selectedDataType = commonDao.update(selectedDataType);
		}
	}
	
	@Command 
	@NotifyChange({"selectedDataType"}) 
	public void reload(){
		if(selectedDataType!=null){
			selectedDataType = commonDao.reload(selectedDataType);
		}
	}
}
