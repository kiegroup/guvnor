package org.drools.repository;


import junit.framework.TestCase;

import org.drools.repository.DslItem;
import org.drools.repository.RuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

public class DslItemTestCase extends TestCase {

    private RulesRepository getRepo() {
        return RepositorySession.getRepository();
    }
    
    public void testDslItem() {
        
        try {
            //This calls the constructor
            String meth = StackUtil.getCurrentMethodName();
            DslItem dslItem1 = getRepo().addDsl(meth, "blah");
            
            assertNotNull(dslItem1);
            assertNotNull(dslItem1.getNode());         
            assertEquals(meth, dslItem1.getName());
        }
        catch(Exception e) {
            fail("Unexpected Exception caught: " + e);
        }
        
        //try constructing a DslItem object with the wrong node type
        try {
            //Get a reference to a node of the incorrect type
            RuleItem ruleItem1 = this.getRepo().addRule("test rule", "test lhs content", "test rhs content");
            
            //this should fail
            DslItem dslItem2 = new DslItem(getRepo(), ruleItem1.getNode());
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
            DslItem dslItem1 = getRepo().addDsl(StackUtil.getCurrentMethodName(), "[then]Send escalation email=sendEscalationEmail( customer, ticket );");
            
            assertNotNull(dslItem1);
            assertEquals("[then]Send escalation email=sendEscalationEmail( customer, ticket );", dslItem1.getContent());
            assertEquals("DSL", dslItem1.getFormat());        
    }
       
    
}
