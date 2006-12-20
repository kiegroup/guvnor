package org.drools.repository;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class AssetItemTest extends TestCase {


    private RulesRepository getRepo() {
        return RepositorySessionUtil.getRepository();
    }
    
    private PackageItem getDefaultPackage() {
        return getRepo().loadDefaultPackage();
    }
    
    public void testAssetItemCreation() throws Exception {
                
            Calendar now = Calendar.getInstance();
        
            Thread.sleep(500); //MN: need this sleep to get the correct date
            
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testRuleItem", "test content");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("testRuleItem", ruleItem1.getName());
        
            assertNotNull(ruleItem1.getCreatedDate());
            
            assertTrue(now.before( ruleItem1.getCreatedDate() ));
            
            String packName = getDefaultPackage().getName();

            assertEquals(packName, ruleItem1.getPackageName());
            
            assertNotNull(ruleItem1.getUUID());
            
        //try constructing with node of wrong type
        try {
            
            PackageItem pitem = getRepo().loadDefaultPackage();
            new AssetItem(getRepo(), pitem.getNode());
            fail("Exception not thrown for node of wrong type");
        }
        catch(RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }

    public void testGetContent() {
            
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetContent", "test content");
            ruleItem1.updateContent( "test content" );
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("test content", ruleItem1.getContent());
    }

    public void testGetURI() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetURI", "blah");
            ruleItem1.checkin( "version0" );
            ruleItem1.updateContentURI( "foo/bar" );
            ruleItem1.checkin( "ha !" );
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals("foo/bar", ruleItem1.getContentURI());
    }
    
    public void testUpdateContent() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testUpdateContent", "test description");
            ruleItem1.updateContent( "test content" );            
            ruleItem1.checkin( "yeah" );
            
            ruleItem1.updateContent( "new rule content");
            
            assertEquals("new rule content", ruleItem1.getContent());
            
            ruleItem1.checkin( "yeah !" );
            
            assertEquals("yeah !", ruleItem1.getCheckinComment());
            
            AssetItem prev = (AssetItem) ruleItem1.getPredecessorVersionsIterator().next();
            assertEquals("test content", prev.getContent());
            assertFalse("yeah !".equals(prev.getCheckinComment()));
            
            
            assertEquals(prev, ruleItem1.getPrecedingVersion());

    }
    

    public void testCategories() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testAddTag", "test content");
            
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
            List result = getRepo().findAssetsByCategory("testAddTagTestTag");            
            assertEquals(1, result.size());            
            AssetItem retItem = (AssetItem) result.get( 0 );
            assertEquals("testAddTag", retItem.getName());
            
            ruleItem1.updateContent( "foo" );
            ruleItem1.checkin( "latest" );
            
            result = getRepo().findAssetsByCategory( "testAddTagTestTag" );
            
            assertEquals(1, result.size());

            ruleItem1 = (AssetItem) result.get( 0 );
            assertEquals(2, ruleItem1.getCategories().size());
            
            assertEquals("foo", ruleItem1.getContent());
            AssetItem prev = (AssetItem) ruleItem1.getPrecedingVersion();
            assertNotNull(prev);
            
    }
    
    public void testFindRulesByCategory() throws Exception {
        
        getRepo().loadCategory( "/" ).addCategory( "testFindRulesByCat", "yeah" );
        AssetItem as1 = getDefaultPackage().addAsset( "testFindRulesByCategory1", "ya", "testFindRulesByCat", "drl" );
        getDefaultPackage().addAsset( "testFindRulesByCategory2", "ya", "testFindRulesByCat", AssetItem.DEFAULT_CONTENT_FORMAT ).checkin( "version0" );
  
        as1.checkin( "version0" );
        
        assertEquals("drl", as1.getFormat());
        
        List rules = getRepo().findAssetsByCategory( "testFindRulesByCat" );
        assertEquals(2, rules.size());
        
        for ( Iterator iter = rules.iterator(); iter.hasNext(); ) {
            AssetItem element = (AssetItem) iter.next();
            assertTrue(element.getName().startsWith( "testFindRulesByCategory" ));
        }
        
    }
    

    public void testRemoveTag() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testRemoveTag", "test content");
            
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
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetTags", "test content");
           
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
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testSetStateString", "test content");
           
            ruleItem1.updateState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());
            
            ruleItem1.updateState("TestState2");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            

    }

    public void testSetStateStateItem() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("foobar", "test description");
           
            StateItem stateItem1 = getRepo().getState("TestState1");
            ruleItem1.updateState(stateItem1);            
            assertNotNull(ruleItem1.getState());
            assertEquals(ruleItem1.getState().getName(), "TestState1");
            
            StateItem stateItem2 = getRepo().getState("TestState2");
            ruleItem1.updateState(stateItem2);
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState2", ruleItem1.getState().getName());            

    }

    public void testStatusStuff() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetState", "test content");
           
            StateItem stateItem1 = ruleItem1.getState();
            assertEquals(StateItem.DRAFT_STATE_NAME, stateItem1.getName());
            
            ruleItem1.updateState("TestState1");
            assertNotNull(ruleItem1.getState());
            assertEquals("TestState1", ruleItem1.getState().getName());    
            
            ruleItem1 = getDefaultPackage().addAsset( "testGetState2", "wa" );
            assertEquals(StateItem.DRAFT_STATE_NAME, ruleItem1.getStateDescription());
            assertEquals(getRepo().getState( StateItem.DRAFT_STATE_NAME ), ruleItem1.getState());
    }
    

    public void testToString() {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testToString", "test content");
           
            assertNotNull(ruleItem1.toString());                        

    }
    
    public void testGetLastModifiedOnCheckin() throws Exception  {
            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetLastModified", "test content");
           
            Calendar cal = Calendar.getInstance();
            long before = cal.getTimeInMillis();           
            
            Thread.sleep( 100 );
            ruleItem1.updateContent("new lhs");
            ruleItem1.checkin( "woot" );
            Calendar cal2 = ruleItem1.getLastModified();
            long lastMod = cal2.getTimeInMillis();           
            
            cal = Calendar.getInstance();
            long after = cal.getTimeInMillis();
            
            
            
            assertTrue(before < lastMod);
            assertTrue(lastMod < after);

    }
    
    public void testGetDateEffective() {

            AssetItem ruleItem1 = getDefaultPackage().addAsset("testGetDateEffective", "test content");
           
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
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetDateExpired", "test content");
           
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
    
    
    public void testSaveAndCheckinDescriptionAndTitle() throws Exception {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetDescription", "");
            ruleItem1.checkin( "version0" );
            
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
        
            getRepo().loadCategory( "/" ).addCategory( "foo", "ka" );
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetPrecedingVersion", "descr");
            ruleItem1.checkin( "version0" );
            assertTrue(ruleItem1.getPrecedingVersion() == null);
            
            ruleItem1.addCategory( "foo" );
            ruleItem1.updateContent( "test content" );
            ruleItem1.checkin( "boo" );
            
            AssetItem predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);            
            
            ruleItem1.updateContent("new content");
            ruleItem1.updateContentURI( "foobar" );
            ruleItem1.checkin( "two changes" );
            
            predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals(1, predecessorRuleItem.getCategories().size());
            CategoryItem cat = (CategoryItem) predecessorRuleItem.getCategories().get( 0 );
            assertEquals("foo", cat.getName());
            
            assertEquals("test content", predecessorRuleItem.getContent());
            assertEquals("descr", predecessorRuleItem.getDescription());
            assertEquals("default", predecessorRuleItem.getPackageName());
            
            ruleItem1.updateContent("newer lhs");
            ruleItem1.checkin( "another" );
            
            predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("new content", predecessorRuleItem.getContent());
            predecessorRuleItem = (AssetItem) predecessorRuleItem.getPrecedingVersion();
            assertNotNull(predecessorRuleItem);
            assertEquals("test content", predecessorRuleItem.getContent());
 
    }
    
    public void testGetSucceedingVersion() {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetSucceedingVersion", "test description");
            ruleItem1.checkin( "version0" );

            assertEquals("1", ruleItem1.getVersionNumber());
            
            AssetItem succeedingRuleItem = (AssetItem) ruleItem1.getSucceedingVersion();
            assertTrue(succeedingRuleItem == null);            
            
            ruleItem1.updateContent("new content");
            ruleItem1.checkin( "la" );
            
            assertEquals("2", ruleItem1.getVersionNumber());
            
            AssetItem predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            assertEquals(null, predecessorRuleItem.getContent());
            succeedingRuleItem = (AssetItem) predecessorRuleItem.getSucceedingVersion();
            assertNotNull(succeedingRuleItem);
            assertEquals(ruleItem1.getContent(), succeedingRuleItem.getContent());                       
    } 
    
    public void testGetSuccessorVersionsIterator() {
        try {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetSuccessorVersionsIterator", "test content");
            ruleItem1.checkin( "version0" );
            
            Iterator iterator = ruleItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateContent("new content").checkin( "ya" );
            
            
            iterator = ruleItem1.getSuccessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());
            
            AssetItem predecessorRuleItem = (AssetItem) ruleItem1.getPrecedingVersion();
            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            AssetItem nextRuleItem = (AssetItem) iterator.next();
            assertEquals("new content", nextRuleItem.getContent());
            assertFalse(iterator.hasNext());
            
            ruleItem1.updateContent("newer content");
            ruleItem1.checkin( "boo" );
                        
            iterator = predecessorRuleItem.getSuccessorVersionsIterator();
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (AssetItem) iterator.next();
            assertEquals("new content", nextRuleItem.getContent());
            assertTrue(iterator.hasNext());
            nextRuleItem = (AssetItem)iterator.next();
            assertEquals("newer content", nextRuleItem.getContent());
            assertFalse(iterator.hasNext());            
        }
        catch(Exception e) {
            fail("Caught unexpected exception: " + e);
        }
    }
    
    public void testGetPredecessorVersionsIterator() {
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetPredecessorVersionsIterator", "test description");
            ruleItem1.checkin( "version0" );
            
            Iterator iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertFalse(iterator.hasNext());            
            
            ruleItem1.updateContent( "test content" );
            ruleItem1.checkin( "lalalalala" );
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            
            ruleItem1.updateContent("new content");
            ruleItem1.checkin( "boo" );
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            AssetItem nextRuleItem = (AssetItem) iterator.next();
            
            assertEquals("test content", nextRuleItem.getContent());
            
            ruleItem1.updateContent("newer content");
            ruleItem1.checkin( "wee" );
            
            
            iterator = ruleItem1.getPredecessorVersionsIterator();            
            assertNotNull(iterator);
            assertTrue(iterator.hasNext());
            nextRuleItem = (AssetItem) iterator.next();
            assertTrue(iterator.hasNext());            
            assertEquals("new content", nextRuleItem.getContent());
            nextRuleItem = (AssetItem) iterator.next();
            
            assertEquals("test content", nextRuleItem.getContent());
            
            assertEquals(null, ((AssetItem) iterator.next()).getContent());

    }
    
    public void testGetTitle() {    
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetTitle", "test content");            
                        
            assertEquals("testGetTitle", ruleItem1.getTitle());
    }
    
    public void testDublinCoreProperties() {
        PackageItem pkg = getRepo().createPackage( "testDublinCore", "wa" );
        
        AssetItem ruleItem = pkg.addAsset( "testDublinCoreProperties", "yeah yeah yeah" );
        ruleItem.updateCoverage( "b" );
        assertEquals("b",ruleItem.getCoverage());
        
        ruleItem.updateLastContributor( "me" );
        ruleItem.checkin( "woo" );
        
        pkg = getRepo().loadPackage( "testDublinCore" );
        ruleItem = (AssetItem) pkg.getAssets().next();
        
        assertEquals("b", ruleItem.getCoverage());
        assertEquals("me", ruleItem.getLastContributor());
        
        assertEquals("", ruleItem.getExternalRelation());
        assertEquals("", ruleItem.getExternalSource());
        
    }
    
    public void testGetFormat() {        
            AssetItem ruleItem1 = getRepo().loadDefaultPackage().addAsset("testGetFormat", "test content");
            
            assertEquals(AssetItem.DEFAULT_CONTENT_FORMAT, ruleItem1.getFormat());     
            
            ruleItem1.updateFormat( "blah" );
            assertEquals("blah", ruleItem1.getFormat());
    }        
    
    public void testAnonymousProperties() {
        AssetItem item = getRepo().loadDefaultPackage().addAsset( "anonymousproperty", "lalalalala" );
        item.updateUserProperty( "fooBar", "value");
        assertEquals("value", item.getUserProperty("fooBar"));
        
        
        
        item.checkin( "lalalala" );
        try {
            item.updateUserProperty( "drools:content", "whee" );
            fail("should not be able to set build in properties this way.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        
    }
}
