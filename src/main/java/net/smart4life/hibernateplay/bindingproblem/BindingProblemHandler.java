package net.smart4life.hibernateplay.bindingproblem;

import net.smart4life.hibernateplay.model.OneToOneManyToOneA;
import net.smart4life.hibernateplay.model.User;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;

@ViewAccessScoped
@Named
public class BindingProblemHandler implements Serializable {
	@Inject
	private Logger logger;
	
	@Inject
	private EntityManager em;

	@Inject
	private BindingProblemState bindingProblemState;
	
	private User entity;

	public void save(){
		logger.info("save()");
	}

	/**
	 * Select.
	 *
	 * @param event the event
	 */
	public void select(SelectEvent event) {
		logger.info("select({})", event);
		bindingProblemState.gotoEdit();
	}

	public User getEntity() {
		return entity;
	}

	public void setEntity(User entity) {
		this.entity = entity;
	}
}
