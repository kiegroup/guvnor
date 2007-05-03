package org.drools.brms.client.packages;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.user.client.Command;

public class SuggestionCompletionCacheTest extends TestCase {

    private boolean executed;
    private boolean loaded;
    
    protected void setUp() throws Exception {
        super.setUp();
        executed = false;
        loaded = false;
    }
    
    public void testCache() {
        SuggestionCompletionCache cache = SuggestionCompletionCache.getInstance();
        assertSame(cache, SuggestionCompletionCache.getInstance());
        
        cache = new SuggestionCompletionCache() {
            
            void loadPackage(String packageName,
                             Command command) {
                loaded = true;
                
            }
        };
        
        cache.doAction( "xyz", new Command() {
            public void execute() {
            }
        });
        assertTrue (loaded);
        SuggestionCompletionEngine eng = new SuggestionCompletionEngine();
        cache.cache.put( "foo",  eng);
        
        cache.doAction( "foo", new Command() {

            public void execute() {
                executed = true;
            }
            
        });
        
        assertTrue(executed);
        
        assertNotNull(cache.getEngineFromCache( "foo" ));
        
        cache.removePackage( "foo" );
        assertFalse(cache.cache.containsKey( "foo" ));
        
        
        
    }
    
}
