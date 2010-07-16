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

package org.drools.guvnor.server.util;

import junit.framework.TestCase;

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
