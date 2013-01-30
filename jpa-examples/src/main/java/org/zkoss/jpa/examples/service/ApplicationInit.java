package org.zkoss.jpa.examples.service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;
import org.zkoss.jpa.examples.entity.Category;
import org.zkoss.jpa.examples.entity.Item;

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

		if (commonDao.list(Item.class).size() == 0) {
			for (int i = 0; i < 10; i++) {
				commonDao.persist(new Item("Item " + i));
			}
		}
		if (commonDao.list(Category.class).size() == 0) {
			commonDao.persist(new Category("Category 0"));
		}
		
		_initialzed = true;
	}
}
