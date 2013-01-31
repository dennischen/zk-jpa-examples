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
import org.zkoss.jpa.examples.entity.Student;
import org.zkoss.jpa.examples.service.CommonDao;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

@VariableResolver(DelegatingVariableResolver.class)
public class Dep2StudentViewModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@WireVariable
	CommonDao commonDao;
	
	Department selectedDepartment;
	ListModelList<Student> availableStudents;
	ListModelList<Department> availableDepartments;

	Set<Student> selectedDepartmentStudents;
	//getter & setter for the binding of the view
	public ListModel<Student> getAvailableStudents() {
		return availableStudents;
	}
	
	public ListModel<Department> getAvailableDepartments() {
		return availableDepartments;
	}
	
	
	public Department getSelectedDepartment(){
		return selectedDepartment;
	}
	
	

	
	public void setSelectedDepartment(Department selectedDepartment) {
		if(selectedDepartment!=null){
			//refresh it to avoid org.hibernate.LazyInitializationException
			this.selectedDepartment = commonDao.reload(selectedDepartment);
			
			//to keep the Students of the Department for remove/add relationship when updating.
			//wrap a set to force the delegate set being loaded to avoid lazy init when further using in update
			this.selectedDepartmentStudents = new LinkedHashSet<Student>(this.selectedDepartment.getStudents());
		}else{
			this.selectedDepartment = null;
			this.selectedDepartmentStudents = null;
		}
	}

	@Init
	public void init(){
		availableDepartments = new ListModelList<Department>(commonDao.list(Department.class));
		availableStudents = new ListModelList<Student>(commonDao.list(Student.class));
		availableStudents.setMultiple(true);
	}

	@Command 
	@NotifyChange({"selectedDepartment"}) 
	public void update(){
		/**
		 * At here, We have to maintain the bi-direction, because 1. The
		 * relationship maintainer is Student, so if you just update Department, the
		 * relationship will not change in DB. 2. No matter you need
		 * object-bi-direction in this page or not, you have to you have to sync
		 * the Student bi-direction relationship to update the relationship in
		 * DB.
		 **/

		// NOTE, for non-maintainer side, if you update it, will cause jpa
		// reload the relation-ship form db.
		// selectedDepartment = commonDao.update(selectedDepartment);

		Set<Student> currStudents = selectedDepartment.getStudents();
		// remove the relationship
		for (Student student : selectedDepartmentStudents) {
			if (!currStudents.contains(student)) {
				student = commonDao.reload(student);// the cat is probably detached, reload it.
				student.setDepartment(null);

				// it would be fine if you don't call this, jpa will update automatically.
				// but I will still call it to enhance the code logic for
				// reading
				commonDao.update(student);
			}
		}

		// add the relationship
		for (Student student : currStudents) {
			if (!selectedDepartmentStudents.contains(student)) {
				student = commonDao.reload(student);// the cat is probably detached, reload it.
				
				student.setDepartment(selectedDepartment);

				// would be fine if you don't call this, jpa will update automatically.
				// but I will still call it to enhance the code logic for
				// reading
				commonDao.update(student);
			}
		}

		// update the selectedDepartment and refresh it.
		selectedDepartment = commonDao.update(selectedDepartment);
		// sync current selected Students
		selectedDepartmentStudents = new LinkedHashSet<Student>(selectedDepartment.getStudents());
	}
	
	@Command 
	@NotifyChange({"selectedDepartment"}) 
	public void reload(){
		int index = availableDepartments.indexOf(selectedDepartment);
		selectedDepartment = commonDao.reload(selectedDepartment);

		// resets the model object too
		availableDepartments.set(index, selectedDepartment);
	}
}
