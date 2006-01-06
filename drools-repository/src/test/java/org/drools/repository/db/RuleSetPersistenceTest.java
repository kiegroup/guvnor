package org.drools.repository.db;

import java.util.HashSet;
import java.util.Set;

import org.drools.repository.AttachmentFile;
import org.drools.repository.MetaData;
import org.drools.repository.RuleDef;
import org.drools.repository.RuleSetAttachment;
import org.drools.repository.RuleSetDef;
import org.drools.repository.RuleSetVersionInfo;

public class RuleSetPersistenceTest extends PersistentCase {

    public void testLoadSaveRuleSet() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale");
        meta.setRights("Unlimited");
        
        RuleSetDef def = new RuleSetDef("my ruleset", meta);
        def.addTag("ME");
        RepositoryImpl repo = getRepo();
        repo.save(def);
        
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
        repo.save(ruleSet);
        
        RuleSetDef loaded = repo.loadRuleSet("Uber 1");
        assertEquals(2, loaded.getRules().size());
        
    }
    
    public void testRuleSetWithAttachment() {
        MetaData meta = new MetaData();
        meta.setCreator("Michael Neale - Uber pimp");
        RuleSetDef ruleSet = new RuleSetDef("Attachmate", meta);
        
        RuleSetAttachment attachment = new RuleSetAttachment("decision-table", "my text file");
        attachment.addFile(new AttachmentFile("blah,blah".getBytes(), "text", "nothing.csv"));
        attachment.addFile(new AttachmentFile("boo,boo".getBytes(), "binary", "smeg.xls"));
        ruleSet.setAttachment(attachment);
        
        RepositoryImpl repo = getRepo();
        repo.save(ruleSet);
        
        RuleSetDef result = repo.loadRuleSet("Attachmate");
        assertNotNull(result);
        assertEquals("decision-table", result.getAttachment().getTypeOfAttachment());
        assertEquals(2, result.getAttachment().getAttachments().size());
    }
    
    public void testRuleSetWithVersionHistory() {
        RuleSetDef def = new RuleSetDef("WithHistory", null);
        Set history = new HashSet();
        RuleSetVersionInfo info = new RuleSetVersionInfo("Michael", 1, "blah");
        
        history.add(info);
        RuleSetVersionInfo info2 = new RuleSetVersionInfo("Michael", 2, "woo");
        history.add(info2);
        
        def.setVersionHistory(history);
        RepositoryImpl repo = getRepo();
        repo.save(def);
        
        RuleSetDef def2 = repo.loadRuleSet("WithHistory");
        assertEquals(2, def2.getVersionHistory().size());
    }
    
}
