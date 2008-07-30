package org.drools.lang;
 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
 
import junit.framework.TestCase;
 
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
 
/**
 */
public class RuleNameExtractorTest extends TestCase {
 
    public void testExtractRuleNames() throws RecognitionException, IOException {
        DroolsTree tree = getTree( "multiple_rules.drl" );
        List<String> ruleNames = new ArrayList<String>();
        extractRuleNames( tree, ruleNames );
 
        assertTrue( ruleNames.contains( "Like Stilton" ) );
        assertTrue( ruleNames.contains( "Like Cheddar" ) );
    }
 
    private void extractRuleNames(DroolsTree tree, List<String> ruleNames ) {
        if( tree.getType() == DRLParser.VK_RULE ) {
            ruleNames.add( getCleanId( (DroolsTree) tree.getChild( 0 ) ) );
        } else {
            for( int i = 0; i < tree.getChildCount(); i++ ) {
                extractRuleNames( (DroolsTree) tree.getChild( i ), ruleNames );
            }
        }
    }
 
    private DroolsTree getTree( final String fileName ) throws RecognitionException, IOException {
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( fileName ) );
        DRLParser parser = new DRLParser( new CommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
        parser.setTreeAdaptor(new DroolsTreeAdaptor());
        return (DroolsTree) parser.compilation_unit().getTree();        
    }
 
    private String getCleanId(DroolsTree id) {
        String cleanedId = id.getText();
        if (cleanedId.startsWith("\"") || cleanedId.startsWith("'")) {
            cleanedId = cleanedId.substring(1, cleanedId.length() - 1);
        }
        return cleanedId;
    }
}