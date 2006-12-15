package org.drools.brms.client.modeldriven;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;

public class SuggestionCompletionTest extends TestCase {

    public void testCompletions() {
        
        
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.load(
                     new HashMap() {{
                         put( "Person", new String[] {"age", "name"} );
                         put( "Vehicle", new String[] {"type", "make"} );
                     }},
                     new HashMap() {{
                         put( "Person.name", new String[] {"==", "!="});
                         put( "Person.age", new String[] {"==", "!=", "<", ">"});
                         
                     }},
                     new HashMap() {{
                         put("Vehicle.make", new String[] {"|="});  
                     }},
                     new HashMap() {{
                         put("foo", new String[] {"bar", "baz"});
                     }},  
                     new ArrayList() {{
                         
                     }}, 
                     new ArrayList() {{
                         
                     }}
                     
        );
        
        String[] c =com.getConditionalElements();
        assertEquals("not", c[0]);
        assertEquals("exists", c[1]);
        assertEquals("or", c[2]);
        
        c = com.getFactTypes();
        assertEquals(2, c.length);
        assertContains("Person", c);
        assertContains("Vehicle", c);

        
        c = com.getFieldCompletions( "Person" );
        assertEquals("age", c[0]);
        assertEquals("name", c[1]);
        
        c = com.getFieldCompletions( "Vehicle" );
        assertEquals("type", c[0]);
        assertEquals( "make", c[1] );
        
        c = com.getOperatorCompletions( "Person", "name" );
        assertEquals(2, c.length);
        assertEquals("==", c[0]);
        assertEquals( "!=", c[1] );
        
        
        c = com.getOperatorCompletions( "Person", "age" );
        assertEquals(4, c.length);
        assertEquals(c[0], "==");
        assertEquals(c[1], "!=");
        assertEquals(c[2], "<" );
        assertEquals(c[3], ">" );

        c = com.getConnectiveOperatorCompletions( "Vehicle", "make" );
        assertEquals(1, c.length);
        assertEquals("|=", c[0]);
        
        c = com.getGlobalVariables();
        assertEquals(1, c.length);
        assertEquals("foo", c[0]);
        
        c = com.getFieldCompletionsForGlobalVariable( "foo" );
        assertEquals(2, c.length);
        assertEquals("bar", c[0]);
        assertEquals("baz", c[1]);
        

        
    }
    
    public void testAdd() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        com.addFact( "Foo", new String[] {"a"});
        
        assertEquals(1, com.getFactTypes().length);
        assertEquals("Foo", com.getFactTypes()[0]);
        
        assertEquals(1, com.getFieldCompletions( "Foo" ).length);
        assertEquals("a", com.getFieldCompletions( "Foo" )[0]);
        
    }
    
    private void assertContains(String string,
                                String[] c) {
        
        for ( int i = 0; i < c.length; i++ ) {
            if (string.equals( c[i] )) {
                return;
            }
        } 
        fail( "String array did not contain: " + string );
        
    }


    public void testOperatorMapping() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        assertEquals("is not equal to", com.getOperatorDisplayName("!="));
        assertEquals("is equal to", com.getOperatorDisplayName("=="));        
        assertEquals("xxx", com.getOperatorDisplayName("xxx"));
    }
    
    public void testCEMapping() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        assertEquals("There is no", com.getCEDisplayName( "not" ));
        assertEquals("There exists", com.getCEDisplayName( "exists" ));
        assertEquals("Any of", com.getCEDisplayName( "or" ));
        assertEquals("xxx", com.getCEDisplayName( "xxx" ));
        
    }
    
    public void testActionMapping() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        assertEquals("Assert", com.getActionDisplayName( "assert" ));
        assertEquals("foo", com.getActionDisplayName( "foo" ));
    }
    
    
    public void testGlobalAndFacts() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();

        com.addGlobal( "y", new String[] {"a"} );
        
        assertFalse(com.isGlobalVariable( "x" ));        
        assertTrue(com.isGlobalVariable( "y" ));
    }
    
    
    
    
    
}
