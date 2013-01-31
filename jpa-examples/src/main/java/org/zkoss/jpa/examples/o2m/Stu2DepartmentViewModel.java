/* 
	Description:
		ZK Tutorial
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.jpa.examples.o2m;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.entity.Department;
import org.zkoss.jpa.examples.entity.Item;
import org.zkoss.jpa.examples.entity.Student;
import org.zkoss.jpa.examples.service.CommonDao;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

@VariableResolver(DelegatingVariableResolver.class)
public class Stu2DepartmentViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CommonDao commonDao;
	
	Student selectedStudent;
	ListModelList<Department> availableDepartments;
	ListModelList<Student> availableStudents;

	//getter & setter for the binding of the view
	public ListModel<Department> getAvailableDepartments() {
		return availableDepartments;
	}
	
	public ListModel<Student> getAvailableStudents() {
		return availableStudents;
	}
	
	
	public Student getSelectedStudent(){
		return selectedStudent;
	}
	
	

	
	public void setSelectedStudent(Student selectedStudent) {
		if(selectedStudent!=null){
			//refresh it to avoid org.hibernate.LazyInitializationException
			this.selectedStudent = commonDao.reload(selectedStudent);
		}else{
			this.selectedStudent = null;
		}
	}

	@Init
	public void init(){
		availableStudents = new ListModelList<Student>(commonDao.list(Student.class));
		availableDepartments = new ListModelList<Department>(commonDao.list(Department.class));
	}

	@Command 
	@NotifyChange({"selectedStudent"}) 
	public void update(){
		/**
		 * At here
		 * 1. The relationship maintainer is Student, so it is ok to just update the Student. 
		 * 2. if you need use both Department,Student relation in the page, then you have to sync the object bi-direction relationship.
		 * (in general case, we always reload the entity before accessing related entity to prevent LazyInitializationException,
		 * so maintain the bi-direction in non-maintainer side (e.g. Department) is useless.
		 **/ 
		
		//you could chose to ignore object-bi-direction (depends on your case)
		//in this example, we don't care the item->category relation in current page,
		//so we can just ignore it to enhance performance (jdbc-query of items)
		
		boolean maintainObjBidirection = true;
		if(maintainObjBidirection){
			
			Department dep = selectedStudent.getDepartment();
			
			if(dep!=null){
				dep = commonDao.reload(dep);
				
				dep.getStudents().add(selectedStudent);
				
				//the change of item is only the relationship which is maintained by Category
				//it would be fine if you don't call this 
				//jpa will handle update automatically.
				//but I will still call it to enhance the code logic for reading
				commonDao.update(dep);
			}
			
			//update the selectedCategory and refresh it.
			selectedStudent = commonDao.update(selectedStudent);
		}
	}
	
	@Command 
	@NotifyChange({"selectedStudent"}) 
	public void reload(){
		int index = availableStudents.indexOf(selectedStudent);
		selectedStudent = commonDao.reload(selectedStudent);

		// resets the model object too
		availableStudents.set(index, selectedStudent);
	}
}
