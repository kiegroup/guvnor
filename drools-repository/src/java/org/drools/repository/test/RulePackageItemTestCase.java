package org.drools.repository.test;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.drools.repository.*;

import junit.framework.TestCase;

public class RulePackageItemTestCase extends TestCase {
//    private RulesRepository rulesRepository;
//    
//    protected void setUp() throws Exception {
//        super.setUp();
//        getRepo() = new RulesRepository(true);        
//    }
//
//    protected void tearDown() throws Exception {
//        super.tearDown();
//        getRepo().logout();
//    }

    public void testRulePackageItem() throws Exception {
        RulesRepository repo = getRepo();
        try {
            
            //calls constructor
            RulePackageItem rulePackageItem1 = repo.createRulePackage("testRulePackage");
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
            DslItem dslItem = repo.addDslFromFile(dslFile1);
            RulePackageItem rulePackageItem2 = new RulePackageItem(repo, dslItem.getNode());
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

    private RulesRepository getRepo() {
        return RepositorySession.getRepository();
    }

    public void testAddRuleRuleItem() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testAddRuleRuleItem");
            
            RuleItem ruleItem1 = getRepo().addRule("test rule", "test lhs content", "test rhs content");
            
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
                        
            RuleItem ruleItem2 = getRepo().addRule("test rule 2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());  
            
            Iterator it = getRepo().listPackages();
            assertTrue(it.hasNext());
            
            RulePackageItem pack = (RulePackageItem) it.next();
            assertEquals("testRulePackage", pack.getName());

    }

    public void testAddRuleRuleItemBoolean() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testAddRuleRuleItemBoolean");
            
            RuleItem ruleItem1 = getRepo().addRule("testAddRuleRuleItemBoolean", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1, true);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testAddRuleRuleItemBoolean", ((RuleItem)rules.get(0)).getName());
            
            //test that it is following the head revision                        
            ruleItem1.updateLhs("new lhs");
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testAddRuleRuleItemBoolean", ((RuleItem)rules.get(0)).getName());
            assertEquals("new lhs", ((RuleItem)rules.get(0)).getLhs());
            
            RuleItem ruleItem2 = getRepo().addRule("testAddRuleRuleItemBoolean2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());
            
            //test not following the head revision
            rulePackageItem1.removeAllRules();
            RuleItem ruleItem3 = getRepo().addRule("testAddRuleRuleItemBoolean3", "test lhs content", "test rhs content");
            
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

    public void testAddFunctionFunctionItem() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testAddFunctionFunctionItem");
            
            FunctionItem functionItem1 = getRepo().addFunction("test function", "test content");
            
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
                        
            FunctionItem functionItem2 = getRepo().addFunction("test function 2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(2, functions.size());                          

    }

    public void testAddFunctionFunctionItemBoolean() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testAddFunctionFunctionItemBoolean");
            
            FunctionItem functionItem1 = getRepo().addFunction("testAddFunctionFunctionItemBoolean", "test content");
            
            rulePackageItem1.addFunction(functionItem1, true);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("testAddFunctionFunctionItemBoolean", ((FunctionItem)functions.get(0)).getName());
            
            //test that it is following the head revision                        
            functionItem1.updateContent("new content");
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("testAddFunctionFunctionItemBoolean", ((FunctionItem)functions.get(0)).getName());
            assertEquals("new content", ((FunctionItem)functions.get(0)).getContent());
            
            FunctionItem functionItem2 = getRepo().addFunction("testAddFunctionFunctionItemBoolean2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(2, functions.size());
            
            //test not following the head revision
            rulePackageItem1.removeAllFunctions();
            FunctionItem functionItem3 = getRepo().addFunction("testAddFunctionFunctionItemBoolean3", "test content");
            
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

    public void testGetFunctions() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testGetFunctions");
                        
            FunctionItem functionItem1 = getRepo().addFunction("testGetFunctions", "test content");
            
            rulePackageItem1.addFunction(functionItem1);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("testGetFunctions", ((FunctionItem)functions.get(0)).getName());
                                  
            FunctionItem functionItem2 = getRepo().addFunction("testGetFunctions2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(2, functions.size());            

    }
    
    public void testGetRules() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testGetRules");
                        
            RuleItem ruleItem1 = getRepo().addRule("testGetRules", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testGetRules", ((RuleItem)rules.get(0)).getName());
                                  
            RuleItem ruleItem2 = getRepo().addRule("testGetRules2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(2, rules.size());            

    }

    public void testToString() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testToString");
            
            RuleItem ruleItem1 = getRepo().addRule("testToString", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            assertNotNull(rulePackageItem1.toString());                        

    }
    
    public void testRemoveRule() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testRemoveRule");
            
            RuleItem ruleItem1 = getRepo().addRule("testRemoveRule", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule", ((RuleItem)rules.get(0)).getName());
                                    
            ruleItem1.updateLhs("new lhs");
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule", ((RuleItem)rules.get(0)).getName());
            assertEquals("new lhs", ((RuleItem)rules.get(0)).getLhs());
            
            RuleItem ruleItem2 = getRepo().addRule("testRemoveRule2", "test lhs content", "test rhs content");
            rulePackageItem1.addRule(ruleItem2);
            
            //remove the rule, make sure the other rule in the pacakge stays around
            rulePackageItem1.removeRule(ruleItem1);
            rules = rulePackageItem1.getRules();
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule2", ((RuleItem)rules.get(0)).getName());
            
            //remove the rule that is following the head revision, make sure the pacakge is now empty
            rulePackageItem1.removeRule(ruleItem2);
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(0, rules.size());

    }
    
    public void testRemoveAllRules() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testRemoveAllRules");
            
            RuleItem ruleItem1 = getRepo().addRule("testRemoveAllRules", "test lhs content", "test rhs content");
            
            rulePackageItem1.addRule(ruleItem1);
            
            List rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testRemoveAllRules", ((RuleItem)rules.get(0)).getName());
            
            rulePackageItem1.removeAllRules();
            
            rules = rulePackageItem1.getRules();
            assertNotNull(rules);
            assertEquals(0, rules.size());            

    }    
    
    public void testRemoveFunction() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testRemoveFunction");
            
            FunctionItem functionItem1 = getRepo().addFunction("testRemoveFunction", "test content");
            
            rulePackageItem1.addFunction(functionItem1);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("testRemoveFunction", ((FunctionItem)functions.get(0)).getName());
                                    
            functionItem1.updateContent("new content");
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("testRemoveFunction", ((FunctionItem)functions.get(0)).getName());
            assertEquals("new content", ((FunctionItem)functions.get(0)).getContent());
            
            FunctionItem functionItem2 = getRepo().addFunction("testRemoveFunction2", "test content");
            rulePackageItem1.addFunction(functionItem2);
            
            //remove the function, make sure the other function in the package stays around
            rulePackageItem1.removeFunction(functionItem1);
            functions = rulePackageItem1.getFunctions();
            assertEquals(1, functions.size());
            assertEquals("testRemoveFunction2", ((FunctionItem)functions.get(0)).getName());
            
            //remove the function that is following the head revision, make sure the package is now empty
            rulePackageItem1.removeFunction(functionItem2);
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(0, functions.size());

    }
    
    public void testRemoveAllFunctions() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testRemoveAllFunctions");
            
            FunctionItem functionItem1 = getRepo().addFunction("testRemoveAllFunctions", "test content");
            
            rulePackageItem1.addFunction(functionItem1);
            
            List functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(1, functions.size());
            assertEquals("testRemoveAllFunctions", ((FunctionItem)functions.get(0)).getName());
            
            rulePackageItem1.removeAllFunctions();
            
            functions = rulePackageItem1.getFunctions();
            assertNotNull(functions);
            assertEquals(0, functions.size());            

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
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testGetFormat");
            assertNotNull(rulePackageItem1);
            assertEquals("Rule Package", rulePackageItem1.getFormat());    

    }        
}