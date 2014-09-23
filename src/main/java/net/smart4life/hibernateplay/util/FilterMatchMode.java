package net.smart4life.hibernateplay.util;

public enum FilterMatchMode {
	startsWith, 
	contains, 
	exact,
	endsWith;
	
	/**
	 * Liefert den Enum-Wert mit <code>Name = value</code>
	 * @param value
	 * @return
	 * FilterMatchMode
	 * @author RET04 - arvato system | Technologies (NMT-MR) © 2013 | 09.12.2013
	 */
	public static FilterMatchMode valueOfObject(Object value) {
		if (value != null) {
			for (FilterMatchMode tmp : values() ) {
				if (tmp.name().equals(value.toString())) {
					return tmp;
				}
			}
		} 
		return contains;
	}

}
