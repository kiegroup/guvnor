/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.client.modeldriven.brl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents a DSL sentence.
 */
public class DSLSentence
    implements
    IPattern,
    IAction {

    public static final String ENUM_TAG    = "ENUM";
    public static final String DATE_TAG    = "DATE";
    public static final String BOOLEAN_TAG = "BOOLEAN";

    private String             sentence;
    private String             definition;
    private List<String>       values;

    /**
     * This will strip off any residual "{" stuff...
     */
    public String toString() {
        getDefinition();
        final char[] chars = this.definition.toCharArray();
        boolean inBracket = false;
        boolean inBracketAfterColon = false;

        String result = "";
        for ( int i = 0; i < chars.length; i++ ) {
            final char c = chars[i];
            if ( c != '{' && c != '}' && c != ':' && !inBracketAfterColon ) {
                result += c;
            } else if ( c == '{' ) {
                inBracket = true;
            } else if ( c == '}' ) {
                inBracket = false;
                inBracketAfterColon = false;
            } else if ( c == ':' && inBracket ) {
                inBracketAfterColon = true;
            } else if ( c == ':' && !inBracket ) {
                result += c;
            }
        }
        return result.replace( "\\n",
                               "\n" );
    }

    /**
     * This will strip off any "{" stuff, substituting values accordingly
     */
    public String interpolate() {
        getValues();
        if ( definition == null ) {
            return "";
        }

        int variableStart = definition.indexOf( "{" );
        if ( variableStart < 0 ) {
            return definition;
        }

        int index = 0;
        int variableEnd = 0;
        StringBuilder sb = new StringBuilder();
        while ( variableStart >= 0 ) {
            sb.append( definition.substring( variableEnd,
                                             variableStart ) );
            variableEnd = getIndexForEndOfVariable( definition,
                                                    variableStart ) + 1;
            variableStart = definition.indexOf( "{",
                                                variableEnd );
            sb.append( values.get( index++ ) );
        }
        if ( variableEnd < definition.length() ) {
            sb.append( definition.substring( variableEnd ) );
        }
        return sb.toString();
    }

    /**
     * This is used by the GUI when adding a sentence to LHS or RHS.
     * 
     * @return
     */
    public DSLSentence copy() {
        final DSLSentence newOne = new DSLSentence();
        newOne.definition = getDefinition();
        List<String> values = getValues();
        if ( values != null ) {
            for ( String value : getValues() ) {
                newOne.getValues().add( value );
            }
        }
        return newOne;
    }

    public String getDefinition() {
        if ( definition == null ) {
            parseSentence();
        }
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<String> getValues() {
        if ( this.values == null ) {
            parseDefinition();
        }
        return values;
    }

    public Map<String, String> getEnumFieldValueMap() {
        if ( this.values == null ) {
            parseDefinition();
        }
        Map<String, String> fieldValueMap = new HashMap<String, String>();
        if ( getValues().isEmpty() ) {
            return fieldValueMap;
        }

        int variableStart = definition.indexOf( "{" );
        int iVariable = 0;
        while ( variableStart >= 0 ) {
            int variableEnd = getIndexForEndOfVariable( definition,
                                                        variableStart );
            String variable = definition.substring( variableStart + 1,
                                                    variableEnd );

            //Extract field name for enumerations
            if ( variable.contains( ENUM_TAG ) ) {
                int lastIndex = variable.lastIndexOf( ":" );
                String factAndField = variable.substring( lastIndex + 1,
                                                          variable.length() );
                int dotIndex = factAndField.indexOf( "." );
                String field = factAndField.substring( dotIndex + 1,
                                                       factAndField.length() );
                fieldValueMap.put( field,
                                   values.get( iVariable ) );
            }
            iVariable++;
            variableStart = definition.indexOf( "{",
                                                variableEnd );
        }

        return fieldValueMap;
    }

    //Build the Definition and Values from a legacy Sentence. Legacy DSLSentence did not 
    //separate DSL definition from values, which led to complications when a user wanted 
    //to set the value of a DSL parameter to text including the special escaping used 
    //to differentiate value, from data-type, from restriction
    private void parseSentence() {
        if ( sentence == null ) {
            return;
        }
        definition = sentence;
        values = new ArrayList<String>();
        sentence = null;

        int variableStart = definition.indexOf( "{" );
        while ( variableStart >= 0 ) {
            int variableEnd = getIndexForEndOfVariable( definition,
                                                        variableStart );
            String variable = definition.substring( variableStart + 1,
                                                    variableEnd );
            values.add( parseValue( variable ) );
            variableStart = definition.indexOf( "{",
                                                variableEnd );
        }
    }

    //Build the Values from the Definition.
    private void parseDefinition() {
        values = new ArrayList<String>();
        if ( getDefinition() == null ) {
            return;
        }

        int variableStart = definition.indexOf( "{" );
        while ( variableStart >= 0 ) {
            int variableEnd = getIndexForEndOfVariable( definition,
                                                        variableStart );
            String variable = definition.substring( variableStart + 1,
                                                    variableEnd );
            values.add( parseValue( variable ) );
            variableStart = definition.indexOf( "{",
                                                variableEnd );
        }
    }

    private int getIndexForEndOfVariable(String dsl,
                                         int start) {
        int end = -1;
        int bracketCount = 0;
        if ( start > dsl.length() ) {
            return end;
        }
        for ( int i = start; i < dsl.length(); i++ ) {
            char c = dsl.charAt( i );
            if ( c == '{' ) {
                bracketCount++;
            }
            if ( c == '}' ) {
                bracketCount--;
                if ( bracketCount == 0 ) {
                    end = i;
                    return end;
                }
            }
        }
        return -1;
    }

    private String parseValue(String variable) {
        if ( !variable.contains( ":" ) ) {
            return variable;
        }

        String value = variable.substring( 0,
                                           variable.indexOf( ":" ) );
        return value;
    }

}
