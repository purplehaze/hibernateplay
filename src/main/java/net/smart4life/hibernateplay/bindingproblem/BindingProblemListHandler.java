package net.smart4life.hibernateplay.bindingproblem;

import net.smart4life.hibernateplay.bindingreference.DataTableReference;
import net.smart4life.hibernateplay.model.User;
import net.smart4life.hibernateplay.util.GenericMmnetLazyDataModel;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.primefaces.component.datatable.DataTable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;

@ViewAccessScoped
@Named
public class BindingProblemListHandler implements Serializable {
	
	@Inject
	private EntityManager em;

	private GenericMmnetLazyDataModel<User> users;
	private GenericMmnetLazyDataModel<User> users2;

	private DataTable dataTable;

	@Inject
	private DataTableReference dataTableReference1;

	@Inject
	private DataTableReference dataTableReference2;
	
	@PostConstruct
	public void init(){
		users = new GenericMmnetLazyDataModel<User>(User.class, em);
		users2 = new GenericMmnetLazyDataModel<User>(User.class, em);
	}

	public int sortUsers(User u1, User u2){
		return -1 * u1.getUsername().compareTo(u2.getUsername());
	}

	public GenericMmnetLazyDataModel<User> getUsers() {
		return users;
	}

	public GenericMmnetLazyDataModel<User> getUsers2() {
		return users2;
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public DataTableReference getDataTableReference1() {
		return dataTableReference1;
	}

	public DataTableReference getDataTableReference2() {
		return dataTableReference2;
	}
}
