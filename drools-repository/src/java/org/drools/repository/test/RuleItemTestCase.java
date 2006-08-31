package org.drools.repository.test;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.repository.*;

public class RuleItemTestCase extends TestCase {


    public RulesRepository getRepo() {
        return RepositorySession.getRepository();
    }
    
    public void testRuleItem() {
        try {            
            //calls constructor
            RuleItem ruleItem1 = getRepo().addRule("testRuleItem", "test lhs content", "test rhs content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("testRuleItem", ruleItem1.getName());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
        
        //try constructing with node of wrong type
        try {
            
            DslItem dslItem = getRepo().addDsl("testRuleItem", "content here");
            RuleItem ruleItem = new RuleItem(getRepo(), dslItem.getNode());
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
            
            RuleItem ruleItem1 = getRepo().addRule("testGetLhs", "test lhs content", "test rhs content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test lhs content", ruleItem1.getLhs());
    }

    public void testGetRhs() {
            RuleItem ruleItem1 = getRepo().addRule("testGetRhs", "test lhs content", "test rhs content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test rhs content", ruleItem1.getRhs());

    }
    
    public void testUpdateLhs() {
            RuleItem ruleItem1 = getRepo().addRule("testUpdateLhs", "test lhs content", "test rhs content");
                        
            ruleItem1.updateLhs("new lhs content");
            
            assertEquals("new lhs content", ruleItem1.getLhs());
            
            ruleItem1.checkin( "yeah !" );
            
            assertEquals("yeah !", ruleItem1.getCheckinComment());
            
            RuleItem prev = (RuleItem) ruleItem1.getPredecessorVersionsIterator().next();
            assertEquals("test lhs content", prev.getLhs());
            assertFalse("yeah !".equals(prev.getCheckinComment()));
            
            
            assertEquals(prev, ruleItem1.getPrecedingVersion());

    }
    
    public void testUpdateRhs() {
            RuleItem ruleItem1 = getRepo().addRule("testUpdateRhs", "test lhs content", "test rhs content");                        
            ruleItem1.updateRhs("new rhs content");            
            assertEquals("new rhs content", ruleItem1.getRhs());
            ruleItem1.checkin( "la" );
            RuleItem prev = (RuleItem) ruleItem1.getPrecedingVersion();
            assertEquals("test rhs content", prev.getRhs());
            
    }

    public void testAddTag() {
            RuleItem ruleItem1 = getRepo().addRule("testAddTag", "test lhs content", "test rhs content");
            
            ruleItem1.addCategory("testAddTagTestTag");
            List tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("testAddTagTestTag", ((CategoryItem)tags.get(0)).getName());
            
            ruleItem1.addCategory("testAddTagTestTag2");
            tags = ruleItem1.getCategories();
            assertEquals(2, tags.size());   
            
            ruleItem1.checkin( "woot" );
            
            //now test retrieve by tags
            List result = getRepo().findRulesByTag("testAddTagTestTag");            
            assertEquals(1, result.size());            
            RuleItem retItem = (RuleItem) result.get( 0 );
            assertEquals("testAddTag", retItem.getName());
            

    }

    public void testRemoveTag() {
            RuleItem ruleItem1 = getRepo().addRule("testRemoveTag", "test lhs content", "test rhs content");
            
            ruleItem1.addCategory("TestTag");                                    
            ruleItem1.removeCategory("TestTag");
            List tags = ruleItem1.getCategories();
            assertEquals(0, tags.size());
            
            ruleItem1.addCategory("TestTag2");                                    
            ruleItem1.addCategory("TestTag3");
            ruleItem1.removeCategory("TestTag2");
            tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("TestTag3", ((CategoryItem)tags.get(0)).getName());            

    }

    public void testGetTags() {
            RuleItem ruleItem1 = getRepo().addRule("testGetTags", "test lhs content", "test rhs content");
           
            List tags = ruleItem1.getCategories();
            assertNotNull(tags);
            assertEquals(0, tags.size());
            
            ruleItem1.addCategory("testGetTagsTestTag");                                    
            tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("testGetTagsTestTag", ((CategoryItem)tags.get(0)).getName());

    }

    public void testSetStateString() {
            RuleItem ruleItem1 = getRepo().addRule("testSetStateString", "test lhs content", "test rhs content");
           
            ruleItem1.setState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());
            
            ruleItem1.setState("TestState2");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            

    }

    public void testSetStateStateItem() {
            RuleItem ruleItem1 = getRepo().addRule("testSetStateStateItem", "test lhs content", "test rhs content");
           
            StateItem stateItem1 = getRepo().getState("TestState1");
            ruleItem1.setState(stateItem1);            
            assertNotNull(ruleItem1.getState());
            assertEquals(ruleItem1.getState().getName(), "TestState1");
            
            StateItem stateItem2 = getRepo().getState("TestState2");
            ruleItem1.setState(stateItem2);
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            

    }

    public void testGetState() {
            RuleItem ruleItem1 = getRepo().addRule("testGetState", "test lhs content", "test rhs content");
           
            StateItem stateItem1 = ruleItem1.getState();
            assertNull(stateItem1);
            
            ruleItem1.setState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());                        
    }

    public void testToString() {
            RuleItem ruleItem1 = getRepo().addRule("testToString", "test lhs content", "test rhs content");
           
            assertNotNull(ruleItem1.toString());                        

    }
    
    public void testGetLastModified() {
            RuleItem ruleItem1 = getRepo().addRule("testGetLastModified", "test lhs content", "test rhs content");
           
            Calendar cal = Calendar.getInstance();
            long before = cal.getTimeInMillis();           
            
            ruleItem1.updateLhs("new lhs");
            ruleItem1.checkin( "woot" );
            Calendar cal2 = ruleItem1.getLastModified();
            long lastMod = cal2.getTimeInMillis();           
            
            cal = Calendar.getInstance();
            long after = cal.getTimeInMillis();
            assertTrue(before < after);
            assertTrue(before < lastMod);
            assertTrue(lastMod < after);

    }
    
    public void testGetDateEffective() {

            RuleItem ruleItem1 = getRepo().addRule("testGetDateEffective", "test lhs content", "test rhs content");
           
            //it should be initialized to null
            assertTrue(ruleItem1.getDateEffective() == null);
            
            //now try setting it, then retrieving it
            Calendar cal = Calendar.getInstance();
            ruleItem1.updateDateEffective(cal);
            Calendar cal2 = ruleItem1.getDateEffective();
            
            assertEquals(cal, cal2);            
    }
    
    public void testGetDateExpired() {
        try {
            RuleItem ruleItem1 = getRepo().addRule("testGetDateExpired", "test lhs content", "test rhs content");
           
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
            RuleItem ruleItem1 = getRepo().addRule("testGetRuleLanguage", "test lhs content", "test rhs content");
           
            //it should be initialized to 'DRL'
            assertEquals("DRL", ruleItem1.getRuleLanguage());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetDescription() {
            RuleItem ruleItem1 = getRepo().addRule("testGetDescription", "test lhs content", "test rhs content");
            
            //it should be "" to begin with
            assertEquals("", ruleItem1.getDescription());
            
            ruleItem1.updateDescription("test description");
            assertEquals("test description", ruleItem1.getDescription());
    }
    
    public void testGetPrecedingVersion() {
            RuleItem ruleItem1 = getRepo().addRule("testGetPrecedingVersion", "test lhs content", "test rhs content");
            
            RuleItem predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertTrue(predecessorRuleItem == null);            
            
            ruleItem1.updateLhs("new lhs");
            ruleItem1.updateRhs( "hola" );
            ruleItem1.checkin( "two changes" );
            
            
            predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test lhs content", predecessorRuleItem.getLhs());
            assertEquals("test rhs content", predecessorRuleItem.getRhs());
            
            
            
            ruleItem1.updateLhs("newer lhs");
            ruleItem1.checkin( "another" );
            
            predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("new lhs", predecessorRuleItem.getLhs());
            predecessorRuleItem = (RuleItem) predecessorRuleItem.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test lhs content", predecessorRuleItem.getLhs());
 
    }
    
    public void testGetSucceedingVersion() {
        try {
            RuleItem ruleItem1 = getRepo().addRule("testGetSucceedingVersion", "test lhs content", "test rhs content");
            
            RuleItem succeedingRuleItem = (RuleItem) ruleItem1.getSucceedingVersion();
            assertTrue(succeedingRuleItem == null);            
            
            ruleItem1.updateLhs("new lhs");
            ruleItem1.checkin( "la" );
            
            
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
            RuleItem ruleItem1 = getRepo().addRule("testGetSuccessorVersionsIterator", "test lhs content", "test rhs content");                        
            
            Iterator iterator = ruleItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateLhs("new lhs").checkin( "ya" );
            
            
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
            ruleItem1.checkin( "boo" );
                        
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
            RuleItem ruleItem1 = getRepo().addRule("testGetPredecessorVersionsIterator", "test lhs content", "test rhs content");                        
            
            Iterator iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateLhs("new lhs");
            ruleItem1.checkin( "boo" );
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            RuleItem nextRuleItem = (RuleItem) iterator.next();
            assertFalse(iterator.hasNext());
            assertEquals("test lhs content", nextRuleItem.getLhs());
            
            ruleItem1.updateLhs("newer lhs");
            ruleItem1.checkin( "wee" );
            
            
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
            RuleItem ruleItem1 = getRepo().addRule("testGetTitle", "test lhs content", "test rhs content");            
                        
            assertEquals("testGetTitle", ruleItem1.getTitle());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetFormat() {        
        try {
            RuleItem ruleItem1 = getRepo().addRule("testGetFormat", "test lhs content", "test rhs content");
            
            assertEquals("Rule", ruleItem1.getFormat());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }        
}
