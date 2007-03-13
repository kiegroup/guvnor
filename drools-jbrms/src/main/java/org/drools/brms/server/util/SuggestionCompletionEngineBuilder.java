/*
 * Copyright 2006 JBoss Inc
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

package org.drools.brms.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.DSLSentenceFragment;

/**
 * A builder to incrementally populate a SuggestionCompletionEngine
 * 
 * @author etirelli
 */
public class SuggestionCompletionEngineBuilder {

    // The following pattern is capable of spliting a DSL sentence into
    // fragments, supporting \{ escapes. Example:
    //
    // {This} is a {pattern} considered pretty \{{easy}\} by most \{people\}. What do you {say}?
    //
    // would be parsed into the following fragments:
    //
    // Fragment: [{This}]
    // Fragment: [ is a ]
    // Fragment: [{pattern}]
    // Fragment: [ considered pretty {]
    // Fragment: [{easy}]
    // Fragment: [} by most {people}. What do you ]
    // Fragment: [{say}]
    // Fragment: [?]
    private final static Pattern       splitter      = Pattern.compile( "(^|[^\\\\])(\\{[(\\\\\\{)|[^\\{]]*?\\})",
                                                                        Pattern.MULTILINE | Pattern.DOTALL );

    private SuggestionCompletionEngine instance      = new SuggestionCompletionEngine();
    private List                       factTypes     = new ArrayList();
    private Map                        fieldsForType = new HashMap();
    private Map                        fieldTypes    = new HashMap();
    private Map                        globalTypes   = new HashMap();
    private List                       dslSentences  = new ArrayList();

    public SuggestionCompletionEngineBuilder() {
    }

    /**
     * Start the creation of a new SuggestionCompletionEngine
     */
    public void newCompletionEngine() {
        this.instance = new SuggestionCompletionEngine();
        this.factTypes = new ArrayList();
        this.fieldsForType = new HashMap();
        this.fieldTypes = new HashMap();
        this.globalTypes = new HashMap();
        this.dslSentences = new ArrayList();
    }

    /**
     * Adds a fact type to the engine
     * 
     * @param factType
     */
    public void addFactType(String factType) {
        this.factTypes.add( factType );
    }

    /**
     * Adds the list of fields for a given type
     * 
     * @param type
     * @param fields
     */
    public void addFieldsForType(String type,
                                 String[] fields) {
        this.fieldsForType.put( type,
                                fields );
    }

    /**
     * @return true if this has the type already registered (field information).
     */
    public boolean hasFieldsForType(String type) {
        return this.fieldsForType.containsKey( type );
    }
    
    /**
     * Adds a type declaration for a field
     * 
     * @param field
     * @param type
     */
    public void addFieldType(String field,
                             String type) {
        this.fieldTypes.put( field,
                             type );
    }

    /**
     * Adds a global and its corresponding type to the engine
     * 
     * @param global
     * @param type
     */
    public void addGlobalType(String global,
                              String type) {
        this.globalTypes.put( global,
                              type );
    }

    /**
     * Adds a DSL Sentence to the engine, splitting it into
     * chunks of editable and non-editable text
     *  
     * @param sentence
     */
    public void addDSLSentence(String sentence) {
        // splitting the sentence in fragments
        Matcher m = splitter.matcher( sentence );
        int lastEnd = 0;
        List fragments = new ArrayList();

        while ( m.find() ) {
            if ( m.start( 2 ) > lastEnd ) {
                // if there is anything after last match and before the current one, add
                // a non-editable fragment
                fragments.add( new DSLSentenceFragment( sentence.substring( lastEnd,
                                                                            m.start( 2 ) ).replaceAll( "\\\\([\\{\\}])",
                                                                                                       "$1" ),
                                                        false ) );
            }
            // add the editable fragment
            fragments.add( new DSLSentenceFragment( m.group( 2 ),
                                                    true ) );
            lastEnd = m.end( 2 );
        }
        if ( lastEnd < sentence.length() ) {
            // if there is anything after the last match, add as a non-editable fragment
            fragments.add( new DSLSentenceFragment( sentence.substring( lastEnd ),
                                                    false ) );
        }

        DSLSentence sen = new DSLSentence();
        sen.elements = (DSLSentenceFragment[]) fragments.toArray( new DSLSentenceFragment[fragments.size()] );
        this.dslSentences.add( sen );
    }

    /**
     * Returns a SuggestionCompletionEngine instance populated with 
     * all the data since last call to newCompletionEngine() method
     * 
     * @return
     */
    public SuggestionCompletionEngine getInstance() {
        this.instance.factTypes = (String[]) this.factTypes.toArray( new String[this.factTypes.size()] );
        this.instance.fieldsForType = this.fieldsForType;
        this.instance.fieldTypes = this.fieldTypes;
        this.instance.globalTypes = this.globalTypes;
        this.instance.actionDSLSentences = (DSLSentence[]) this.dslSentences.toArray( new DSLSentence[this.dslSentences.size()] );
        return this.instance;
    }

}
