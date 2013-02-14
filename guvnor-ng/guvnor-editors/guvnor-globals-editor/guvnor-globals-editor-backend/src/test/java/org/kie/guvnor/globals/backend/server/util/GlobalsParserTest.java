package org.kie.guvnor.globals.backend.server.util;

import java.util.List;

import org.junit.Test;
import org.kie.guvnor.globals.model.Global;

import static org.junit.Assert.*;

/**
 * Tests for GlobalsParser.
 */
public class GlobalsParserTest {

    @Test
    public void testSimpleEntry() {
        final String content = "global java.util.List myList;";
        final List<Global> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 1,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getAlias() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getClassName() );
    }

    @Test
    public void testMultipleEntries() {
        final String content = "global java.util.List myList;\n"
                + "global java.lang.String myString;";
        final List<Global> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 2,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getAlias() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getClassName() );
        assertEquals( "myString",
                      globals.get( 1 ).getAlias() );
        assertEquals( "java.lang.String",
                      globals.get( 1 ).getClassName() );
    }

    @Test
    public void testCommentedEntry() {
        final String content = "global java.util.List myList;\n"
                + "#global java.lang.String myString;";
        final List<Global> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 1,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getAlias() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getClassName() );
    }

    @Test
    public void testWhiteSpace() {
        final String content = "  global    java.util.List myList;\n"
                + "  global   java.lang.String   myString;   ";
        final List<Global> globals = GlobalsParser.parseGlobals( content );

        assertNotNull( globals );
        assertEquals( 2,
                      globals.size() );
        assertEquals( "myList",
                      globals.get( 0 ).getAlias() );
        assertEquals( "java.util.List",
                      globals.get( 0 ).getClassName() );
        assertEquals( "myString",
                      globals.get( 1 ).getAlias() );
        assertEquals( "java.lang.String",
                      globals.get( 1 ).getClassName() );
    }


}
