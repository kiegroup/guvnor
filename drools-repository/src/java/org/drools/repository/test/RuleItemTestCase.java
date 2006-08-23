package org.drools.repository.test;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
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
            //calls constructor
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test rule", ruleItem1.getName());
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

    public void testGetLhs() {
        try {            
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test lhs content", ruleItem1.getLhs());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetRhs() {
        try {            
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test rhs content", ruleItem1.getRhs());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testUpdateLhs() {
        //TODO: maybe add some testing on the versioning stuff more - check the content of the
        //      previous version, etc.
        try {                        
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
                        
            ruleItem1.updateLhs("new lhs content");
            
            assertEquals("new lhs content", ruleItem1.getLhs());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testUpdateRhs() {
        //TODO: maybe add some testing on the versioning stuff more - check the content of the
        //      previous version, etc.
        try {                        
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
                        
            ruleItem1.updateRhs("new rhs content");
            
            assertEquals("new rhs content", ruleItem1.getRhs());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testAddTag() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
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
            assertEquals("test rule", retItem.getName());
            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testRemoveTag() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
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
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
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
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
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
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
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
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
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
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
            assertNotNull(ruleItem1.toString());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetLastModified() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
            Calendar cal = Calendar.getInstance();
            long before = cal.getTimeInMillis();           
            
            ruleItem1.updateLhs("new lhs");
            Calendar cal2 = ruleItem1.getLastModified();
            long lastMod = cal2.getTimeInMillis();           
            
            cal = Calendar.getInstance();
            long after = cal.getTimeInMillis();
            assertTrue(before < after);
            assertTrue(before < lastMod);
            assertTrue(lastMod < after);
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetDateEffective() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
            //it should be initialized to null
            assertTrue(ruleItem1.getDateEffective() == null);
            
            //now try setting it, then retrieving it
            Calendar cal = Calendar.getInstance();
            ruleItem1.updateDateEffective(cal);
            Calendar cal2 = ruleItem1.getDateEffective();
            
            assertEquals(cal, cal2);            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetDateExpired() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
            //it should be initialized to null
            assertTrue(ruleItem1.getDateExpired() == null);
            
            //now try setting it, then retrieving it
            Calendar cal = Calendar.getInstance();
            ruleItem1.updateDateExpired(cal);
            Calendar cal2 = ruleItem1.getDateExpired();
            
            assertEquals(cal, cal2);            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetRuleLanguage() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
           
            //it should be initialized to 'DRL'
            assertEquals("DRL", ruleItem1.getRuleLanguage());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetDescription() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            //it should be "" to begin with
            assertEquals("", ruleItem1.getDescription());
            
            ruleItem1.updateDescription("test description");
            assertEquals("test description", ruleItem1.getDescription());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetPrecedingVersion() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            RuleItem predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertTrue(predecessorRuleItem == null);            
            
            ruleItem1.updateLhs("new lhs");
            
            predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test lhs content", predecessorRuleItem.getLhs());
            
            ruleItem1.updateLhs("newer lhs");
            
            predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("new lhs", predecessorRuleItem.getLhs());
            predecessorRuleItem = (RuleItem) predecessorRuleItem.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test lhs content", predecessorRuleItem.getLhs());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }   
    }
    
    public void testGetSucceedingVersion() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            RuleItem succeedingRuleItem = (RuleItem) ruleItem1.getSucceedingVersion();
            assertTrue(succeedingRuleItem == null);            
            
            ruleItem1.updateLhs("new lhs");
            
            RuleItem predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertEquals("test lhs content", predecessorRuleItem.getLhs());
            succeedingRuleItem = (RuleItem) predecessorRuleItem.getSucceedingVersion();
            assertNotNull(succeedingRuleItem);
            assertEquals(ruleItem1.getLhs(), succeedingRuleItem.getLhs());                       
        }        
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }   
    } 
    
    public void testGetSuccessorVersionsIterator() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");                        
            
            Iterator iterator = ruleItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateLhs("new lhs");
            
            iterator = ruleItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            RuleItem predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            RuleItem nextRuleItem = (RuleItem) iterator.next();
            assertEquals("new lhs", nextRuleItem.getLhs());
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateLhs("newer lhs");
                        
            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (RuleItem) iterator.next();
            assertEquals("new lhs", nextRuleItem.getLhs());
            assertTrue(iterator.hasNext());
            nextRuleItem = (RuleItem)iterator.next();
            assertEquals("newer lhs", nextRuleItem.getLhs());
            assertFalse(iterator.hasNext());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetPredecessorVersionsIterator() {
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");                        
            
            Iterator iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateLhs("new lhs");
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            RuleItem nextRuleItem = (RuleItem) iterator.next();
            assertFalse(iterator.hasNext());
            assertEquals("test lhs content", nextRuleItem.getLhs());
            
            ruleItem1.updateLhs("newer lhs");
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (RuleItem) iterator.next();
            assertTrue(iterator.hasNext());            
            assertEquals("new lhs", nextRuleItem.getLhs());
            nextRuleItem = (RuleItem) iterator.next();
            assertFalse(iterator.hasNext());
            assertEquals("test lhs content", nextRuleItem.getLhs());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetTitle() {    
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");            
                        
            assertEquals("test rule", ruleItem1.getTitle());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetContributor() {
        //can't implement this until we figure out login / JAAS stuff.
        fail("not yet implemented");        
    }
    
    public void testGetFormat() {        
        try {
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            assertEquals("Rule", ruleItem1.getFormat());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }        
}
