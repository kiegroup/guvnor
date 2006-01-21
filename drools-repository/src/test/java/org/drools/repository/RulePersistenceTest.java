package org.drools.repository;

import java.util.List;
import java.util.Set;

import org.drools.repository.MetaData;
import org.drools.repository.RuleDef;
import org.drools.repository.db.PersistentCase;

public class RulePersistenceTest extends PersistentCase {

    public void testStoreNewRuleDef() throws Exception {
        RepositoryManager repo = getRepo();
        RuleDef def = new RuleDef("myRule", "A rule"); 
            repo.save(def);
        assertNotNull(def.getId());        
        repo.save(new RuleDef("myRule2", "A rule2"));               
        
        def = new RuleDef("myRule3", "A rule3");
        def.addTag("tag1").addTag("tag2").addTag("HR");
        repo.save(def);        
        
        assertNotNull(def.getId());
        Long id = def.getId();
        
        def.setContent("new content");
        repo.save(def);
        
        def = repo.loadRule("myRule3", 1);
        assertEquals(id, def.getId());
        
        assertEquals("new content", def.getContent());
        assertEquals(3, def.getTags().size());
        def.removeTag("tag1");
        repo.save(def);
        def = repo.loadRule("myRule3", 1);
        
        assertEquals(2, def.getTags().size());
    }
        
    public void testRetreieveRuleWithTags() {
        RepositoryManager repo = getRepo();
        RuleDef newRule = new RuleDef("my rule RWT", "content");
        newRule.addTag("RWT").addTag("RWT2");
        repo.save(newRule);
        
        RuleDef rule = repo.loadRule("my rule RWT", 1);
        assertNotNull(rule);
        assertEquals("my rule RWT", rule.getName());

        Set tags = rule.getTags();
        assertEquals(2, tags.size());

        Tag firstTag = (Tag) rule.getTags().iterator().next();
        assertTrue(firstTag.getTag().equals("RWT") || firstTag.getTag().equals("RWT2"));
        
        List rules = repo.findRulesByTag("RWT");
        assertEquals(1, rules.size());
        rule = (RuleDef) rules.get(0);
        assertEquals("my rule RWT", rule.getName());
    }
    
    public void testRuleCopy() {
        RepositoryManager repo = getRepo();
        
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
    
    public void testRuleRuleSetHistory() {
        RuleSetDef rs = new RuleSetDef("rule history", null);
        RuleDef first = rs.addRule(new RuleDef("rh1", "xxxxx"));
        rs.addRule(new RuleDef("rh2", "xxxxx"));
        rs.addRule(new RuleDef("rh3", "xxxxx"));
        
        RepositoryManager repo = getRepo();
        repo.save(rs);
        
        rs = repo.loadRuleSet("rule history", 1);
        rs.createNewVersion("yeah");
        repo.save(rs);
        
        
        List list = repo.listRuleVersions("rh1");
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof RuleDef);
        
        RuleDef rule = (RuleDef) list.get(0);
        rule.addTag("XYZ");
        repo.save(rule);

        list = repo.listRuleVersions("rh1");
        assertEquals(2, list.size());
        
        rule.setContent("NEW CONTENT");
        repo.save(rule);
        
        rule.setContent("MORE NEW");
        repo.save(rule);
        
        list = repo.listRuleVersions("rh1");
        assertEquals(2, list.size());
        
        list = repo.listSaveHistory(rule); 
        assertEquals(2, list.size());

        rs = repo.loadRuleSet("rule history", 1);
        RuleDef firstLoaded = rs.findRuleByName("rh1");
        firstLoaded.setContent("new again");
        repo.save(rs);
        
        rs = repo.loadRuleSet("rule history", 1);
        RuleDef loadedAgain = rs.findRuleByName("rh1");
        
        assertEquals(firstLoaded.getContent(), loadedAgain.getContent());
        
    }
    
    public void testCheckinOut() {
        RuleDef rule = new RuleDef("checkin", "some rule");
        
        RepositoryManager repo = getRepo();
        repo.save(rule);
        
        repo.checkOutRule(rule, "u=Michael.Neale");
        rule = repo.loadRule("checkin", 1);
        
        assertEquals(true, rule.isCheckedOut());
        assertEquals("u=Michael.Neale", rule.getCheckedOutBy());
        
        try {
            repo.checkInRule(rule, "u=Rohit.Mathur");
        } catch (RepositoryException e) {
            assertNotNull(e.getMessage());
        }
        
        repo.checkInRule(rule, "u=Michael.Neale");
        assertEquals(false, rule.isCheckedOut());
        
    }
    
    
    


    
    

    
}
