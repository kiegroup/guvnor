package org.drools.repository;

import java.util.Calendar;
import java.util.Iterator;

import javax.jcr.NodeIterator;

import junit.framework.TestCase;

public class RulesRepositoryTestCase extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testDefaultPackage() throws Exception {
        RulesRepository repo = RepositorySession.getRepository();
        
        Iterator it = repo.listPackages();
        boolean foundDefault = false;
        while(it.hasNext()) {
            RulePackageItem item = (RulePackageItem) it.next();
            if (item.getName().equals( RulesRepository.DEFAULT_PACKAGE )) {
                foundDefault = true;
            }
        }
        assertTrue(foundDefault);
        
        RulePackageItem def = repo.loadDefaultRulePackage();
        assertNotNull(def);
        assertEquals("default", def.getName());
        
        
    }
    
    public void testAddVersionARule() throws Exception {
        RulesRepository repo = RepositorySession.getRepository();
        RulePackageItem pack = repo.createRulePackage( "testAddVersionARule", "description" );
        repo.save();
        
        RuleItem rule = pack.addRule( "my rule", "foobar" );
        assertEquals("my rule", rule.getName());
        
        rule.updateRuleContent( "foo foo" );
        rule.checkin( "foobar" );
        
        pack.addRule( "other rule", "description" );
        
        RulePackageItem pack2 =  repo.loadRulePackage( "testAddVersionARule" );
        
        Iterator it =  pack2.getRules();
        
        it.next();
        it.next();
        
        assertFalse(it.hasNext());
        
        RuleItem prev = (RuleItem) rule.getPrecedingVersion();
       
        assertEquals("foo foo", rule.getRuleContent());
        assertFalse("foo foo".equals( prev.getRuleContent() ));
        
        
        
    }

    
    public void testLoadRuleByUUID() throws Exception {
        RulesRepository repo = RepositorySession.getRepository();
        
        RulePackageItem rulePackageItem = repo.loadDefaultRulePackage();
        RuleItem rule = rulePackageItem.addRule( "testLoadRuleByUUID", "this is a description");
        
        repo.save();
                
        String uuid = rule.getNode().getUUID();

        RuleItem loaded = repo.loadRuleByUUID(uuid);
        assertNotNull(loaded);
        assertEquals("testLoadRuleByUUID", loaded.getName());
        assertEquals( "this is a description", loaded.getDescription());
        
        // try loading rule package that was not created 
        try {
            repo.loadRuleByUUID("01010101-0101-0101-0101-010101010101");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }        
    }
    
    public void testAddRuleCalendarWithDates() {
        RulesRepository rulesRepository = RepositorySession.getRepository();

                        
            Calendar effectiveDate = Calendar.getInstance();
            Calendar expiredDate = Calendar.getInstance();
            expiredDate.setTimeInMillis(effectiveDate.getTimeInMillis() + (1000 * 60 * 60 * 24));
            RuleItem ruleItem1 = rulesRepository.loadDefaultRulePackage().addRule("testAddRuleCalendarCalendar", "desc");
            ruleItem1.updateDateEffective( effectiveDate );
            ruleItem1.updateDateExpired( expiredDate );
     
            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals(effectiveDate, ruleItem1.getDateEffective());
            assertEquals(expiredDate, ruleItem1.getDateExpired());
            
            ruleItem1.checkin( "ho " );            
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
        
        
            RulePackageItem rulePackageItem1 = rulesRepository.createRulePackage("testListPackages", "desc");
            
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
