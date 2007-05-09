package org.drools.brms.server.repository;

import org.drools.brms.server.util.UserIdentity;
import org.drools.repository.RulesRepository;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * This enhances the BRMS repository for lifecycle management.
 * @author Michael Neale
 */
@Scope(ScopeType.EVENT)
@AutoCreate
@Name("repository")
public class RulesRepositoryManager {

    @In 
    BRMSRepositoryConfiguration repositoryConfiguration;
    
    private RulesRepository repository;
    
    @In
    UserIdentity currentUser;
    
    @Create
    public void create() {
        String userName = currentUser.getUserName();
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
