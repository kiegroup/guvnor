package org.drools.repository;

import java.util.Iterator;
import java.util.List;

import org.drools.repository.RulesRepository;
import org.drools.repository.CategoryItem;

import junit.framework.TestCase;

public class CategoryItemTestCase extends TestCase {

    
    
    public void testTagItem() {
            CategoryItem tagItem1 = getRepo().getOrCreateCategory("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());                        
            
            CategoryItem tagItem2 = getRepo().getOrCreateCategory("TestTag");
            assertNotNull(tagItem2);
            assertEquals("TestTag", tagItem2.getName());
            assertEquals(tagItem1, tagItem2);
            
            List originalCats = getRepo().listCategoryNames();
            
            
            getRepo().getOrCreateCategory( "FootestTagItem" );
            
            List cats = getRepo().listCategoryNames();
            assertEquals(originalCats.size() + 1, cats.size());
            
            
            boolean found = false;
            for ( Iterator iter = cats.iterator(); iter.hasNext(); ) {
                String element = (String) iter.next();
                if (element.equals( "FootestTagItem" )) {
                    found = true; break;
                }
            }
            
            assertTrue(found);
    
    }    
    
    public void testGetChildTags() {
        try {            
            CategoryItem tagItem1 = getRepo().getOrCreateCategory("TestTag");
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
            CategoryItem tagItem1 = getRepo().getOrCreateCategory("testGetChildTag");
            assertNotNull(tagItem1);
            assertEquals("testGetChildTag", tagItem1.getName());                        
            
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
            CategoryItem tagItem1 = getRepo().getOrCreateCategory("TestTag");
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

    private RulesRepository getRepo() {
        return RepositorySession.getRepository();
    }
}
