package org.drools.guvnor.client.rpc;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SuggestionCompletionEngineServiceAsync {
    
    /**
     * Loads up the SuggestionCompletionEngine for the given package. As this
     * doesn't change that often, its safe to cache. However, if a change is
     * made to a package, should blow away the cache.
     */
    void loadSuggestionCompletionEngine(String packageName,
                                        AsyncCallback<SuggestionCompletionEngine> async);
}
