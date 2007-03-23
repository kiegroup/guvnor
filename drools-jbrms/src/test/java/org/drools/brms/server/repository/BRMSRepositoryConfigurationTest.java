package org.drools.brms.server.repository;

import junit.framework.TestCase;

public class BRMSRepositoryConfigurationTest extends TestCase {

    public void testConfiguration() {
        
        BRMSRepositoryConfiguration config = new BRMSRepositoryConfiguration();
        config.create();
        assertNotNull(config.newSession("foo"));
        assertNotSame(config.newSession("foo"), config.newSession("foo"));
    }
    
}
