package org.drools.brms.server.util;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.drools.repository.RulesRepositoryAdministrator;

/**
 * This is only to be used for testing, eg in hosted mode, or unit tests.
 * 
 * @author Michael Neale
 */
public class TestEnvironmentSessionHelper {


    public static Session testSession;
    
    public static Session getSession() throws Exception {
        return getSession(true);
    }
    
    public static Session getSession(boolean erase) throws Exception {
        if (testSession == null) {
            JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();
            Repository repo = RepositoryManager.getJCRRepository( config );
            testSession = repo.login(
                                                                     new SimpleCredentials("alan_parsons", "password".toCharArray()));

            RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(testSession);
            if (erase && admin.isRepositoryInitialized()) {
                
                admin.clearRulesRepository( );
            } 
            config.setupRulesRepository( testSession );
        }
        return testSession;
        
    }
        
    
}
