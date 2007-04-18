package org.drools.brms.server.builder;

import java.io.InputStream;

import junit.framework.TestCase;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;

public class ContentPackageAssemblerTest extends TestCase {

    
    
    
    public void testPackageConfigWithErrors() throws Exception {
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
        assertEquals(1, assembler.builder.getDSLMappingFiles().size());
        
        
        pkg.updateHeader( "koo koo ca choo" );
        assembler = new ContentPackageAssembler(pkg);
        assertTrue(assembler.hasErrors());
        assertTrue(assembler.isPackageConfigurationInError());
        
        pkg.updateHeader( "import java.util.Date" );
        assembler = new ContentPackageAssembler(pkg);
        assertTrue(assembler.hasErrors());
        assertTrue(assembler.getErrors().get(0).itemInError instanceof AssetItem);
        
        assertEquals("func1", assembler.getErrors().get( 0 ).itemInError.getName());
        try {
            assembler.getBinaryPackage();
            fail("should not work as is in error.");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }
        
    }

    private RulesRepository getRepo() throws Exception {
        return new RulesRepository( TestEnvironmentSessionHelper.getSession() );
    }
    
    public void testSimplePackageBuildNoErrors() throws Exception {
        RulesRepository repo = getRepo();
        
        PackageItem pkg = repo.createPackage( "testSimplePackageBuildNoErrors", "" );
        AssetItem model = pkg.addAsset( "model", "qed" );
        model.updateFormat( AssetFormats.MODEL );
        
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        
        pkg.updateHeader( "import com.billasurf.Board\n global com.billasurf.Person customer" );
        
        AssetItem rule1 = pkg.addAsset( "rule_1", "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when Board() \n then customer.setAge(42); \n end"); 
        rule1.checkin( "" );
        
        AssetItem rule2 = pkg.addAsset( "rule2", "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "agenda-group 'q' \n when \n Board() \n then \n System.err.println(42);" );
        rule2.checkin( "" );

        AssetItem rule3 = pkg.addAsset( "A file", "" );
        rule3.updateFormat( AssetFormats.DRL );
        rule3.updateContent( "package foo\n rule 'rule3' \n when \n then \n customer.setAge(43); \n end \n" +
                "rule 'rule4' \n when \n then \n System.err.println(44); \n end" );
        rule3.checkin( "" );
        
        repo.save();
        
        
        ContentPackageAssembler asm = new ContentPackageAssembler(pkg);
        assertFalse(asm.hasErrors());
        assertNotNull(asm.getBinaryPackage());
        Package bin = asm.getBinaryPackage();
        assertEquals(pkg.getName(), bin.getName());
        assertTrue(bin.isValid());
        
        assertEquals(4, bin.getRules().length);
        
        
        
    }
    
    public void testErrorsInFunctionAndRuleAsset() {
        
    }
    
    public void testComplexAssets() {
        
    }
    
    
    
}
