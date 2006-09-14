package org.drools.repository;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;

import org.drools.repository.*;

import junit.framework.TestCase;

public class RulesRepositoryTestCase extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddRuleDslItemBoolean() {
        RulesRepository rulesRepository = RepositorySession.getRepository();
            
            
            
            DslItem dslItem1 = rulesRepository.addDsl("testAddRuleDslItemBoolean", "original content");
            assertNotNull(dslItem1);
            
            RuleItem ruleItem1 = rulesRepository.addRule("testAddRuleDslItemBoolean", "test lhs content", "test rhs content", dslItem1, true);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            
            //test that this follows the head version
            
            dslItem1.updateContent("new content");
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            
            //now do the same thing, but test not following head:                                    
            RuleItem ruleItem2 = rulesRepository.addRule("testAddRuleDslItemBoolean2", "test lhs content", "test rhs content", dslItem1, false);
            
            assertNotNull(ruleItem2);
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem2.getDsl().getContent());
            
            //test that this stays tied to the specific revision of the DSL node
            String originalContent = ruleItem2.getDsl().getContent();
            dslItem1.updateContent("new content");
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(originalContent, ruleItem2.getDsl().getContent());
    }
    
    public void testAddRuleCalendarCalendar() {
        RulesRepository rulesRepository = RepositorySession.getRepository();

                        
            Calendar effectiveDate = Calendar.getInstance();
            Calendar expiredDate = Calendar.getInstance();
            expiredDate.setTimeInMillis(effectiveDate.getTimeInMillis() + (1000 * 60 * 60 * 24));
            RuleItem ruleItem1 = rulesRepository.addRule("testAddRuleCalendarCalendar", "test lhs content", "test rhs content", effectiveDate, expiredDate);
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals(effectiveDate, ruleItem1.getDateEffective());
            assertEquals(expiredDate, ruleItem1.getDateExpired());                       
    }
    
    public void testAddRuleDslItemBooleanCalendarCalendar() {
        RulesRepository rulesRepository = RepositorySession.getRepository();
            DslItem dslItem1 = rulesRepository.addDsl("testAddRuleDslItemBooleanCalendarCalendar", "content here");
            assertNotNull(dslItem1);
            
            Calendar effectiveDate = Calendar.getInstance();
            Calendar expiredDate = Calendar.getInstance();
            expiredDate.setTimeInMillis(effectiveDate.getTimeInMillis() + (1000 * 60 * 60 * 24));
            
            RuleItem ruleItem1 = rulesRepository.addRule("testAddRuleDslItemBooleanCalendarCalendar", "test lhs content", "test rhs content", dslItem1, true, effectiveDate, expiredDate, "test description");
            
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            assertEquals(effectiveDate, ruleItem1.getDateEffective());
            assertEquals(expiredDate, ruleItem1.getDateExpired());
            assertEquals("test description", ruleItem1.getDescription());
            
            //test that this follows the head version
            
            dslItem1.updateContent("more content");
            assertNotNull(ruleItem1.getNode());
            assertNotNull(ruleItem1.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem1.getDsl().getContent());
            
            //now do the same thing, but test not following head:                                    
            RuleItem ruleItem2 = rulesRepository.addRule("testAddRuleDslItemBooleanCalendarCalendar2", "test lhs content", "test rhs content", dslItem1, false, effectiveDate, expiredDate, "test description 2");
            
            assertNotNull(ruleItem2);
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(dslItem1.getContent(), ruleItem2.getDsl().getContent());
            assertEquals(effectiveDate, ruleItem2.getDateEffective());
            assertEquals(expiredDate, ruleItem2.getDateExpired());
            assertEquals("test description 2", ruleItem2.getDescription());
            
            //test that this stays tied to the specific revision of the DSL node
            String originalContent = ruleItem2.getDsl().getContent();
            dslItem1.updateContent("more content");
            assertNotNull(ruleItem2.getNode());
            assertNotNull(ruleItem2.getDsl());
            assertEquals(originalContent, ruleItem2.getDsl().getContent());
    }
    

    public void testGetState() {
        RulesRepository rulesRepository = RepositorySession.getRepository();
            
            
            StateItem stateItem1 = rulesRepository.getState("testGetState");
            assertNotNull(stateItem1);
            assertEquals("testGetState", stateItem1.getName());
            
            StateItem stateItem2 = rulesRepository.getState("testGetState");
            assertNotNull(stateItem2);
            assertEquals("testGetState", stateItem2.getName());
            assertEquals(stateItem1, stateItem2);
    }

    public void testGetTag() {
            RulesRepository rulesRepository = RepositorySession.getRepository();
            
            CategoryItem root = rulesRepository.loadCategory( "/" );
            CategoryItem tagItem1 = root.addCategory( "testGetTag", "ho");
            assertNotNull(tagItem1);
            assertEquals("testGetTag", tagItem1.getName());
            assertEquals("testGetTag", tagItem1.getFullPath());
            
            CategoryItem tagItem2 = rulesRepository.loadCategory("testGetTag");
            assertNotNull(tagItem2);
            assertEquals("testGetTag", tagItem2.getName());
            assertEquals(tagItem1, tagItem2);
            
            //now test getting a tag down in the tag hierarchy
            CategoryItem tagItem3 = tagItem2.addCategory( "TestChildTag1", "ka");
            assertNotNull(tagItem3);
            assertEquals("TestChildTag1", tagItem3.getName());
            assertEquals("testGetTag/TestChildTag1", tagItem3.getFullPath());                                   
    }
    
    public void testAddFunctionStringString() {
        RulesRepository rulesRepository = RepositorySession.getRepository();
            FunctionItem functionItem1 = rulesRepository.addFunction("testAddFunctionStringString", "test content");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("testAddFunctionStringString", functionItem1.getName());
            assertEquals("test content", functionItem1.getContent());
            assertEquals("", functionItem1.getDescription());
    }
    
    public void testAddFunctionStringStringString() {
        RulesRepository rulesRepository = RepositorySession.getRepository();
                        
            FunctionItem functionItem1 = rulesRepository.addFunction("testAddFunctionStringStringString", "test content", "test description");
            
            assertNotNull(functionItem1);
            assertNotNull(functionItem1.getNode());
            assertEquals("testAddFunctionStringStringString", functionItem1.getName());
            assertEquals("test content", functionItem1.getContent());
            assertEquals("test description", functionItem1.getDescription());
    }
    
    public void testListPackages() {
        RulesRepository rulesRepository = RepositorySession.getRepository();
        
        
            RulePackageItem rulePackageItem1 = rulesRepository.createRulePackage("testListPackages");
            
            Iterator it = rulesRepository.listPackages();
            assertTrue(it.hasNext());
            
            boolean found = false;
            while ( it.hasNext() ) {
                RulePackageItem element = (RulePackageItem) it.next();
                if (element.getName().equals( "testListPackages" ))
                {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
            
    }
}
