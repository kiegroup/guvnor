package org.drools.repository.db;

import org.drools.repository.RuleDef;

import junit.framework.TestCase;

public class RepositoryImplTest extends TestCase {

    public void testStoreNewRuleDef() throws Exception {
        RepositoryImpl repo = new RepositoryImpl();
        RuleDef def = repo.addNewRule("myRule", "A rule", "some comment");
        assertNotNull(def.getId());
    }
    
}
