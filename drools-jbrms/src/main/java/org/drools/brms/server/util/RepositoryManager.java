package org.drools.brms.server.util;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpSession;

import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.RulesRepository;

/**
 * Currently a collection of hacked utilities for setting up the repo.
 * TODO: repository itself should be injected into the context,
 * and session should be created on each request.
 * 
 * Also, repository creation should be distinct from instantiation.
 * 
 * @author Michael Neale
 *
 */
public class RepositoryManager {

    /**
     * The shared repository instance. This could be bound to JNDI eventually.
     */
    public static Repository repository;
    
    /** This will create a new repository instance (should only happen once after startup) */
    private Session initialiseRepo(JCRRepositoryConfigurator config) throws LoginException,
                                                                 RepositoryException {
        Session session = getJCRRepository(config).login(
                                           new SimpleCredentials("alan_parsons", "password".toCharArray()));


        
        config.setupRulesRepository( session );
        return session;
    }
    
    public synchronized static Repository getJCRRepository(JCRRepositoryConfigurator config) {
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
    
    /**
     * Pull or create the repository from session.
     * If it is not found, it will create one and then bind it to the session.
     */
    public RulesRepository getRepositoryFrom(HttpSession session) {
//        Object obj = session.getAttribute( "drools.repository" );
//        if ( obj == null ) {
//            obj = createRuleRepositoryInstance();
//            session.setAttribute( "drools.repository",
//                                  obj );
//        }
//        return (RulesRepository) obj;
        return createRuleRepositoryInstance();
    }
    
}
