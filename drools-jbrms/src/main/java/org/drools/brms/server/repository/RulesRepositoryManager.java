package org.drools.brms.server.repository;

import org.drools.repository.RulesRepository;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * This enhances the BRMS repository for lifecycle management.
 * @author Michael Neale
 */
@Scope(ScopeType.EVENT)
@AutoCreate
@Name("repository")
public class RulesRepositoryManager {

    private static String READ_ONLY_USER = "anonymous";
    
    @In 
    BRMSRepositoryConfiguration repositoryConfiguration;
    
    private RulesRepository repository;
    
    
    @Create
    public void create() {
        String userName = READ_ONLY_USER;
        if (Contexts.isApplicationContextActive()) {
            userName = Identity.instance().getUsername();
        }
        if (userName == null) {
            userName = READ_ONLY_USER;
        }        
        repository = new RulesRepository(repositoryConfiguration.newSession(userName) );
    }
    
    @Unwrap
    public RulesRepository getRepository() {
        return repository;
    }
    
    @Destroy
    void close() {
        repository.logout();
    }
    

    
}
