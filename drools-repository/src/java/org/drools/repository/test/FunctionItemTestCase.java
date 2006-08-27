package org.drools.repository.test;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.repository.*;

public class FunctionItemTestCase extends TestCase {
    private RulesRepository rulesRepository;
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rulesRepository = new RulesRepository(true);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        rulesRepository.logout();
    }

    public void testFunctionItem() {
        try {            
            //calls constructor
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("test function", functionItem1.getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
        
        //try constructing with node of wrong type
        try {
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            DslItem dslItem = rulesRepository.addDslFromFile(dslFile1);
            FunctionItem functionItem = new FunctionItem(this.rulesRepository, dslItem.getNode());
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
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("test content", functionItem1.getContent());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testUpdateContent() {
        //TODO: maybe add some testing on the versioning stuff more - check the content of the
        //      previous version, etc.
        try {                        
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
                        
            functionItem1.updateContent("new content");
            
            assertEquals("new content", functionItem1.getContent());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
        
    public void testAddTag() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            functionItem1.addTag("TestTag");
            List tags = functionItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("TestTag", ((CategoryItem)tags.get(0)).getName());
            
            functionItem1.addTag("TestTag2");
            tags = functionItem1.getTags();
            assertEquals(2, tags.size());   
                        
            //now test retrieve by tags
            List result = this.rulesRepository.findFunctionsByTag("TestTag");            
            assertEquals(1, result.size());            
            FunctionItem retItem = (FunctionItem) result.get(0);
            assertEquals("test function", retItem.getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testRemoveTag() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
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
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetTags() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
           
            List tags = functionItem1.getTags();
            assertNotNull(tags);
            assertEquals(0, tags.size());
            
            functionItem1.addTag("TestTag");                                    
            tags = functionItem1.getTags();
            assertEquals(1, tags.size());
            assertEquals("TestTag", ((CategoryItem)tags.get(0)).getName());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testSetStateString() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
           
            functionItem1.setState("TestState1");
            assertNotNull(functionItem1.getState());
            assertEquals("TestState1", functionItem1.getState().getName());
            
            functionItem1.setState("TestState2");
            assertNotNull(functionItem1.getState());
            assertEquals("TestState2", functionItem1.getState().getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testSetStateStateItem() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test content", "test content");
           
            StateItem stateItem1 = rulesRepository.getState("TestState1");
            functionItem1.setState(stateItem1);            
            assertNotNull(functionItem1.getState());
            assertEquals(functionItem1.getState().getName(), "TestState1");
            
            StateItem stateItem2 = rulesRepository.getState("TestState2");
            functionItem1.setState(stateItem2);
            assertNotNull(functionItem1.getState());
            assertEquals("TestState2", functionItem1.getState().getName());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetState() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
           
            StateItem stateItem1 = functionItem1.getState();
            assertNull(stateItem1);
            
            functionItem1.setState("TestState1");
            assertNotNull(functionItem1.getState());
            assertEquals("TestState1", functionItem1.getState().getName());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testToString() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
           
            assertNotNull(functionItem1.toString());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetLastModified() {
        //common functionality with FunctionItem - tested there
    }
        
    public void testFunctionRuleLanguage() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
           
            //it should be initialized to 'Java'
            assertEquals("Java", functionItem1.getFunctionLanguage());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetDescription() {
        //common functionality with FunctionItem - tested there
    }
    
    public void testGetPrecedingVersion() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
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
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }   
    }
    
    public void testGetSucceedingVersion() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            FunctionItem succeedingFunctionItem = (FunctionItem) functionItem1.getSucceedingVersion();
            assertTrue(succeedingFunctionItem == null);            
            
            functionItem1.updateContent("new content");
            
            FunctionItem predecessorFunctionItem = (FunctionItem) functionItem1.getPrecedingVersion();
            assertEquals("test content", predecessorFunctionItem.getContent());
            succeedingFunctionItem = (FunctionItem) predecessorFunctionItem.getSucceedingVersion();
            assertNotNull(succeedingFunctionItem);
            assertEquals(functionItem1.getContent(), succeedingFunctionItem.getContent());                       
        }        
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }   
    } 
    
    public void testGetSuccessorVersionsIterator() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");                        
            
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
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetPredecessorVersionsIterator() {
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");                        
            
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
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetTitle() {    
        try {
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");            
                        
            assertEquals("test function", functionItem1.getTitle());
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
            FunctionItem functionItem1 = this.rulesRepository.addFunction("test function", "test content");
            
            assertEquals("Function", functionItem1.getFormat());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }        
}
