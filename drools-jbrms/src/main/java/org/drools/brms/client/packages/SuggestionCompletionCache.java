package org.drools.brms.client.packages;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;

/**
 * This utility cache will maintain a cache of suggestion completion engines,
 * as they are somewhat heavy to load. 
 * If it needs to be loaded, then it will load, and then call the appropriate action, 
 * and keep it in the cache.
 * 
 * @author Michael Neale
 *
 */
public class SuggestionCompletionCache {

    private static SuggestionCompletionCache INSTANCE = new SuggestionCompletionCache();
    
    Map cache = new HashMap();
    
    
    
    public static SuggestionCompletionCache getInstance() {
        return INSTANCE;
    }


    /**
     * This will do the action, after refreshing the cache if necessary.
     */
    public void doAction(String packageName,
                         Command command) {
        
        if (!this.cache.containsKey( packageName )) {
            loadPackage(packageName, command);
        } else {
            command.execute();
        }
        
        
    }
    
    public SuggestionCompletionEngine getEngineFromCache(String packageName) {
        SuggestionCompletionEngine eng = (SuggestionCompletionEngine) cache.get( packageName );
        if (eng == null) {
            ErrorPopup.showMessage( "Unable to get content assistance for this rule." );
            return null;
        }
        return eng;
    }


    void loadPackage(final String packageName, final Command command) {
        
        RepositoryServiceFactory.getService().loadSuggestionCompletionEngine( packageName, new GenericCallback() {
            public void onSuccess(Object data) {
                SuggestionCompletionEngine engine = (SuggestionCompletionEngine) data;
                cache.put( packageName, engine );
                command.execute();
            }
        });
    }


    /**
     * Removed the package from the cache, causing it to be loaded the next time.
     */
    public void removePackage(String packageName) {
        cache.remove( packageName );
    }
    
}
