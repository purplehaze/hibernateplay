package net.smart4life.hibernateplay.util;

import net.smart4life.hibernateplay.model.BaseEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * The Class GenericMmnetLazyDataModel.
 *
 * @param <T> the generic type
 */
public class GenericMmnetLazyDataModel<T extends BaseEntity> extends LazyDataModel<T> {

	private static final Logger logger = LoggerFactory.getLogger(GenericMmnetLazyDataModel.class);

	/** The Constant MAIN_OBJECT_PREFIX. */
	public static final String MAIN_OBJECT_PREFIX = "o";
	private static final String SELECT_PLACEHOLDER = ":select_placeholder";
	private static final String STATIC_FROM_PLACEHOLDER = ":static_from_placeholder";

	private EntityManager em;
	private Class<T> entityClass;
	private String selectedFields = MAIN_OBJECT_PREFIX;
	private String selectFieldForCountQuery = "count(" + MAIN_OBJECT_PREFIX + ".id)";
	private String staticFromClause;
	private String staticFromCountClause;
	private String defaultOrderByFields;
	private String staticWhereClauseCriteria;
	private Map<String, Object> staticWhereClauseCriteriaMap;
	private boolean distinct = false;
	
	/** The global criteria model list. */
	private List<CriteriaModel> globalCriteriaModelList;
	
	/** The filtered objects list. */
	private List<T> filteredObjectsList;
	private SafeSort<T> safeSortComparator = null;
	private FilterMatchMode defaultFilterMatchMode;
	private List<MmnetDataModelListener<T>> mmnetDataModelListeners;
	private Map<String, String> savedFilters = new HashMap<>();

	/**
	 * Instantiates a new generic mmnet lazy data model.
	 *
	 * @param entityClass the entity class
	 * @param entityManager the entity manager
	 */
	public GenericMmnetLazyDataModel(Class<T> entityClass, EntityManager entityManager) {
		this(entityClass, entityManager, FilterMatchMode.contains);
	}

	/**
	 * Instantiates a new generic mmnet lazy data model.
	 *
	 * @param entityClass the entity class
	 * @param entityManager the entity manager
	 * @param defaultFilterMatchMode the default filter match mode
	 */
	public GenericMmnetLazyDataModel(Class<T> entityClass, EntityManager entityManager,
									 FilterMatchMode defaultFilterMatchMode) {
		this.entityClass = entityClass;
		this.em = entityManager;
		this.staticFromClause = entityClass.getSimpleName() + " " + MAIN_OBJECT_PREFIX;
		this.defaultFilterMatchMode = defaultFilterMatchMode;
	}

	/* (non-Javadoc)
	 * @see org.primefaces.model.LazyDataModel#getRowData(java.lang.String)
	 */
	@Override
	public T getRowData(String rowKey) {
		T entity = null;
        try{
            entity = em.find(entityClass, Long.valueOf(rowKey));
        }catch (NumberFormatException nfe){
            ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
            logger.error("Can not parse rowKey='"+rowKey+"' as Integer value. requestServletPath="+extCtx.getRequestServletPath());
            throw nfe;
        }
		return entity;
	}

	/* (non-Javadoc)
	 * @see org.primefaces.model.LazyDataModel#getRowKey(java.lang.Object)
	 */
	@Override
	public Object getRowKey(T entity) {
		return entity.getId();
	}

	/*
	 * Main method where the whole search starts If you will implement some custom
	 * DataModel object for some entity then you can override this method
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#load( int, int, java.lang.String,
	 * org.primefaces.model.SortOrder, java.util.Map)
	 */
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		List<T> result = null;

		Clock clock = new Clock();
		
		if(mmnetDataModelListeners != null && mmnetDataModelListeners.size() > 0){
			for(MmnetDataModelListener<T> l : mmnetDataModelListeners){
				l.beforeDataLoad(first, pageSize, sortField, sortOrder, filters);
			}
		}
		
