package org.drools.repository.test;

import org.drools.repository.RulesRepository;

public class RepositorySession {

    private static ThreadLocal repo = new ThreadLocal();

    public static RulesRepository getRepository() {
        Object repoInstance = repo.get();
        if ( repoInstance == null ) {
            repoInstance = new RulesRepository( true );
            repo.set( repoInstance );
            
        }
        return (RulesRepository) repoInstance;        
    }

}
