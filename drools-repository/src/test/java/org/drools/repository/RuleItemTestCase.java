package org.drools.repository;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class RuleItemTestCase extends TestCase {


    private RulesRepository getRepo() {
        return RepositorySession.getRepository();
    }
    
    private RulePackageItem getDefaultPackage() {
        return getRepo().loadDefaultRulePackage();
    }
    
    public void testRuleItem() {
            //calls constructor
        
            RuleItem ruleItem1 = getDefaultPackage().addRule("testRuleItem", "test content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("testRuleItem", ruleItem1.getName());
        
        //try constructing with node of wrong type
        try {
            
            DslItem dslItem = getRepo().addDsl("testRuleItem", "content here");
            new RuleItem(getRepo(), dslItem.getNode());
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
            
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetContent", "test content");
            ruleItem1.updateRuleContent( "test content" );
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test content", ruleItem1.getRuleContent());
    }

    public void testGetURI() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testGetURI", "blah");
            ruleItem1.updateRuleContentURI( "foo/bar" );
            ruleItem1.checkin( "ha !" );
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("foo/bar", ruleItem1.getRuleContentURI());
    }
    
    public void testUpdateContent() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testUpdateContent", "test description");
            ruleItem1.updateRuleContent( "test content" );            
            ruleItem1.checkin( "yeah" );
            
            ruleItem1.updateRuleContent( "new rule content");
            
            assertEquals("new rule content", ruleItem1.getRuleContent());
            
            ruleItem1.checkin( "yeah !" );
            
            assertEquals("yeah !", ruleItem1.getCheckinComment());
            
            RuleItem prev = (RuleItem) ruleItem1.getPredecessorVersionsIterator().next();
            assertEquals("test content", prev.getRuleContent());
            assertFalse("yeah !".equals(prev.getCheckinComment()));
            
            
            assertEquals(prev, ruleItem1.getPrecedingVersion());

    }
    

    public void testCategories() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testAddTag", "test content");
            
            getRepo().loadCategory( "/" ).addCategory( "testAddTagTestTag", "description" );
            
            ruleItem1.addCategory("testAddTagTestTag");
            List tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("testAddTagTestTag", ((CategoryItem)tags.get(0)).getName());
            
            getRepo().loadCategory( "/" ).addCategory( "testAddTagTestTag2", "description" );            
            ruleItem1.addCategory("testAddTagTestTag2");
            tags = ruleItem1.getCategories();
            assertEquals(2, tags.size());   
            
            ruleItem1.checkin( "woot" );
            
            //now test retrieve by tags
            List result = getRepo().findRulesByCategory("testAddTagTestTag");            
            assertEquals(1, result.size());            
            RuleItem retItem = (RuleItem) result.get( 0 );
            assertEquals("testAddTag", retItem.getName());
            
            ruleItem1.updateRuleContent( "foo" );
            ruleItem1.checkin( "latest" );
            
            result = getRepo().findRulesByCategory( "testAddTagTestTag" );
            
            assertEquals(1, result.size());

            ruleItem1 = (RuleItem) result.get( 0 );
            assertEquals(2, ruleItem1.getCategories().size());
            
            assertEquals("foo", ruleItem1.getRuleContent());
            RuleItem prev = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(prev);
            
    }
    
    public void testFindRulesByCategory() throws Exception {
        
        getRepo().loadCategory( "/" ).addCategory( "testFindRulesByCat", "yeah" );
        getDefaultPackage().addRule( "testFindRulesByCategory1", "ya", "testFindRulesByCat" );
        getDefaultPackage().addRule( "testFindRulesByCategory2", "ya", "testFindRulesByCat" );
  
        
        List rules = getRepo().findRulesByCategory( "testFindRulesByCat" );
        assertEquals(2, rules.size());
        
        for ( Iterator iter = rules.iterator(); iter.hasNext(); ) {
            RuleItem element = (RuleItem) iter.next();
            assertTrue(element.getName().startsWith( "testFindRulesByCategory" ));
        }
        
    }
    

    public void testRemoveTag() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testRemoveTag", "test content");
            
            getRepo().loadCategory( "/" ).addCategory( "TestRemoveCategory", "description" );
            
            ruleItem1.addCategory("TestRemoveCategory");   
            List tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            ruleItem1.removeCategory("TestRemoveCategory");
            tags = ruleItem1.getCategories();
            assertEquals(0, tags.size());

            getRepo().loadCategory( "/" ).addCategory( "TestRemoveCategory2", "description" );
            getRepo().loadCategory( "/" ).addCategory( "TestRemoveCategory3", "description" );
            ruleItem1.addCategory("TestRemoveCategory2");                                    
            ruleItem1.addCategory("TestRemoveCategory3");
            ruleItem1.removeCategory("TestRemoveCategory2");
            tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("TestRemoveCategory3", ((CategoryItem)tags.get(0)).getName());            

    }

    public void testGetTags() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testGetTags", "test content");
           
            List tags = ruleItem1.getCategories();
            assertNotNull(tags);
            assertEquals(0, tags.size());
            
            getRepo().loadCategory( "/" ).addCategory( "testGetTagsTestTag", "description" );
            
            ruleItem1.addCategory("testGetTagsTestTag");                                    
            tags = ruleItem1.getCategories();
            assertEquals(1, tags.size());
            assertEquals("testGetTagsTestTag", ((CategoryItem)tags.get(0)).getName());

    }

    public void testSetStateString() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testSetStateString", "test content");
           
            ruleItem1.updateState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());
            
            ruleItem1.updateState("TestState2");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            

    }

    public void testSetStateStateItem() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("foobar", "test description");
           
            StateItem stateItem1 = getRepo().getState("TestState1");
            ruleItem1.updateState(stateItem1);            
            assertNotNull(ruleItem1.getState());
            assertEquals(ruleItem1.getState().getName(), "TestState1");
            
            StateItem stateItem2 = getRepo().getState("TestState2");
            ruleItem1.updateState(stateItem2);
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            

    }

    public void testGetState() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testGetState", "test content");
           
            StateItem stateItem1 = ruleItem1.getState();
            assertNull(stateItem1);
            
            ruleItem1.updateState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());    
            
            ruleItem1 = getDefaultPackage().addRule( "testGetState2", "wa" );
            assertEquals("", ruleItem1.getStateDescription());
            assertNull(ruleItem1.getState());
    }
    

    public void testToString() {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testToString", "test content");
           
            assertNotNull(ruleItem1.toString());                        

    }
    
    public void testGetLastModifiedOnCheckin() throws Exception  {
            RuleItem ruleItem1 = getDefaultPackage().addRule("testGetLastModified", "test content");
           
            Calendar cal = Calendar.getInstance();
            long before = cal.getTimeInMillis();           
            
            Thread.sleep( 100 );
            ruleItem1.updateRuleContent("new lhs");
            ruleItem1.checkin( "woot" );
            Calendar cal2 = ruleItem1.getLastModified();
            long lastMod = cal2.getTimeInMillis();           
            
            cal = Calendar.getInstance();
            long after = cal.getTimeInMillis();
            
            
            
            assertTrue(before < lastMod);
            assertTrue(lastMod < after);

    }
    
    public void testGetDateEffective() {

            RuleItem ruleItem1 = getDefaultPackage().addRule("testGetDateEffective", "test content");
           
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
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetDateExpired", "test content");
           
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
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetRuleLanguage", "test content");
           
            //it should be initialized to 'DRL'
            assertEquals("DRL", ruleItem1.getRuleLanguage());                        
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testSaveAndCheckinDescriptionAndTitle() throws Exception {
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetDescription", "");
            
            //it should be "" to begin with
            assertEquals("", ruleItem1.getDescription());
            
            ruleItem1.updateDescription("test description");
            assertEquals("test description", ruleItem1.getDescription());
            
            
            
            
            assertTrue(getRepo().getSession().hasPendingChanges());
            
            ruleItem1.updateTitle( "This is a title" );
            assertTrue(getRepo().getSession().hasPendingChanges());
            ruleItem1.checkin( "ya" );

            
            //we can save without a checkin
            getRepo().getSession().save();

            assertFalse(getRepo().getSession().hasPendingChanges());

            
            try {
                ruleItem1.getPrecedingVersion().updateTitle( "baaad" );
                fail("should not be able to do this");
            } catch (RulesRepositoryException e) {
                assertNotNull(e.getMessage());
            }
            
    }
    
    public void testGetPrecedingVersion() {
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetPrecedingVersion", "descr");
            assertTrue(ruleItem1.getPrecedingVersion() == null);   
            ruleItem1.updateRuleContent( "test content" );
            ruleItem1.checkin( "boo" );
            
            
            RuleItem predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);            
            
            ruleItem1.updateRuleContent("new content");
            ruleItem1.updateRuleContentURI( "foobar" );
            ruleItem1.checkin( "two changes" );
            
            predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test content", predecessorRuleItem.getRuleContent());
            
            
            
            
            ruleItem1.updateRuleContent("newer lhs");
            ruleItem1.checkin( "another" );
            
            predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("new content", predecessorRuleItem.getRuleContent());
            predecessorRuleItem = (RuleItem) predecessorRuleItem.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test content", predecessorRuleItem.getRuleContent());
 
    }
    
    public void testGetSucceedingVersion() {
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetSucceedingVersion", "test description");

            assertEquals("1", ruleItem1.getVersionNumber());
            
            RuleItem succeedingRuleItem = (RuleItem) ruleItem1.getSucceedingVersion();
            assertTrue(succeedingRuleItem == null);            
            
            ruleItem1.updateRuleContent("new content");
            ruleItem1.checkin( "la" );
            
            assertEquals("2", ruleItem1.getVersionNumber());
            
            RuleItem predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            assertEquals(null, predecessorRuleItem.getRuleContent());
            succeedingRuleItem = (RuleItem) predecessorRuleItem.getSucceedingVersion();
            assertNotNull(succeedingRuleItem);
            assertEquals(ruleItem1.getRuleContent(), succeedingRuleItem.getRuleContent());                       
    } 
    
    public void testGetSuccessorVersionsIterator() {
        try {
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetSuccessorVersionsIterator", "test content");                        
            
            Iterator iterator = ruleItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateRuleContent("new content").checkin( "ya" );
            
            
            iterator = ruleItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            RuleItem predecessorRuleItem = (RuleItem) ruleItem1.getPrecedingVersion();
            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            RuleItem nextRuleItem = (RuleItem) iterator.next();
            assertEquals("new content", nextRuleItem.getRuleContent());
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateRuleContent("newer content");
            ruleItem1.checkin( "boo" );
                        
            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (RuleItem) iterator.next();
            assertEquals("new content", nextRuleItem.getRuleContent());
            assertTrue(iterator.hasNext());
            nextRuleItem = (RuleItem)iterator.next();
            assertEquals("newer content", nextRuleItem.getRuleContent());
            assertFalse(iterator.hasNext());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetPredecessorVersionsIterator() {
        try {
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetPredecessorVersionsIterator", "test description");
            
            Iterator iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());            
            
            ruleItem1.updateRuleContent( "test content" );
            ruleItem1.checkin( "lalalalala" );
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            
            ruleItem1.updateRuleContent("new content");
            ruleItem1.checkin( "boo" );
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            RuleItem nextRuleItem = (RuleItem) iterator.next();
            
            assertEquals("test content", nextRuleItem.getRuleContent());
            
            ruleItem1.updateRuleContent("newer content");
            ruleItem1.checkin( "wee" );
            
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (RuleItem) iterator.next();
            assertTrue(iterator.hasNext());            
            assertEquals("new content", nextRuleItem.getRuleContent());
            nextRuleItem = (RuleItem) iterator.next();
            
            assertEquals("test content", nextRuleItem.getRuleContent());
            
            assertEquals(null, ((RuleItem) iterator.next()).getRuleContent());
            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetTitle() {    
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetTitle", "test content");            
                        
            assertEquals("testGetTitle", ruleItem1.getTitle());
    }
    
    public void testDublinCoreProperties() {
        RulePackageItem pkg = getRepo().createRulePackage( "testDublinCore", "wa" );
        
        RuleItem ruleItem = pkg.addRule( "testDublinCoreProperties", "yeah yeah yeah" );
        ruleItem.updateCoverage( "b" );
        assertEquals("b",ruleItem.getCoverage());
        
        ruleItem.updateLastContributor( "me" );
        ruleItem.checkin( "woo" );
        
        pkg = getRepo().loadRulePackage( "testDublinCore" );
        ruleItem = (RuleItem) pkg.getRules().next();
        
        assertEquals("b", ruleItem.getCoverage());
        assertEquals("me", ruleItem.getLastContributor());
        
    }
    
    public void testGetFormat() {        
            RuleItem ruleItem1 = getRepo().loadDefaultRulePackage().addRule("testGetFormat", "test content");
            
            assertEquals("DRL", ruleItem1.getFormat());     
            
            ruleItem1.updateFormat( "blah" );
            assertEquals("blah", ruleItem1.getFormat());
    }        
}
