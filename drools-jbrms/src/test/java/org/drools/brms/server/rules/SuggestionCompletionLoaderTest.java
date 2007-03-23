package org.drools.brms.server.rules;

import junit.framework.TestCase;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

public class SuggestionCompletionLoaderTest extends TestCase {

    public void testLoader() throws Exception {
        
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        PackageItem item = repo.createPackage( "testLoader", "to test the loader" );
        item.updateHeader( "import java.util.Date" );
        repo.save();
        
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
    
    public void testFactTemplates() throws Exception {
        
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        PackageItem item = repo.createPackage( "testLoader2", "to test the loader for fact templates" );
        item.updateHeader( "import java.util.Date\ntemplate Person\njava.lang.String name\nDate birthDate\nend" );
        repo.save();
        
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine engine = loader.getSuggestionEngine( item );
        assertNotNull(engine);
        String[] factTypes = engine.getFactTypes();
        
        assertEquals( 2, factTypes.length );
        assertEquals("Date", factTypes[0]);
        assertEquals("Person", factTypes[1]);
        
        String[] fieldsForType = engine.getFieldCompletions( "Person" );
        assertEquals( 2, fieldsForType.length );
        assertEquals("name", fieldsForType[0]);
        assertEquals("birthDate", fieldsForType[1]);
        
        String fieldType = engine.getFieldType( "Person", "name" );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING, fieldType );
        fieldType = engine.getFieldType( "Person", "birthDate" );
        assertEquals( SuggestionCompletionEngine.TYPE_COMPARABLE, fieldType );
    }
    
    public void testLoadDSLs() throws Exception {
        String dsl = "[when]The agents rating is {rating}=doNothing()\n[then]Send a notification to manufacturing '{message}'=foo()";
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        PackageItem item = repo.createPackage( "testLoadDSLs", "to test the loader for DSLs" );
        AssetItem asset = item.addAsset( "mydsl", "" );
        asset.updateFormat( AssetFormats.DSL );
        asset.updateContent( dsl );
        asset.checkin( "ok" );
        
        item = repo.loadPackage( "testLoadDSLs" );
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( item );
        assertEquals(1, eng.actionDSLSentences.length);
        assertEquals(1, eng.conditionDSLSentences.length);
        
        assertEquals( "The agents rating is {rating}", eng.conditionDSLSentences[0].sentence );
        assertEquals("Send a notification to manufacturing '{message}'",eng.actionDSLSentences[0].sentence);
        
        
        
        
    }
    
    public void testErrors() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        PackageItem item = repo.createPackage( "testErrorsInPackage", "to test error handling" );

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        assertNotNull(loader.getSuggestionEngine( item ));
        assertFalse(loader.hasErrors());
        
        item.updateHeader( "gooble de gook" );
        loader = new SuggestionCompletionLoader();
        loader.getSuggestionEngine( item );
        assertTrue(loader.hasErrors());
        
        
        item.updateHeader( "import foo.bar; \nglobal goo.Bar baz;" );
        loader = new SuggestionCompletionLoader();
        loader.getSuggestionEngine( item );
        assertTrue(loader.hasErrors());
        
        
    }
    
}
