/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarInputStream;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.selector.AssetSelector;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mvel2.MVEL;

/**
 * This will unit test package assembly into a binary.
 */
public class ContentPackageAssemblerTest extends GuvnorTestBase {

    @Before
    public void setUp() {
        setUpSeamAndRepository();
        setUpMockIdentity();
    }

    @After
    public void tearDown() {
        tearAllDown();
    }

    /**
     * Test package configuration errors,
     * including header, functions, DSL files.
     */
    @Test
    public void testPackageConfigWithErrors() throws Exception {
        //test the config, no rule assets yet
        RulesRepository repo = getRulesRepository();
        PackageItem pkg = repo.createPackage( "testBuilderPackageConfig",
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
        repo.save();

        //now lets light it up
        ContentPackageAssembler assembler = new ContentPackageAssembler( pkg );
        assertFalse( assembler.hasErrors() );
        Package bin = assembler.getBinaryPackage();
        assertNotNull( bin );
        assertEquals( "testBuilderPackageConfig",
                      bin.getName() );
        assertEquals( 2,
                      bin.getFunctions().size() );

        assertTrue( bin.isValid() );
        assertEquals( 1,
                      assembler.builder.getDSLMappingFiles().size() );

        DroolsHeader.updateDroolsHeader( "koo koo ca choo",
                                                  pkg );
        assembler = new ContentPackageAssembler( pkg );
        assertTrue( assembler.hasErrors() );
        assertTrue( assembler.isPackageConfigurationInError() );

        DroolsHeader.updateDroolsHeader( "import java.util.Date",
                                                  pkg );
        assembler = new ContentPackageAssembler( pkg );
        assertTrue( assembler.hasErrors() );
        assertTrue( assembler.getErrors().get( 0 ).isAssetItem() );

        assertEquals( "func1",
                      assembler.getErrors().get( 0 ).getName() );
        try {
            assembler.getBinaryPackage();
            fail( "should not work as is in error." );
        } catch ( IllegalStateException e ) {
            assertNotNull( e.getMessage() );
        }

        //fix it up
        DroolsHeader.updateDroolsHeader( "import java.util.List",
                                                  pkg );
        assembler = new ContentPackageAssembler( pkg );
        assertFalse( assembler.hasErrors() );

        //now break a DSL and check the error
        ass.updateContent( "rubbish" );
        ass.checkin( "" );
        assembler = new ContentPackageAssembler( pkg );

        //now fix it up
        ass.updateContent( "[when]foo=String()" );
        ass.checkin( "" );
        assembler = new ContentPackageAssembler( pkg );
        assertFalse( assembler.hasErrors() );

        //break a func, and check for error
        func.updateContent( "goo" );
        func.checkin( "" );
        assembler = new ContentPackageAssembler( pkg );
        assertTrue( assembler.hasErrors() );
        assertFalse( assembler.isPackageConfigurationInError() );
        assertTrue( assembler.getErrors().get( 0 ).getName().equals( func.getName() ) );
        assertNotEmpty( assembler.getErrors().get( 0 ).getErrorReport() );
    }

    @Test
    public void testLoadConfProperties() throws Exception {

        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testLoadConfProperties",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertEquals( "org.drools.base.accumulators.MaxAccumulateFunction",
                      asm.builder.getPackageBuilderConfiguration().getAccumulateFunction( "groupCount" ).getClass().getName() );
        assertEquals( "org.drools.base.accumulators.MinAccumulateFunction",
                      asm.builder.getPackageBuilderConfiguration().getAccumulateFunction( "groupFun" ).getClass().getName() );

    }

    @Test
    public void testPackageWithRuleflow() throws Exception {
        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testPackageWithRuleFlow",
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

        AssetItem ruleFlow = pkg.addAsset( "ruleFlow",
                                           "" );
        ruleFlow.updateFormat( AssetFormats.RULE_FLOW_RF );

        ruleFlow.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/ruleflow.rfm" ) );
        ruleFlow.checkin( "" );

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.hasErrors() );
        Map<String, org.drools.definition.process.Process> flows = asm.getBinaryPackage().getRuleFlows();
        assertNotNull( flows );

