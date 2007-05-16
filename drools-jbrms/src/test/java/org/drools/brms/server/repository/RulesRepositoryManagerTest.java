package org.drools.brms.server.repository;

import junit.framework.TestCase;

public class RulesRepositoryManagerTest extends TestCase {

    public void testDecorator() {
        RulesRepositoryManager dec = new RulesRepositoryManager();
        BRMSRepositoryConfiguration config = new BRMSRepositoryConfiguration();
        MockRepo repo = new MockRepo();
        config.repository = repo;
        dec.repositoryConfiguration = config;

        dec.create();
        
        assertNotNull(dec.getRepository().getSession());
        
        
        assertFalse(repo.session.loggedout);
        dec.close();
        assertTrue(repo.session.loggedout);
        
        
    }
    
    
}
