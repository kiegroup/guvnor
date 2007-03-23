package org.drools.brms.server.repository;

import junit.framework.TestCase;

public class RulesRepositoryDecoratorTest extends TestCase {

    public void testDecorator() {
        RulesRepositoryManager dec = new RulesRepositoryManager();
        BRMSRepositoryConfiguration config = new BRMSRepositoryConfiguration();
        config.create();
        
        dec.repositoryConfiguration = config;
        dec.userName = "test";
        dec.create();
        
        assertNotNull(dec.getRepository().getSession());
        assertTrue(dec.getRepository().getSession().isLive());
        dec.close();
        assertFalse(dec.getRepository().getSession().isLive());
        
        
    }
    
}
