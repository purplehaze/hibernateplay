package net.smart4life.hibernateplay.util;

/**
 * Sort Criteria to use with SafeSort
 * @see de.arvatosystems.util.sort.SafeSort
 * You have to override abstract getValue(T comparableObject) method
 * to provide values which should be compared/sorted.
 * For example if your T(ex. Company) class has getCompanyName() method
 * which you will use as sort criteria then you override so:
 * 
 * public Object getValue(Company comparableObject){
 * 		return comparableObject.getCompanyName();
 * }
 * 
 * you can write more complex business rules here as well
 * 
 * public Object getValue(Company comparableObject){
 * 		List<Address> addresses = comparableObject.getAddresses();
 * 		if(addresses.....){
 * 			return addresses.get(1).getStreet();
 * 		} else {
 * 			return addresses.get(0).getStreet();
 * 		}
 * }
 * 
 * @author ILIN02
 *
 * @param <T>
 */
public abstract class SortCrit<T extends Object> {
	private SortDirection sortDirection;
	
	public SortCrit(SortDirection sortDirection){
		this.sortDirection = sortDirection;
	}
	
	public abstract Object getValue(T comparableObject);
	
	Object getValueNullSave(T comparableObject){
		Object val = null;
		try{
			val = getValue(comparableObject);
		}catch(NullPointerException e){
			
		}
		return val;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	@Override
	public String toString() {
		return "SortCrit [sortDirection=" + sortDirection + "]";
	}
}