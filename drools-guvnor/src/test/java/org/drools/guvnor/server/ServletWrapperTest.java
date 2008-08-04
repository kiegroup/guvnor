package org.drools.guvnor.server;

import junit.framework.TestCase;

public class ServletWrapperTest extends TestCase {

	public void testMainService() {
		RepositoryServiceServlet serv = new RepositoryServiceServlet();
		serv.listPackages();
	}

	public void testSecurityService() {
		SecurityServiceServlet serv = new SecurityServiceServlet();
		serv.getCurrentUser();
	}

}
