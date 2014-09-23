package net.smart4life.hibernateplay.util;

import net.smart4life.hibernateplay.model.BaseEntity;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

public interface MmnetDataModelListener<T extends BaseEntity> {
	void beforeDataLoad(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters);
	void afterDataLoad(List<T> data);
}
