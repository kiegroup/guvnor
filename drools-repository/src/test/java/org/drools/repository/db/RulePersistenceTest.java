package org.drools.repository.db;

import java.util.List;
import java.util.Set;

import org.drools.repository.MetaData;
import org.drools.repository.RuleDef;

public class RulePersistenceTest extends PersistentCase {

    public void testStoreNewRuleDef() throws Exception {
        RepositoryImpl repo = getRepo();
        RuleDef def = repo.saveOrUpdateRule(new RuleDef("myRule", "A rule"));
        assertNotNull(def.getId());        
        def = repo.saveOrUpdateRule(new RuleDef("myRule2", "A rule2"));               
        def = new RuleDef("myRule3", "A rule3");
        def.addTag("tag1").addTag("tag2").addTag("HR");
        def = repo.saveOrUpdateRule(def);               
        assertNotNull(def.getId());
    }
    
    public void testListRules() {
        RepositoryImpl repo = getRepo();
        repo.saveOrUpdateRule(new RuleDef("blah", "blah"));
        List list = repo.listAllRules(true);
        assertTrue(list.size() > 0);        
    }
    
    public void testRetreieveRuleWithTags() {
        RepositoryImpl repo = getRepo();
        RuleDef newRule = new RuleDef("my rule", "content");
        newRule.addTag("HR").addTag("SALARY");
        repo.saveOrUpdateRule(newRule);
        
        RuleDef rule = repo.loadRule("my rule", 1);
        assertNotNull(rule);
        assertEquals("my rule", rule.getName());

        Set tags = rule.getTags();
        assertEquals(2, tags.size());
        String[] tagList = rule.listTags();
        assertTrue(tagList[0].equals("HR") || tagList[0].equals("SALARY"));
        
        List rules = repo.findRulesByTag("HR");
        assertTrue(rules.size() > 0);
        
    }
    
    public void testNewVersionOfRule() {
        RepositoryImpl repo = getRepo();
        
        RuleDef rule1 = new RuleDef("newVersionTest", "XXX");
        MetaData meta = new MetaData();
        meta.setCreator("Peter Jackson");
        rule1.setMetaData(meta);
        
        repo.saveOrUpdateRule(rule1);
        
        //?????repo.saveOrUpdateRule(rule1);
        //repo.merge(rule1);
        
        RuleDef rule2 = rule1.createNewVersion();
        rule2.addTag("PJ");
        
        repo.saveOrUpdateRule(rule2);
        
        
        RuleDef latest = repo.loadRule("newVersionTest", 2);
        assertEquals("Peter Jackson", latest.getMetaData().getCreator());
        
        List ruleHistory = repo.listRuleHistory("newVersionTest");
        assertEquals(2, ruleHistory.size());
        assertEquals(1, ((RuleDef) ruleHistory.get(0)).getVersionNumber());
        assertEquals(2, ((RuleDef) ruleHistory.get(1)).getVersionNumber());        
    }


    
    

    
}
