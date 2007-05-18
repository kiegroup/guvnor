package org.drools.brms.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.RuleDescr;

/**
 * This class imports legacy DRL into a structure suitable for storing more
 * normalised in the repository.
 * 
 * @author Michael Neale
 */
public class ClassicDRLImporter {

    private String       source;

    private String       packageName;

    private List<Rule>   rules = new ArrayList<Rule>();

    private StringBuffer header;

    private boolean      usesDSL;



    
    public ClassicDRLImporter(InputStream in) throws IOException, DroolsParserException {
        String line = "";
        StringBuffer drl = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ( (line = reader.readLine())  != null) {
            drl.append( "\n" + line);        
        }
        this.source = drl.toString();
        
        parse();
    }
    
    

    private void parse() throws DroolsParserException {
        StringTokenizer lines = new StringTokenizer( source,
                                                     "\r\n" );

        header = new StringBuffer();

        while ( lines.hasMoreTokens() ) {
            String line = lines.nextToken().trim();
            if ( line.startsWith( "package" ) ) {
                packageName = getPackage( line );
            } else if ( line.startsWith( "rule" ) ) {
                String ruleName = getRuleName( line );
                StringBuffer currentRule = new StringBuffer();
                laConsumeToEnd( lines, currentRule );
                addRule( ruleName, currentRule );

            }  else if ( line.startsWith( "expander" ) ) {
                usesDSL = true;
            } else {

                header.append( line );
                header.append( "\n" );
            }
        }
    }




    private void laConsumeToEnd(StringTokenizer lines, StringBuffer currentRule) {
        String line;
        while ( true && lines.hasMoreTokens()) {
            line = lines.nextToken().trim();
            if ( line.equals( "end" ) ) {
                break;
            }
            currentRule.append( line );
            currentRule.append( "\n" );
        }
    }

    private void addRule(String ruleName, StringBuffer currentRule) {
        this.rules.add( new Rule( ruleName,
                                  currentRule.toString() ) );
    }

    private String getRuleName(String line) throws DroolsParserException {
        DrlParser parser = new DrlParser();
        RuleDescr rule = (RuleDescr) parser.parse( line ).getRules().get( 0 );
        return rule.getName();
    }

    private String getPackage(String line) throws DroolsParserException {
        DrlParser parser = new DrlParser();
        return parser.parse( line ).getName();

    }

    public List<Rule> getRules() {
        return this.rules;
    }
    

    public String getPackageName() {
        return this.packageName;
    }

    public String getPackageHeader() {
        return this.header.toString();
    }

    public boolean isDSLEnabled() {
        return this.usesDSL;
    }

    /**
     * Holds a rule to import. The content does not include the "end".
     * 
     * @author Michael Neale
     */
    public static class Rule {

        public Rule(
                    String name, String content) {
            this.name = name;
            this.content = content;
        }

        public String name;
        public String content;
    }
    

}
