package org.drools.repository.test;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.drools.repository.*;

public class RuleItemTestCase extends TestCase {
    private RulesRepository rulesRepository;
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rulesRepository = new RulesRepository(true);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        rulesRepository.logout();
    }

    public void testRuleItem() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");
            
            //calls constructor
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("drl1.drl", ruleItem1.getName());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
        
        //try constructing with node of wrong type
        try {
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            DslItem dslItem = rulesRepository.addDslFromFile(dslFile1);
            RuleItem ruleItem = new RuleItem(this.rulesRepository, dslItem.getNode());
            fail("Exception not thrown for node of type: " + dslItem.getNode().getPrimaryNodeType().getName());
        }
        catch(RulesRepositoryException e) {
            //this is good
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetContent() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl3.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("package org.drools.examples", ruleItem1.getContent());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testUpdateContentFromFile() {
        //TODO: maybe add some testing on the versioning stuff more - check the content of the
        //      previous version, etc.
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            File drlFile2 = new File("./src/java/org/drools/repository/test/test_data/drl3.drl");
            ruleItem1.updateContentFromFile(drlFile2);
            
            assertEquals("package org.drools.examples", ruleItem1.getContent());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testAddTag() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            ruleItem1.addTag("TestTag");
            List tags = ruleItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("TestTag", ((CategoryItem)tags.get(0)).getName());
            
            ruleItem1.addTag("TestTag2");
            tags = ruleItem1.getTags();
            assertEquals(2, tags.size());   
            
            
            //now test retrieve by tags
            List result = this.rulesRepository.findRulesByTag("TestTag");            
            assertEquals(1, result.size());            
            RuleItem retItem = (RuleItem) result.get( 0 );
            assertEquals("drl1.drl", retItem.getName());
            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testRemoveTag() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
            
            ruleItem1.addTag("TestTag");                                    
            ruleItem1.removeTag("TestTag");
            List tags = ruleItem1.getTags();
            assertEquals(0, tags.size());
            
            ruleItem1.addTag("TestTag2");                                    
            ruleItem1.addTag("TestTag3");
            ruleItem1.removeTag("TestTag2");
            tags = ruleItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("TestTag3", ((CategoryItem)tags.get(0)).getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetTags() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
           
            List tags = ruleItem1.getTags();
            assertNotNull(tags);
            assertEquals(0, tags.size());
            
            ruleItem1.addTag("TestTag");                                    
            tags = ruleItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("TestTag", ((CategoryItem)tags.get(0)).getName());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testSetStateString() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
           
            ruleItem1.setState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());
            
            ruleItem1.setState("TestState2");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testSetStateStateItem() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
           
            StateItem stateItem1 = rulesRepository.getState("TestState1");
            ruleItem1.setState(stateItem1);            
            assertNotNull(ruleItem1.getState());
            assertEquals(ruleItem1.getState().getName(), "TestState1");
            
            StateItem stateItem2 = rulesRepository.getState("TestState2");
            ruleItem1.setState(stateItem2);
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetState() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
           
            StateItem stateItem1 = ruleItem1.getState();
            assertNull(stateItem1);
            
            ruleItem1.setState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testToString() {
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/drl1.drl");            
            RuleItem ruleItem1 = this.rulesRepository.addRuleFromFile(drlFile1);
           
            assertNotNull(ruleItem1.toString());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }

    }
}
