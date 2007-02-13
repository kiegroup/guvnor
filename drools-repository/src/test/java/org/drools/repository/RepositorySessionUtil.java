package org.drools.repository;

import javax.jcr.Repository;
import javax.jcr.Session;

import junit.framework.Assert;

/**
 * This is a utility to simulate session behavior for the test suite.
 * @author Michael Neale
 *
 */
public class RepositorySessionUtil {

    private static ThreadLocal repo = new ThreadLocal();

    public static RulesRepository getRepository() {
        Object repoInstance = repo.get();
        if ( repoInstance == null ) {
            RepositoryConfigurator config = new RepositoryConfigurator();
            
            //create a repo instance (startup)
            Repository repository = config.createRepository();
            
            //create a session
            Session session;
            try {
                session = config.login( repository );
                //clear out and setup
                config.clearRulesRepository( session );
                config.setupRulesRepository( session );
                
                repoInstance = new RulesRepository( session );
                repo.set( repoInstance );                
            } catch ( Exception e) {
                Assert.fail("Unable to initialise repository :" + e.getMessage());
            }
            

            
        }
        return (RulesRepository) repoInstance;        
    }

}
