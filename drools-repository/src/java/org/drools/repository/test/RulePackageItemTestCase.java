package org.drools.repository.test;

import java.io.File;
import java.util.List;

import org.drools.repository.*;

import junit.framework.TestCase;

public class RulePackageItemTestCase extends TestCase {
    private RulesRepository rulesRepository;
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rulesRepository = new RulesRepository(true);        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        this.rulesRepository.logout();
    }

    public void testRulePackageItem() {
        try {
            //calls constructor
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            assertNotNull(rulePackageItem1);
            assertEquals("testRulePackage", rulePackageItem1.getName());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
        
        //try constructing with node of wrong type
        try {
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            DslItem dslItem = rulesRepository.addDslFromFile(dslFile1);
            RulePackageItem rulePackageItem2 = new RulePackageItem(this.rulesRepository, dslItem.getNode());
            fail("Exception not thrown for node of type: " + dslItem.getNode().getPrimaryNodeType().getName());
        }
        catch(RulesRepositoryException e) {
            //this is good
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testAddRuleRuleItem() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
            
            //test that it is following the head revision                        
            File drlFile2 = new File("./src/java/org/drools/repository/test/test_data/drl3.drl");
            ruleItem1.updateContentFromFile(drlFile2);
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
            assertEquals("package org.drools.examples", ((RuleItem)rules.get(0)).getContent());
            
            File drlFile3 = new File("./src/java/org/drools/repository/test/test_data/drl2.drl");
            RuleItem ruleItem2 = this.rulesRepository.addRuleFromFile(drlFile3);
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());            
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testAddRuleRuleItemBoolean() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            rulePackageItem1.addRule(ruleItem1, true);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
            
            //test that it is following the head revision                        
            File drlFile2 = new File("./src/java/org/drools/repository/test/test_data/drl3.drl");
            ruleItem1.updateContentFromFile(drlFile2);
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
            assertEquals("package org.drools.examples", ((RuleItem)rules.get(0)).getContent());
            
            File drlFile3 = new File("./src/java/org/drools/repository/test/test_data/drl2.drl");
            RuleItem ruleItem2 = this.rulesRepository.addRuleFromFile(drlFile3);
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());
            
            //test not following the head revision
            rulePackageItem1.removeAllRules();
            RuleItem ruleItem3 = this.rulesRepository.addRuleFromFile(drlFile2);
            
            rulePackageItem1.addRule(ruleItem1, false);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("package org.drools.examples", ((RuleItem)rules.get(0)).getContent());
                                    
            ruleItem1.updateContentFromFile(drlFile1);
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("package org.drools.examples", ((RuleItem)rules.get(0)).getContent());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetRules() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
                                  
            File drlFile3 = new File("./src/java/org/drools/repository/test/test_data/drl2.drl");
            RuleItem ruleItem2 = this.rulesRepository.addRuleFromFile(drlFile3);
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());            
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testToString() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            rulePackageItem1.addRule(ruleItem1);
            assertNotNull(rulePackageItem1.toString());                        
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testRemoveRule() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
                                    
            File drlFile2 = new File("./src/java/org/drools/repository/test/test_data/drl3.drl");
            ruleItem1.updateContentFromFile(drlFile2);
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
            assertEquals("package org.drools.examples", ((RuleItem)rules.get(0)).getContent());
            
            File drlFile3 = new File("./src/java/org/drools/repository/test/test_data/drl2.drl");
            RuleItem ruleItem2 = this.rulesRepository.addRuleFromFile(drlFile3);
            rulePackageItem1.addRule(ruleItem2);
            
            //remove the rule, make sure the other rule int the pacakge stays around
            rulePackageItem1.removeRule(ruleItem1);
            rules = rulePackageItem1.getRules();
            assertEquals(1, rules.size());
            assertEquals("drl2.drl", ((RuleItem)rules.get(0)).getName());
            
            //remove the rule that is following the head revision, make sure the pacakge is now empty
            rulePackageItem1.removeRule(ruleItem2);
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(0, rules.size());
                        
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testRemoveAllRules() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("drl1.drl", ((RuleItem)rules.get(0)).getName());
            
            rulePackageItem1.removeAllRules();
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(0, rules.size());            
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }
}
