package org.drools.repository;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;

import junit.framework.Assert;

import org.drools.repository.RulesRepository;

public class RepositorySession {

    private static ThreadLocal repo = new ThreadLocal();

    public static RulesRepository getRepository() {
        Object repoInstance = repo.get();
        if ( repoInstance == null ) {
            RepositoryConfigurator config = new RepositoryConfigurator(true);
            
            try {
                repoInstance = new RulesRepository( config.login() );
            } catch ( LoginException e ) {
                Assert.fail( "Unable to login " + e.getMessage() );
            } catch ( RepositoryException e ) {
                Assert.fail("Repo exception when logging in: " + e.getMessage());
            }
            repo.set( repoInstance );
            
        }
        return (RulesRepository) repoInstance;        
    }

}
