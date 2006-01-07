package org.drools.repository;

import java.util.List;
import java.util.Set;

import org.drools.repository.MetaData;
import org.drools.repository.RuleDef;
import org.drools.repository.db.PersistentCase;
import org.drools.repository.db.RepositoryImpl;

public class RulePersistenceTest extends PersistentCase {

    public void testStoreNewRuleDef() throws Exception {
        RepositoryImpl repo = getRepo();
        RuleDef def = repo.save(new RuleDef("myRule", "A rule"));
        assertNotNull(def.getId());        
        def = repo.save(new RuleDef("myRule2", "A rule2"));               
        def = new RuleDef("myRule3", "A rule3");
        def.addTag("tag1").addTag("tag2").addTag("HR");
        def = repo.save(def);               
        assertNotNull(def.getId());
    }
    

    
    public void testRetreieveRuleWithTags() {
        RepositoryImpl repo = getRepo();
        RuleDef newRule = new RuleDef("my rule", "content");
        newRule.addTag("HR").addTag("SALARY");
        repo.save(newRule);
        
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
    
    public void testRuleCopy() {
        RepositoryImpl repo = getRepo();
        
        RuleDef rule1 = new RuleDef("newVersionTest", "XXX");
        rule1.addTag("HR").addTag("BOO");
        
        MetaData meta = new MetaData();
        meta.setCreator("Peter Jackson");
        rule1.setMetaData(meta);
        
        repo.save(rule1);
        RuleDef ruleCopy  = (RuleDef) rule1.copy();
        assertEquals(null, ruleCopy.getId());
        assertEquals(2, ruleCopy.getTags().size());
        assertEquals("Peter Jackson", ruleCopy.getMetaData().getCreator());
        
    }


    
    

    
}
