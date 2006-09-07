package org.drools.repository;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.repository.*;

public class FunctionItemTestCase extends TestCase {

    
    

    public void testFunctionItem() {
        try {            
            //calls constructor
            FunctionItem functionItem1 = this.getRepo().addFunction("testFunctionItem", "test content");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("testFunctionItem", functionItem1.getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
        
        //try constructing with node of wrong type
        try {
            
            DslItem dslItem = getRepo().addDsl("testFunctionItem", "content here");
            FunctionItem functionItem = new FunctionItem(this.getRepo(), dslItem.getNode());
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
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetContent", "test content");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("test content", functionItem1.getContent());
    }
    
    public void testUpdateContent() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testUpdateContent", "test content");
                        
            functionItem1.updateContent("new content");
            
            assertEquals("new content", functionItem1.getContent());
    }
        
    public void testAddCategory() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testAddCategory", "test content");
            
            functionItem1.addTag("testAddCategoryTestTag");
            List tags = functionItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("testAddCategoryTestTag", ((CategoryItem)tags.get(0)).getName());
            
            functionItem1.addTag("testAddCategoryTestTag2");
            tags = functionItem1.getTags();
            assertEquals(2, tags.size());   
                        
            //now test retrieve by tags
            List result = this.getRepo().findFunctionsByTag("testAddCategoryTestTag");            
            assertEquals(1, result.size());            
            FunctionItem retItem = (FunctionItem) result.get(0);
            assertEquals("testAddCategory", retItem.getName());            
    }

    public void testRemoveTag() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testRemoveTag", "test content");
            
            functionItem1.addTag("TestTag");                                    
            functionItem1.removeTag("TestTag");
            List tags = functionItem1.getTags();
            assertEquals(0, tags.size());
            
            functionItem1.addTag("TestTag2");                                    
            functionItem1.addTag("TestTag3");
            functionItem1.removeTag("TestTag2");
            tags = functionItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("TestTag3", ((CategoryItem)tags.get(0)).getName());            
    }

    public void testGetTags() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetTags", "test content");
           
            List tags = functionItem1.getTags();
            assertNotNull(tags);
            assertEquals(0, tags.size());
            
