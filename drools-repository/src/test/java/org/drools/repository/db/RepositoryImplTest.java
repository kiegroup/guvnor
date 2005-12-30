package org.drools.repository.db;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.drools.repository.RuleDef;

public class RepositoryImplTest extends PersistCase {

    public void testStoreNewRuleDef() throws Exception {
        RepositoryImpl repo = getRepo();
        RuleDef def = repo.addNewRule(new RuleDef("myRule", "A rule"));
        assertNotNull(def.getId());        
        def = repo.addNewRule(new RuleDef("myRule2", "A rule2"));               
        def = new RuleDef("myRule3", "A rule3");
        def.addTag("tag1").addTag("tag2").addTag("HR");
        def = repo.addNewRule(def);               
        assertNotNull(def.getId());
    }
    
    public void testListRules() {
        RepositoryImpl repo = getRepo();
        repo.addNewRule(new RuleDef("blah", "blah"));
        List list = repo.listAllRules(true);
        assertTrue(list.size() > 0);        
    }
    
    public void testRetreieveRuleWithTags() {
        RepositoryImpl repo = getRepo();
        RuleDef newRule = new RuleDef("my rule", "content");
        newRule.addTag("HR").addTag("SALARY");
        repo.addNewRule(newRule);
        
        RuleDef rule = repo.retrieveRule("my rule", 1);
        assertNotNull(rule);
        assertEquals("my rule", rule.getName());

        Set tags = rule.getTags();
        assertEquals(2, tags.size());
        String[] tagList = rule.listTags();
        assertTrue(tagList[0].equals("HR") || tagList[0].equals("SALARY"));
        
        List rules = repo.findRulesByTag("HR");
        assertTrue(rules.size() > 0);
        
    }
    
    

    
}
