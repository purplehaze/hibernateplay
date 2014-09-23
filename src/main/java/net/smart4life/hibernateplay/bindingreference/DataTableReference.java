package net.smart4life.hibernateplay.bindingreference;

import org.primefaces.component.datatable.DataTable;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.enterprise.inject.New;
import javax.faces.component.StateHelper;
import javax.faces.component.TransientStateHelper;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by ILIN02 on 19.09.2014.
 */
public class DataTableReference implements Serializable {
	private DataTableState dataTableState;

	@Inject @New
	private DTRequestScopedReference dtRequestScopedReference;

	public DataTable getDataTable() {
		synchronized (dtRequestScopedReference) {
			DataTable dt = dtRequestScopedReference.getDataTable();
			if(dt != null && !dtRequestScopedReference.isSynchronizedFromState()){
				dtRequestScopedReference.syncFromState(this, dataTableState);
			}
			return dt;
		}
	}

	public void setDataTable(DataTable dataTable) {
		synchronized (dtRequestScopedReference) {
			dtRequestScopedReference.setDataTable(dataTable);
			if(dataTable != null && !dtRequestScopedReference.isSynchronizedFromState()){
				dtRequestScopedReference.syncFromState(this, dataTableState);
			}
		}
	}

	public void setDataTableStateFromDataTable(DataTable dataTable) {
		if (dataTableState == null) {
			dataTableState = new DataTableState();
		}

		try {
			dataTableState.setRows(dataTable.getRows());
			dataTableState.setRowIndex(dataTable.getRowIndex());
			dataTableState.setFirst(dataTable.getFirst());
			dataTableState.setFilteredValue(dataTable.getFilteredValue());
			dataTableState.setFilters(dataTable.getFilters());
			dataTableState.setSortBy(dataTable.getSortBy());
			dataTableState.setSortByVE(dataTable.getSortByVE());
			dataTableState.setSortColumn(dataTable.getSortColumn());
			dataTableState.setSortFunction(dataTable.getSortFunction());
			dataTableState.setSortMode(dataTable.getSortMode());
			dataTableState.setSortOrder(dataTable.getSortOrder());
			dataTableState.setMultiSortMeta(dataTable.getMultiSortMeta());
//			dataTableState.setColumns(dataTable.getColumns());
//			dataTableState.setCaseSensitiveSort(dataTable.isCaseSensitiveSort());
//			dataTableState.setDynamicColumns(dataTable.getDynamicColumns());

			ValueExpression stateSortBy = dataTable.getValueExpression("sortBy");
			dataTableState.setStateSortBy(stateSortBy);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}