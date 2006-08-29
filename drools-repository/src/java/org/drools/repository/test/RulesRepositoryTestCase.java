package org.drools.repository.test;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;

import org.drools.repository.*;

import junit.framework.TestCase;

public class RulesRepositoryTestCase extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRulesRepository() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
        }
        catch(Exception e) {
            fail("Caught unexpected Exception: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }

    public void testLogout() {
        //this is covered by testRulesRepository() above
    }

    public void testAddDslFromFile() {
        //this is covered by the DslItemTestCase
    }

    public void testAddRule() {
        //this is covered by the RuleItemTestCase
    }

    public void testAddRuleDslItemBoolean() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            
            DslItem dslItem1 = rulesRepository.addDslFromFile(dslFile1);
            assertNotNull(dslItem1);
            
            RuleItem ruleItem1 = rulesRepository.addRule("test rule", "test lhs content", "test rhs content", dslItem1, true);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            
            //test that this follows the head version
            File dslFile2 = new File("./src/java/org/drools/repository/test/test_data/dsl2.dsl");
            dslItem1.updateContentFromFile(dslFile2);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            
            //now do the same thing, but test not following head:                                    
            RuleItem ruleItem2 = rulesRepository.addRule("test rule 2", "test lhs content", "test rhs content", dslItem1, false);
            
            assertNotNull(ruleItem2);
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem2.getDsl().getContent());
            
            //test that this stays tied to the specific revision of the DSL node
            String originalContent = ruleItem2.getDsl().getContent();
            dslItem1.updateContentFromFile(dslFile2);
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(originalContent, ruleItem2.getDsl().getContent());
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }
    
    public void testAddRuleCalendarCalendar() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
                        
            Calendar effectiveDate = Calendar.getInstance();
            Calendar expiredDate = Calendar.getInstance();
            expiredDate.setTimeInMillis(effectiveDate.getTimeInMillis() + (1000 * 60 * 60 * 24));
            RuleItem ruleItem1 = rulesRepository.addRule("test rule", "test lhs content", "test rhs content", effectiveDate, expiredDate);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals(effectiveDate, ruleItem1.getDateEffective());
            assertEquals(expiredDate, ruleItem1.getDateExpired());                       
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }
    
    public void testAddRuleDslItemBooleanCalendarCalendar() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            
            DslItem dslItem1 = rulesRepository.addDslFromFile(dslFile1);
            assertNotNull(dslItem1);
            
            Calendar effectiveDate = Calendar.getInstance();
            Calendar expiredDate = Calendar.getInstance();
            expiredDate.setTimeInMillis(effectiveDate.getTimeInMillis() + (1000 * 60 * 60 * 24));
            
            RuleItem ruleItem1 = rulesRepository.addRule("test rule", "test lhs content", "test rhs content", dslItem1, true, effectiveDate, expiredDate, "test description");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            assertEquals(effectiveDate, ruleItem1.getDateEffective());
            assertEquals(expiredDate, ruleItem1.getDateExpired());
            assertEquals("test description", ruleItem1.getDescription());
            
            //test that this follows the head version
            File dslFile2 = new File("./src/java/org/drools/repository/test/test_data/dsl2.dsl");
            dslItem1.updateContentFromFile(dslFile2);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            
            //now do the same thing, but test not following head:                                    
            RuleItem ruleItem2 = rulesRepository.addRule("test rule 2", "test lhs content", "test rhs content", dslItem1, false, effectiveDate, expiredDate, "test description 2");
            
            assertNotNull(ruleItem2);
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem2.getDsl().getContent());
            assertEquals(effectiveDate, ruleItem2.getDateEffective());
            assertEquals(expiredDate, ruleItem2.getDateExpired());
            assertEquals("test description 2", ruleItem2.getDescription());
            
            //test that this stays tied to the specific revision of the DSL node
            String originalContent = ruleItem2.getDsl().getContent();
            dslItem1.updateContentFromFile(dslFile2);
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(originalContent, ruleItem2.getDsl().getContent());
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }
    
    public void testCreateRulePackage() {
        //this is covered by RulePackageItemTestCase
    }

    public void testGetState() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
            
            StateItem stateItem1 = rulesRepository.getState("TestState");
            assertNotNull(stateItem1);
            assertEquals("TestState", stateItem1.getName());
            
            StateItem stateItem2 = rulesRepository.getState("TestState");
            assertNotNull(stateItem2);
            assertEquals("TestState", stateItem2.getName());
            assertEquals(stateItem1, stateItem2);
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }

    public void testGetTag() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
            
            CategoryItem tagItem1 = rulesRepository.getOrCreateCategory("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());
            assertEquals("TestTag", tagItem1.getFullPath());
            
            CategoryItem tagItem2 = rulesRepository.getOrCreateCategory("TestTag");
            assertNotNull(tagItem2);
            assertEquals("TestTag", tagItem2.getName());
            assertEquals(tagItem1, tagItem2);
            
            //now test getting a tag down in the tag hierarchy
            CategoryItem tagItem3 = rulesRepository.getOrCreateCategory("TestTag/TestChildTag1");
            assertNotNull(tagItem3);
            assertEquals("TestChildTag1", tagItem3.getName());
            assertEquals("TestTag/TestChildTag1", tagItem3.getFullPath());                                   
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }
    
    public void testAddFunctionStringString() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
                        
            FunctionItem functionItem1 = rulesRepository.addFunction("test function", "test content");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("test function", functionItem1.getName());
            assertEquals("test content", functionItem1.getContent());
            assertEquals("", functionItem1.getDescription());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }
    
    public void testAddFunctionStringStringString() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
                        
            FunctionItem functionItem1 = rulesRepository.addFunction("test function", "test content", "test description");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("test function", functionItem1.getName());
            assertEquals("test content", functionItem1.getContent());
            assertEquals("test description", functionItem1.getDescription());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }
    
    public void testListPackages() {
        RulesRepository rulesRepository = RepositorySession.getRepository();
        try {
            RulePackageItem rulePackageItem1 = rulesRepository.createRulePackage("testListPackages");
            
            Iterator it = rulesRepository.listPackages();
            assertTrue(it.hasNext());
            
            RulePackageItem pack = (RulePackageItem) it.next();
            assertEquals("testListPackages", pack.getName());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
        finally {
            if(rulesRepository != null) {
                try {
                    rulesRepository.logout();
                }
                catch(Exception e) {
                    fail("Caught unexpected Exception: " + e);
                }
            }
        }
    }
}
