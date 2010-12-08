package org.drools.guvnor.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ISO8601 {

	private final static SimpleDateFormat ISO8601Format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
	
	/**
	 * This turns a Date into a String following the ISO8601 specification.
	 *
	 * @param date
	 * @return
	 */
	public static String format( Calendar cal ) {
		String text = null;
		if (cal!=null) {
			Date date = cal.getTime();
			text = ISO8601Format.format(date);
			if (text.length() < 29) {
				//add the colon if it is not there. 
				text = text.substring(0, 26) + ":" + text.substring(26);
			}
		}
		return text;
	}
}
