package org.drools.repository;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

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
            JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();
            
            //create a repo instance (startup)
            Repository repository = config.getJCRRepository(null);
            
            //create a session
            Session session;
            try {
                session = repository.login(new SimpleCredentials("alan_parsons", "password".toCharArray()));
                RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(session);
                //clear out and setup
                if (admin.isRepositoryInitialized()) {
                    admin.clearRulesRepository();
                }
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
