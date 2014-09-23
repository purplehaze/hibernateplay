package net.smart4life.hibernateplay.util;


/**
 * Sort Direction ASC|DESC for use with SafeSort's SortCrit
 * 
 * @see de.arvatosystems.util.sort.SafeSort
 * @see de.arvatosystems.util.sort.SortCrit
 * 
 * @author ILIN02
 *
 */
public enum SortDirection {
	ASC, DESC;

	/**
	 * Liefert den Enum-Wert mit <code>Name = value</code>
	 * @param value
	 * @return
	 * SortDirection
	 * @author RET04 - arvato system | Technologies (NMT-MR) © 2013 | 09.12.2013
	 */
	public static SortDirection valueOfObject(Object value) {
		if (value != null) {
			for (SortDirection tmp : values() ) {
				if (tmp.name().equals(value.toString())) {
					return tmp;
				}
			}
		} 
		return ASC;
	}

}