		savedFilters = filters;		

		String selectTemplateUnordered = buildSelectTemplate(filters);

		/*
		 * Wenn eine fromCountClause angegeben wurde, wird diese verwendet. Wenn
		 * keine vorhanden ist, wird die Standard fromClause verwendet. Aus diesem
		 * werden für das Zählen jedochen alle fetches entfernt, weil diese in der
		 * Count-Abfrage nicht zulässig sind.
		 */
//		String tmpFromClause = getStaticFromCountClause() != null 
//				? getStaticFromCountClause() : getStaticFromClause()
//				.replace("fetch ", "").replace("Fetch ", "").replace("FETCH ", "");
	
				boolean fetchFound = false;
				String tmpFromClause = null;
				if (getStaticFromCountClause() == null) {
					tmpFromClause = getStaticFromClause();
					if(tmpFromClause.toLowerCase().contains("fetch")){
						tmpFromClause = tmpFromClause.replace("fetch ", "").replace("Fetch ", "").replace("FETCH ", "");
						fetchFound = true;
					}
				} else {
					tmpFromClause = getStaticFromCountClause();
				}

		// rowCount
		String rowCountJpql = null; 
		
		if(fetchFound || distinct){
			// Dies ist etwas inperformanter, aber bei einem Distinct Statement nicht anders machbar.
			rowCountJpql = selectTemplateUnordered
				.replace(SELECT_PLACEHOLDER, getSelectedFields())
				.replace(STATIC_FROM_PLACEHOLDER, tmpFromClause);
			rowCountJpql = "select count(c) from " + entityClass.getSimpleName() + " c where c in(" + rowCountJpql + ")";
		} else {
			rowCountJpql = selectTemplateUnordered
				.replace(SELECT_PLACEHOLDER, getSelectFieldForCountQuery())
				.replace(STATIC_FROM_PLACEHOLDER, tmpFromClause);
		}
		
		
		if (logger.isDebugEnabled()) {
			logger.debug("fire ROW COUNT query: " + rowCountJpql);
		}
		