        assertEquals( 1,
                      flows.size() );
        Object flow = flows.values().iterator().next();
        assertNotNull( flow );
        assertTrue( flow instanceof RuleFlowProcess );

        //now check we can do some MVEL stuff from the classloader...
        List<JarInputStream> jars = BRMSPackageBuilder.getJars( pkg );
        PackageBuilder builder = BRMSPackageBuilder.getInstance( jars,
                                                                 new Properties() );
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
        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testSimplePackageWithDeclaredTypes1",
                                              "" );
        AssetItem rule1 = pkg.addAsset( "rule_1",
                                        "" );
        rule1.updateFormat( AssetFormats.DRL_MODEL );
        rule1.checkin( "" );

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.getErrors().toString(),
                     asm.hasErrors() );

    }

    @Test
    public void testSimplePackageWithDeclaredTypes() throws Exception {
        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testSimplePackageWithDeclaredTypes2",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.getErrors().toString(),
                     asm.hasErrors() );

        assertNotNull( asm.getBinaryPackage() );
        Package bin = asm.getBinaryPackage();
        assertEquals( pkg.getName(),
                      bin.getName() );
        assertTrue( bin.isValid() );

        asm = new ContentPackageAssembler( pkg,
                                           false );
        String drl = asm.getDRL();

        assertTrue( drl.indexOf( "declare Album" ) > -1 );

    }
    
    @Test
    public void testSimplePackageWithDeclaredTypesUsingDependency() throws Exception {
        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testSimplePackageWithDeclaredTypesUsingDependency",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.getErrors().toString(),
                     asm.hasErrors() );

        assertNotNull( asm.getBinaryPackage() );
        Package bin = asm.getBinaryPackage();
        assertEquals( pkg.getName(),
                      bin.getName() );
        assertTrue( bin.isValid() );

        asm = new ContentPackageAssembler( pkg,
                                           false );
        String drl = asm.getDRL();

        assertTrue( drl.indexOf( "genre2" ) == -1 );
        assertTrue( drl.indexOf( "genre3" ) > -1 );
        
        pkg.updateDependency("/drools:repository/drools:package_area/testSimplePackageWithDeclaredTypesUsingDependency/assets/model?version=2");
        pkg.checkin("Update dependency");
        
        ContentPackageAssembler asm2 = new ContentPackageAssembler( pkg );
        assertFalse( asm2.getErrors().toString(),
                     asm2.hasErrors() );

        assertNotNull( asm2.getBinaryPackage() );
        Package bin2 = asm2.getBinaryPackage();
        assertEquals( pkg.getName(),
                      bin2.getName() );
        assertTrue( bin2.isValid() );

        asm2 = new ContentPackageAssembler( pkg,
                                           false );
        String drl2 = asm2.getDRL();

        assertTrue( drl2.indexOf( "genre2" ) > -1 );
        assertTrue( drl2.indexOf( "genre3" ) == -1 );

    }

    @Test
    public void testSimplePackageBuildNoErrors() throws Exception {
        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testSimplePackageBuildNoErrors",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.hasErrors() );
        assertNotNull( asm.getBinaryPackage() );
        Package bin = asm.getBinaryPackage();
        assertEquals( pkg.getName(),
                      bin.getName() );
        assertTrue( bin.isValid() );

        assertEquals( 4,
                      bin.getRules().length );

        //now create a snapshot
        repo.createPackageSnapshot( pkg.getName(),
                                    "SNAP_1" );

        //and screw up the the non snapshot one
        DroolsHeader.updateDroolsHeader( "koo koo ca choo",
                                                  pkg );
        asm = new ContentPackageAssembler( pkg );
        assertTrue( asm.hasErrors() );

        //check the snapshot is kosher
        pkg = repo.loadPackageSnapshot( pkg.getName(),
                                        "SNAP_1" );
        asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.hasErrors() );

    }

    @Test
    public void testIgnoreArchivedItems() throws Exception {
        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testIgnoreArchivedItems",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertTrue( asm.hasErrors() );

        rule2.archiveItem( true );
        rule2.checkin( "" );

        assertTrue( rule2.isArchived() );
        asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.hasErrors() );

    }

    /**
     * This this case we will test errors that occur in rule assets,
     * not in functions or package header.
     */
    @Test
    public void testErrorsInRuleAsset() throws Exception {

        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testErrorsInRuleAsset",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertTrue( asm.hasErrors() );
        assertFalse( asm.isPackageConfigurationInError() );

        for ( ContentAssemblyError err : asm.getErrors() ) {
            assertTrue( err.getName().equals( badRule.getName() ) );
            assertNotEmpty( err.getErrorReport() );
        }

    }

    @Test
    public void testEventingExample() throws Exception {

        RulesRepository repo = getRulesRepository();

        PackageItem pkg = repo.createPackage( "testEventingExample",
                                              "" );
        AssetItem model = pkg.addAsset( "model",
                                        "qed" );
        model.updateFormat( AssetFormats.MODEL );

        model.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/eventing-example.jar" ) );
        model.checkin( "" );

        DroolsHeader.updateDroolsHeader( "import org.drools.examples.eventing.EventRequest\n",
                                                  pkg );
        AssetItem asset = pkg.addAsset( "whee",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );
        asset.updateContent( "rule 'zaa'\n  when \n  request: EventRequest( status == EventRequest.Status.ACTIVE )\n   then \n request.setStatus(EventRequest.Status.ACTIVE); \n  end" );
        asset.checkin( "" );

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
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
        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testRuleAndDSLAndFunction",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.hasErrors() );
        Package bin = asm.getBinaryPackage();
        assertNotNull( bin );
        assertEquals( 3,
                      bin.getRules().length );
        assertEquals( 1,
                      bin.getFunctions().size() );

    }

    @Test
    public void testShowSource() throws Exception {
        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testShowSource",
                                              "" );

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
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=FooBarBaz()" );
        dsl.checkin( "" );

        AssetItem rule = pkg.addAsset( "rule1",
                                       "" );
        rule.updateFormat( AssetFormats.DRL );
        rule.updateContent( "rule 'foo' when Goo() then end" );
        rule.checkin( "" );

        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "" );

        AssetItem rule3 = pkg.addAsset( "model1",
                                        "" );
        rule3.updateFormat( AssetFormats.DRL_MODEL );
        rule3.updateContent( "garbage" );
        rule3.updateDisabled( true );
        rule3.checkin( "" );

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg,
                                                                   false );
        String drl = asm.getDRL();

        assertNotNull( drl );

        assertContains( "import com.billasurf.Board\n global com.billasurf.Person customer",
                        drl );
        assertContains( "package testShowSource",
                        drl );
        assertContains( "function void foo() { System.out.println(42); }",
                        drl );
        assertContains( "foo();",
                        drl );
        assertContains( "FooBarBaz()",
                        drl );
        assertContains( "rule 'foo' when Goo() then end",
                        drl );

        assertEquals( -1,
                      drl.indexOf( "garbage" ) );

    }
    
    @Test
    public void testShowSourceUsingDependency() throws Exception {
        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testShowSourceUsingDependency",
                                              "" );

        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\n global com.billasurf.Person customer",
                                                  pkg );
        repo.save();

        AssetItem func = pkg.addAsset( "func",
                                       "" );
        func.updateFormat( AssetFormats.FUNCTION );
        func.updateContent( "function void foo() { System.out.println(version 1); }" );
        func.checkin( "version 1" );        
        func.updateContent( "function void foo() { System.out.println(version 2); }" );
        func.checkin( "version 2" ); 

        AssetItem dsl = pkg.addAsset( "myDSL",
                                      "" );
        dsl.updateFormat( AssetFormats.DSL );
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=FooBarBaz1()" );
        dsl.checkin( "version 1" );
        dsl.updateContent( "[then]call a func=foo();\n[when]foo=FooBarBaz2()" );
        dsl.checkin( "version 2" );
        
        AssetItem rule = pkg.addAsset( "rule1", "" );
        rule.updateFormat( AssetFormats.DRL );
        rule.updateContent( "rule 'foo' when Goo() then end" );
        rule.checkin( "version 1" );
        rule.updateContent( "rule 'foo' when Eoo() then end" );
        rule.checkin( "version 2" );
        
        AssetItem rule2 = pkg.addAsset( "rule2",
                                        "" );
        rule2.updateFormat( AssetFormats.DSL_TEMPLATE_RULE );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 1" );
        rule2.updateContent( "when \n foo \n then \n call a func" );
        rule2.checkin( "version 2" );
        
        AssetItem rule3 = pkg.addAsset( "model1",
                                        "" );
        rule3.updateFormat( AssetFormats.DRL_MODEL );
        rule3.updateContent( "garbage" );
        rule3.updateDisabled( true );
        rule3.checkin( "version 1" );
        rule3.updateContent( "declare Album\n genre1: String \n end" );
        rule3.checkin( "version 2" );
        
        repo.save();
        
        //NOTE: dont use version=0. Version 0 is the root node. 
        pkg.updateDependency("/drools:repository/drools:package_area/testShowSourceUsingDependency/assets/func?version=1");
        pkg.updateDependency("/drools:repository/drools:package_area/testShowSourceUsingDependency/assets/myDSL?version=1");
        pkg.updateDependency("/drools:repository/drools:package_area/testShowSourceUsingDependency/assets/rule1?version=1");
        pkg.updateDependency("/drools:repository/drools:package_area/testShowSourceUsingDependency/assets/rule2?version=1");
        pkg.updateDependency("/drools:repository/drools:package_area/testShowSourceUsingDependency/assets/model1?version=1");
        pkg.checkin("Update dependency");

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg,
                                                                   false );
        String drl = asm.getDRL();

        assertNotNull( drl );

        assertContains( "import com.billasurf.Board\n global com.billasurf.Person customer",
                        drl );
        assertContains( "package testShowSource",
                        drl );
        assertContains( "function void foo() { System.out.println(version 1); }",
                        drl );
        assertContains( "foo();",
                        drl );
        assertContains( "FooBarBaz1()",
                        drl );
        assertContains( "rule 'foo' when Goo() then end",
                        drl );

        assertEquals( -1,
                      drl.indexOf( "garbage" ) );
        assertEquals( -1,
                drl.indexOf( "Album" ) );
    }

    @Test
    public void testBuildPackageWithEmptyHeader() throws Exception {
        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testBuildPackageWithEmptyHeader",
                                              "" );

        DroolsHeader.updateDroolsHeader( "\n",
                                                  pkg );
        repo.save();

        ContentPackageAssembler asm = null;
        try {
            asm = new ContentPackageAssembler( pkg );
        } catch ( NullPointerException e ) {
            // Possible cause: Header has only white spaces "\n\t".
            fail( e.toString() );
        }
        String drl = asm.getDRL();

        assertNotNull( drl );
        assertEquals( "package testBuildPackageWithEmptyHeader",
                      drl.trim() );

    }

    @Test
    public void testSkipDisabledPackageStuff() throws Exception {
        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testSkipDisabledPackageStuff",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.hasErrors() );

    }

    @Test
    public void testSkipDisabledAssets() throws Exception {
        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testSkipDisabledAssets",
                                              "" );
        repo.save();

        AssetItem assertRule1 = pkg.addAsset( "rule1",
                                              "" );
        assertRule1.updateFormat( AssetFormats.DRL );
        assertRule1.updateContent( "rule 'foo1' when then end" );
        assertRule1.checkin( "" );
        assertRule1.updateDisabled( false );

        AssetItem assertRule2 = pkg.addAsset( "rule2",
                                              "" );
        assertRule2.updateFormat( AssetFormats.DRL );
        assertRule2.updateContent( "rule 'foo2' when then end" );
        assertRule2.checkin( "" );
        assertRule2.updateDisabled( true );

        AssetItem assertRule3 = pkg.addAsset( "rule3",
                                              "" );
        assertRule3.updateFormat( AssetFormats.DRL );
        assertRule3.updateContent( "rule 'foo3' when then end" );
        assertRule3.checkin( "" );

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg,
                                                                   true );
        assertFalse( asm.hasErrors() );
        Package p = asm.builder.getPackage();

        String errors = p.getErrorSummary();
        assertNull( errors );

        Rule rule1 = p.getRule( "foo1" );
        assertNotNull( rule1 );
        Rule rule2 = p.getRule( "foo2" );
        assertNull( rule2 );
        Rule rule3 = p.getRule( "foo3" );
        assertNotNull( rule3 );

        String drl = asm.getDRL();

        assertNotNull( drl );

        assertContains( "rule 'foo1' when then end",
                        drl );
        assertDoesNotContain( "rule 'foo2' when then end",
                              drl );
        assertContains( "rule 'foo3' when then end",
                        drl );

    }

    @Test
    public void testXLSDecisionTable() throws Exception {

        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testXLSDecisionTable",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        if ( asm.hasErrors() ) {
            System.err.println( asm.getErrors().get( 0 ).getErrorReport() );
            System.err.println( asm.getDRL() );
        }
        assertFalse( asm.hasErrors() );

        String drl = asm.getDRL();
        System.out.println( drl );

        assertContains( "policy: Policy",
                        drl );

        Package bin = asm.getBinaryPackage();

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

        asset.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/SampleDecisionTable_WithError.xls" ) );
        asset.checkin( "" );
        asm = new ContentPackageAssembler( pkg );
        assertTrue( asm.hasErrors() );
        assertEquals( asset.getName(),
                      asm.getErrors().get( 0 ).getName() );
        asm = new ContentPackageAssembler( pkg,
                                           false );
        assertFalse( asm.hasErrors() );
        drl = asm.getDRL();
        assertNotNull( drl );
        assertContains( "Driverx",
                        drl );

    }

    @Test
    public void testSkipDisabledImports() throws Exception {

        RulesRepository repo = getRulesRepository();

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage( "testXLSDecisionTableIgnoreImports",
                                              "" );

        repo.save();

        InputStream xls = this.getClass().getResourceAsStream( "/Sample.xls" );
        assertNotNull( xls );

        AssetItem asset = pkg.addAsset( "MyDT",
                                        "" );
        asset.updateFormat( AssetFormats.DECISION_SPREADSHEET_XLS );
        asset.updateBinaryContentAttachment( xls );
        asset.checkin( "" );

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg,
                                                                   true );
        String drl = asm.getDRL();
        System.err.println( drl );

        assertTrue( drl.indexOf( "package ",
                                 2 ) == -1 ); //skip a few, make sure we only have one instance of "package "
    }

    @Test
    public void testBRXMLWithDSLMixedIn() throws Exception {
        RulesRepository repo = getRulesRepository();

        //create our package
        PackageItem pkg = repo.createPackage( "testBRLWithDSLMixedIn",
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
        pattern.boundName = "p";
        ActionSetField action = new ActionSetField( "p" );
        ActionFieldValue value = new ActionFieldValue( "age",
                                                       "42",
                                                       SuggestionCompletionEngine.TYPE_NUMERIC );
        action.addFieldValue( value );

        model.addLhsItem( pattern );
        model.addRhsItem( action );

        DSLSentence dslCondition = new DSLSentence();
        dslCondition.sentence = "This is a sentence";

        model.addLhsItem( dslCondition );

        DSLSentence dslAction = new DSLSentence();
        dslAction.sentence = "say {42}";

        model.addRhsItem( dslAction );

        rule1.updateContent( BRXMLPersistence.getInstance().marshal( model ) );
        rule1.checkin( "" );
        repo.save();

        //now add a rule with no DSL
        model = new RuleModel();
        model.name = "ruleNODSL";
        pattern = new FactPattern( "Person" );
        pattern.boundName = "p";
        action = new ActionSetField( "p" );
        value = new ActionFieldValue( "age",
                                      "42",
                                      SuggestionCompletionEngine.TYPE_NUMERIC );
        action.addFieldValue( value );

        model.addLhsItem( pattern );
        model.addRhsItem( action );

        AssetItem ruleNODSL = pkg.addAsset( "ruleNoDSL",
                                            "" );
        ruleNODSL.updateFormat( AssetFormats.BUSINESS_RULE );

        ruleNODSL.updateContent( BRXMLPersistence.getInstance().marshal( model ) );
        ruleNODSL.checkin( "" );

        pkg = repo.loadPackage( "testBRLWithDSLMixedIn" );
        ContentPackageAssembler asm = new ContentPackageAssembler( pkg );
        assertFalse( asm.hasErrors() );
        Package bpkg = asm.getBinaryPackage();
        assertEquals( 2,
                      bpkg.getRules().length );

    }

    @Test
    public void testCustomSelector() throws Exception {
        RulesRepository repo = getRulesRepository();

        //create our package
        PackageItem pkg = repo.createPackage( "testCustomSelector",
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

        ContentPackageAssembler asm = new ContentPackageAssembler( pkg,
                                                                   true,
                                                                   "customSelector",
                                                                   null,
                                                                   null,
                                                                   false,
                                                                   null,
                                                                   null,
                                                                   false,
                                                                   "testSelect" );

        Package pk = asm.getBinaryPackage();
        assertEquals( 1,
                      pk.getRules().length );
        assertEquals( "rule2",
                      pk.getRules()[0].getName() );

        asm = new ContentPackageAssembler( pkg,
                                           true );
        pk = asm.getBinaryPackage();
        assertEquals( 2,
                      pk.getRules().length );

        asm = new ContentPackageAssembler( pkg,
                                           true,
                                           "customSelector",
                                           null,
                                           null,
                                           false,
                                           null,
                                           null,
                                           false,
                                           "nothing valid" );
        assertTrue( asm.hasErrors() );
        assertEquals( 1,
                      asm.getErrors().size() );
        assertEquals( pkg.getName(),
                      asm.getErrors().get( 0 ).getName() );
        assertTrue( asm.getErrors().get( 0 ).isPackageItem() );
        assertEquals( pkg.getUUID(),
                      asm.getErrors().get( 0 ).getUUID() );

        asm = new ContentPackageAssembler( pkg,
                                           true,
                                           "customSelector",
                                           null,
                                           null,
                                           false,
                                           null,
                                           null,
                                           false,
                                           "" );
        pk = asm.getBinaryPackage();
        assertEquals( 2,
                      pk.getRules().length );

    }

    private void assertContains(String sub,
                                String text) {
        if ( text.indexOf( sub ) == -1 ) {
            fail( "the text: '" + sub + "' was not found." );
        }
    }

    private void assertDoesNotContain(String sub,
                                      String text) {
        if ( text.indexOf( sub ) > -1 ) {
            fail( "the text: '" + sub + "' was found." );
        }

    }

    private void assertNotEmpty(String s) {
        if ( s == null ) fail( "should not be null" );
        if ( s.trim().equals( "" ) ) fail( "should not be empty string" );
    }

}
