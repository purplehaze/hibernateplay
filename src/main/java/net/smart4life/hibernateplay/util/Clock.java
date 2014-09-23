package net.smart4life.hibernateplay.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <H3>Clock</H3>
 * <p>
 * Stoppuhr zum Messen einer Dauer.
 * </p>
 * 
 * <DT><B>Project: </B>
 * <DD>
 * </DD>
 * 
 * <DT><B>Company: </B>
 * <DD>
 * arvato systems - Bertelsmann</DD> <DT><B>Copyright: </B>
 * <DD>
 * (c) 2005 arvato systems - Bertelsmann</DD> <DT><B>Date: </B>
 * <DD>
 * 08.11.2005</DD> <DT><B>last Update:</B>
 * <DD>
 * 08.11.2005, Frank Martens</DD>
 * 
 * @author Frank Martens
 * @version 2.0
 */
public class Clock {

	private Date startZeit = null;
	private Date endeZeit = null;
	private Date zwischenMessung = null;
	private String clockName;

	public Clock() {
		startZeit = new Date();
	}

	/**
	 * Create Clock object with a given Name which we can use in log outputs
	 * 
	 * @param clockName
	 */
	public Clock(String clockName) {
		this();
		this.clockName = clockName;
	}

	/**
	 * <p>
	 * Dauer seit dem Start der Clock.
	 * </p>
	 * 
	 * @return
	 * @author Jan 23, 2009 Frank Martens - arvato systems (NMM-R)
	 */
	public long getDauerMillisFromStart() {
		return (System.currentTimeMillis() - startZeit.getTime());
	}

	/** Gibt die Dauer zum Startzeitpunkt zurück. */
	public String getDauer() {
		endeZeit = new Date();
		String ret = "";
		long dauer = (endeZeit.getTime() - startZeit.getTime());
		ret = millis2HourMinSecMillisString(dauer);
		return ret;
	}

	/** Gibt die Dauer zum letzten Messzeitpunkt zurück */
	private String getDauerLetzteMessung() {
		endeZeit = new Date();
		String ret = "";
		long dauer = (endeZeit.getTime() - zwischenMessung.getTime());
		ret = millis2HourMinSecMillisString(dauer);
		return ret;
	}

	public String toString() {
		endeZeit = new Date();
		String ret = "\n\t# Start: " + startZeit.toString();
		if (endeZeit != null)
			ret += "\n\t# Messzeitpunkt: " + endeZeit.toString();
		if (zwischenMessung != null)
			ret += "\n\t# Dauer letzte Messung    --> jetzt: " + getDauerLetzteMessung();
		ret += "\n\t# Dauer Start der Messung --> jetzt: " + getDauer();
		zwischenMessung = new Date();
		return ret;
	}

	/**
	 * Return formatted time from start until now with additionalMemo and clock name if given
	 * 
	 * @param additionalMemo
	 * @return
	 */
	public String toStringCompact(String additionalMemo) {
		endeZeit = new Date();
		StringBuilder ret = new StringBuilder("Clock(");
		if (clockName != null) {
			ret.append(clockName);
		}
		if (additionalMemo != null) {
			ret.append(" - " + additionalMemo);
		}
		ret.append(") Dauer von start = ");
		long dauer = (endeZeit.getTime() - startZeit.getTime());
		ret.append(millis2HourMinSecMillisString(dauer));
		return ret.toString();
	}

	private String millis2HourMinSecMillisString(long dauer){
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:ss.SSS");
		result = sdf.format(new Date(dauer));
		return  result;
	}

}
