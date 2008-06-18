package org.drools.brms.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * For showing a log in the GUI (last X messages).
 * @author Michael Neale
 */
public class LogEntry implements IsSerializable {

	public int severity;
	public String message;
	public Date timestamp;

}
