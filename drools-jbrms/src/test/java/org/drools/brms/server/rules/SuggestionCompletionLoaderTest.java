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
    
}
