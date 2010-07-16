/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
