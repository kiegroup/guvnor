/**
 * 
 */
package org.drools.brms.server.repository;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.RulesRepositoryException;

class MockRepositoryConfigurator implements JCRRepositoryConfigurator {

    
    
    public Repository getJCRRepository(String repositoryRootDirectory) {
        return new MockRepo();
    }

    public void setupRulesRepository(Session session) throws RulesRepositoryException {
        
    }
    
}