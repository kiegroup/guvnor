package org.drools.repository.util;

import junit.framework.TestCase;

public class DefaultVersionNumberGeneratorTest extends TestCase {

    public void testGenerator() {
        DefaultVersionNumberGenerator gen = new DefaultVersionNumberGenerator();
        
        assertEquals("1", gen.calculateNextVersion( null, null ));
        assertEquals("1", gen.calculateNextVersion( "0", null ));
        assertEquals("1", gen.calculateNextVersion( "", null ));
        
        assertEquals("42", gen.calculateNextVersion( "41", null ));
        
        assertEquals("1000", gen.calculateNextVersion( "999", null ));
        
        try {
            gen.calculateNextVersion( "a", null );
            fail("should not be able to take a letter for default.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
    
}
