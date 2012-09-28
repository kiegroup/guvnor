/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.selector.AssetSelector;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.MVEL;

/**
 * This will unit test package assembly into a binary.
 */
public class PackageAssemblerIntegrationTest extends GuvnorIntegrationTest {

    /**
     * Test package configuration errors, including header, functions, DSL
     * files.
     */
    @Test
    public void testPackageConfigWithErrors() throws Exception {
        //test the config, no rule assets yet
        ModuleItem pkg = rulesRepository.createModule( "testBuilderPackageConfig",
                                                       "x" );
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                         pkg );
        AssetItem func = pkg.addAsset( "func1",
                                       "a function" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void doSomething() { \n System.err.println(List.class.toString()); }" );
        func.checkin( "yeah" );

        func = pkg.addAsset( "func2",
                             "q" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { \nSystem.err.println(42); \n}" );
        func.checkin( "" );

        AssetItem ass = pkg.addAsset( "dsl",
                                      "m" );
        ass.updateFormat( AssetFormats.DSL );
        ass.updateContent( "[when]Foo bar=String()" );
        ass.checkin( "" );
        rulesRepository.save();

        //now lets light it up
        PackageAssembler assembler = new PackageAssembler();
        assembler.init(pkg, null);
        assembler.compile();
        assertFalse( assembler.hasErrors() );
        
        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( assembler.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package bin = binPkgs[0];
        assertNotNull( bin );

        assertEquals( "testBuilderPackageConfig",
                      bin.getName() );
        assertEquals( 2,
                      bin.getFunctions().size() );

        assertTrue( bin.isValid() );
        assertEquals( 1,
                      assembler.getBuilder().getDSLMappingFiles().size() );

        DroolsHeader.updateDroolsHeader( "koo koo ca choo",
                                         pkg );
        assembler = new PackageAssembler();
        assembler.init(pkg, null);
        assembler.compile();
        assertTrue( assembler.hasErrors() );
        assertTrue( assembler.isModuleConfigurationInError() );

        DroolsHeader.updateDroolsHeader( "import java.util.Date",
                                         pkg );
        assembler = new PackageAssembler();
        assembler.init(pkg, null);
        assembler.compile();
        assertTrue( assembler.hasErrors() );
        assertTrue( assembler.getErrors().get( 0 ).isAssetItem() );

        assertEquals( "func1",
                      assembler.getErrors().get( 0 ).getName() );
        try {
            assembler.getCompiledBinary();
            fail( "should not work as is in error." );
        } catch ( IllegalStateException e ) {
            assertNotNull( e.getMessage() );
        }

        //fix it up
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                         pkg );
        assembler = new PackageAssembler();
        assembler.init(pkg, null);
        assembler.compile();
        assertFalse( assembler.hasErrors() );

        //now break a DSL and check the error
        ass.updateContent( "rubbish" );
        ass.checkin( "" );
        assembler = new PackageAssembler();
        assembler.init(pkg, null);
        assembler.compile();

        //now fix it up
        ass.updateContent( "[when]foo=String()" );
        ass.checkin( "" );
        assembler = new PackageAssembler();
        assembler.init(pkg, null);
        assembler.compile();
        assertFalse( assembler.hasErrors() );

        //break a func, and check for error
        func.updateContent( "goo" );
        func.checkin( "" );
        assembler = new PackageAssembler();
        assembler.init(pkg, null);
        assembler.compile();
        assertTrue( assembler.hasErrors() );
        assertFalse( assembler.isModuleConfigurationInError() );
        assertTrue( assembler.getErrors().get( 0 ).getName().equals( func.getName() ) );
        assertNotEmpty( assembler.getErrors().get( 0 ).getErrorReport() );
    }

    @Test
    public void testLoadConfProperties() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testLoadConfProperties",
                                            "" );
        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.MODEL );

        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer",
                                         pkg );

        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when Board() \n then customer.setAge(42); \n end" );
        rule1.checkin( "" );

        AssetItem props1 = pkg.addAsset( "conf1",
                                         "" );
        props1.updateFormat( "properties" );
        props1.updateContent( "drools.accumulate.function.groupCount = org.drools.base.accumulators.MaxAccumulateFunction" );
        props1.checkin( "" );

        AssetItem props2 = pkg.addAsset( "conf2",
                                         "" );
        props2.updateFormat( "conf" );
        props2.updateBinaryContentAttachment( new ByteArrayInputStream( "drools.accumulate.function.groupFun = org.drools.base.accumulators.MinAccumulateFunction".getBytes() ) );
        props2.checkin( "" );

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertEquals( "org.drools.base.accumulators.MaxAccumulateFunction",
                      asm.getBuilder().getPackageBuilderConfiguration().getAccumulateFunction( "groupCount" ).getClass().getName() );
        assertEquals( "org.drools.base.accumulators.MinAccumulateFunction",
                      asm.getBuilder().getPackageBuilderConfiguration().getAccumulateFunction( "groupFun" ).getClass().getName() );

    }

    @Test
    public void testPackageWithRuleflow() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem packageItem = repo.createModule( "testPackageWithRuleFlow",
                                                    "" );
        AssetItem model = packageItem.addAsset( "model",
                                                "qed" );
        model.updateFormat( AssetFormats.MODEL );

        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer",
                                         packageItem );

        AssetItem rule1 = packageItem.addAsset( "rule_1",
                                                "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when Board() \n then customer.setAge(42); \n end" );
        rule1.checkin( "" );

        AssetItem ruleFlow = packageItem.addAsset( "ruleFlow",
                                                   "" );
        ruleFlow.updateFormat( AssetFormats.RULE_FLOW_RF );

        ruleFlow.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "drools/ruleflow.rfm" ) );
        ruleFlow.checkin( "" );

        PackageAssembler asm = new PackageAssembler();
        asm.init(packageItem, null);
        asm.compile();
        assertFalse( asm.hasErrors() );
        Map<String, org.drools.definition.process.Process> flows = asm.getBuilder().getPackage().getRuleFlows();
        assertNotNull( flows );

        assertEquals( 1,
                      flows.size() );
        Object flow = flows.values().iterator().next();
        assertNotNull( flow );
        assertTrue( flow instanceof RuleFlowProcess );

        //now check we can do some MVEL stuff from the classloader...
        ClassLoaderBuilder classLoaderBuilder = new ClassLoaderBuilder( packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat( AssetFormats.MODEL ) );
        PackageBuilder builder = new BRMSPackageBuilder( new Properties(),
                                                         classLoaderBuilder.buildClassLoader() );
        ClassLoader newCL = builder.getPackageBuilderConfiguration().getClassLoader();
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();

        //set the CL for the current thread so MVEL can find it
        Thread.currentThread().setContextClassLoader( newCL );

        Object o = MVEL.eval( "new com.billasurf.Board()" );
        assertEquals( "com.billasurf.Board",
                      o.getClass().getName() );
        System.err.println( o.toString() );

        Thread.currentThread().setContextClassLoader( oldCL );

        builder.addPackageFromDrl( new StringReader( "package foo\n import com.billasurf.Board" ) );
        Object o2 = builder.getPackageRegistry( "foo" ).getTypeResolver().resolveType( "Board" );
        assertNotNull( o2 );
        assertEquals( "com.billasurf.Board",
                      ((Class< ? >) o2).getName() );
    }

    @Test
    public void testWithNoDeclaredTypes() throws Exception {

        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testSimplePackageWithDeclaredTypes1",
                                            "" );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL_MODEL );
        rule1.checkin( "" );

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.getErrors().toString(),
                     asm.hasErrors() );

    }

    @Test
    public void testSimplePackageWithDeclaredTypes() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testSimplePackageWithDeclaredTypes2",
                                            "" );

        DroolsHeader.updateDroolsHeader( "import java.util.HashMap",
                                         pkg );

        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n dialect 'mvel' \n when Album() \n then \nAlbum a = new Album(); \n end" );
        rule1.checkin( "" );

        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.DRL_MODEL );

        model.updateContent( "declare Album\n genre: String \n end" );
        model.checkin( "" );

        repo.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.getErrors().toString(),
                     asm.hasErrors() );

        assertNotNull( asm.getCompiledBinary() );
        
        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package bin = binPkgs[0];
        assertNotNull( bin );

        assertEquals( pkg.getName(),
                      bin.getName() );
        assertTrue( bin.isValid() );
    }

    @Test
    public void testSimplePackageAttributes() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testSimplePackageAttributes",
                                            "" );

        DroolsHeader.updateDroolsHeader( "import java.util.HashMap\nno-loop true\nagenda-group \"albums\"\ndialect \"java\"\n",
                                         pkg );

        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n dialect \"mvel\" \n when Album() \n then \nAlbum a = new Album(); \n end" );
        rule1.checkin( "" );

        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.DRL_MODEL );

        model.updateContent( "declare Album\n genre: String \n end" );
        model.checkin( "" );

        repo.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.hasErrors() );

        assertNotNull( asm.getCompiledBinary() );
        
        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package bin = binPkgs[0];
        assertNotNull( bin );

        assertEquals( pkg.getName(),
                      bin.getName() );
        assertTrue( bin.isValid() );

        assertEquals( 1,
                      bin.getRules().length );
        assertEquals( "albums",
                      bin.getRule( "rule1" ).getAgendaGroup() );
        assertEquals( true,
                      bin.getRule( "rule1" ).isNoLoop() );
        assertEquals( "mvel",
                      bin.getRule( "rule1" ).getDialect() );

    }

    @Test
    public void testSimplePackageWithDeclaredTypesUsingDependency() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testSimplePackageWithDeclaredTypesUsingDependency",
                                            "" );

        DroolsHeader.updateDroolsHeader( "import java.util.HashMap",
                                         pkg );

        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n dialect 'mvel' \n when Album() \n then \nAlbum a = new Album(); \n end" );
        rule1.checkin( "" );

        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.DRL_MODEL );

        model.updateContent( "declare Album\n genre1: String \n end" );
        model.checkin( "version 0" );
        model.updateContent( "declare Album\n genre2: String \n end" );
        model.checkin( "version 1" );
        model.updateContent( "declare Album\n genre3: String \n end" );
        model.checkin( "version 2" );
        repo.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.getErrors().toString(),
                     asm.hasErrors() );

        assertNotNull( asm.getCompiledBinary() );
        
        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package bin = binPkgs[0];
        assertNotNull( bin );

        assertEquals( pkg.getName(),
                      bin.getName() );
        assertTrue( bin.isValid() );

        pkg.updateDependency( "model?version=2" );
        pkg.checkin( "Update dependency" );

        PackageAssembler asm2 = new PackageAssembler();
        asm2.init(pkg, null);
        asm2.compile();
        assertFalse( asm2.getErrors().toString(),
                     asm2.hasErrors() );

        Package[] bin2Pkgs = (Package[]) DroolsStreamUtils.streamIn( asm2.getCompiledBinary() );

        assertNotNull( bin2Pkgs );
        assertEquals( 1,
                      bin2Pkgs.length );

        Package bin2 = bin2Pkgs[0];
        assertNotNull( bin2 );

        assertEquals( pkg.getName(),
                      bin2.getName() );
        assertTrue( bin2.isValid() );
    }

    @Test
    public void testSimplePackageBuildNoErrors() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testSimplePackageBuildNoErrors",
                                            "" );
        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.MODEL );

        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer",
                                         pkg );

        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when Board() \n then customer.setAge(42); \n end" );
        rule1.checkin( "" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "agenda-group 'q' \n when \n Board() \n then \n System.err.println(42);" );
        rule2.checkin( "" );

        AssetItem rule3 = pkg.addAsset( "A file",
                                        "" );
        rule3.updateFormat( AssetFormats.DRL );
        rule3.updateContent( "package testSimplePackageBuildNoErrors\n rule 'rule3' \n when \n then \n customer.setAge(43); \n end \n" + "rule 'rule4' \n when \n then \n System.err.println(44); \n end" );
        rule3.checkin( "" );

        repo.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.hasErrors() );
        Package bin = asm.getBuilder().getPackage();

        assertEquals( pkg.getName(),
                      bin.getName() );
        assertTrue( bin.isValid() );

        assertEquals( 4,
                      bin.getRules().length );

        //now create a snapshot
        repo.createModuleSnapshot( pkg.getName(),
                                   "SNAP_1" );

        //and screw up the the non snapshot one
        DroolsHeader.updateDroolsHeader( "koo koo ca choo",
                                         pkg );
        asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertTrue( asm.hasErrors() );

        //check the snapshot is kosher
        pkg = repo.loadModuleSnapshot( pkg.getName(),
                                       "SNAP_1" );
        asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.hasErrors() );

    }

    @Test
    public void testIgnoreArchivedItems() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testIgnoreArchivedItems",
                                            "" );
        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.MODEL );

        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer",
                                         pkg );

        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateContent( "rule 'rule1' \n when Board() \n then customer.setAge(42); \n end" );
        rule1.checkin( "" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "agenda-group 'q' \n when \n Boardx() \n then \n System.err.println(42);" );
        rule2.checkin( "" );

        repo.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertTrue( asm.hasErrors() );

        rule2.archiveItem( true );
        rule2.checkin( "" );

        assertTrue( rule2.isArchived() );
        asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.hasErrors() );

    }

    /**
     * This this case we will test errors that occur in rule assets, not in
     * functions or package header.
     */
    @Test
    public void testErrorsInRuleAsset() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        ModuleItem pkg = repo.createModule( "testErrorsInRuleAsset",
                                            "" );
        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer",
                                         pkg );
        repo.save();

        AssetItem goodRule = pkg.addAsset( "goodRule",
                                           "" );
        goodRule.updateFormat( AssetFormats.DRL );
        goodRule.updateContent( "rule 'yeah' \n when \n Board() \n then \n System.out.println(42); end" );
        goodRule.checkin( "" );

        AssetItem badRule = pkg.addAsset( "badRule",
                                          "xxx" );
        badRule.updateFormat( AssetFormats.DRL );
        badRule.updateContent( "if something then another" );
        badRule.checkin( "" );

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertTrue( asm.hasErrors() );
        assertFalse( asm.isModuleConfigurationInError() );

        for ( ContentAssemblyError err : asm.getErrors() ) {
            assertTrue( err.getName().equals( badRule.getName() ) );
            assertNotEmpty( err.getErrorReport() );
        }

    }

    @Test
    @Ignore("Temporally ignored -Rikkola-")
    public void testEventingExample() throws Exception {
        RulesRepository repo = rulesRepository;

        ModuleItem pkg = repo.createModule( "testEventingExample",
                                            "" );
        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.MODEL );

        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "drools/eventing-example.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import org.drools.examples.eventing.EventRequest\n",
                                         pkg );
        AssetItem asset = pkg.addAsset( "whee",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'zaa'\n  when \n  request: EventRequest( status == EventRequest.Status.ACTIVE )\n   then \n request.setStatus(EventRequest.Status.ACTIVE); \n  end" );
        asset.checkin( "" );

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        if ( asm.hasErrors() ) {
            for ( ContentAssemblyError err : asm.getErrors() ) {
                System.err.println( err.getErrorReport() );
            }
            fail();
        }

    }

    /**
     * This time, we mix up stuff a bit
     */
    @Test
    public void testRuleAndDSLAndFunction() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        ModuleItem pkg = repo.createModule( "testRuleAndDSLAndFunction",
                                            "" );
        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.MODEL );
        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        model.checkin( "" );
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer",
                                         pkg );
        repo.save();

        AssetItem func = pkg.addAsset( "func",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { System.out.println(42); }" );
        func.checkin( "" );

        AssetItem dsl = pkg.addAsset( "myDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[then]call a func=foo();" );
        dsl.checkin( "" );

        AssetItem dsl2 = pkg.addAsset( "myDSL2",
                                       "" );
        dsl2.updateFormat( AssetFormats.DSL );
        dsl2.updateContent( "[when]There is a board=Board()" );
        dsl2.checkin( "" );

        AssetItem rule = pkg.addAsset( "myRule",
                                       "" );
        rule.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule.updateContent( "when \n There is a board \n then \n call a func" );
        rule.checkin( "" );

        AssetItem rule2 = pkg.addAsset( "myRule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule2.updateContent( "package testRuleAndDSLAndFunction \n rule 'myRule2222' \n when \n There is a board \n then \n call a func \nend" );
        rule2.checkin( "" );

        AssetItem rule3 = pkg.addAsset( "myRule3",
                                        "" );
        rule3.updateFormat( AssetFormats.DRL );
        rule3.updateContent( "package testRuleAndDSLAndFunction\n rule 'rule3' \n when \n Board() \n then \n System.err.println(42); end" );
        rule3.checkin( "" );

        repo.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init( pkg,
                  null );
        asm.compile();
        assertFalse( asm.hasErrors() );
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {

            Thread.currentThread().setContextClassLoader( asm.getBuilder().getRootClassLoader() );

            Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

            assertNotNull( binPkgs );
            assertEquals( 1,
                          binPkgs.length );

            Package bin = binPkgs[0];
            assertNotNull( bin );

            assertEquals( 3,
                          bin.getRules().length );
            assertEquals( 1,
                          bin.getFunctions().size() );
        } finally {
            Thread.currentThread().setContextClassLoader( currentClassLoader );
        }

    }

    @Test
    public void testSkipDisabledPackageStuff() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        ModuleItem pkg = repo.createModule( "testSkipDisabledPackageStuff",
                                            "" );
        repo.save();

        AssetItem assertRule1 = pkg.addAsset( "model1",
                                              "" );
        assertRule1.updateFormat( AssetFormats.DRL_MODEL );
        assertRule1.updateContent( "garbage" );
        assertRule1.updateDisabled( true );
        assertRule1.checkin( "" );

        assertRule1 = pkg.addAsset( "function1",
                                    "" );
        assertRule1.updateFormat( AssetFormats.FUNCTION );
        assertRule1.updateContent( "garbage" );
        assertRule1.updateDisabled( true );
        assertRule1.checkin( "" );

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.hasErrors() );

    }

    @Test
    public void testXLSDecisionTable() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        ModuleItem pkg = repo.createModule( "testXLSDecisionTable",
                                            "" );

        DroolsHeader.updateDroolsHeader( "import org.acme.insurance.Policy\n import org.acme.insurance.Driver",
                                         pkg );
        repo.save();

        InputStream xls = this.getClass().getResourceAsStream( "/SampleDecisionTable.xls" );
        assertNotNull( xls );

        AssetItem asset = pkg.addAsset( "MyDT",
                                        "" );
        asset.updateFormat( AssetFormats.DECISION_SPREADSHEET_XLS );
        asset.updateBinaryContentAttachment( xls );
        asset.checkin( "" );

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        if ( asm.hasErrors() ) {
            System.err.println( asm.getErrors().get( 0 ).getErrorReport() );
        }
        assertFalse( asm.hasErrors() );

        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package bin = binPkgs[0];
        assertNotNull( bin );

        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( bin );

        WorkingMemory wm = rb.newStatefulSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        wm.insert( driver );
        wm.insert( policy );

        wm.fireAllRules();

        assertEquals( 120,
                      policy.getBasePrice() );

        asset.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "drools/SampleDecisionTable_WithError.xls" ) );
        asset.checkin( "" );
        asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertTrue( asm.hasErrors() );
        assertEquals( asset.getName(),
                      asm.getErrors().get( 0 ).getName() );
        asm = new PackageAssembler();
        asm.init(pkg, null);
        assertFalse( asm.hasErrors() );
    }

    @Test
    public void testBRXMLWithDSLMixedIn() throws Exception {
        RulesRepository repo = rulesRepository;

        //create our package
        ModuleItem pkg = repo.createModule( "testBRLWithDSLMixedIn",
                                            "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                         pkg );
        AssetItem rule1 = pkg.addAsset( "rule2",
                                        "" );
        rule1.updateFormat( AssetFormats.BUSINESS_RULE );

        AssetItem dsl = pkg.addAsset( "MyDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[when]This is a sentence=Person()\n[then]say {hello}=System.err.println({hello});" );
        dsl.checkin( "" );

        RuleModel model = new RuleModel();
        model.name = "rule2";
        FactPattern pattern = new FactPattern( "Person" );
        pattern.setBoundName( "p" );
        ActionSetField action = new ActionSetField( "p" );
        ActionFieldValue value = new ActionFieldValue( "age",
                                                       "42",
                                                       SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        action.addFieldValue( value );

        model.addLhsItem( pattern );
        model.addRhsItem( action );

        DSLSentence dslCondition = new DSLSentence();
        dslCondition.setDefinition( "This is a sentence" );

        model.addLhsItem( dslCondition );

        DSLSentence dslAction = new DSLSentence();
        dslAction.setDefinition( "say {42}" );

        model.addRhsItem( dslAction );

        rule1.updateContent( BRXMLPersistence.getInstance().marshal( model ) );
        rule1.checkin( "" );
        repo.save();

        //now add a rule with no DSL
        model = new RuleModel();
        model.name = "ruleNODSL";
        pattern = new FactPattern( "Person" );
        pattern.setBoundName( "p" );
        action = new ActionSetField( "p" );
        value = new ActionFieldValue( "age",
                                      "42",
                                      SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        action.addFieldValue( value );

        model.addLhsItem( pattern );
        model.addRhsItem( action );

        AssetItem ruleNODSL = pkg.addAsset( "ruleNoDSL",
                                            "" );
        ruleNODSL.updateFormat( AssetFormats.BUSINESS_RULE );

        ruleNODSL.updateContent( BRXMLPersistence.getInstance().marshal( model ) );
        ruleNODSL.checkin( "" );

        pkg = repo.loadModule( "testBRLWithDSLMixedIn" );
        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        assertFalse( asm.hasErrors() );
        
        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package bin = binPkgs[0];

        assertEquals( 2,
                      bin.getRules().length );

    }

    @Test
    public void testCustomSelector() throws Exception {
        RulesRepository repo = rulesRepository;

        //create our package
        ModuleItem pkg = repo.createModule( "testCustomSelector",
                                            "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                         pkg );
        AssetItem rule1 = pkg.addAsset( "rule1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );

        rule1.updateContent( "when \n Person() \n then \n System.out.println(\"yeah\");\n" );
        rule1.checkin( "" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateContent( "when \n Person() \n then \n System.out.println(\"yeah\");\n" );
        rule2.checkin( "" );

        SelectorManager sm = SelectorManager.getInstance();
        sm.selectors.put( "testSelect",
                          new AssetSelector() {
                              public boolean isAssetAllowed(AssetItem asset) {
                                  return asset.getName().equals( "rule2" );
                              }
                          } );

        ModuleAssemblerConfiguration configuration = new ModuleAssemblerConfiguration();
        configuration.setBuildMode( "customSelector" );
        configuration.setCustomSelectorConfigName( "testSelect" );
        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, configuration);

        asm.compile();

        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package pk = binPkgs[0];
        assertNotNull( pk );

        assertEquals( 1,
                      pk.getRules().length );
        assertEquals( "rule2",
                      pk.getRules()[0].getName() );

        asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        
        binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        pk = binPkgs[0];
        assertNotNull( pk );

        assertEquals( 2,
                      pk.getRules().length );

        configuration = new ModuleAssemblerConfiguration();
        configuration.setBuildMode( "customSelector" );
        configuration.setCustomSelectorConfigName( "nothing valid" );
        asm = new PackageAssembler();
        asm.init(pkg, configuration);
        asm.compile();
        assertTrue( asm.hasErrors() );
        assertEquals( 1,
                      asm.getErrors().size() );
        assertEquals( pkg.getName(),
                      asm.getErrors().get( 0 ).getName() );
        assertTrue( asm.getErrors().get( 0 ).isModuleItem() );
        assertEquals( pkg.getUUID(),
                      asm.getErrors().get( 0 ).getUUID() );

        configuration = new ModuleAssemblerConfiguration();
        configuration.setBuildMode( "customSelector" );
        configuration.setCustomSelectorConfigName( "" );
        asm = new PackageAssembler();
        asm.init(pkg, configuration);
        asm.compile();
        
        binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        pk = binPkgs[0];
        assertNotNull( pk );

        assertEquals( 2,
                      pk.getRules().length );
    }

    @Test
    public void testBuiltInSelector() throws Exception {
        RulesRepository repo = rulesRepository;

        CategoryItem rootCat = repo.loadCategory( "/" );
        CategoryItem testBuiltInSelectorCategory1 = rootCat.addCategory( "testBuiltInSelectorCategory1",
                                                                         "yeah" );
        testBuiltInSelectorCategory1.addCategory( "testBuiltInSelectorCategory1Child",
                                                  "yeah" );
        CategoryItem testBuiltInSelectorCategory2 = rootCat.addCategory( "testBuiltInSelectorCategory2",
                                                                         "yeah" );
        testBuiltInSelectorCategory2.addCategory( "testBuiltInSelectorCategory2Child",
                                                  "yeah" );

        //create our package
        ModuleItem pkg = repo.createModule( "testBuiltInSelector",
                                            "" );
        DroolsHeader.updateDroolsHeader( "import org.drools.Person",
                                         pkg );
        AssetItem rule1 = pkg.addAsset( "rule1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL );
        rule1.updateCategoryList( new String[]{"testBuiltInSelectorCategory1/testBuiltInSelectorCategory1Child"} );

        rule1.updateContent( "when \n Person() \n then \n System.out.println(\"yeah\");\n" );
        rule1.checkin( "" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DRL );
        rule2.updateCategoryList( new String[]{"testBuiltInSelectorCategory2/testBuiltInSelectorCategory2Child"} );
        rule2.updateContent( "when \n Person() \n then \n System.out.println(\"yeah\");\n" );
        rule2.checkin( "" );

        SelectorManager sm = SelectorManager.getInstance();
        sm.selectors.put( "testSelect",
                          new AssetSelector() {
                              public boolean isAssetAllowed(AssetItem asset) {
                                  return asset.getName().equals( "rule2" );
                              }
                          } );

        ModuleAssemblerConfiguration packageAssemblerConfiguration = new ModuleAssemblerConfiguration();
        packageAssemblerConfiguration.setBuildMode( "BuiltInSelector" );
        packageAssemblerConfiguration.setEnableStatusSelector( false );
        packageAssemblerConfiguration.setCategoryOperator( "=" );
        packageAssemblerConfiguration.setCategoryValue( "testBuiltInSelectorCategory1/testBuiltInSelectorCategory1Child" );
        packageAssemblerConfiguration.setEnableCategorySelector( true );

        //without selector
        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        asm.compile();
        
        Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        Package pk = binPkgs[0];
        assertNotNull( pk );

        assertEquals( 2,
                      pk.getRules().length );

        //with built-in selector
        asm = new PackageAssembler();
        asm.init(pkg, packageAssemblerConfiguration);
        asm.compile();
        
        binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        pk = binPkgs[0];
        assertNotNull( pk );

        assertEquals( 1,
                      pk.getRules().length );
        assertEquals( "rule1",
                      pk.getRules()[0].getName() );

        packageAssemblerConfiguration = new ModuleAssemblerConfiguration();
        packageAssemblerConfiguration.setBuildMode( "BuiltInSelector" );
        packageAssemblerConfiguration.setEnableStatusSelector( false );
        packageAssemblerConfiguration.setCategoryOperator( "!=" );
        packageAssemblerConfiguration.setCategoryValue( "testBuiltInSelectorCategory1/testBuiltInSelectorCategory1Child" );
        packageAssemblerConfiguration.setEnableCategorySelector( true );

        //with built-in selector
        asm = new PackageAssembler();
        asm.init(pkg, packageAssemblerConfiguration);
        asm.compile();
        
        binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

        assertNotNull( binPkgs );
        assertEquals( 1,
                      binPkgs.length );

        pk = binPkgs[0];
        assertNotNull( pk );

        assertEquals( 1,
                      pk.getRules().length );
        assertEquals( "rule2",
                      pk.getRules()[0].getName() );
    }
    
    @Test
    public void testFunctionWithFactType() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        ModuleItem pkg = repo.createModule( "testFunctionWithFactType",
                                            "" );

        AssetItem func = pkg.addAsset( "func",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function String PersonToString(Person p) {\n" +
                "String result = \"\";\n" +
                "result = p.getName() + \", age: \" + p.getAge();\n" +
                "return result;\n" +
                "}\n" );
        func.checkin( "" );

        AssetItem testRule1 = pkg.addAsset( "testRule1",
                                        "" );
        testRule1.updateFormat( AssetFormats.DRL );
        testRule1.updateContent( "dialect 'mvel'\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "System.out.println(PersonToString($p));\n" +
                "end");
        testRule1.checkin( "" );

        repo.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init( pkg,
                  null );
        asm.compile();
        assertFalse( asm.hasErrors() );
        
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {

            Thread.currentThread().setContextClassLoader( asm.getBuilder().getRootClassLoader() );

            Package[] binPkgs = (Package[]) DroolsStreamUtils.streamIn( asm.getCompiledBinary() );

            assertNotNull( binPkgs );
            assertEquals( 1,
                          binPkgs.length );

            Package bin = binPkgs[0];
            assertNotNull( bin );

            assertEquals( 2,
                          bin.getRules().length );
            assertEquals( 1,
                          bin.getFunctions().size() );
        } finally {
            Thread.currentThread().setContextClassLoader( currentClassLoader );
        }

    }
    
    private void assertNotEmpty(String s) {
        if ( s == null ) fail( "should not be null" );
        if ( s.trim().equals( "" ) ) fail( "should not be empty string" );
    }
}
