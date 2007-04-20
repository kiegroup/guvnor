package org.drools.brms.server.builder;

import junit.framework.TestCase;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;

public class ContentPackageAssemblerTest extends TestCase {

    
    
    /**
     * Test package configuration errors, 
     * including header, functions, DSL files.
     */
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
        
        //fix it up
        pkg.updateHeader( "import java.util.List" );
        assembler = new ContentPackageAssembler(pkg);
        assertFalse(assembler.hasErrors());
        
        //now break a DSL and check the error
        ass.updateContent( "rubbish" );
        ass.checkin( "" );
        assembler = new ContentPackageAssembler(pkg);
        assertTrue(assembler.hasErrors());
        assertTrue(assembler.getErrors().get( 0 ).itemInError.getName().equals( ass.getName() ));
        assertNotEmpty(assembler.getErrors().get( 0 ).errorReport);
        assertFalse(assembler.isPackageConfigurationInError());
        
        //now fix it up
        ass.updateContent( "[when]foo=String()" );
        ass.checkin( "" );
        assembler = new ContentPackageAssembler(pkg);
        assertFalse(assembler.hasErrors());        
        
        //break a func, and check for error
        func.updateContent( "goo" );
        func.checkin( "" );
        assembler = new ContentPackageAssembler(pkg);
        assertTrue(assembler.hasErrors());
        assertFalse(assembler.isPackageConfigurationInError());
        assertTrue(assembler.getErrors().get( 0 ).itemInError.getName().equals( func.getName() ));
        assertNotEmpty(assembler.getErrors().get( 0 ).errorReport);
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
        
        //now create a snapshot
        repo.createPackageSnapshot( pkg.getName(), "SNAP_1" );
        
        //and screw up the the non snapshot one
        pkg.updateHeader( "koo koo ca choo" );
        asm = new ContentPackageAssembler(pkg);
        assertTrue(asm.hasErrors());
        
        
        //check the snapshot is kosher
        pkg = repo.loadPackageSnapshot( pkg.getName(), "SNAP_1" );
        asm = new ContentPackageAssembler(pkg);
        assertFalse(asm.hasErrors());
        
    }
    
    /**
     * This this case we will test errors that occur in rule assets,
     * not in functions or package header.
     */
    public void testErrorsInRuleAsset() throws Exception {

        RulesRepository repo = getRepo();
        
        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testErrorsInRuleAsset", "" );
        AssetItem model = pkg.addAsset( "model", "qed" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        pkg.updateHeader( "import com.billasurf.Board\n global com.billasurf.Person customer" );
        repo.save();
        
        AssetItem goodRule = pkg.addAsset( "goodRule", "" );
        goodRule.updateFormat( AssetFormats.DRL );
        goodRule.updateContent( "rule 'yeah' \n when \n Board() \n then \n System.out.println(42); end" );
        goodRule.checkin( "" );
        
        AssetItem badRule = pkg.addAsset( "badRule", "xxx" );
        badRule.updateFormat( AssetFormats.DRL );
        badRule.updateContent( "if something then another" );
        badRule.checkin( "" );
        
        ContentPackageAssembler asm = new ContentPackageAssembler(pkg);
        assertTrue(asm.hasErrors());
        assertFalse(asm.isPackageConfigurationInError());

        for ( ContentAssemblyError err : asm.getErrors() ) {
            assertTrue(err.itemInError.getName().equals( badRule.getName() ));
            assertNotEmpty(err.errorReport);
        }
        
    }
    


    /**
     * This time, we mix up stuff a bit
     *
     */
    public void testRuleAndDSLAndFunction() throws Exception {
        RulesRepository repo = getRepo();
        
        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testRuleAndDSLAndFunction", "" );
        AssetItem model = pkg.addAsset( "model", "qed" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        pkg.updateHeader( "import com.billasurf.Board\n global com.billasurf.Person customer" );
        repo.save();
        
        AssetItem func = pkg.addAsset( "func", "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { System.out.println(42); }" );
        func.checkin( "" );
        
        AssetItem dsl = pkg.addAsset( "myDSL", "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[then]call a func=foo();" );
        dsl.checkin( "" );

        AssetItem dsl2 = pkg.addAsset( "myDSL2", "" );
        dsl2.updateFormat( AssetFormats.DSL );
        dsl2.updateContent( "[when]There is a board=Board()" );
        dsl2.checkin( "" );
        
        
        AssetItem rule = pkg.addAsset( "myRule", "" );
        rule.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule.updateContent( "when \n There is a board \n then \n call a func" );
        rule.checkin( "" );
        
        AssetItem rule2 = pkg.addAsset( "myRule2", "" );
        rule2.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule2.updateContent( "package xyz \n rule 'myRule2222' \n when \n There is a board \n then \n call a func \nend" );
        rule2.checkin( "" );
        
        AssetItem rule3 = pkg.addAsset( "myRule3", "" );
        rule3.updateFormat( AssetFormats.DRL );
        rule3.updateContent( "package QED\n rule 'rule3' \n when \n Board() \n then \n System.err.println(42); end");
        rule3.checkin( "" );
        
        repo.save();
        
        ContentPackageAssembler asm = new ContentPackageAssembler(pkg);
        assertFalse(asm.hasErrors());
        Package bin = asm.getBinaryPackage();
        assertNotNull(bin);
        assertEquals(3, bin.getRules().length);
        assertEquals(1, bin.getFunctions().size());
        
    }
    
    private void assertNotEmpty(String s) {
        if (s == null) fail("should not be null");
        if (s.trim().equals( "" )) fail("should not be empty string");
    }
    
    private RulesRepository getRepo() throws Exception {
        return new RulesRepository( TestEnvironmentSessionHelper.getSession() );
    }
    
    
}
