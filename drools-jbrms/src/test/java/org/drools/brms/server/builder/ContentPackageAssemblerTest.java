package org.drools.brms.server.builder;

import java.util.List;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.ruleeditor.CheckinPopup;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class ContentPackageAssemblerTest extends TestCase {

    
    
    
    public void testPackageConfig() throws Exception {
        //test the config, no rule assets yet
        RulesRepository repo = getRepo();
        PackageItem pkg = repo.createPackage( "testBuilderPackageConfig", "x" );
        pkg.updateHeader( "import java.util.List" );
        AssetItem func = pkg.addAsset( "func1", "a function" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void doSomething() { \n System.err.println(List.class.toString()); }" );
        func.checkin( "yeah" );
        
        func = pkg.addAsset( "func2", "q" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { \nSystem.err.println(42); \n}");
        func.checkin( "" );
        
        AssetItem ass = pkg.addAsset( "dsl", "m");
        ass.updateFormat( AssetFormats.DSL );
        ass.updateContent( "[when]Foo bar=String()" );
        ass.checkin( "" );
        repo.save();


        //now lets light it up
        ContentPackageAssembler assembler = new ContentPackageAssembler(pkg);
        assertFalse(assembler.hasErrors());
        Package bin = assembler.getBinaryPackage();
        assertNotNull(bin);
        assertEquals("testBuilderPackageConfig", bin.getName());
        assertEquals(2, bin.getFunctions().size());
        
        assertTrue(bin.isValid());
        assertEquals(1, assembler.dslFiles.size());
        
        
        pkg.updateHeader( "koo koo ca choo" );
        assembler = new ContentPackageAssembler(pkg);
        assertTrue(assembler.hasErrors());
        assertTrue(assembler.isPackageConfigurationInError());
        
        pkg.updateHeader( "import java.util.Date" );
        assembler = new ContentPackageAssembler(pkg);
        assertTrue(assembler.hasErrors());
        assertTrue(assembler.getErrors().get(0).itemInError instanceof AssetItem);
        
        assertEquals("func1", assembler.getErrors().get( 0 ).itemInError.getName());
        
            
        
    }

    private RulesRepository getRepo() throws Exception {
        return new RulesRepository( TestEnvironmentSessionHelper.getSession() );
    }
    
    public void FIXME_testSimplePackage() throws Exception {
        PackageItem pkg = null;
        ContentPackageAssembler asm = new ContentPackageAssembler(pkg);
        assertFalse(asm.hasErrors());
        assertNotNull(asm.getBinaryPackage());
        org.drools.rule.Package bin = asm.getBinaryPackage();
        assertEquals(pkg.getName(), bin.getName());
        assertTrue(bin.isValid());
        
        assertEquals(2, bin.getRules().length);
        
        
        
    }
    
    public void testErrorsInConfig() {
        
    }
    
    public void testErrorsInFunctionAndRuleAsset() {
        
    }
    
    public void testComplexAssets() {
        
    }
    
    
    
}
