package org.drools.guvnor.server.util;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.drools.guvnor.client.rpc.LogEntry;

public class LoggingHelperTest extends TestCase {


	public void testMessages() throws Exception {
		final MessageList ml = new MessageList();

		ml.add("heh",0);

		assertEquals("heh", ml.getMessages()[0].message);

		for (int i = 0; i < 10000; i++) {
			ml.add("entry "+i, 0);
		}

		LogEntry[] results = ml.getMessages();
		for (int i = 0; i < results.length; i++) {
			assertNotNull("" + i, results[i]);
			assertFalse(results[i].message.equals("heh"));
		}

		Thread t1 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 10000; i++) {
					ml.add("thread1 "+1,0);
				}
				LogEntry[] results = ml.getMessages();
				for (int i = 0; i < results.length; i++) {
					assertNotNull("" + i, results[i]);
					assertFalse(results[i].message.equals("heh"));
				}
			}
		});


		Thread t2 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 10000; i++) {
					ml.add("thread2 "+1,0);
				}
				LogEntry[] results = ml.getMessages();
				for (int i = 0; i < results.length; i++) {
					assertNotNull("" + i, results[i]);
					assertFalse(results[i].message.equals("heh"));
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
			assertFalse(results[i].message.equals("heh"));
		}

	}

}
