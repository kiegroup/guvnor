package org.drools.brms.server;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.drools.repository.RepositoryConfigurator;

public class SessionHelper {

    public static Session testSession;
    
    public static Session getSession() throws Exception {
        if (testSession == null) {
            RepositoryConfigurator config = new RepositoryConfigurator();
            Repository repo = config.createRepository();
            testSession = config.login( repo );
            config.clearRulesRepository( testSession );
            config.setupRulesRepository( testSession );
        }
        return testSession;
        
    }
    
}
