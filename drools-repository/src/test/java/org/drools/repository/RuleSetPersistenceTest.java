package org.drools.repository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.repository.AttachmentFile;
import org.drools.repository.MetaData;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetAttachment;
import org.drools.repository.RuleSetDef;
import org.drools.repository.RuleSetVersionInfo;
import org.drools.repository.db.PersistentCase;
import org.drools.repository.db.RepositoryImpl;

public class RuleSetPersistenceTest extends PersistentCase {

    public void testLoadSaveRuleSet() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale");
        meta.setRights("Unlimited");
        
        RuleSetDef def = new RuleSetDef("my ruleset", meta);
        def.addTag("ME");
        RepositoryImpl repo = getRepo();
        repo.save(def);
        
        RuleSetDef def2 = repo.loadRuleSet("my ruleset", 1);
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
        repo.save(ruleSet);
        
        RuleSetDef loaded = repo.loadRuleSet("Uber 1", 1);
        assertEquals(2, loaded.getRules().size());
        
    }
    
    public void testRuleSetWithAttachment() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale - Uber pimp");
        RuleSetDef ruleSet = new RuleSetDef("Attachmate", meta);
        
        RuleSetAttachment attachment = new RuleSetAttachment("decision-table", 
                                                             "my text file", 
                                                             "content".getBytes(), 
                                                             "file.txt");
        ruleSet.addAttachment(attachment);
        
        RepositoryImpl repo = getRepo();
        repo.save(ruleSet);
        
        RuleSetDef result = repo.loadRuleSet("Attachmate", 1);             
        assertEquals(1, result.getAttachments().size());
        RuleSetAttachment at2 = (RuleSetAttachment) result.getAttachments().iterator().next();
        assertEquals("file.txt", at2.getOriginalFileName());
    }
    
    public void testRuleSetWithVersionHistory() {
        RuleSetDef def = new RuleSetDef("WithHistory", null);
        Set history = new HashSet();
        RuleSetVersionInfo info = new RuleSetVersionInfo(1, "blah");
        
        history.add(info);
        RuleSetVersionInfo info2 = new RuleSetVersionInfo(2, "woo");
        history.add(info2);
        
        def.setVersionHistory(history);
        RepositoryImpl repo = getRepo();
        repo.save(def);
        
        RuleSetDef def2 = repo.loadRuleSet("WithHistory", 1);
        assertEquals(2, def2.getVersionHistory().size());
    }
    
    public void testNewVersioning() {
        RuleSetDef set = new RuleSetDef("InMemory", null);
        RuleDef def1 = new RuleDef("Rule1", "blah");
        RuleDef def2 = new RuleDef("Rule2", "blah2");
        
        def1.addTag("S").addTag("A");
        set.addRule(def1).addRule(def2);
        set.addAttachment(new RuleSetAttachment("x", "x", "x".getBytes(), "x"));
        
        assertEquals(2, set.getRules().size());
        assertEquals(1, def1.getVersionNumber());
        assertEquals(1, def2.getVersionNumber());
        assertEquals(1, set.getWorkingVersionNumber());
        
        //once we create a new version, we double the asset counts, one for each version
        set.createNewVersion("New version", "Draft");
        assertEquals(4, set.getRules().size());
        assertEquals(2, set.getAttachments().size());
        assertEquals(1, def1.getVersionNumber());
        
        //now check that the new version rules are kosher (tag wise)
        for ( Iterator iter = set.getRules().iterator(); iter.hasNext(); ) {
            RuleDef rule = (RuleDef) iter.next();
            if (rule.getVersionNumber() == 2) {
                assertEquals("New version", rule.getVersionComment());
                if (rule.getName().equals("Rule1")) {
                    assertEquals(2, rule.getTags().size());
                }
            }            
        }
        
        
        RepositoryImpl repo = getRepo();
        repo.save(set);
        
        //now when we load it, the filter only loads the workingVersion that we specify
        RuleSetDef loaded = repo.loadRuleSet("InMemory", 2);
        
        //now should have half as many as before, as old versions are not loaded.
        assertEquals(2, loaded.getRules().size());        
        assertEquals(1, loaded.getAttachments().size());
        
        //now check the version numbers and comment, use attachment as there is only one in set so easy..
        RuleSetAttachment att = (RuleSetAttachment) loaded.getAttachments().iterator().next();
        assertEquals(2, att.getVersionNumber());
        assertEquals("New version", att.getVersionComment());
        
        //now run it again, with OLD VERSION...
        loaded = repo.loadRuleSet("InMemory", 1);
        assertEquals(2, loaded.getRules().size());        
        assertEquals(1, loaded.getAttachments().size());

        //now the version number should be one
        att = (RuleSetAttachment) loaded.getAttachments().iterator().next();
        assertEquals(1, att.getVersionNumber());
        assertFalse("New version".equals(att.getVersionComment()));
        
        
        
    }
    
}
