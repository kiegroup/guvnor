package org.drools.brms.server.files;

import org.apache.util.Base64;

import junit.framework.TestCase;

public class RestAPIServletTest extends TestCase {


	public void testUnpack() {
		String b42 = "BASIC " + new String( Base64.encode("user:pass".getBytes()) );
		RestAPIServlet serv = new RestAPIServlet();
		String[] d = serv.unpack(b42);
		assertEquals("user", d[0]);
		assertEquals("pass", d[1]);
	}

	public void testAllowUser() {
		RestAPIServlet serv = new RestAPIServlet();
		assertFalse(serv.allowUser(null));
		assertFalse(serv.allowUser(""));
		assertFalse(serv.allowUser("bgoo"));
		String b42 = "BASIC " + new String( Base64.encode("user:pass".getBytes()) );
		assertFalse(serv.allowUser(b42));
		b42 = "BASIC " + new String( Base64.encode("test:password".getBytes()) );
		assertTrue(serv.allowUser(b42));
	}

	public void testGetAPI() throws Exception {
		RestAPIServlet serv = new RestAPIServlet();
		assertNotNull(serv.getAPI());
	}

}
