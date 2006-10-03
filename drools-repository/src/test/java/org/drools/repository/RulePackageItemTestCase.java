package org.drools.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.UnsupportedRepositoryOperationException;

import org.drools.repository.*;

import junit.framework.TestCase;

public class RulePackageItemTestCase extends TestCase {

    public void testRulePackageItem() throws Exception {
        RulesRepository repo = getRepo();
        try {
            
            //calls constructor
            RulePackageItem rulePackageItem1 = repo.createRulePackage("testRulePackage", "desc");
            assertNotNull(rulePackageItem1);
            assertEquals("testRulePackage", rulePackageItem1.getName());
            
            Iterator it = getRepo().listPackages();
            assertTrue(it.hasNext());

            while (it.hasNext()) {
                RulePackageItem pack = (RulePackageItem) it.next();
                if (pack.getName().equals( "testRulePackage" )) {
                    return;
                }
                
               
            }
            fail("should have picked up the testRulePackage but didnt.");
            
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception: " + e);
        }
        
        //try constructing with node of wrong type
        try {            
            DslItem dslItem = repo.addDsl("testRulePackageItem", "content");
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

    public void testLoadRulePackageItem() {

        RulePackageItem rulePackageItem = getRepo().createRulePackage("testLoadRuleRuleItem", "desc");

        rulePackageItem = getRepo().loadRulePackage("testLoadRuleRuleItem");
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItem", rulePackageItem.getName());
        
        assertEquals("desc", rulePackageItem.getDescription());
        // try loading rule package that was not created 
        try {
            rulePackageItem = getRepo().loadRulePackage("anotherRuleRuleItem");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }    
    
    public void testLoadRulePackageItemByUUID() throws Exception {

        RulePackageItem rulePackageItem = getRepo().createRulePackage("testLoadRuleRuleItemByUUID", "desc");

        String uuid = null;
            uuid = rulePackageItem.getNode().getUUID();


        rulePackageItem = getRepo().loadRulePackageByUUID(uuid);
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItemByUUID", rulePackageItem.getName());
        
        // try loading rule package that was not created 
        try {
            rulePackageItem = getRepo().loadRulePackageByUUID("01010101-0101-0101-0101-010101010101");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }    
    
    public void testAddRuleRuleItem() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testAddRuleRuleItem","desc");

            
            RuleItem ruleItem1 = rulePackageItem1.addRule("testAddRuleRuleItem", "test description");
            ruleItem1.updateRuleContent( "test content" );
            ruleItem1.checkin( "updated the rule content" );
            
            Iterator rulesIt = rulePackageItem1.getRules();
            assertNotNull(rulesIt);
            RuleItem first = (RuleItem) rulesIt.next();
            assertFalse(rulesIt.hasNext());
            assertEquals("testAddRuleRuleItem", first.getName());
            
            //test that it is following the head revision                        
            ruleItem1.updateRuleContent("new lhs");
            ruleItem1.checkin( "updated again" );
            rulesIt = rulePackageItem1.getRules();
            assertNotNull(rulesIt);
            
            List rules = iteratorToList( rulesIt );
            assertEquals(1, rules.size());
            assertEquals("testAddRuleRuleItem", ((RuleItem)rules.get(0)).getName());
            assertEquals("new lhs", ((RuleItem)rules.get(0)).getRuleContent());
                        
            RuleItem ruleItem2 = rulePackageItem1.addRule("testAddRuleRuleItem2", "test content");
            
            rules = iteratorToList(rulePackageItem1.getRules());
            assertNotNull(rules);
            assertEquals(2, rules.size());  

    }

    
    private List iteratorToList(Iterator it) {
        List list = new ArrayList();
        while(it.hasNext()) {
            list.add( it.next() );
        }
        return list;
    }

    public void testAddFunctionFunctionItem() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testAddFunctionFunctionItem", "desc");
            
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
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testAddFunctionFunctionItemBoolean", "desc");
            
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
            

    }

    public void testGetFunctions() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testGetFunctions", "desc");
                        
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
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testGetRules", "desc");
                        
            RuleItem ruleItem1 = rulePackageItem1.addRule("testGetRules", "desc" );
            ruleItem1.updateRuleContent( "test lhs content" );

            
            List rules = iteratorToList(rulePackageItem1.getRules());
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testGetRules", ((RuleItem)rules.get(0)).getName());
                                  
            RuleItem ruleItem2 = rulePackageItem1.addRule("testGetRules2", "desc" );
            ruleItem2.updateRuleContent( "test lhs content" );
            
            rules = iteratorToList(rulePackageItem1.getRules());
            assertNotNull(rules);
            assertEquals(2, rules.size());            

    }

    public void testToString() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testToStringPackage", "desc");
            
            RuleItem ruleItem1 = rulePackageItem1.addRule("testToStringPackage", "test lhs content" );
            ruleItem1.updateRuleContent( "test lhs content" );
            
            assertNotNull(rulePackageItem1.toString());                        

    }
    
    public void testRemoveRule() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testRemoveRule", "desc");
            
            RuleItem ruleItem1 = rulePackageItem1.addRule("testRemoveRule", "test lhs content" );
            ruleItem1.updateRuleContent( "test lhs content" ); 
            
            
            
            Iterator rulesIt = rulePackageItem1.getRules();
            RuleItem next = (RuleItem) rulesIt.next();
            
            assertFalse(rulesIt.hasNext());
            assertEquals("testRemoveRule", next.getName());
                               
            
            
            ruleItem1.updateRuleContent("new lhs");
            List rules = iteratorToList(rulePackageItem1.getRules());
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule", ((RuleItem)rules.get(0)).getName());
            assertEquals("new lhs", ((RuleItem)rules.get(0)).getRuleContent());
                        
            RuleItem ruleItem2 = rulePackageItem1.addRule("testRemoveRule2", "test lhs content");
            
            //remove the rule, make sure the other rule in the pacakge stays around
            rulePackageItem1.removeRule(ruleItem1.getName());
            rulePackageItem1.rulesRepository.save();
            rules = iteratorToList(rulePackageItem1.getRules());
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule2", ((RuleItem)rules.get(0)).getName());
            
            //remove the rule that is following the head revision, make sure the pacakge is now empty
            rulePackageItem1.removeRule(ruleItem2.getName());
            rules = iteratorToList(rulePackageItem1.getRules());
            assertNotNull(rules);
            assertEquals(0, rules.size());

    }
    
 
    
    public void testRemoveFunction() {
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testRemoveFunction", "yayayaya");
            
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
    
    
    public void testGetFormat() {        
            RulePackageItem rulePackageItem1 = getRepo().createRulePackage("testGetFormat", "woot");
            assertNotNull(rulePackageItem1);
            assertEquals("Rule Package", rulePackageItem1.getFormat());    

    }        
}