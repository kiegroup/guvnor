package org.drools.brms.server.security;

import junit.framework.TestCase;

public class SecurityServiceImplTest extends TestCase {

    public void testLogin() throws Exception {
        SecurityServiceImpl impl = new SecurityServiceImpl();
        assertTrue(impl.login( "XXX", null ));
    }    
    
    public void testUser() throws Exception {
        SecurityServiceImpl impl = new SecurityServiceImpl();
        assertNotNull(impl.getCurrentUser());
    }
    
}
