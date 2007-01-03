package org.drools.brms.client.rpc.brxml;


/**
 * This represents a DSL sentence.
 * @author Michael Neale
 */
public class DSLSentence
    implements
    IPattern,
    IAction {

    public DSLSentenceFragment[] elements;
    
    public String toString() {
        String result = "";
        if (elements != null) {
            for ( int i = 0; i < elements.length; i++ ) {
                result += elements[i].value + " ";
            }
        }
        return result;
    }
}
