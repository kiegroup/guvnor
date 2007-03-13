package org.drools.brms.server.rules;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.SessionHelper;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

import junit.framework.TestCase;

public class SuggestionCompletionLoaderTest extends TestCase {

    public void testLoader() throws Exception {
        
        RulesRepository repo = new RulesRepository(SessionHelper.getSession());
        PackageItem item = repo.createPackage( "testLoader", "to test the loader" );
        item.updateHeader( "import java.util.Date" );
        
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine engine = loader.getSuggestionEngine( item );
        assertNotNull(engine);
        
    }
    
    public void testStripUnNeededFields() {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        String[] result = loader.removeIrrelevantFields( new String[] {"foo", "toString", "class", "hashCode"} );
        assertEquals(1, result.length);
        assertEquals("foo", result[0]);
    }
    
    public void testGetShortNameOfClass() {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        
        assertEquals("Object", loader.getShortNameOfClass( Object.class.getName() ));
        
        assertEquals("Foo", loader.getShortNameOfClass( "Foo" ));
        
        
        
    }
    
}
