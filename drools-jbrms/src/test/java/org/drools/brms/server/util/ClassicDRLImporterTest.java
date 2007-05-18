package org.drools.brms.server.util;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

public class ClassicDRLImporterTest extends TestCase {

    
    
    public void testStandardDRL() throws Exception {
        
        
        ClassicDRLImporter imp = new ClassicDRLImporter(getDrl("sample_legacy.drl"));
        assertEquals( "foo", imp.getPackageName() );
        assertEquals(2, imp.getRules().size());
        
        assertEquals("blah", imp.getRules().get( 0 ).name);
        assertEquals("cha", imp.getRules().get( 1 ).name);
        
        System.err.println(imp.getPackageHeader());
        
        assertTrue(imp.getPackageHeader().indexOf( "import goo.wee" ) > -1);
        assertTrue(imp.getPackageHeader().indexOf( "package" ) == -1);
        
        assertFalse(imp.isDSLEnabled());
        
        assertEqualsIgnoreWhitespace( "when Whee() then goo();", imp.getRules().get( 0 ).content);
        assertEqualsIgnoreWhitespace( "when Sup() then ka();", imp.getRules().get( 1 ).content);
        
        
    }
    
    public void testWithDSL() throws Exception {
        
        ClassicDRLImporter imp = new ClassicDRLImporter(getDrl("sample_legacy_with_dsl.drl"));

        assertTrue(imp.isDSLEnabled());
        assertEquals(2, imp.getRules().size());
        assertEquals("foo", imp.getPackageName());
        assertEqualsIgnoreWhitespace( "import goo.wee global ka.cha", imp.getPackageHeader() );
        
        assertEqualsIgnoreWhitespace( "when ka chow then bam", imp.getRules().get( 0 ).content );
        assertEqualsIgnoreWhitespace( "when ka chiga then ka chow", imp.getRules().get( 1 ).content );
        
        
    }

    private InputStream getDrl(String file) throws IOException {
        return this.getClass().getResourceAsStream( file );
    }
    
    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }    
    
}