		try{
		
		TypedQuery<Long> countQuery = em.createQuery(rowCountJpql, Long.class);
		countQuery = addJpqlQueryParams(countQuery, staticWhereClauseCriteriaMap);
		int dataSize = countQuery.getSingleResult().intValue();
		this.setRowCount(dataSize);
		// build main query
		String orderBy = buildOrderBy(sortField, sortOrder);
		String selectTemplateOrdered = selectTemplateUnordered + orderBy;
		String selectJpql = selectTemplateOrdered.replace(SELECT_PLACEHOLDER, getSelectedFields()).replace(
				STATIC_FROM_PLACEHOLDER, getStaticFromClause());

		TypedQuery<T> query = em.createQuery(selectJpql, entityClass);
		query = addJpqlQueryParams(query, staticWhereClauseCriteriaMap);

		// paginate
		if (dataSize > pageSize) {
			try {
				query.setFirstResult(first);
				query.setMaxResults(pageSize);
				if (logger.isDebugEnabled()) {
					String msg = "fire QUERY: " + selectJpql + "; firstResult=" + first + ", maxResults=" + pageSize;
					if(staticWhereClauseCriteriaMap != null){
						msg += ", staticParams="+staticWhereClauseCriteriaMap;
					}
					logger.debug(msg);
				}
				result = query.getResultList();
			} catch (IndexOutOfBoundsException e) {
				query.setFirstResult(first);
				int maxResults = first + (dataSize % pageSize);
				query.setMaxResults(maxResults);
				if (logger.isDebugEnabled()) {
					logger.debug("fire QUERY: " + selectJpql + "; firstResult=" + first + ", maxResults=" + maxResults);
				}
				result = query.getResultList();
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("fire QUERY: " + selectJpql + " staticWhereParams:" + staticWhereClauseCriteriaMap);
			}
			result = query.getResultList();
		}

		if (safeSortComparator != null) {
			safeSortComparator.setInputCollection(result);
			result = safeSortComparator.getSortedList();
		}
		
		if(mmnetDataModelListeners != null && mmnetDataModelListeners.size() > 0){
			for(MmnetDataModelListener<T> l : mmnetDataModelListeners){
				l.afterDataLoad(result);
			}
		}
		
		if(clock.getDauerMillisFromStart()>10000){
			logger.warn("Folgende Abfrage lief länger als 10 Sekunden!: \n {}\n params={}\n firstResult={}\n maxResult={}\n count query=", 
					new Object[]{selectJpql,staticWhereClauseCriteriaMap,first,pageSize,rowCountJpql});
		}

		if (logger.isDebugEnabled()) {
			logger.debug("SELECT-query-size: " + result.size() + ", COUNT-query-result: " + dataSize);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Laden des LazyDataModels benötigt {}.", clock.getDauer());
		}
		return result;
		} catch(Exception e){
			logger.error("Fehler beim Laden des LazyDataModels! " + e);
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * This method generates jpql query without order by part. Placeholders for
	 * selected objects and user defined from part are set instead of proper
	 * values. These placeholders will be replaced in count and normal queries
	 * with proper values.
	 *
	 * @param filters the filters
	 * @return the string
	 */
	public String buildSelectTemplate(Map<String, String> filters) {
		String globalFilterValue = filters.get("globalFilter");
		globalFilterValue = (globalFilterValue == null || globalFilterValue.isEmpty()) ? null : globalFilterValue;
		filters.remove("globalFilter");

		StringBuilder jpql = new StringBuilder("select " + (distinct ? "distinct " : "") 
				+ SELECT_PLACEHOLDER + " from ");
		List<CriteriaModel> criteriaModelList = buildCriteriaModelList(filters);

		jpql.append(STATIC_FROM_PLACEHOLDER);
		// dynamic data table from clause
		String dataTableDynamicFromClause = getDataTableDynamicFromClause(criteriaModelList, false);
		jpql.append(dataTableDynamicFromClause);
		// global search data table from clause
		if (globalFilterValue != null) {
			String dataTableGlobalFromClause = getDataTableDynamicFromClause(globalCriteriaModelList, true);
			jpql.append(dataTableGlobalFromClause);
		}

		jpql.append(" where 1 = 1 ");
		// append user defined additional static where criteria
		String staticWhereCriteria = getStaticWhereClauseCriteria();
		if (staticWhereCriteria != null && !staticWhereCriteria.trim().isEmpty()) {
			staticWhereCriteria = staticWhereCriteria.trim();
			if (!staticWhereCriteria.toLowerCase().startsWith("and")) {
				jpql.append(" and ");
			}
			jpql.append(staticWhereCriteria);
		}
		// append data table dynamic where criteria
		String dataTableFilterCriteria = getDataTableFilterCriteria(criteriaModelList, null);
		jpql.append(dataTableFilterCriteria);
		// append data table global where criteria
		if (globalFilterValue != null) {
			String dataTableGlobalFilterCriteria = 
					getDataTableFilterCriteria(globalCriteriaModelList, globalFilterValue);
			jpql.append(dataTableGlobalFilterCriteria);
		}

		return jpql.toString();
	}

	/**
	 * this method generated additional Objects and aliases for properties with
	 * collection in path.
	 *
	 * @param criteriaModelList the criteria model list
	 * @param isGlobalClause the is global clause
	 * @return the data table dynamic from clause
	 */
	private String getDataTableDynamicFromClause(List<CriteriaModel> criteriaModelList, boolean isGlobalClause) {
		StringBuilder from = new StringBuilder();
		for (CriteriaModel model : criteriaModelList) {
			if (model.collectionClass != null) {
				from.append(", ").append(model.collectionClass.getSimpleName())
						.append(" ").append(getObjectAlias(model.collectionClass, model.idx, isGlobalClause)).append(" ");
			}
		}
		return from.toString();
	}

	/**
	 * This method generates order by clause.
	 *
	 * @param sortField value of this field comes from dataTable
	 * @param sortOrder value of this field comes from dataTable
	 * @return the string
	 */
	public String buildOrderBy(String sortField, SortOrder sortOrder) {
		StringBuilder orderBy = new StringBuilder(" ");
		if (sortField != null && sortOrder != SortOrder.UNSORTED) {
			orderBy.append("order by o.").append(sortField).append(" ");
			if (sortOrder == SortOrder.DESCENDING) {
				orderBy.append("DESC ");
			} else if (sortOrder == SortOrder.ASCENDING) {
				orderBy.append("ASC ");
			}
		} else if (defaultOrderByFields != null) {
			orderBy.append("order by " + defaultOrderByFields);
		}

		return orderBy.toString();
	}

	/**
	 * This method generates dynamic where clauses for dataTable filters and
	 * global dataTable filter.
	 *
	 * @param criteriaList List of GenericMmnetLazyDataModel.CriteriaModel objects which
	 * contains information about every searched property
	 * @param globalFilterValue this is value of global filter. if null then we do search not
	 * global but separated filters
	 * @return the data table filter criteria
	 */
	public String getDataTableFilterCriteria(List<CriteriaModel> criteriaList, String globalFilterValue) {
		StringBuilder jpql = new StringBuilder();
		String fullPropertyName = null;
		boolean isGlobalClause = globalFilterValue != null && !globalFilterValue.isEmpty();
		if (isGlobalClause) {
			jpql.append(" and ( ");
		}
		int cnt = 0;
		for (CriteriaModel model : criteriaList) {
			try {
				fullPropertyName = model.fullPropertyName;
				String queryObjectAlias = MAIN_OBJECT_PREFIX;
				Class<?> type = model.targetPropertyClass;
				String filterValue = isGlobalClause ? globalFilterValue : model.filterValue;
				String filterProperty = model.fullPropertyName;
				if (!isGlobalClause) {
					jpql.append(" and ");
				} else if (isGlobalClause && cnt > 0) {
					jpql.append(" or ");
				}
				if (model.collectionClass != null) {
					queryObjectAlias = getObjectAlias(model.collectionClass, model.idx, isGlobalClause);
					jpql.append("( ").append(queryObjectAlias).append(".id member of ").append(MAIN_OBJECT_PREFIX);
					if (model.propertiesBeforeCollection != null && !model.propertiesBeforeCollection.isEmpty()) {
						jpql.append(".").append(model.propertiesBeforeCollection);
					}
					jpql.append(".").append(model.collectionName).append(" and ");
					filterProperty = model.propertiesAfterCollection;
				}

				String typedCriteria = buildTypedCriteria(type, queryObjectAlias + "." + filterProperty, filterValue,
						!isGlobalClause);
				jpql.append(typedCriteria);

				if (model.collectionClass != null) {
					jpql.append(") ");
				}

			} catch (Exception e) {
				logger.warn("can not filter by property='" + fullPropertyName + "' on class " 
						+ entityClass.getCanonicalName(), e);
			}
			cnt++;
		}

		if (isGlobalClause) {
			jpql.append(" ) ");
		}

		return jpql.toString();
	}

	/**
	 * Builds the typed criteria.
	 *
	 * @param type the type
	 * @param queryField the query field
	 * @param filterValue the filter value
	 * @param exactIdsMatch the exact ids match
	 * @return the string
	 */
	private String buildTypedCriteria(Class<?> type, String queryField, String filterValue, boolean exactIdsMatch) {
		StringBuilder jpql = new StringBuilder();
		if (type == String.class) {
			jpql.append("lower(").append(queryField).append(") like lower(").append(buildLikeFilterValue(filterValue)).append(") ");
		} else if (type == Integer.class || type == Long.class || type == Short.class || type == Float.class
				|| type == Double.class || type == BigDecimal.class) {
			if (exactIdsMatch && queryField.toLowerCase().endsWith(".id")) {
				jpql.append(queryField).append(" = ").append(filterValue).append(" ");
			} else {
				jpql.append("str(").append(queryField).append(") like ").append(buildLikeFilterValue(filterValue)).append(" ");
			}
		}

		return jpql.toString();
	}

	/**
	 * Builds the like filter value.
	 *
	 * @param inputValue the input value
	 * @return the string
	 */
	private String buildLikeFilterValue(String inputValue) {
		switch (getDefaultFilterMatchMode()) {
		case startsWith:
			return "'" + inputValue + "%'";
		case contains:
			return "'%" + inputValue + "%'";
		case exact:
			return "'" + inputValue + "'";
		default:
			throw new RuntimeException("not supported defaultFilterMatchMode: " + defaultFilterMatchMode);
		}
	}

	/**
	 * Gets the object alias.
	 *
	 * @param objectClass the object class
	 * @param objectIndex the object index
	 * @param isGlobalClause the is global clause
	 * @return the object alias
	 */
	private String getObjectAlias(Class<?> objectClass, int objectIndex, boolean isGlobalClause) {
		String suffix = isGlobalClause ? "_global_" : "_dynamic_";
		return objectClass.getSimpleName() + suffix + objectIndex;
	}

	/**
	 * Builds the criteria model list.
	 *
	 * @param filters the filters
	 * @return the list
	 */
	private List<CriteriaModel> buildCriteriaModelList(Map<String, String> filters) {
		List<CriteriaModel> criteriaList = new ArrayList<CriteriaModel>();
		String filterProperty = null;
		int counter = 0;
		for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
			try {
				filterProperty = it.next();
				String filterValue = filters.get(filterProperty);
				CriteriaModel model = new CriteriaModel(counter, filterProperty, filterValue);
				counter++;
				fillCriteriaModel(model, entityClass, filterProperty);
				criteriaList.add(model);
			} catch (Exception e) {
				logger.warn("can not filter by property='" + filterProperty + "' on class " 
							+ entityClass.getCanonicalName(), e);
			}
		}
		return criteriaList;
	}

