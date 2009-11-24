package org.drools.guvnor.server.util;

import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.drools.guvnor.client.rpc.LogEntry;
import org.drools.guvnor.server.ServiceImplementation;

/**
 * Collects messages for displaying in the GUI as well as providing a logger.
 * @author Michael Neale.
 *
 */
public class LoggingHelper {

	static final MessageList messages = new MessageList();

	public static LogEntry[] getMessages() {
		return messages.getMessages();
	}

	public static void cleanLog() {
		messages.cleanEntry();
	}
	
	public static Logger getLogger(Class cls) {

		Logger l = Logger.getLogger( cls );

		l.addAppender(new Appender() {

			public void addFilter(Filter arg0) {
			}

			public void clearFilters() {
			}

			public void close() {
			}

			public void doAppend(LoggingEvent e) {
				LogEntry ev = new LogEntry();
				ev.message = e.getRenderedMessage();
				ev.timestamp = new Date();
				if (e.getLevel().equals(Level.ERROR)) {
					ev.severity = 0;
					messages.add(ev);
				} else if (e.getLevel().equals(Level.INFO)) {
					ev.severity = 1;
					messages.add(ev);
				}
			}

			public ErrorHandler getErrorHandler() {
				return null;
			}

			public Filter getFilter() {
				return null;
			}

			public Layout getLayout() {
				return null;
			}

			public String getName() {
				return "guilogger";
			}

			public boolean requiresLayout() {
				return false;
			}

			public void setErrorHandler(ErrorHandler arg0) {
			}

			public void setLayout(Layout arg0) {
			}

			public void setName(String arg0) {
			}

		});
		return l;

	}

}

class MessageList {
	static int MAX = 500;
	LogEntry[] messages = new LogEntry[MAX];
	int current = 0;
	
	public MessageList() {

	}

	public synchronized void add(LogEntry e) {
		if (current == MAX) {
			current = 0;
		}
		messages[current++] = e;
	}

	public LogEntry[] getMessages() {
		return messages;
	}
	
	public synchronized void cleanEntry() {
		messages = new LogEntry[MAX];
	}

}

