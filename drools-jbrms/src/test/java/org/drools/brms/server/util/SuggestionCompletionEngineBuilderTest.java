package org.drools.brms.server.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

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
        builder.addDSLActionSentence(  input );
        builder.addDSLConditionSentence( "foo bar" );
        SuggestionCompletionEngine engine = builder.getInstance();

        assertEquals( 1,
                      engine.actionDSLSentences.length );
        assertEquals( 1,
                      engine.conditionDSLSentences.length );
        


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
