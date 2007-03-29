package org.drools.brms.client.modeldriven;

import junit.framework.TestCase;

public class HumanReadableTest extends TestCase {

    public void testOperatorMapping() {

        assertEquals("is not equal to", HumanReadable.getOperatorDisplayName("!="));
        assertEquals("is equal to", HumanReadable.getOperatorDisplayName("=="));        
        assertEquals("xxx", HumanReadable.getOperatorDisplayName("xxx"));
    }
    
    public void testCEMapping() {

        assertEquals("There is no", HumanReadable.getCEDisplayName( "not" ));
        assertEquals("There exists", HumanReadable.getCEDisplayName( "exists" ));
        assertEquals("Any of", HumanReadable.getCEDisplayName( "or" ));
        assertEquals("xxx", HumanReadable.getCEDisplayName( "xxx" ));
        
    }
    
    public void testActionMapping() {

        assertEquals("Assert", HumanReadable.getActionDisplayName( "assert" ));
        assertEquals("foo", HumanReadable.getActionDisplayName( "foo" ));
    }
    
    
    
    
    
    
}
