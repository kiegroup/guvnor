package org.drools.repository;

import junit.framework.TestCase;

public class IntegrationTest extends TestCase {

    
    public void testStateful() {
        RepositoryManager repo1 = RepositoryFactory.getStatefulRepository();
        RepositoryManager repo2 = RepositoryFactory.getStatefulRepository();
        
        RuleDef rule1 = new RuleDef("repo1", "Dsadsadsadsa");
        repo1.save(rule1);
        repo1.close();
        repo2.save(rule1);
        
//        RuleDef rule2 = repo2.loadRule("repo1", 1);
//        rule2.setContent("ABNBNBN");
//        repo2.save(rule2);
//        
//        rule1.setContent("bnmbmnbmn");
//        repo1.save(rule1);
        
        
    }
    
}
