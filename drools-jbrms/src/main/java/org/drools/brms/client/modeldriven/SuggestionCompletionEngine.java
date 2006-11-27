package org.drools.brms.client.modeldriven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An suggestion completion processor. This should be usable in both GWT/Web and the IDE.
 *  
 * @author Michael Neale
 */
public class SuggestionCompletionEngine {

    
    private Map factToFields = new HashMap();
    private Map factFieldToOperator = new HashMap();
    private Map factFieldToConnectiveOperator = new HashMap();
    private Map boundFacts = new HashMap();
    private List boundFields = new ArrayList();
    private Map globals = new HashMap();

    /**
     * For bulk loading up the data (from a previous rule save)
     * 
     * @param factToFields A map of "FactType" (key - String) to String[] (value) 
     * @param factFieldToOperator A map of "<FactType>.<field>" (key - String) to String[] operators
     * @param factFieldToConnectiveOperator A map of "<FactType>.<field>" (key -String) to String[] operators 
     *                                  that are valid CONNECTIVE operators.
     *                                  
     * @param globals A map of global variable name to its fields (String[]).
                                      
     */
    public void load( 
                      Map factToFields, 
                      Map factFieldToOperator, 
                      Map factFieldToConnectiveOperator,
                      Map globals                                     
                    ) {
        this.factToFields = factToFields;
        this.factFieldToOperator = factFieldToOperator;
        this.factFieldToConnectiveOperator = factFieldToConnectiveOperator;
        this.globals = globals;
        
    }
    
    public SuggestionCompletionEngine() {        
    }
    
    
    /**
     * Add a fact, with the applicable fields.
     */
    public void addFact(String factType, String[] fields) {
        this.factToFields.put(factType, fields);
    }

    /**
     * add an operator, to the applicable fact/field combination.
     */
    public void addOperators(String factType, String fieldName, String[] operators) {
        this.factFieldToOperator.put( factType + "." + fieldName, operators );
    }
    
    /**
     * add a connective operator (as they are generally a subset of the full set of operators).
     */
    public void addConnectiveOperators(String factType, String fieldName, String[] operators) {
        this.factFieldToConnectiveOperator.put( factType + "." + fieldName, operators );
    }
    
    
    /**
     * Add a global with the specified fields.
     */
    public void addGlobal(String global, String[] fields) {
        this.globals.put( global, fields );
    }

    public String[] getConditionalElements() {       
        return new String[] {"not", "exists", "or"};
    }

    public String[] getConnectiveOperatorCompletions(String factType,
                                                     String fieldName) {
        return (String[]) this.factFieldToConnectiveOperator.get( factType + "." + fieldName );
    }

    public String[] getFactTypes() {
        return toStringArray( this.factToFields.keySet() );
    }

    public String[] getFieldCompletions(String factType) {
        
        return (String[]) this.factToFields.get( factType );
    }

    public String[] getOperatorCompletions(String factType,
                                           String fieldName) {
        return (String[]) this.factFieldToOperator.get( factType + "." + fieldName );        
    }



    /**
     * This adds a bound field which may be used later on (fields values cannot be set this way).
     */
    public void addBoundField(String fieldName) {
        this.boundFields.add(fieldName);
    }
    
    public String[] getBoundFacts() {
        return toStringArray( boundFacts.keySet() );
    }
    
    private String[] toStringArray(Set set) {
        String[] f = new String[set.size()];
        int i = 0;
        for ( Iterator iter = set.iterator(); iter.hasNext(); ) {
            f[i] = (String) iter.next();
            i++;
        }
        return f;                
    }


    private String[] toStringArray(List list) { 
        String[] result = new String[list.size()];    
        for(int i = 0; i < list.size(); i ++) {
            result[i] = (String) list.get( i );
        }
        return result;
    }


    public String[] getBoundFields() {
        return toStringArray( boundFields );
    }


    public void addBoundFact(String varName,
                             String factType) {        
        this.boundFacts.put(varName, factType);
    }


    public String[] getFieldCompletionsForBoundFact(String varName) {
        String factType = (String) this.boundFacts.get( varName );
        return this.getFieldCompletions( factType );
    }


    public String[] getFieldCompletionsForGlobalVariable(String varName) {        
        return (String[]) this.globals.get( varName );
    }


    public String[] getGlobalVariables() { 
        return toStringArray( this.globals.keySet() );
    }

}