            functionItem1.addTag("TestTag");                                    
            tags = functionItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("TestTag", ((CategoryItem)tags.get(0)).getName());
    }

    public void testSetStateString() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testSetStateString", "test content");
           
            functionItem1.setState("TestState1");
            assertNotNull(functionItem1.getState());
            assertEquals("TestState1", functionItem1.getState().getName());
            
            functionItem1.setState("TestState2");
            assertNotNull(functionItem1.getState());
            assertEquals("TestState2", functionItem1.getState().getName());            
    }

    public void testSetStateStateItem() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testSetStateStateItem", "test content");
           
            StateItem stateItem1 = getRepo().getState("TestState1");
            functionItem1.setState(stateItem1);            
            assertNotNull(functionItem1.getState());
            assertEquals(functionItem1.getState().getName(), "TestState1");
            
            StateItem stateItem2 = getRepo().getState("TestState2");
            functionItem1.setState(stateItem2);
            assertNotNull(functionItem1.getState());
            assertEquals("TestState2", functionItem1.getState().getName());            
    }

    public void testGetState() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetState", "test content");
           
            StateItem stateItem1 = functionItem1.getState();
            assertNull(stateItem1);
            
            functionItem1.setState("TestState1");
            assertNotNull(functionItem1.getState());
            assertEquals("TestState1", functionItem1.getState().getName());                        
    }

    public void testToString() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testToString", "test content");           
            assertNotNull(functionItem1.toString());                        
    }
    
        
    public void testFunctionRuleLanguage() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testFunctionRuleLanguage", "test content");
           
            //it should be initialized to 'Java'
            assertEquals("Java", functionItem1.getFunctionLanguage());                        
    }
    
    
    public void testGetPrecedingVersion() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetPrecedingVersion", "test content");
            
            FunctionItem predecessorFunctionItem = (FunctionItem) functionItem1.getPrecedingVersion();
            assertTrue(predecessorFunctionItem == null);            
            
            functionItem1.updateContent("new content");
            
            predecessorFunctionItem = (FunctionItem) functionItem1.getPrecedingVersion();
            assertNotNull(predecessorFunctionItem);
            assertEquals("test content", predecessorFunctionItem.getContent());
            
            functionItem1.updateContent("newer content");
            
            predecessorFunctionItem = (FunctionItem) functionItem1.getPrecedingVersion();
            assertNotNull(predecessorFunctionItem);
            assertEquals("new content", predecessorFunctionItem.getContent());
            predecessorFunctionItem = (FunctionItem) predecessorFunctionItem.getPrecedingVersion();
            assertNotNull(predecessorFunctionItem);
            assertEquals("test content", predecessorFunctionItem.getContent());
    }
    
    public void testGetSucceedingVersion() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetSucceedingVersion", "test content");
            
            FunctionItem succeedingFunctionItem = (FunctionItem) functionItem1.getSucceedingVersion();
            assertTrue(succeedingFunctionItem == null);            
            
            functionItem1.updateContent("new content");
            
            FunctionItem predecessorFunctionItem = (FunctionItem) functionItem1.getPrecedingVersion();
            assertEquals("test content", predecessorFunctionItem.getContent());
            succeedingFunctionItem = (FunctionItem) predecessorFunctionItem.getSucceedingVersion();
            assertNotNull(succeedingFunctionItem);
            assertEquals(functionItem1.getContent(), succeedingFunctionItem.getContent());                       
    } 
    
    public void testGetSuccessorVersionsIterator() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetSuccessorVersionsIterator", "test content");                        
            
            Iterator iterator = functionItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            functionItem1.updateContent("new content");
            
            iterator = functionItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            FunctionItem predecessorFunctionItem = (FunctionItem) functionItem1.getPrecedingVersion();
            iterator = predecessorFunctionItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            FunctionItem nextFunctionItem = (FunctionItem) iterator.next();
            assertEquals("new content", nextFunctionItem.getContent());
            assertFalse(iterator.hasNext());
            
            functionItem1.updateContent("newer content");
                        
            iterator = predecessorFunctionItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextFunctionItem = (FunctionItem) iterator.next();
            assertEquals("new content", nextFunctionItem.getContent());
            assertTrue(iterator.hasNext());
            nextFunctionItem = (FunctionItem)iterator.next();
            assertEquals("newer content", nextFunctionItem.getContent());
            assertFalse(iterator.hasNext());            
    }
    
    public void testGetPredecessorVersionsIterator() {
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetPredecessorVersionsIterator", "test content");                        
            
            Iterator iterator = functionItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            functionItem1.updateContent("new content");
            
            iterator = functionItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            FunctionItem nextFunctionItem = (FunctionItem) iterator.next();
            assertFalse(iterator.hasNext());
            assertEquals("test content", nextFunctionItem.getContent());
            
            functionItem1.updateContent("newer content");
            
            iterator = functionItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextFunctionItem = (FunctionItem) iterator.next();
            assertTrue(iterator.hasNext());            
            assertEquals("new content", nextFunctionItem.getContent());
            nextFunctionItem = (FunctionItem) iterator.next();
            assertFalse(iterator.hasNext());
            assertEquals("test content", nextFunctionItem.getContent());
    }
    
    public void testGetTitle() {    
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetTitle", "test content");            
            assertEquals("testGetTitle", functionItem1.getTitle());
    }
    
    
    public void testGetFormat() {        
            FunctionItem functionItem1 = this.getRepo().addFunction("testGetFormat", "test content");
            assertEquals("Function", functionItem1.getFormat());            
    }

    private RulesRepository getRepo() {

        return RepositorySession.getRepository();
    }        
}
