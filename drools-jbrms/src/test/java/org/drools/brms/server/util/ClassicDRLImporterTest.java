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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.server.contenthandler.DRLFileContentHandler;
import org.drools.brms.server.util.ClassicDRLImporter.Asset;
import org.drools.lang.DRLParser;

import junit.framework.TestCase;

public class ClassicDRLImporterTest extends TestCase {



    public void testStandardDRL() throws Exception {




        ClassicDRLImporter imp = new ClassicDRLImporter(getDrl("sample_legacy.drl"));
        assertEquals( "foo", imp.getPackageName() );
        assertEquals(2, imp.getAssets().size());

        assertEquals("blah", imp.getAssets().get( 0 ).name);
        assertEquals("cha", imp.getAssets().get( 1 ).name);

        System.err.println(imp.getPackageHeader());

        assertTrue(imp.getPackageHeader().indexOf( "import goo.wee" ) > -1);
        assertTrue(imp.getPackageHeader().indexOf( "package" ) == -1);

        assertFalse(imp.isDSLEnabled());

        assertEqualsIgnoreWhitespace( "when Whee() then goo();", imp.getAssets().get( 0 ).content);
        assertEqualsIgnoreWhitespace( "when Sup() then ka();", imp.getAssets().get( 1 ).content);
        assertTrue(imp.getAssets().get( 0 ).content.indexOf( " Whee()") > -1);

    }

    public void testWithFunction() throws Exception {
//    	Pattern p = Pattern.compile("function\\s+.*\\s+(.*)\\(.*\\).*");
//    	Matcher m = p.matcher("function void fooBar() {");
//    	assertTrue(m.matches());
//    	System.err.println(m.group());
//    	assertEquals("fooBar", m.group(1));

    	ClassicDRLImporter imp = new ClassicDRLImporter(getDrl("sample_legacy_functions.drl"));
    	assertFalse(imp.isDSLEnabled());

    	assertEquals(4, imp.getAssets().size());
    	assertEquals(AssetFormats.FUNCTION, imp.getAssets().get(0).format);
    	assertEquals(AssetFormats.FUNCTION, imp.getAssets().get(1).format);

    	assertEquals("goo1", imp.getAssets().get(0).name);
    	assertEqualsIgnoreWhitespace("function void goo1() { //do something ! { yeah } }", imp.getAssets().get(0).content);

    	assertEquals("goo2", imp.getAssets().get(1).name);
    	assertEqualsIgnoreWhitespace("function String goo2(String la) { //yeah man ! return \"whee\"; }", imp.getAssets().get(1).content);

    	assertEquals(AssetFormats.DRL, imp.getAssets().get(2).format);
    	assertEquals(AssetFormats.DRL, imp.getAssets().get(3).format);
    	assertNotNull(imp.getAssets().get(3).content);

    }

    public void testWithDSL() throws Exception {

        ClassicDRLImporter imp = new ClassicDRLImporter(getDrl("sample_legacy_with_dsl.drl"));

        assertTrue(imp.isDSLEnabled());
        assertEquals(2, imp.getAssets().size());
        assertEquals("foo", imp.getPackageName());
        assertEqualsIgnoreWhitespace( "import goo.wee global ka.cha", imp.getPackageHeader() );

        assertEqualsIgnoreWhitespace( "when ka chow then bam", imp.getAssets().get( 0 ).content );
        assertEqualsIgnoreWhitespace( "when ka chiga then ka chow", imp.getAssets().get( 1 ).content );


        Asset as = imp.getAssets().get(0);
        assertEquals(AssetFormats.DSL_TEMPLATE_RULE, as.format);

    }
    public void testComplexExample() throws Exception {
        ClassicDRLImporter imp = new ClassicDRLImporter(getDrl("sample_complex.drl"));
        assertFalse(imp.isDSLEnabled());
        assertEquals(2, imp.getAssets().size());

        assertTrue(DRLFileContentHandler.isStandAloneRule(imp.getAssets().get(0).content));
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