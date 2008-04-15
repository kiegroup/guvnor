package org.drools.repository.remoteapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.drools.repository.remoteapi.Response.Binary;

import junit.framework.TestCase;

public class ResponseTest extends TestCase {

	public void testBinary() throws Exception {
		Binary b = new Response.Binary();
		ByteArrayInputStream in = new ByteArrayInputStream("abc".getBytes());
		b.stream = in;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		b.writeData(out);

		byte[] d = out.toByteArray();
		assertEquals(3, d.length);

		String s = new String(d);
		assertEquals("abc", s);

	}

}