	/**
	 * Add one or more properties for global filtering. Those properties will be
	 * searched on data model main object only
	 *
	 * @param filterProperties the filter properties
	 */
	public void addGlobalFilters(String... filterProperties) {
		if (filterProperties == null || filterProperties.length < 1) {
			return;
		}
		if (globalCriteriaModelList == null) {
			globalCriteriaModelList = new ArrayList<>();
		}
		for (String filterProperty : filterProperties) {
			int idx = globalCriteriaModelList.size() + 1;
			CriteriaModel model = new CriteriaModel(idx, filterProperty, null);
			fillCriteriaModel(model, entityClass, filterProperty);
			globalCriteriaModelList.add(model);
		}
	}

	/**
	 * Recursivelly fill of given CriteriaModel parameter. determine target
	 * property class eg. String|Integer ... If we have some collection property
	 * in properties path then save in model collection name, class, before and
	 * after path
	 *
	 * @param model the model
	 * @param inspectedClass the inspected class
	 * @param inspectedProperty the inspected property
	 */
	private void fillCriteriaModel(CriteriaModel model, Class<?> inspectedClass, String inspectedProperty) {
		try {
			String currProperty, nextProperty = null;
			if (inspectedProperty.contains(".")) {
				currProperty = inspectedProperty.substring(0, inspectedProperty.indexOf("."));
				nextProperty = inspectedProperty.substring(inspectedProperty.indexOf(".") + 1);
			} else {
				currProperty = inspectedProperty;
			}

			Field field = getFieldByName(inspectedClass, currProperty);
			Class<?> targetPropertyClass = field.getType();

			Class<?> genericType = null;
			if (Collection.class.isAssignableFrom(targetPropertyClass)) {
				Type t = field.getGenericType();
				if (t instanceof ParameterizedType) {
					genericType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
					System.out.println("do something with this class");
				}
			}

			if (genericType == null) {
				if (model.collectionName == null) {
					if (model.propertiesBeforeCollection != null && !model.propertiesBeforeCollection.isEmpty()) {
						model.propertiesBeforeCollection = model.propertiesBeforeCollection + ".";
					}
					model.propertiesBeforeCollection = model.propertiesBeforeCollection + currProperty;
				} else {
					if (model.propertiesAfterCollection != null && !model.propertiesAfterCollection.isEmpty()) {
						model.propertiesAfterCollection = model.propertiesAfterCollection + ".";
					}
					model.propertiesAfterCollection = model.propertiesAfterCollection + currProperty;
				}
			} else {
				model.collectionName = currProperty;
				model.collectionClass = genericType;
			}

			if (nextProperty == null) {
				if (nextProperty == null && genericType != null) {
					throw new RuntimeException("search on collection type as last property is not supported");
				}
				if (currProperty.equalsIgnoreCase("id")
						&& (targetPropertyClass == Integer.class || targetPropertyClass == Long.class)) {
					model.isIdSearch = true;
				}
				model.targetPropertyClass = targetPropertyClass;
			} else if (nextProperty != null && genericType == null) {
				fillCriteriaModel(model, targetPropertyClass, nextProperty);
			} else if (nextProperty != null && genericType != null) {
				fillCriteriaModel(model, genericType, nextProperty);
			}
		} catch (RuntimeException e) {
			logger.warn("can not determine class type for filter property='" + inspectedProperty + "' on class "
					+ inspectedClass.getCanonicalName());
		} catch (NoSuchFieldException e) {
			logger.warn("can not determine class type for filter property='" + inspectedProperty + "' on class "
					+ inspectedClass.getCanonicalName());
		}
	}

