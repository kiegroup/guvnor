package org.drools.brms.server.util;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
        assertTrue(imp.getRules().get( 0 ).content.indexOf( " Whee()") > -1);
        
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