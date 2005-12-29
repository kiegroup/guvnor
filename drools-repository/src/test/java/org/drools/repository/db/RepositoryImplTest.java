package org.drools.repository.db;

import java.util.List;

import org.drools.repository.RuleDef;

import junit.framework.TestCase;

public class RepositoryImplTest extends TestCase {

    public void testStoreNewRuleDef() throws Exception {
        RepositoryImpl repo = getRepo();
        RuleDef def = repo.addNewRule("myRule", "A rule", "some comment");
        assertNotNull(def.getId());        
        System.out.println(def.getId());
        

        def = repo.addNewRule("myRule2", "A rule2", "some comment");               
        System.out.println(def.getId());

        def = repo.addNewRule("myRule3", "A rule3", "some comment");               
        assertNotNull(def.getDateCreated());
    }
    
    public void testListRules() {
        RepositoryImpl repo = getRepo();
        repo.addNewRule("blah", "blah", "yeah");
        List list = repo.listRules(true);
        assertTrue(list.size() > 0);        
    }
    
    
    
    private RepositoryImpl getRepo() {
        RepositoryImpl repo = new RepositoryImpl();
        return repo;
    }
    
    
}
