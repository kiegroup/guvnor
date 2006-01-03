package org.drools.repository.db;

import junit.framework.TestCase;

public class PersistentCase extends TestCase {

    public void testDummy() {
        //I need this as I often run all tests from within eclipse
        getRepo();
    }
    
    
    public RepositoryImpl getRepo() {
        RepositoryImpl repo = new RepositoryImpl();
        return repo;
    }
        
    
}
