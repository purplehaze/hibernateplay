package net.smart4life.hibernateplay.util;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.*;

/**
 * Use SafeSort to sort Collection of Objects in type safe way. Create SafeSort
 * object with some Collection in constructor as parameter. Then add one or more
 * SortCrit sort criteria with overriden getValue(...) method and then call
 * getSortedList() on SafeSort object to get your sorted Collection.
 * 
 * ex:
 * 
 * List<FooModel> mySortedModels = new SafeSort<FooModel>(models) .addCriteria(
 * new SortCrit<FooModel>(SortDirection.DESC) {
 * 
 * @Override public Object getValue(FooModel comparableObject) { return
 *           comparableObject.getName(); } } ).addCriteria( new
 *           SortCrit<FooModel>(SortDirection.ASC) {
 * @Override public Object getValue(FooModel comparableObject) { return
 *           comparableObject.getCreatedAt(); } } ).getSortedList();
 * 
 * 
 *
 * @author ILIN02
 * 
 * @param <T>
 */
public class SafeSort<T> {
	private List<SortCrit<T>> sortCriterias;
	private Collection<T> inputCollection;
	private Collator deCollator;

	public SafeSort() {
		this.inputCollection = new ArrayList<T>();
		this.sortCriterias = new ArrayList<SortCrit<T>>();
		try {
			deCollator = new RuleBasedCollator(((RuleBasedCollator) Collator.getInstance(Locale.GERMANY)).getRules());
		} catch (Exception e) {
			throw new RuntimeException("Can not create RuleBasedCollator.");
		}
	}
	
	public SafeSort(Collection<T> inputCollection) {
		this();
		setInputCollection(inputCollection);
	}

	public SafeSort(List<T> inputList, List<SortCrit<T>> sortCriterias) {
		this(inputList);
		this.sortCriterias.addAll(sortCriterias);
	}

	public SafeSort<T> addCriteria(SortCrit<T> sortCriteria) {
		sortCriterias.add(sortCriteria);
		return this;
	}

	public List<T> getSortedList() {
		return sort();
	}
	
	public void setInputCollection(Collection<T> inputCollection) {
		this.inputCollection = inputCollection;
	}

	private List<T> sort() {
		List<T> sortedList = new ArrayList<T>(inputCollection);
		if (sortCriterias != null && sortCriterias.size() > 0) {
			Collections.sort(sortedList, new Comparator<T>() {
				public int compare(T o1, T o2) {
					int returnValue = 0;
					for (SortCrit<T> sortCrit : sortCriterias) {
						Object val1 = sortCrit.getValueNullSave(o1);
						Object val2 = sortCrit.getValueNullSave(o2);

						if (val1 != null && val2 != null) {
							returnValue = compareValues(val1, val2, sortCrit.getSortDirection());
						} else if (val1 == null && val2 != null) {
							returnValue = sortCrit.getSortDirection() == SortDirection.ASC ? -1 : 1;
						} else if (val1 != null && val2 == null) {
							returnValue = sortCrit.getSortDirection() == SortDirection.ASC ? 1 : -1;
						}

						if (returnValue != 0) {
							break;
						}
					}

					return returnValue;
				}

				private int compareValues(Object val1, Object val2, SortDirection sortDirection) {
					int returnValue = 0;
					if (!val1.getClass().equals(val2.getClass())) {
						throw new IllegalArgumentException("comparable values must have the same class: " + " val1.class="
								+ val1.getClass().getCanonicalName() + ", val2.class=" + val2.getClass().getCanonicalName());
					}
					if (val1 instanceof Integer) {
						returnValue = ((Integer) val1).compareTo((Integer) val2);
					} else if (val1 instanceof Short) {
						returnValue = ((Short) val1).compareTo((Short) val2);
					} else if (val1 instanceof Float) {
						returnValue = ((Float) val1).compareTo((Float) val2);
					} else if (val1 instanceof Double) {
						returnValue = ((Double) val1).compareTo((Double) val2);
					} else if (val1 instanceof BigDecimal) {
						returnValue = ((BigDecimal) val1).compareTo((BigDecimal) val2);
					} else if (val1 instanceof Date) {
						returnValue = ((Date) val1).compareTo((Date) val2);
					} else {
						returnValue = deCollator.compare(val1.toString().toUpperCase(Locale.GERMANY),
								val2.toString().toUpperCase(Locale.GERMANY));
					}

					if (returnValue != 0 && sortDirection == SortDirection.DESC) {
						returnValue = -returnValue;
					}
					return returnValue;
				}
			});
		}

		return sortedList;
	}

}
