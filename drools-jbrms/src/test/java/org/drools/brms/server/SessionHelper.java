package org.drools.brms.server;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.drools.brms.server.util.RepositoryManager;
import org.drools.repository.RepositoryConfigurator;

/**
 * A simple utility to create a single session for the unit tests.
 * This is not used by the servlet test, only by all the other ones.
 */
public class SessionHelper {

    public static Session testSession;
    
    public static Session getSession() throws Exception {
        if (testSession == null) {
            RepositoryConfigurator config = new RepositoryConfigurator();
            Repository repo = RepositoryManager.getJCRRepository( config );
            testSession = config.login( repo );
            config.clearRulesRepository( testSession );
            config.setupRulesRepository( testSession );
        }
        return testSession;
        
    }
    
}
