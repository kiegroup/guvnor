package org.drools.repository.db;

import org.drools.repository.MetaData;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetDef;

public class RuleSetPersistenceTest extends PersistentCase {

    public void testLoadSaveRuleSet() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale");
        meta.setRights("Unlimited");
        
        RuleSetDef def = new RuleSetDef("my ruleset", meta);
        def.addTag("ME");
        RepositoryImpl repo = getRepo();
        repo.saveOrUpdateRuleSet(def);
        
        RuleSetDef def2 = repo.loadRuleSet("my ruleset");
        assertEquals("my ruleset", def2.getName());
        assertEquals("Michael Neale", def2.getMetaData().getCreator());
        assertEquals(1, def2.getTags().size());
        
    }
    
    public void testRuleSetWithRules() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale");
        RuleSetDef ruleSet = new RuleSetDef("Uber 1", meta);
        ruleSet.addRule(new RuleDef("UBER1Rule", "This is a rule"));
        RuleDef def = new RuleDef("UBER2Rule", "this is also a rule");
        def.addTag("HR").addTag("BUS");
        ruleSet.addRule(def);
        ruleSet.addTag("HR");
        
        RepositoryImpl repo = getRepo();
        repo.saveOrUpdateRuleSet(ruleSet);
        
        RuleSetDef loaded = repo.loadRuleSet("Uber 1");
        assertEquals(2, loaded.getRules().size());
        
    }
    
}
