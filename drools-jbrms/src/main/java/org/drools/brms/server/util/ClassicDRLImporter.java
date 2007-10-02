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



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.brms.client.common.AssetFormats;
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

    private List<Asset>   assets = new ArrayList<Asset>();

    private StringBuffer header;

    private boolean      usesDSL;

	private static Pattern functionPattern = Pattern.compile("function\\s+.*\\s+(.*)\\(.*\\).*");


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
                laConsumeToEnd( lines, currentRule, "end" );
                addRule( ruleName, currentRule );

            }  else if (line.startsWith("function")) {
            	String functionName = getFuncName( line );
            	StringBuffer currentFunc = new StringBuffer();
            	currentFunc.append(line + "\n");
            	laConsumeToEnd( lines, currentFunc, "}");
            	currentFunc.append("}\n");
            	addFunction( functionName, currentFunc );

            }else if ( line.startsWith( "expander" ) ) {

                usesDSL = true;
            } else {

                header.append( line );
                header.append( "\n" );
            }
        }
    }




    private void addFunction(String functionName, StringBuffer currentFunc) {
    	this.assets.add(new Asset(functionName, currentFunc.toString(), AssetFormats.FUNCTION));
	}



	private String getFuncName(String line) {
    	Matcher m = functionPattern.matcher(line);
    	m.matches();
    	return m.group(1);
	}



	private void laConsumeToEnd(StringTokenizer lines, StringBuffer currentRule, String end) {
        String line;
        while ( true && lines.hasMoreTokens()) {
            line = lines.nextToken();
            if ( line.trim().startsWith( end ) ) {
                break;
            }
            currentRule.append( line );
            currentRule.append( "\n" );
        }
    }

    private void addRule(String ruleName, StringBuffer currentRule) {
    	if (this.isDSLEnabled()) {
	        this.assets.add( new Asset( ruleName,
                    currentRule.toString(), AssetFormats.DSL_TEMPLATE_RULE ));
    	} else {
	        this.assets.add( new Asset( ruleName,
	                                  currentRule.toString(), AssetFormats.DRL ));
    	}
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

		public Asset(
                    String name, String content, String format) {
            this.name = name;
            this.content = content;
            this.format = format;
        }

        public String format;
        public String name;
        public String content;
    }


}