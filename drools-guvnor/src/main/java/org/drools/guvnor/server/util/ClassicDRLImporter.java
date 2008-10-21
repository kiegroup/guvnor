package org.drools.guvnor.server.util;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.lang.descr.RuleDescr;

/**
 * This class imports legacy DRL into a structure suitable for storing more
 * normalised in the repository.
 *
 * @author Michael Neale
 */
public class ClassicDRLImporter {

    private String         source;

    private String         packageName;

    private List<Asset>    assets          = new ArrayList<Asset>();

    private StringBuffer   header;

    private boolean        usesDSL;

    private static Pattern functionPattern = Pattern.compile( "function\\s+.*\\s+(.*)\\(.*\\).*" );

    public ClassicDRLImporter(InputStream in) throws IOException,
                                             DroolsParserException {
        String line = "";
        StringBuffer drl = new StringBuffer();
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        while ( (line = reader.readLine()) != null ) {
            drl.append( "\n" + line );
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
                laConsumeToEnd( lines,
                                currentRule,
                                "end" );
                addRule( ruleName,
                         currentRule );

            } else if ( line.startsWith( "function" ) ) {
                String functionName = getFuncName( line );
                StringBuffer currentFunc = new StringBuffer();

                int counter = 0;

                currentFunc.append( line + "\n" );

                counter = countBrackets( counter,
                                         line );

                if ( counter > 0 ) {
                    laConsumeBracketsToEnd( counter,
                                            lines,
                                            currentFunc );
                }
                addFunction( functionName,
                             currentFunc );

            } else if ( line.startsWith( "/*" ) ) {

                StringBuffer comment = new StringBuffer();
                laConsumeToEnd( lines,
                                comment,
                                "*/" );

                header.append( comment );

            } else if ( line.startsWith( "expander" ) ) {

                usesDSL = true;
            } else {

                header.append( line );
                header.append( "\n" );
            }
        }
    }

    private void addFunction(String functionName,
                             StringBuffer currentFunc) {
        this.assets.add( new Asset( functionName,
                                    currentFunc.toString(),
                                    AssetFormats.FUNCTION ) );
    }

    private String getFuncName(String line) {
        Matcher m = functionPattern.matcher( line );
        m.matches();
        return m.group( 1 );
    }

    /**
     * Consumes function to the ending curly bracket.
     *
     * @param lines
     * @param currentFunc
     */
    private void laConsumeBracketsToEnd(int counter,
                                        StringTokenizer lines,
                                        StringBuffer currentFunc) {
        /*
         * Check if the first line contains matching amount of brackets.
         */
        boolean multilineIsOpen = false;
        // Start counting brackets
        while ( lines.hasMoreTokens() ) {
            String line = lines.nextToken();

            currentFunc.append( line );
            currentFunc.append( "\n" );

            if ( multilineIsOpen ) {
                int commentEnd = line.indexOf( "*/" );

                if ( commentEnd != -1 ) {
                    multilineIsOpen = false;
                    line = line.substring( commentEnd );
                }
            } else {
                multilineIsOpen = checkIfMultilineCommentStarts( line );
                line = removeComments( line );
            }

            if ( !multilineIsOpen ) {
                counter = countBrackets( counter,
                                         line );
            }

            if ( counter == 0 ) {
                break;
            }
        }
    }

    /**
     * @param line
     * @return
     */
    private boolean checkIfMultilineCommentStarts(String line) {

        int commentMultiLineStart = line.indexOf( "/*" );
        int commentMultiLineEnd = line.indexOf( "*/" );
        //        int commentSingleLine = line.indexOf( "//" );

        if ( commentMultiLineStart != -1 && commentMultiLineEnd == -1 ) {
            return true;
        } else {
            return false;
        }
    }

    private int countBrackets(int counter,
                              String line) {
        char[] chars = line.toCharArray();
        for ( int i = 0; i < chars.length; i++ ) {
            if ( chars[i] == '{' ) {
                counter++;
            } else if ( chars[i] == '}' ) {
                counter--;
            }
        }
        return counter;
    }

    private String removeComments(String line) {

        int commentMultiLineStart = line.indexOf( "/*" );
        int commentMultiLineEnd = line.indexOf( "*/" );
        int commentSingleLine = line.indexOf( "//" );

        // Single line comment is first
        // Case: some code // /* */
        // Another case: some code // No comments
        if ( commentSingleLine != -1 && commentMultiLineStart > commentSingleLine ) {
            return line.substring( 0,
                                   commentSingleLine );
        }

        // There is only a start for the multiline comment.
        // Case: some code here /* commented out
        if ( commentMultiLineStart != -1 && commentMultiLineEnd == -1 ) {
            return line.substring( 0,
                                   commentMultiLineStart );
        }

        // Two ends are on the same line
        // some code /* comment */
        if ( commentMultiLineStart != -1 && commentMultiLineEnd != -1 ) {

            line = line.substring( commentMultiLineEnd );
            line = line.substring( 0,
                                   commentMultiLineStart );

            return line;
        }

        return line;
    }

    private void laConsumeToEnd(StringTokenizer lines,
                                StringBuffer currentRule,
                                String end) {
        String line;
        while ( lines.hasMoreTokens() ) {
            line = lines.nextToken();
            if ( line.trim().startsWith( end ) ) {
                break;
            }
            currentRule.append( line );
            currentRule.append( "\n" );
        }
    }

    private void addRule(String ruleName,
                         StringBuffer currentRule) {
    	ruleName = ruleName.replace('\'', ' ');
        if ( this.isDSLEnabled() ) {
            this.assets.add( new Asset( ruleName,
                                        currentRule.toString(),
                                        AssetFormats.DSL_TEMPLATE_RULE ) );
        } else {
            this.assets.add( new Asset( ruleName,
                                        currentRule.toString(),
                                        AssetFormats.DRL ) );
        }
    }

    private String getRuleName(String line) throws DroolsParserException {
        DrlParser parser = new DrlParser();
        line = line + "\n when\n then \n end";
        RuleDescr rule = (RuleDescr) parser.parse( line ).getRules().get( 0 );
        return rule.getName();
    }

    private String getPackage(String line) throws DroolsParserException {
        DrlParser parser = new DrlParser();
        return parser.parse( line ).getName();

    }

    public List<Asset> getAssets() {
        return this.assets;
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
    public static class Asset {

        public Asset(String name,
                     String content,
                     String format) {
            this.name = name;
            this.content = content;
            this.format = format;
        }

        public String format;
        public String name;
        public String content;
    }

    /**
     * This merges the toMerge new schtuff into the existing. Line by line, simple stuff.
     */
    public static String mergeLines(String existing,
                                    String toMerge) {

        if ( toMerge == null || toMerge.equals( "" ) ) {
            return existing;
        }
        if ( existing == null || existing.equals( "" ) ) {
            return toMerge;
        }
        Set existingLines = new HashSet<String>( Arrays.asList( existing.split( "\n" ) ) );
        String[] newLines = toMerge.split( "\n" );
        for ( int i = 0; i < newLines.length; i++ ) {
            String newLine = newLines[i].trim();

            if ( !newLine.equals( "" ) && !existingLines.contains( newLines[i].trim() ) ) {
                existing = existing + "\n" + newLines[i];
            }
        }
        return existing;

    }

}