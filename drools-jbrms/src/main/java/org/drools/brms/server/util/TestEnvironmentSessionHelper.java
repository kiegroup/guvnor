package org.drools.brms.server.util;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpSession;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryAdministrator;

/**
 * This is only to be used for testing, eg in hosted mode, or unit tests.
 * 
 * @author Michael Neale
 */
public class TestEnvironmentSessionHelper {


    public static Session testSession;
    public static Repository repository;
    
    /** This will create a new repository instance (should only happen once after startup) */
    private Session initialiseRepo(JCRRepositoryConfigurator config) throws LoginException,
                                                                 RepositoryException {
        Session session = getJCRRepository(config).login(
                                           new SimpleCredentials("alan_parsons", "password".toCharArray()));


        
        config.setupRulesRepository( session );
        return session;
    }
    
    synchronized static Repository getJCRRepository(JCRRepositoryConfigurator config) {
        if (repository == null) {
            repository = config.getJCRRepository(null);
        }
        return repository;
    }
    
    /** Initialse the repository, set it up if it is brand new */
    public RulesRepository createRuleRepositoryInstance() {
        
        JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();

        try {
            
            Session session;
            if (repository == null) {
                //this should only need to be done on server startup
                long start = System.currentTimeMillis();
                session = initialiseRepo( config );
                System.out.println("initialise repo time: " + (System.currentTimeMillis() - start));
            }  else {
                //ok this is probably fast enough to do with each request I think
                long start = System.currentTimeMillis();
                session = repository.login(
                                                   new SimpleCredentials("alan_parsons", "password".toCharArray()));

                System.out.println("login repo time: " + (System.currentTimeMillis() - start));
                
            }
            
            return new RulesRepository( session );
        } catch ( LoginException e ) {
            throw new RuntimeException( e );
        } catch ( RepositoryException e ) {
            throw new RuntimeException( "Unable to get a repository: " + e.getMessage() );
        }
    }
    
    
    public static Session getSession() throws Exception {
        return getSession(true);
    }
    
    public static Session getSession(boolean erase) throws Exception {
        if (testSession == null) {
            JCRRepositoryConfigurator config = new JackrabbitRepositoryConfigurator();
            Repository repo = getJCRRepository( config );
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
