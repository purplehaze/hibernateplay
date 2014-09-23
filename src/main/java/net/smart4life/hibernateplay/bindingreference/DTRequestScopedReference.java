package net.smart4life.hibernateplay.bindingreference;

import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import org.primefaces.component.datatable.DataTable;
import org.slf4j.Logger;

import javax.el.ValueExpression;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

@RequestScoped
public class DTRequestScopedReference {
	private DataTable dataTable;
	private DataTableReference backReference;
	private boolean synchronizedFromState = false;
	private DataTableState dataTableState;

	@Inject
	private Logger logger;

	private void logValues(String beforeAfter, PhaseEvent event){
		logger.info("!!!!!!!!!!!!!!!!!!!!!!! Observing {} the [{}] event.", beforeAfter,  event.getPhaseId());
		if(dataTable == null){
			logger.info("!!!!!!!!!!!!!!!!! dataTable is null");
		} else {
			ValueExpression sortBy = dataTable.getValueExpression("sortBy");
			logger.info("!!!!!!!!!!!!!!!!!! sortBy={}", sortBy);
		}
	}

	public void afterRenderResponse(@Observes @AfterPhase(JsfPhaseId.RENDER_RESPONSE) PhaseEvent event) {
		syncFromUI();
	}

//	public void before(@Observes @BeforePhase(JsfPhaseId.ANY_PHASE) PhaseEvent event) {
//		logValues("before", event);
//	}
//
//	public void after(@Observes @AfterPhase(JsfPhaseId.ANY_PHASE) PhaseEvent event) {
//		logValues("after", event);
//	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public void syncFromState(DataTableReference backReference, DataTableState initState){
		this.backReference = backReference;
		dataTableState = initState;
		if(dataTableState != null){
			getDataTable().setFirst(dataTableState.getFirst());
			getDataTable().setRows(dataTableState.getRows());
			getDataTable().setRowIndex(dataTableState.getRowIndex());
			getDataTable().setFilteredValue(dataTableState.getFilteredValue());
			getDataTable().setFilters(dataTableState.getFilters());

			getDataTable().setSortBy(dataTableState.getSortBy());
			getDataTable().setSortByVE(dataTableState.getSortByVE());
			getDataTable().setSortColumn(dataTableState.getSortColumn());
			getDataTable().setSortFunction(dataTableState.getSortFunction());

			getDataTable().setSortMode(dataTableState.getSortMode());
			getDataTable().setSortOrder(dataTableState.getSortOrder());
			getDataTable().setMultiSortMeta(dataTableState.getMultiSortMeta());

//			getDataTable().setColumns(dataTableState.getColumns());
//			getDataTable().setCaseSensitiveSort(dataTableState.isCaseSensitiveSort());
//			getDataTable().setDynamicColumns(dataTableState.getDynamicColumns());

			getDataTable().setValueExpression("sortBy", dataTableState.getStateSortBy());

		}

		synchronizedFromState = true;
	}

	private void syncFromUI(){
		if(getDataTable() != null){
			backReference.setDataTableStateFromDataTable(getDataTable());
		}
	}

	public boolean isSynchronizedFromState() {
		return synchronizedFromState;
	}
}
