package org.drools.guvnor.server.util;

import org.apache.log4j.Logger;
import org.drools.guvnor.client.rpc.LogEntry;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.server.util.MessageList;

import junit.framework.TestCase;

public class LoggingHelperTest extends TestCase {

	public void testAppender() {
		Logger l = LoggingHelper.getLogger();
		assertNotNull(l.getAppender("guilogger"));
	}

	public void testMessages() throws Exception {
		final MessageList ml = new MessageList();

		final LogEntry e = new LogEntry();
		e.message = "heh";
		ml.add(e);

		assertEquals(e, ml.getMessages()[0]);

		for (int i = 0; i < 10000; i++) {
			ml.add(new LogEntry());
		}

		LogEntry[] results = ml.getMessages();
		for (int i = 0; i < results.length; i++) {
			assertNotNull("" + i, results[i]);
			assertFalse(results[i] == e);
		}

		Thread t1 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 10000; i++) {
					ml.add(new LogEntry());
				}
				LogEntry[] results = ml.getMessages();
				for (int i = 0; i < results.length; i++) {
					assertNotNull("" + i, results[i]);
					assertFalse(results[i] == e);
				}
			}
		});


		Thread t2 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 10000; i++) {
					ml.add(new LogEntry());
				}
				LogEntry[] results = ml.getMessages();
				for (int i = 0; i < results.length; i++) {
					assertNotNull("" + i, results[i]);
					assertFalse(results[i] == e);
				}
			}
		});

		t1.start();
		t2.start();
		t1.join();
		t2.join();

		results = ml.getMessages();
		for (int i = 0; i < results.length; i++) {
			assertNotNull("" + i, results[i]);
			assertFalse(results[i] == e);
		}

	}

}
