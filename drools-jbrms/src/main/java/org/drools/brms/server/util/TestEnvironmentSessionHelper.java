package org.drools.brms.server.util;

import java.io.File;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpSession;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryAdministrator;

/**
 * This is only to be used for testing, eg in hosted mode, or unit tests.
 * 
 * @author Michael Neale
 */
public class TestEnvironmentSessionHelper {


    public static Repository repository;
    
    
    public static Session getSession() throws Exception {
        return getSession(true);
    }
    
    public static synchronized Session getSession(boolean erase) throws Exception {
        if (repository == null) {
            
            if (erase) {
                File repoDir = new File("repository");
                System.out.println("DELETE test repo dir: " + repoDir.getAbsolutePath());
                RepositorySessionUtil.deleteDir( repoDir );
                System.out.println("TEST repo dir deleted.");
            }
            
            JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();
            repository = config.getJCRRepository(null);;
        
            Session testSession = repository.login(
                                                                     new SimpleCredentials("alan_parsons", "password".toCharArray()));

            RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(testSession);
            if (erase && admin.isRepositoryInitialized()) {
                
                admin.clearRulesRepository( );
            } 
            config.setupRulesRepository( testSession );
            return testSession;
        } else {
            return repository.login(
                             new SimpleCredentials("alan_parsons", "password".toCharArray()));            
        }
        
    }
        
    
}
