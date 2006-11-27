package org.drools.brms.client.modeldriven;

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
        
        com.addBoundFact( "myFact", "Person" );
        
        c = com.getFieldCompletionsForBoundFact( "myFact" );
        assertEquals(2, c.length);
        assertEquals("age", c[0]);
        assertEquals("name", c[1]);
        
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

    public void testBoundVariables() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        com.addBoundFact( "foo", "Person" );
        com.addBoundFact( "bar", "Vehicle" );
        assertEquals(0, com.getBoundFields().length);
        assertEquals(2, com.getBoundFacts().length);
        
        com.addBoundField( "x" );
        assertEquals(1, com.getBoundFields().length);
        
        assertEquals( "foo", com.getBoundFacts()[0]);
        assertEquals( "bar", com.getBoundFacts()[1]);
        
        assertEquals( "x", com.getBoundFields()[0]);
        
        
        
    }
    
    
    
}
