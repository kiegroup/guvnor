package org.drools.brms.server.repository;

import org.drools.brms.server.util.UserIdentity;

import junit.framework.TestCase;

public class RulesRepositoryDecoratorTest extends TestCase {

    public void testDecorator() {
        RulesRepositoryManager dec = new RulesRepositoryManager();
        BRMSRepositoryConfiguration config = new BRMSRepositoryConfiguration();
        MockRepo repo = new MockRepo();
        config.repository = repo;
        dec.repositoryConfiguration = config;
        dec.currentUser = new UserIdentity();
        dec.create();
        
        assertNotNull(dec.getRepository().getSession());
        
        
        
        assertFalse(repo.session.loggedout);
        dec.close();
        assertTrue(repo.session.loggedout);
        
        
    }
    
    
}
