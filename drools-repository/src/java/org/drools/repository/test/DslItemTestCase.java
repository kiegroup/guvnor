package org.drools.repository.test;

import java.io.File;

import org.drools.repository.DslItem;
import org.drools.repository.RuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

import junit.framework.TestCase;

public class DslItemTestCase extends TestCase {
    private RulesRepository rulesRepository;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        rulesRepository = new RulesRepository(true);
    }

    protected void tearDown() throws Exception {        
        super.tearDown();  
        rulesRepository.logout();
    }

    public void testDslItem() {               
        try {
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl1.dsl");
            
            //This calls the constructor
            DslItem dslItem1 = rulesRepository.addDslFromFile(dslFile1);
            
            assertNotNull(dslItem1);
            assertNotNull(dslItem1.getNode());         
            assertEquals("dsl1.dsl", dslItem1.getName());
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
        
        //try constructing a DslItem object with the wrong node type
        try {
            File drlFile1 = new File("./src/java/org/drools/repository/test/test_data/rule1.drl");
            
            //Get a reference to a node of the incorrect type
            RuleItem ruleItem1 = this.rulesRepository.addRule("test rule", "test lhs content", "test rhs content");
            
            //this should fail
            DslItem dslItem2 = new DslItem(rulesRepository, ruleItem1.getNode());
            fail("Exception not thrown by constructor for node of type: " + ruleItem1.getNode().getPrimaryNodeType().getName());            
        }
        catch(RulesRepositoryException e) {
            //this is what we expect
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
    }

    public void testGetContent() {
        try {
            File dslFile1 = new File("./src/java/org/drools/repository/test/test_data/dsl2.dsl");
            
            //This calls the constructor
            DslItem dslItem1 = rulesRepository.addDslFromFile(dslFile1);
            
            assertNotNull(dslItem1);
            assertEquals("[then]Send escalation email=sendEscalationEmail( customer, ticket );", dslItem1.getContent());
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
    }
}
