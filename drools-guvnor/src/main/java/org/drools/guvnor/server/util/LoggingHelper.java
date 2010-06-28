package org.drools.guvnor.server.util;

import java.util.Date;

import org.drools.guvnor.client.rpc.LogEntry;
import org.slf4j.LoggerFactory;

/**
 * Collects messages for displaying in the GUI as well as providing a logger.
 * @author Michael Neale.
 *
 */
public class LoggingHelper {
    private final org.slf4j.Logger log;

    static final MessageList       messages = new MessageList();

    public static LogEntry[] getMessages() {
        return messages.getMessages();
    }

    public static void cleanLog() {
        messages.cleanEntry();
    }

    public static LoggingHelper getLogger(Class< ? > cls) {
        return new LoggingHelper( cls );
    }

    private LoggingHelper(Class< ? > cls) {
        log = LoggerFactory.getLogger( cls );
    }

    public void info(String message) {
        log.info( message );
        messages.add( message,
                      1 );
    }

    public void info(String message,
                     Throwable error) {
        log.info( message,
                  error );
        messages.add( message + " " + error.getMessage(),
                      1 );
    }

    public void debug(String message) {
        log.debug( message );
    }

    public void error(String message) {
        log.error( message );
        messages.add( message,
                      0 );
    }

    public void error(String message,
                      Throwable error) {
        log.error( message,
                   error );
        messages.add( message + " " + error.getMessage(),
                      0 );
    }

    public void warn(String message) {
        log.warn( message );
    }

}

class MessageList {
    static int MAX      = 500;
    LogEntry[] messages = new LogEntry[MAX];
    int        current  = 0;

    public MessageList() {

    }

    public synchronized void add(String message,
                                 int severity) {
        LogEntry entry = new LogEntry();
        entry.message = message;
        entry.timestamp = new Date();
        entry.severity = severity;

        if ( current == MAX ) {
            current = 0;
        }
        messages[current++] = entry;
    }

    public LogEntry[] getMessages() {
        return messages;
    }

    public synchronized void cleanEntry() {
        messages = new LogEntry[MAX];
    }
}
