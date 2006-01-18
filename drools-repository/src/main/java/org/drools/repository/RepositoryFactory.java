package org.drools.repository;

import java.lang.reflect.Proxy;

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
        RepoProxyHandler handler = new RepoProxyHandler();        
        RepositoryManager manager = getProxy( handler );
        return manager;        
    }
    
    /**
     * This repository is stateful, meaning that a connection to the repository database is
     * expected to be available.
     * If an error occurs, the repository is invalid and will have to be created again to
     * "try again".
     * This version can have performance benefits in some cases.
     */
    public static RepositoryManager getStatefulRepository() {        
        RepoProxyHandler handler = new RepoProxyHandler(true);        
        RepositoryManager manager = getProxy( handler );
        return manager;        
    }

    private static RepositoryManager getProxy(RepoProxyHandler handler) {
        RepositoryManager manager = (RepositoryManager) Proxy.newProxyInstance(RepositoryFactory.class.getClassLoader(), 
                               new Class[] {RepositoryManager.class},
                               handler);
        return manager;
    }
    
    

}
