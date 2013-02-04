package org.zkoss.jpa.examples.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;
import org.zkoss.jpa.examples.entity.AEnum;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.entity.DataType;
import org.zkoss.jpa.examples.entity.Department;
import org.zkoss.jpa.examples.entity.Item;
import org.zkoss.jpa.examples.entity.Student;
import org.zkoss.jpa.examples.entity.Unit;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ApplicationInit implements ServletContextAware, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	CommonDao commonDao;

	ServletContext servletContext;

	boolean _initialzed;

	public ApplicationInit() {
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	// http://www.tikalk.com/java/doing-transactional-work-spring-service-using-postconstruct-method
	// http://javatweet.blogspot.tw/2011/03/spring-managed-service-classes-post.html
	@PostConstruct
	@Transactional
	public void init() throws Exception {
		// @Transactional doesn't work in postConstruct
		// categoryService.init();
	}

	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (_initialzed)
			return;
		// initial demo data when startup

		initDataType();

		initOne2Many();

		initMany2Many();

		initTree();

		_initialzed = true;
	}

	private void initTree() {
		Unit root = new Unit();
		root.setName("Root");
		commonDao.persist(root);
		for(int i=0;i<3;i++){
			initTree(root,"U_"+i,6);
		}
		
		
	}
	
	private void initTree(Unit parent,String name,int depthRemains) {
		Unit unit = new Unit();
		unit.setName(name);
		unit.setParent(parent);
		commonDao.persist(unit);
		depthRemains--;
		if(depthRemains>0){
			for(int i=0;i<3;i++){
				initTree(unit,name+"_"+i,depthRemains-1);
			}
		}
		
	}

	private void initMany2Many() {
		// many to many demo
		for (int i = 0; i < 10; i++) {
			commonDao.persist(new Item("Item " + i));
		}

		for (int i = 0; i < 100; i++) {
			commonDao.persist(new Category("Category " + i));
		}
	}

	private void initOne2Many() {
		// one to many demo
		for (int i = 0; i < 10; i++) {
			commonDao.persist(new Student("Student " + i));
		}

		for (int i = 0; i < 5; i++) {
			commonDao.persist(new Department("Department " + i));
		}
	}

	private void initDataType() {
		// data type demo
		DataType dt = new DataType("Value 1");
		dt.setTypeString("one");
		dt.setTypeBoolean(Boolean.FALSE);
		dt.setTypeInteger(12);
		dt.setTypeLong(12345L);
		dt.setTypeFloat(12.34F);
		dt.setTypeDouble(1234.5678D);
		dt.setTypeBigDecimal(new BigDecimal("9876543210.123"));
		dt.setTypeDate(toDate("20130909"));
		dt.setTypeTime(toTime("12:33"));
		dt.setTypeDateTime(toDateTime("20150215 17:56"));
		dt.setTypeEnum(AEnum.HIGH);

		commonDao.persist(dt);
		dt = new DataType("Value2");
		dt.setTypeString("another");
		dt.setTypeBoolean(Boolean.TRUE);
		dt.setTypeInteger(99);
		dt.setTypeLong(99999L);
		dt.setTypeFloat(99.99F);
		dt.setTypeDouble(9999.9999D);
		dt.setTypeBigDecimal(new BigDecimal("1234567890.123"));
		dt.setTypeDate(toDate("20130101"));
		dt.setTypeTime(toTime("20:19"));
		dt.setTypeDateTime(toDateTime("20130214 07:07"));
		dt.setTypeEnum(AEnum.LOW);

		commonDao.persist(dt);
	}

	private Date toDateTime(String string) {
		try {
			return new SimpleDateFormat("yyyyMMDD HH:mm").parse(string);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private Date toTime(String string) {
		try {
			return new SimpleDateFormat("HH:mm").parse(string);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private Date toDate(String string) {
		try {
			return new SimpleDateFormat("yyyyMMDD").parse(string);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
