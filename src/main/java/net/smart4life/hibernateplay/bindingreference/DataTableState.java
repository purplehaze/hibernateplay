package net.smart4life.hibernateplay.bindingreference;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import java.util.List;
import java.util.Map;

/**
 * Created by ILIN02 on 22.09.2014.
 */
public class DataTableState {
	private int first;
	private int rows;
	private int rowIndex;

	private List filteredValue;
	private Map<String, String> filters;

	private Object sortBy;
	private ValueExpression sortByVE;
	private UIColumn sortColumn;
	private MethodExpression sortFunction;
	private String sortMode;
	private String sortOrder;
	private List<SortMeta> multiSortMeta;
	private boolean caseSensitiveSort;
	private ValueExpression stateSortBy;

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public List getFilteredValue() {
		return filteredValue;
	}

	public void setFilteredValue(List filteredValue) {
		this.filteredValue = filteredValue;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

	public Object getSortBy() {
		return sortBy;
	}

	public void setSortBy(Object sortBy) {
		this.sortBy = sortBy;
	}

	public ValueExpression getSortByVE() {
		return sortByVE;
	}

	public void setSortByVE(ValueExpression sortByVE) {
		this.sortByVE = sortByVE;
	}

	public UIColumn getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(UIColumn sortColumn) {
		this.sortColumn = sortColumn;
	}

	public MethodExpression getSortFunction() {
		return sortFunction;
	}

	public void setSortFunction(MethodExpression sortFunction) {
		this.sortFunction = sortFunction;
	}

	public String getSortMode() {
		return sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public List<SortMeta> getMultiSortMeta() {
		return multiSortMeta;
	}

	public void setMultiSortMeta(List<SortMeta> multiSortMeta) {
		this.multiSortMeta = multiSortMeta;
	}

	public boolean isCaseSensitiveSort() {
		return caseSensitiveSort;
	}

	public void setCaseSensitiveSort(boolean caseSensitiveSort) {
		this.caseSensitiveSort = caseSensitiveSort;
	}

	public ValueExpression getStateSortBy() {
		return stateSortBy;
	}

	public void setStateSortBy(ValueExpression stateSortBy) {
		this.stateSortBy = stateSortBy;
	}
}
