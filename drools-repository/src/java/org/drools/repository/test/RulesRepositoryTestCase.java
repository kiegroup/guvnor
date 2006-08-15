package org.drools.repository.test;

import java.io.File;

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

    public void testAddRuleFromFileFile() {
        //this is covered by the RuleItemTestCase
    }

    public void testAddRuleFromFileFileDslItem() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            
            DslItem dslItem1 = rulesRepository.addDslFromFile(dslFile1);
            assertNotNull(dslItem1);
            
            File ruleFile1 = new File("./src/java/org/drools/repository/test/test_data/drl2.drl");
            RuleItem ruleItem1 = rulesRepository.addRuleFromFile(ruleFile1, dslItem1);
            
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

    public void testAddRuleFromFileFileDslItemBoolean() {
        RulesRepository rulesRepository = null;
        try {
            rulesRepository = new RulesRepository(true);
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            
            DslItem dslItem1 = rulesRepository.addDslFromFile(dslFile1);
            assertNotNull(dslItem1);
            
            File ruleFile1 = new File("./src/java/org/drools/repository/test/test_data/drl2.drl");
            RuleItem ruleItem1 = rulesRepository.addRuleFromFile(ruleFile1, dslItem1, true);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1, ruleItem1.getDsl());
            
            //test that this follows the head version
            File dslFile2 = new File("./src/java/org/drools/repository/test/test_data/dsl2.dsl");
            
            dslItem1.updateContentFromFile(dslFile2);
            
            
            
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(ruleItem1.getDsl(), dslItem1);                                   
            
            //now test not following the head revision
            rulesRepository.logout();
            rulesRepository = new RulesRepository(true);
            
            dslItem1 = rulesRepository.addDslFromFile(dslFile1);
            assertNotNull(dslItem1);
            
            ruleItem1 = rulesRepository.addRuleFromFile(ruleFile1, dslItem1, false);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            
            //test that this follows the head version
            dslItem1.updateContentFromFile(dslFile2);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertFalse(ruleItem1.getDsl().equals(dslItem1));
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
            
            TagItem tagItem1 = rulesRepository.getTag("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());
            assertEquals("TestTag", tagItem1.getFullPath());
            
            TagItem tagItem2 = rulesRepository.getTag("TestTag");
            assertNotNull(tagItem2);
            assertEquals("TestTag", tagItem2.getName());
            assertEquals(tagItem1, tagItem2);
            
            //now test getting a tag down in the tag hierarchy
            TagItem tagItem3 = rulesRepository.getTag("TestTag/TestChildTag1");
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
}
