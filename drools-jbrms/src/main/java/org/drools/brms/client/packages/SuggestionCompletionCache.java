package org.drools.brms.client.packages;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

public class SuggestionCompletionCache {

    private static SuggestionCompletionCache INSTANCE = new SuggestionCompletionCache();
    
    private Map cache = new HashMap();
    
    public static SuggestionCompletionCache getInstance() {
        return INSTANCE;
    }
    
    public SuggestionCompletionEngine getSuggestions(String packageName) {
        if (cache.containsKey( packageName )) {
            return (SuggestionCompletionEngine) cache.get( packageName );
        } else {
            return null;
        }
    }
    
    
}
