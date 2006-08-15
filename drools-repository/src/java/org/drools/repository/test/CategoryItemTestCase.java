package org.drools.repository.test;

import java.util.List;

import org.drools.repository.RulesRepository;
import org.drools.repository.CategoryItem;

import junit.framework.TestCase;

public class CategoryItemTestCase extends TestCase {
    private RulesRepository rulesRepository = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        rulesRepository = new RulesRepository(true);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        rulesRepository.logout();
    }

    public void testTagItem() {
        try {            
            CategoryItem tagItem1 = rulesRepository.getOrCreateCategory("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());                        
            
            CategoryItem tagItem2 = rulesRepository.getOrCreateCategory("TestTag");
            assertNotNull(tagItem2);
            assertEquals("TestTag", tagItem2.getName());
            assertEquals(tagItem1, tagItem2);
            
            rulesRepository.getOrCreateCategory( "Foo" );
            
            List cats = rulesRepository.listCategoryNames();
            assertEquals(2, cats.size());
            
            assertEquals("TestTag", cats.get( 0 ));
            assertEquals("Foo", cats.get( 1 ));
            
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }        
    }    
    
    public void testGetChildTags() {
        try {            
            CategoryItem tagItem1 = rulesRepository.getOrCreateCategory("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());                        
            
            List childTags = tagItem1.getChildTags();
            assertNotNull(childTags);
            assertEquals(0, childTags.size());
            
            CategoryItem childTagItem1 = tagItem1.getChildTag("TestChildTag1");
            assertNotNull(childTagItem1);
            assertEquals("TestChildTag1", childTagItem1.getName());
            
            childTags = tagItem1.getChildTags();
            assertNotNull(childTags);
            assertEquals(1, childTags.size());
            assertEquals("TestChildTag1", ((CategoryItem)childTags.get(0)).getName());
            
            CategoryItem childTagItem2 = tagItem1.getChildTag("TestChildTag2");
            assertNotNull(childTagItem2);
            assertEquals("TestChildTag2", childTagItem2.getName());
            
            childTags = tagItem1.getChildTags();
            assertNotNull(childTags);
            assertEquals(2, childTags.size());
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
    }
    
    public void testGetChildTag() {
        try {            
            CategoryItem tagItem1 = rulesRepository.getOrCreateCategory("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());                        
            
            //test that child is added if not already in existence
            List childTags = tagItem1.getChildTags();
            assertNotNull(childTags);
            assertEquals(0, childTags.size());
            
            CategoryItem childTagItem1 = tagItem1.getChildTag("TestChildTag1");
            assertNotNull(childTagItem1);
            assertEquals("TestChildTag1", childTagItem1.getName());
                        
            //test that if already there, it is returned
            CategoryItem childTagItem2 = tagItem1.getChildTag("TestChildTag1");
            assertNotNull(childTagItem2);
            assertEquals("TestChildTag1", childTagItem2.getName());
            assertEquals(childTagItem1, childTagItem2);
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
    }
    
    public void testGetFullPath() {
        try {            
            CategoryItem tagItem1 = rulesRepository.getOrCreateCategory("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getFullPath());                        
                                    
            CategoryItem childTagItem1 = tagItem1.getChildTag("TestChildTag1");
            assertNotNull(childTagItem1);
            assertEquals("TestTag/TestChildTag1", childTagItem1.getFullPath());
                        
            CategoryItem childTagItem2 = childTagItem1.getChildTag("TestChildTag2");
            assertNotNull(childTagItem2);
            assertEquals("TestTag/TestChildTag1/TestChildTag2", childTagItem2.getFullPath());
            
            
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
    }
}
