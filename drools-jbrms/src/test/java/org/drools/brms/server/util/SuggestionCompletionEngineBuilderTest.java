package org.drools.brms.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.DSLSentenceFragment;

public class SuggestionCompletionEngineBuilderTest extends TestCase {
    SuggestionCompletionEngineBuilder builder = new SuggestionCompletionEngineBuilder();

    protected void setUp() throws Exception {
        super.setUp();
        builder.newCompletionEngine();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddDSLSentence() {
        String input = "{This} is a {pattern} considered pretty \\{{easy}\\} by most \\{people\\}. What do you {say}?";
        String[] strFrags = new String[] {"{This}", " is a ", "{pattern}", " considered pretty {", "{easy}", "} by most {people}. What do you ", "{say}", "?"};
        boolean[] editable = new boolean[] { true, false, true, false, true, false, true, false };
        builder.addDSLSentence( input );

        SuggestionCompletionEngine engine = builder.getInstance();

        assertEquals( 1,
                      engine.actionDSLSentences.length );
        DSLSentenceFragment[] fragments = engine.actionDSLSentences[0].elements;
        assertEquals( 8,
                      fragments.length );
        for( int i = 0; i < 8; i++ ) {
            assertEquals( strFrags[i],
                          fragments[i].value );
            assertEquals( editable[i],
                          fragments[i].isEditableField );
        }

    }
    
    public void testPattern() {
        Pattern imports = Pattern.compile( "import\\s*([\\.a-zA-Z0-9_]+)\\s*[;$]?", Pattern.MULTILINE );
        
        String input = "\nimport java.util.List  \nimport java.util.Map ; \n import a.b.c import x.y.z";
        
        Matcher m = imports.matcher( input );
        
        while( m.find() ) {
            System.out.println("Found: ["+m.group(1)+"]");
        }
        
        
    }
}
