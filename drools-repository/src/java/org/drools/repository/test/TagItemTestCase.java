package org.drools.repository.test;

import java.util.List;

import org.drools.repository.RulesRepository;
import org.drools.repository.TagItem;

import junit.framework.TestCase;

public class TagItemTestCase extends TestCase {
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
            TagItem tagItem1 = rulesRepository.getTag("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());                        
            
            TagItem tagItem2 = rulesRepository.getTag("TestTag");
            assertNotNull(tagItem2);
            assertEquals("TestTag", tagItem2.getName());
            assertEquals(tagItem1, tagItem2);
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }        
    }    
    
    public void testGetChildTags() {
        try {            
            TagItem tagItem1 = rulesRepository.getTag("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());                        
            
            List childTags = tagItem1.getChildTags();
            assertNotNull(childTags);
            assertEquals(0, childTags.size());
            
            TagItem childTagItem1 = tagItem1.getChildTag("TestChildTag1");
            assertNotNull(childTagItem1);
            assertEquals("TestChildTag1", childTagItem1.getName());
            
            childTags = tagItem1.getChildTags();
            assertNotNull(childTags);
            assertEquals(1, childTags.size());
            assertEquals("TestChildTag1", ((TagItem)childTags.get(0)).getName());
            
            TagItem childTagItem2 = tagItem1.getChildTag("TestChildTag2");
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
            TagItem tagItem1 = rulesRepository.getTag("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getName());                        
            
            //test that child is added if not already in existence
            List childTags = tagItem1.getChildTags();
            assertNotNull(childTags);
            assertEquals(0, childTags.size());
            
            TagItem childTagItem1 = tagItem1.getChildTag("TestChildTag1");
            assertNotNull(childTagItem1);
            assertEquals("TestChildTag1", childTagItem1.getName());
                        
            //test that if already there, it is returned
            TagItem childTagItem2 = tagItem1.getChildTag("TestChildTag1");
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
            TagItem tagItem1 = rulesRepository.getTag("TestTag");
            assertNotNull(tagItem1);
            assertEquals("TestTag", tagItem1.getFullPath());                        
                                    
            TagItem childTagItem1 = tagItem1.getChildTag("TestChildTag1");
            assertNotNull(childTagItem1);
            assertEquals("TestTag/TestChildTag1", childTagItem1.getFullPath());
                        
            TagItem childTagItem2 = childTagItem1.getChildTag("TestChildTag2");
            assertNotNull(childTagItem2);
            assertEquals("TestTag/TestChildTag1/TestChildTag2", childTagItem2.getFullPath());
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
    }
}
