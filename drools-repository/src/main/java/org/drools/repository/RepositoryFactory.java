package org.drools.repository;

import java.lang.reflect.Proxy;
import java.security.Principal;
import java.security.acl.Permission;

import org.drools.repository.db.RepoProxyHandler;


/**
 * This factory class provides instances of the repository in various flavours.
 * 
 * This is the place to start.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public final class RepositoryFactory {

    /**
     * The default repository. This is stateless, meaning that a continuous connection is not required.
     */
    public static RepositoryManager getRepository() {      
        return getRepo(null, false);
    }
    
    /**
     * This repository is stateful, meaning that a connection to the repository database is
     * expected to be available.
     * If an error occurs, the repository is invalid and will have to be created again to
     * "try again".
     * This version can have performance benefits in some cases.
     */
    public static RepositoryManager getStatefulRepository() {        
        return getRepo(null, true);        
    }
    
    /**
     * User this factory if you want to have audited, controlled access to the repository.
     * @param currentUser the user accessing the repository.
     * @param stateful If the session is to be stateful or not (generally false is ideal).
     */
    public static RepositoryManager getRepository(Principal currentUser, boolean stateful) {
        return getRepo(currentUser, stateful);
    }

    private static RepositoryManager getProxy(RepoProxyHandler handler) {
        RepositoryManager manager = (RepositoryManager) Proxy.newProxyInstance(RepositoryFactory.class.getClassLoader(), 
                               new Class[] {RepositoryManager.class},
                               handler);
        
        return manager;
    }
    
    
    private static RepositoryManager getRepo(Principal user, boolean stateful) {
        RepoProxyHandler handler = new RepoProxyHandler(stateful);
        handler.setCurrentUser(user);
        return getProxy(handler);
    }
    

}
