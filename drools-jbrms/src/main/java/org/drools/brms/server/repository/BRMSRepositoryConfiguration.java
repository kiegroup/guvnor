package org.drools.brms.server.repository;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.drools.repository.RulesRepositoryAdministrator;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

/**
 * This startup class manages the JCR repository, sets it up if necessary.
 * @author Michael Neale
 */
@Scope(ScopeType.APPLICATION)
@Startup
@Name("repositoryConfiguration")
public class BRMSRepositoryConfiguration {

    JCRRepositoryConfigurator configurator = new JackrabbitRepositoryConfigurator();
    String repositoryHomeDirectory = null;
    
    Repository repository;
    
    @Create
    public void create() {      
        repository = configurator.getJCRRepository( repositoryHomeDirectory );
        Session sessionForSetup = newSession("admin");
        create( sessionForSetup );
    }


    void create(Session sessionForSetup) {
                
        RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(sessionForSetup);
        if (!admin.isRepositoryInitialized()) {
            configurator.setupRulesRepository( sessionForSetup );
        }
        sessionForSetup.logout();
    }


    public void setHomeDirectory(String home) {
        this.repositoryHomeDirectory = home;
    }
    
    public void setRepositoryConfigurator(String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            Class cls = Class.forName( clazz );
            this.configurator = (JCRRepositoryConfigurator) cls.newInstance();
    }
    
     
    /**
     * This will create a new Session, based on the current user.
     * @return
     */
    public Session newSession(String userName) {
        
        try {
            return repository.login( new SimpleCredentials(userName, "password".toCharArray()) );
        } catch ( LoginException e ) {
            throw new RulesRepositoryException( "Unable to login to JCR backend." );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }
    
    
}
