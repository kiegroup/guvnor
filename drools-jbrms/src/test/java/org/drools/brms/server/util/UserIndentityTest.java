package org.drools.brms.server.util;

import junit.framework.TestCase;

public class UserIndentityTest extends TestCase {

    public void testDefault() {
        UserIdentity id = new UserIdentity();
        id.setUserName( null );
        assertEquals("default", id.getUserName());
        
        id = new UserIdentity();
        id.setUserName( "foo" );
        assertEquals("foo", id.getUserName());
    }
    
}
