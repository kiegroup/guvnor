package org.drools.repository.test;

import java.io.File;
import java.util.Iterator;
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

    public void testRulePackageItem() throws Exception {
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
            
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
            
            //test that it is following the head revision                        
            ruleItem1.updateLhs("new lhs");
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
            assertEquals("new lhs", ((RuleItem)rules.get(0)).getLhs());
                        
            RuleItem ruleItem2 = this.rulesRepository.addRule("test rule 2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());  
            
            Iterator it = rulesRepository.listPackages();
            assertTrue(it.hasNext());
            
            RulePackageItem pack = (RulePackageItem) it.next();
            assertEquals("testRulePackage", pack.getName());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testAddRuleRuleItemBoolean() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1, true);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
            
            //test that it is following the head revision                        
            ruleItem1.updateLhs("new lhs");
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
            assertEquals("new lhs", ((RuleItem)rules.get(0)).getLhs());
            
            RuleItem ruleItem2 = this.rulesRepository.addRule("test rule 2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());
            
            //test not following the head revision
            rulePackageItem1.removeAllRules();
            RuleItem ruleItem3 = this.rulesRepository.addRule("test rule 3", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem3, false);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test lhs content", ((RuleItem)rules.get(0)).getLhs());
                                    
            ruleItem3.updateLhs("new lhs");
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test lhs content", ((RuleItem)rules.get(0)).getLhs());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testAddFunctionFunctionItem() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            rulePackageItem1.addFunction(functionItem1);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
            
            //test that it is following the head revision                        
            functionItem1.updateContent("new content");
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
            assertEquals("new content", ((FunctionItem)functions.get(0)).getContent());
                        
            FunctionItem functionItem2 = this.rulesRepository.addFunction("test function 2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(2, functions.size());                          
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testAddFunctionFunctionItemBoolean() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            rulePackageItem1.addFunction(functionItem1, true);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
            
            //test that it is following the head revision                        
            functionItem1.updateContent("new content");
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
            assertEquals("new content", ((FunctionItem)functions.get(0)).getContent());
            
            FunctionItem functionItem2 = this.rulesRepository.addFunction("test function 2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(2, functions.size());
            
            //test not following the head revision
            rulePackageItem1.removeAllFunctions();
            FunctionItem functionItem3 = this.rulesRepository.addFunction("test function 3", "test content");
            
            rulePackageItem1.addFunction(functionItem3, false);
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test content", ((FunctionItem)functions.get(0)).getContent());
                                    
            functionItem3.updateContent("new content");
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test content", ((FunctionItem)functions.get(0)).getContent());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetFunctions() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
                        
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            rulePackageItem1.addFunction(functionItem1);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
                                  
            FunctionItem functionItem2 = this.rulesRepository.addFunction("test function 2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(2, functions.size());            
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetRules() {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
                        
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
                                  
            RuleItem ruleItem2 = this.rulesRepository.addRule("test rule 2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());            

    }

    public void testToString() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
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
            
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
                                    
            ruleItem1.updateLhs("new lhs");
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
            assertEquals("new lhs", ((RuleItem)rules.get(0)).getLhs());
            
            RuleItem ruleItem2 = this.rulesRepository.addRule("test rule 2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            //remove the rule, make sure the other rule in the pacakge stays around
            rulePackageItem1.removeRule(ruleItem1);
            rules = rulePackageItem1.getRules();
            assertEquals(1, rules.size());
            assertEquals("test rule 2", ((RuleItem)rules.get(0)).getName());
            
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
            
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("test rule", ((RuleItem)rules.get(0)).getName());
            
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
    
    public void testRemoveFunction() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            rulePackageItem1.addFunction(functionItem1);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
                                    
            functionItem1.updateContent("new content");
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
            assertEquals("new content", ((FunctionItem)functions.get(0)).getContent());
            
            FunctionItem functionItem2 = this.rulesRepository.addFunction("test function 2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            //remove the function, make sure the other function in the package stays around
            rulePackageItem1.removeFunction(functionItem1);
            functions = rulePackageItem1.getFunctions();
            assertEquals(1, functions.size());
            assertEquals("test function 2", ((FunctionItem)functions.get(0)).getName());
            
            //remove the function that is following the head revision, make sure the package is now empty
            rulePackageItem1.removeFunction(functionItem2);
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(0, functions.size());
                        
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testRemoveAllFunctions() {
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            rulePackageItem1.addFunction(functionItem1);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("test function", ((FunctionItem)functions.get(0)).getName());
            
            rulePackageItem1.removeAllFunctions();
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(0, functions.size());            
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetPrecedingVersion() {
        //not bothering to implement this test since it is pretty much covered by the RuleItemTestCase   
    }
    
    public void testGetSucceedingVersion() {
        //not bothering to implement this test since it is pretty much covered by the RuleItemTestCase   
    } 
    
    public void testGetSuccessorVersionsIterator() {
        //This is covered by the test in RuleItemTestCase - all functionality under test
        // resides in the common subclass, VersionableItem
    }
    
    public void testGetPredecessorVersionsIterator() {
        //This is covered by the test in RuleItemTestCase - all functionality under test
        // resides in the common subclass, VersionableItem
    }
    
    public void testGetTitle() {
        //This is covered by the test in RuleItemTestCase - all functionality under test
        // resides in the common subclass, VersionableItem
    }
    
    public void testGetContributor() {
        //This is covered by the test in RuleItemTestCase - all functionality under test
        // resides in the common subclass, VersionableItem        
    }
    
    public void testGetFormat() {        
        try {
            RulePackageItem rulePackageItem1 = this.rulesRepository.createRulePackage("testRulePackage");
            assertNotNull(rulePackageItem1);
            assertEquals("Rule Package", rulePackageItem1.getFormat());    
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }        
}