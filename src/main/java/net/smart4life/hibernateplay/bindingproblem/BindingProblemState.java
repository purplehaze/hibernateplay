package net.smart4life.hibernateplay.bindingproblem;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by ILIN02 on 18.09.2014.
 */
@ViewAccessScoped
@Named
public class BindingProblemState implements Serializable {
	private static final String PAGE_PREFIX = "/bindingproblem/";
	private String activePage;

	private Long idFilterValue;
	private String nameFilterValue;

	@PostConstruct
	private void init(){
		gotoList();
	}

	public void gotoList(){
		activePage = PAGE_PREFIX+"listInc.xhtml";
	}

	public void gotoEdit(){
		activePage = PAGE_PREFIX+"editInc.xhtml";
	}

	public String getActivePage() {
		return activePage;
	}

	public void setActivePage(String activePage) {
		this.activePage = activePage;
	}

	public Long getIdFilterValue() {
		return idFilterValue;
	}

	public void setIdFilterValue(Long idFilterValue) {
		this.idFilterValue = idFilterValue;
	}

	public String getNameFilterValue() {
		return nameFilterValue;
	}

	public void setNameFilterValue(String nameFilterValue) {
		this.nameFilterValue = nameFilterValue;
	}
}
