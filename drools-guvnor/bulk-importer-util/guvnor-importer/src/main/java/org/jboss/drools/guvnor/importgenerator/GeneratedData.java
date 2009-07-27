package org.jboss.drools.guvnor.importgenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Provides any generated input to inject into the output templates
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 *
 */
public class GeneratedData {
  /** formatter for the drools-guvnor xml timestamp */
	public static SimpleDateFormat XMLDATEFORMAT  = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH:mm:ss.SSSZ", Locale.UK);
	
	/** generated uuid for new objects within the drools-guvnor jcr repository */
  public static String generateUUID(){
		return UUID.randomUUID().toString();
	}
  
  /** @returns the current time in timestamp format. ie ie. 2009-06-09T19:06:44.783+01:00 */
	public static String getTimestamp(){
		Date now=new Date();
		//ie. 2009-06-09T19:06:44.783+01:00
		StringBuffer sb=new StringBuffer(XMLDATEFORMAT.format(now));
		sb.insert(sb.length()-2, ":");
		return sb.toString();
	}
}