	/**
	 * Gets the field by name.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param fieldName
	 *          the field name
	 * @return the field by name
	 * @throws NoSuchFieldException
	 *           the no such field exception
	 */
	private Field getFieldByName(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() != null) {
				return getFieldByName(clazz.getSuperclass(), fieldName);
			} else {
				throw e;
			}
		}
	}

	/*
	 * We override this method because of a NullPointerException bug in primefaces
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#setRowIndex(int)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#setRowIndex(int)
	 */
	@Override
	public void setRowIndex(int rowIndex) {
		/*
		 * The following is in ancestor (LazyDataModel): this.rowIndex = rowIndex ==
		 * -1 ? rowIndex : (rowIndex % pageSize);
		 */
		if (rowIndex == -1 || getPageSize() == 0) {
			super.setRowIndex(-1);
		} else {
			super.setRowIndex(rowIndex % getPageSize());
		}
	}

	/**
	 * Gets the selected fields.
	 *
	 * @return the selected fields
	 */
	public String getSelectedFields() {
		return selectedFields;
	}

	/**
	 * Sets the selected fields.
	 *
	 * @param selectedFields the new selected fields
	 */
	public void setSelectedFields(String selectedFields) {
		this.selectedFields = selectedFields;
	}

	/**
	 * Gets the select field for count query.
	 *
	 * @return the select field for count query
	 */
	public String getSelectFieldForCountQuery() {
		return selectFieldForCountQuery;
	}

	/**
	 * Sets the select field for count query.
	 *
	 * @param selectFieldForCountQuery the new select field for count query
	 */
	public void setSelectFieldForCountQuery(String selectFieldForCountQuery) {
		this.selectFieldForCountQuery = selectFieldForCountQuery;
	}

	/**
	 * Gets the static from clause.
	 *
	 * @return the static from clause
	 */
	public String getStaticFromClause() {
		return staticFromClause;
	}

	/**
	 * Sets the static from clause.
	 *
	 * @param staticFromClause the new static from clause
	 */
	public void setStaticFromClause(String staticFromClause) {
		this.staticFromClause = staticFromClause;
	}

	/**
	 * Gets the default order by fields.
	 *
	 * @return the default order by fields
	 */
	public String getDefaultOrderByFields() {
		return defaultOrderByFields;
	}

	/**
	 * Ein oder mehrere Sortierfelder als JPQL Code. Die "order by" Anweisung
	 * selbst darf nicht enthalten sein, da sie durch diese Klasse hinzugefügt
	 * wird.
	 * <p/>
	 * Beispiel: "o.name, o.creaDate "
	 *
	 * @param defaultOrderByFields the new default order by fields
	 */
	public void setDefaultOrderByFields(String defaultOrderByFields) {
		this.defaultOrderByFields = defaultOrderByFields;
	}

	/**
	 * Gets the static where clause criteria.
	 *
	 * @return the static where clause criteria
	 */
	public String getStaticWhereClauseCriteria() {
		return staticWhereClauseCriteria;
	}

	/**
	 * Sets the static where clause criteria.
	 *
	 * @param staticWhereClauseCriteria the new static where clause criteria
	 */
	public void setStaticWhereClauseCriteria(String staticWhereClauseCriteria) {
		this.staticWhereClauseCriteria = staticWhereClauseCriteria;
	}

	/**
	 * Sets the static where clause criteria.
	 *
	 * @param staticWhereClauseCriteria the static where clause criteria
	 * @param staticWhereClauseCriteriaMap the static where clause criteria map
	 */
	public void setStaticWhereClauseCriteria(String staticWhereClauseCriteria,
			Map<String, Object> staticWhereClauseCriteriaMap) {
		this.staticWhereClauseCriteria = staticWhereClauseCriteria;
		this.staticWhereClauseCriteriaMap = staticWhereClauseCriteriaMap;
	}

	/**
	 * Gets the static from count clause.
	 *
	 * @return the static from count clause
	 */
	public String getStaticFromCountClause() {
		return staticFromCountClause;
	}

	/**
	 * Gets the static where clause criteria map.
	 *
	 * @return the static where clause criteria map
	 */
	public Map<String, Object> getStaticWhereClauseCriteriaMap() {
		return staticWhereClauseCriteriaMap;
	}

	/**
	 * Sets the static from count clause.
	 *
	 * @param staticFromCountClause the new static from count clause
	 */
	public void setStaticFromCountClause(String staticFromCountClause) {
		this.staticFromCountClause = staticFromCountClause;
	}

	/**
	 * Gets the safe sort comparator.
	 *
	 * @return the safe sort comparator
	 */
	public SafeSort<T> getSafeSortComparator() {
		return safeSortComparator;
	}

	/**
	 * Sets the safe sort comparator.
	 *
	 * @param safeSortComparator the new safe sort comparator
	 */
	public void setSafeSortComparator(SafeSort<T> safeSortComparator) {
		this.safeSortComparator = safeSortComparator;
	}
	
	/**
	 * Add MmnetDataModelListener which executes after data loaded and sorted
	 */
	public void addMmnetDataModelListener(MmnetDataModelListener<T> listener) {
		if (listener != null) {
			if (mmnetDataModelListeners == null) {
				mmnetDataModelListeners = new ArrayList<>();
			}
			mmnetDataModelListeners.add(listener);
		}
	}
	
	/**
	 * Copy of filters from last executed load method
	 */
	public Map<String, String> getSavedFilters() {
		return savedFilters;
	}

	/**
	 * The Class CriteriaModel.
	 */
	class CriteriaModel {
		
		/** The idx. */
		int idx;
		
		/** The full property name. */
		String fullPropertyName;
		
		/** The filter value. */
		String filterValue;
		
		/** The target property class. */
		Class<?> targetPropertyClass;
		
		/** The collection name. */
		String collectionName;
		
		/** The collection class. */
		Class<?> collectionClass;
		
		/** The properties before collection. */
		String propertiesBeforeCollection = "";
		
		/** The properties after collection. */
		String propertiesAfterCollection = "";
		
		/** The is id search. */
		boolean isIdSearch;

		/**
		 * Instantiates a new criteria model.
		 *
		 * @param idx the idx
		 * @param fullPropertyname the full propertyname
		 * @param filterValue the filter value
		 */
		public CriteriaModel(int idx, String fullPropertyname, String filterValue) {
			this.idx = idx;
			this.fullPropertyName = fullPropertyname;
			this.filterValue = filterValue;
		}

	}

	/**
	 * Checks if is distinct.
	 *
	 * @return true, if is distinct
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * Wenn dieses Feld true ist, wird hinter dem select des Statements ein
	 * distinct eingebaut. Dies ist bei left joins von 1:n Beziehungen erfoderlich
	 * damit sich die Datensätze nicht vervielfachen.
	 * <br>
	 * Allerdings ist dann keine Sortierung im LazyDataModel ohne weiteres möglich.
	 * <br>
	 * Alternativ zum distinct kan der JOIN auch mit einem fetch versehen werden. 
	 * In dem Fall werden die Ergebnisse auch nicht multipliziert.
	 *
	 * @param distinct the new distinct
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * Gets the filtered objects list.
	 *
	 * @return the filtered objects list
	 */
	public List<T> getFilteredObjectsList() {
		return filteredObjectsList;
	}

	/**
	 * Sets the filtered objects list.
	 *
	 * @param filteredObjectsList the new filtered objects list
	 */
	public void setFilteredObjectsList(List<T> filteredObjectsList) {
		this.filteredObjectsList = filteredObjectsList;
	}

	/**
	 * Adds the jpql query params.
	 *
	 * @param <X> the generic type
	 * @param query the query
	 * @param params the params
	 * @return the typed query
	 */
	private <X extends Object> TypedQuery<X> addJpqlQueryParams(TypedQuery<X> query, Map<String, Object> params) {
		if (params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				query.setParameter(key, params.get(key));
			}
		}

		return query;
	}

	/**
	 * Gets the default filter match mode.
	 *
	 * @return the default filter match mode
	 */
	public FilterMatchMode getDefaultFilterMatchMode() {
		return defaultFilterMatchMode;
	}

	/**
	 * Sets the default filter match mode.
	 *
	 * @param defaultFilterMatchMode the new default filter match mode
	 */
	public void setDefaultFilterMatchMode(FilterMatchMode defaultFilterMatchMode) {
		this.defaultFilterMatchMode = defaultFilterMatchMode;
	}

//	/**
//	 * The Enum FilterMatchMode.
//	 */
//	public static enum FilterMatchMode {
//		/** The starts with. */
//		startsWith, 
//		/** The contains. */
//		contains, 
//		/** The exact. */
//		exact;
//	}

	/**
	 * Ermittelt anhand des Lazy-Datamodels die Filterkriterien und erstellt die
	 * Gesamtliste.
	 *
	 * @return the list by lazy data model query
	 * since RET04 - arvato system | Technologies (NMT-MR) © 2013 | 08.03.2013
	 */
	@SuppressWarnings("unchecked")
	public List<T> getListByLazyDataModelQuery() {
		String fromClause = this.getStaticFromClause();
		String whereClause = this.getStaticWhereClauseCriteria();
		whereClause = whereClause != null ? whereClause : "";
		
		Map<String, Object> params = this.getStaticWhereClauseCriteriaMap();
		String orderBy = defaultOrderByFields != null ? " order by " + defaultOrderByFields : ""; 

		Query q = em.createQuery("select o from " + fromClause + " where 1=1 " + whereClause + orderBy);
		if (params != null) {
			for (String key : params.keySet()) {
				q.setParameter(key, params.get(key));
			}
		}
		return q.getResultList();
	}
}
