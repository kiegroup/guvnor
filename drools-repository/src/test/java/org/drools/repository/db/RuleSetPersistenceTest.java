package org.drools.repository.db;

import org.drools.repository.MetaData;
import org.drools.repository.RuleSetDef;



import junit.framework.TestCase;

public class RuleSetPersistenceTest extends PersistCase {

    public void testLoadSaveRuleSet() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale");
        meta.setRights("Unlimited");
        
        RuleSetDef def = new RuleSetDef("my ruleset", meta);
        def.addTag("ME");
        RepositoryImpl repo = getRepo();
        repo.saveRuleSet(def);
        
        RuleSetDef def2 = repo.retrieveRuleSet("my ruleset", 1);
        assertEquals("my ruleset", def2.getName());
        assertEquals("Michael Neale", def2.getMetaData().getCreator());
        assertEquals(1, def2.getTags().size());
        
    }
    
}
