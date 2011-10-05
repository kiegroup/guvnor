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

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.selector.AssetSelector;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.*;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.MVEL;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * This will unit test package assembly into a binary.
 */
public class PackageAssemblerTest extends GuvnorTestBase {

    /**
     * Test package configuration errors, including header, functions, DSL
     * files.
     */
    @Test
    public void testPackageConfigWithErrors() throws Exception {
        //test the config, no rule assets yet
        PackageItem pkg = rulesRepository.createPackage("testBuilderPackageConfig",
                "x");
        DroolsHeader.updateDroolsHeader("import java.util.List",
                pkg);
        AssetItem func = pkg.addAsset("func1",
                "a function");
        func.updateFormat(AssetFormats.FUNCTION);
        func.updateContent("function void doSomething() { \n System.err.println(List.class.toString()); }");
        func.checkin("yeah");

        func = pkg.addAsset("func2",
                "q");
        func.updateFormat(AssetFormats.FUNCTION);
        func.updateContent("function void foo() { \nSystem.err.println(42); \n}");
        func.checkin("");

        AssetItem ass = pkg.addAsset("dsl",
                "m");
        ass.updateFormat(AssetFormats.DSL);
        ass.updateContent("[when]Foo bar=String()");
        ass.checkin("");
        rulesRepository.save();

        //now lets light it up
        PackageAssembler assembler = new PackageAssembler(pkg);
        assembler.compile();
        assertFalse(assembler.hasErrors());
        Package bin = assembler.getBinaryPackage();
        assertNotNull(bin);
        assertEquals("testBuilderPackageConfig",
                bin.getName());
        assertEquals(2,
                bin.getFunctions().size());

        assertTrue(bin.isValid());
        assertEquals(1,
                assembler.getBuilder().getDSLMappingFiles().size());

        DroolsHeader.updateDroolsHeader("koo koo ca choo",
                pkg);
        assembler = new PackageAssembler(pkg);
        assembler.compile();
        assertTrue(assembler.hasErrors());
        assertTrue(assembler.isPackageConfigurationInError());

        DroolsHeader.updateDroolsHeader("import java.util.Date",
                pkg);
        assembler = new PackageAssembler(pkg);
        assembler.compile();
        assertTrue(assembler.hasErrors());
        assertTrue(assembler.getErrors().get(0).isAssetItem());

        assertEquals("func1",
                assembler.getErrors().get(0).getName());
        try {
            assembler.getBinaryPackage();
            fail("should not work as is in error.");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }

        //fix it up
        DroolsHeader.updateDroolsHeader("import java.util.List",
                pkg);
        assembler = new PackageAssembler(pkg);
        assembler.compile();
        assertFalse(assembler.hasErrors());

        //now break a DSL and check the error
        ass.updateContent("rubbish");
        ass.checkin("");
        assembler = new PackageAssembler(pkg);
        assembler.compile();

        //now fix it up
        ass.updateContent("[when]foo=String()");
        ass.checkin("");
        assembler = new PackageAssembler(pkg);
        assembler.compile();
        assertFalse(assembler.hasErrors());

        //break a func, and check for error
        func.updateContent("goo");
        func.checkin("");
        assembler = new PackageAssembler(pkg);
        assembler.compile();
        assertTrue(assembler.hasErrors());
        assertFalse(assembler.isPackageConfigurationInError());
        assertTrue(assembler.getErrors().get(0).getName().equals(func.getName()));
        assertNotEmpty(assembler.getErrors().get(0).getErrorReport());
    }

    @Test
    public void testLoadConfProperties() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testLoadConfProperties",
                "");
        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.MODEL);

        model.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/billasurf.jar"));
        model.checkin("");

        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);

        AssetItem rule1 = pkg.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n when Board() \n then customer.setAge(42); \n end");
        rule1.checkin("");

        AssetItem props1 = pkg.addAsset("conf1",
                "");
        props1.updateFormat("properties");
        props1.updateContent("drools.accumulate.function.groupCount = org.drools.base.accumulators.MaxAccumulateFunction");
        props1.checkin("");

        AssetItem props2 = pkg.addAsset("conf2",
                "");
        props2.updateFormat("conf");
        props2.updateBinaryContentAttachment(new ByteArrayInputStream("drools.accumulate.function.groupFun = org.drools.base.accumulators.MinAccumulateFunction".getBytes()));
        props2.checkin("");

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertEquals("org.drools.base.accumulators.MaxAccumulateFunction",
                asm.getBuilder().getPackageBuilderConfiguration().getAccumulateFunction("groupCount").getClass().getName());
        assertEquals("org.drools.base.accumulators.MinAccumulateFunction",
                asm.getBuilder().getPackageBuilderConfiguration().getAccumulateFunction("groupFun").getClass().getName());

    }

    @Test
    public void testPackageWithRuleflow() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem packageItem = repo.createPackage("testPackageWithRuleFlow",
                "");
        AssetItem model = packageItem.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.MODEL);

        model.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/billasurf.jar"));
        model.checkin("");

        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                packageItem);

        AssetItem rule1 = packageItem.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n when Board() \n then customer.setAge(42); \n end");
        rule1.checkin("");

        AssetItem ruleFlow = packageItem.addAsset("ruleFlow",
                "");
        ruleFlow.updateFormat(AssetFormats.RULE_FLOW_RF);

        ruleFlow.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/ruleflow.rfm"));
        ruleFlow.checkin("");

        PackageAssembler asm = new PackageAssembler(packageItem);
        asm.compile();
        assertFalse(asm.hasErrors());
        Map<String, org.drools.definition.process.Process> flows = asm.getBinaryPackage().getRuleFlows();
        assertNotNull(flows);

        assertEquals(1,
                flows.size());
        Object flow = flows.values().iterator().next();
        assertNotNull(flow);
        assertTrue(flow instanceof RuleFlowProcess);

        //now check we can do some MVEL stuff from the classloader...
        ClassLoaderBuilder classLoaderBuilder = new ClassLoaderBuilder(packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(AssetFormats.MODEL));
        PackageBuilder builder = new BRMSPackageBuilder(new Properties(), classLoaderBuilder.buildClassLoader());
        ClassLoader newCL = builder.getPackageBuilderConfiguration().getClassLoader();
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();

        //set the CL for the current thread so MVEL can find it
        Thread.currentThread().setContextClassLoader(newCL);

        Object o = MVEL.eval("new com.billasurf.Board()");
        assertEquals("com.billasurf.Board",
                o.getClass().getName());
        System.err.println(o.toString());

        Thread.currentThread().setContextClassLoader(oldCL);

        builder.addPackageFromDrl(new StringReader("package foo\n import com.billasurf.Board"));
        Object o2 = builder.getPackageRegistry("foo").getTypeResolver().resolveType("Board");
        assertNotNull(o2);
        assertEquals("com.billasurf.Board",
                ((Class<?>) o2).getName());
    }

    @Test
    public void testWithNoDeclaredTypes() throws Exception {

        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testSimplePackageWithDeclaredTypes1",
                "");
        AssetItem rule1 = pkg.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL_MODEL);
        rule1.checkin("");

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.getErrors().toString(),
                asm.hasErrors());

    }

    @Test
    public void testSimplePackageWithDeclaredTypes() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testSimplePackageWithDeclaredTypes2",
                "");

        DroolsHeader.updateDroolsHeader("import java.util.HashMap",
                pkg);

        AssetItem rule1 = pkg.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n dialect 'mvel' \n when Album() \n then \nAlbum a = new Album(); \n end");
        rule1.checkin("");

        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.DRL_MODEL);

        model.updateContent("declare Album\n genre: String \n end");
        model.checkin("");

        repo.save();

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.getErrors().toString(),
                asm.hasErrors());

        assertNotNull(asm.getBinaryPackage());
        Package bin = asm.getBinaryPackage();
        assertEquals(pkg.getName(),
                bin.getName());
        assertTrue(bin.isValid());
    }

    @Test
    public void testSimplePackageAttributes() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testSimplePackageAttributes",
                "");

        DroolsHeader.updateDroolsHeader("import java.util.HashMap\nno-loop true\nagenda-group \"albums\"\ndialect \"java\"\n",
                pkg);

        AssetItem rule1 = pkg.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n dialect \"mvel\" \n when Album() \n then \nAlbum a = new Album(); \n end");
        rule1.checkin("");

        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.DRL_MODEL);

        model.updateContent("declare Album\n genre: String \n end");
        model.checkin("");

        repo.save();

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.hasErrors());

        assertNotNull(asm.getBinaryPackage());
        Package bin = asm.getBinaryPackage();
        assertEquals(pkg.getName(),
                bin.getName());
        assertTrue(bin.isValid());

        assertEquals(1,
                bin.getRules().length);
        assertEquals("albums",
                bin.getRule("rule1").getAgendaGroup());
        assertEquals(true,
                bin.getRule("rule1").isNoLoop());
        assertEquals("mvel",
                bin.getRule("rule1").getDialect());

    }

    @Test
    public void testSimplePackageWithDeclaredTypesUsingDependency() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testSimplePackageWithDeclaredTypesUsingDependency",
                "");

        DroolsHeader.updateDroolsHeader("import java.util.HashMap",
                pkg);

        AssetItem rule1 = pkg.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n dialect 'mvel' \n when Album() \n then \nAlbum a = new Album(); \n end");
        rule1.checkin("");

        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.DRL_MODEL);

        model.updateContent("declare Album\n genre1: String \n end");
        model.checkin("version 0");
        model.updateContent("declare Album\n genre2: String \n end");
        model.checkin("version 1");
        model.updateContent("declare Album\n genre3: String \n end");
        model.checkin("version 2");
        repo.save();

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.getErrors().toString(),
                asm.hasErrors());

        assertNotNull(asm.getBinaryPackage());
        Package bin = asm.getBinaryPackage();
        assertEquals(pkg.getName(),
                bin.getName());
        assertTrue(bin.isValid());

        pkg.updateDependency("model?version=2");
        pkg.checkin("Update dependency");

        PackageAssembler asm2 = new PackageAssembler(pkg);
        asm2.compile();
        assertFalse(asm2.getErrors().toString(),
                asm2.hasErrors());

        assertNotNull(asm2.getBinaryPackage());
        Package bin2 = asm2.getBinaryPackage();
        assertEquals(pkg.getName(),
                bin2.getName());
        assertTrue(bin2.isValid());
    }

    @Test
    public void testSimplePackageBuildNoErrors() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testSimplePackageBuildNoErrors",
                "");
        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.MODEL);

        model.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/billasurf.jar"));
        model.checkin("");

        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);

        AssetItem rule1 = pkg.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n when Board() \n then customer.setAge(42); \n end");
        rule1.checkin("");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DRL);
        rule2.updateContent("agenda-group 'q' \n when \n Board() \n then \n System.err.println(42);");
        rule2.checkin("");

        AssetItem rule3 = pkg.addAsset("A file",
                "");
        rule3.updateFormat(AssetFormats.DRL);
        rule3.updateContent("package testSimplePackageBuildNoErrors\n rule 'rule3' \n when \n then \n customer.setAge(43); \n end \n" + "rule 'rule4' \n when \n then \n System.err.println(44); \n end");
        rule3.checkin("");

        repo.save();

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.hasErrors());
        assertNotNull(asm.getBinaryPackage());
        Package bin = asm.getBinaryPackage();
        assertEquals(pkg.getName(),
                bin.getName());
        assertTrue(bin.isValid());

        assertEquals(4,
                bin.getRules().length);

        //now create a snapshot
        repo.createPackageSnapshot(pkg.getName(),
                "SNAP_1");

        //and screw up the the non snapshot one
        DroolsHeader.updateDroolsHeader("koo koo ca choo",
                pkg);
        asm = new PackageAssembler(pkg);
        asm.compile();
        assertTrue(asm.hasErrors());

        //check the snapshot is kosher
        pkg = repo.loadPackageSnapshot(pkg.getName(),
                "SNAP_1");
        asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.hasErrors());

    }

    @Test
    public void testIgnoreArchivedItems() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testIgnoreArchivedItems",
                "");
        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.MODEL);

        model.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/billasurf.jar"));
        model.checkin("");

        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);

        AssetItem rule1 = pkg.addAsset("rule_1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' \n when Board() \n then customer.setAge(42); \n end");
        rule1.checkin("");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DRL);
        rule2.updateContent("agenda-group 'q' \n when \n Boardx() \n then \n System.err.println(42);");
        rule2.checkin("");

        repo.save();

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertTrue(asm.hasErrors());

        rule2.archiveItem(true);
        rule2.checkin("");

        assertTrue(rule2.isArchived());
        asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.hasErrors());

    }

    /**
     * This this case we will test errors that occur in rule assets, not in
     * functions or package header.
     */
    @Test
    public void testErrorsInRuleAsset() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage("testErrorsInRuleAsset",
                "");
        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.MODEL);
        model.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/billasurf.jar"));
        model.checkin("");
        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);
        repo.save();

        AssetItem goodRule = pkg.addAsset("goodRule",
                "");
        goodRule.updateFormat(AssetFormats.DRL);
        goodRule.updateContent("rule 'yeah' \n when \n Board() \n then \n System.out.println(42); end");
        goodRule.checkin("");

        AssetItem badRule = pkg.addAsset("badRule",
                "xxx");
        badRule.updateFormat(AssetFormats.DRL);
        badRule.updateContent("if something then another");
        badRule.checkin("");

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertTrue(asm.hasErrors());
        assertFalse(asm.isPackageConfigurationInError());

        for (ContentAssemblyError err : asm.getErrors()) {
            assertTrue(err.getName().equals(badRule.getName()));
            assertNotEmpty(err.getErrorReport());
        }

    }

    @Test
    @Ignore("Temporally ignored -Rikkola-")
    public void testEventingExample() throws Exception {
        RulesRepository repo = rulesRepository;

        PackageItem pkg = repo.createPackage("testEventingExample",
                "");
        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.MODEL);

        model.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/eventing-example.jar"));
        model.checkin("");

        DroolsHeader.updateDroolsHeader("import org.drools.examples.eventing.EventRequest\n",
                pkg);
        AssetItem asset = pkg.addAsset("whee",
                "");
        asset.updateFormat(AssetFormats.DRL);
        asset.updateContent("rule 'zaa'\n  when \n  request: EventRequest( status == EventRequest.Status.ACTIVE )\n   then \n request.setStatus(EventRequest.Status.ACTIVE); \n  end");
        asset.checkin("");

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        if (asm.hasErrors()) {
            for (ContentAssemblyError err : asm.getErrors()) {
                System.err.println(err.getErrorReport());
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
        PackageItem pkg = repo.createPackage("testRuleAndDSLAndFunction",
                "");
        AssetItem model = pkg.addAsset("model",
                "qed");
        model.updateFormat(AssetFormats.MODEL);
        model.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/billasurf.jar"));
        model.checkin("");
        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);
        repo.save();

        AssetItem func = pkg.addAsset("func",
                "");
        func.updateFormat(AssetFormats.FUNCTION);
        func.updateContent("function void foo() { System.out.println(42); }");
        func.checkin("");

        AssetItem dsl = pkg.addAsset("myDSL",
                "");
        dsl.updateFormat(AssetFormats.DSL);
        dsl.updateContent("[then]call a func=foo();");
        dsl.checkin("");

        AssetItem dsl2 = pkg.addAsset("myDSL2",
                "");
        dsl2.updateFormat(AssetFormats.DSL);
        dsl2.updateContent("[when]There is a board=Board()");
        dsl2.checkin("");

        AssetItem rule = pkg.addAsset("myRule",
                "");
        rule.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
        rule.updateContent("when \n There is a board \n then \n call a func");
        rule.checkin("");

        AssetItem rule2 = pkg.addAsset("myRule2",
                "");
        rule2.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
        rule2.updateContent("package testRuleAndDSLAndFunction \n rule 'myRule2222' \n when \n There is a board \n then \n call a func \nend");
        rule2.checkin("");

        AssetItem rule3 = pkg.addAsset("myRule3",
                "");
        rule3.updateFormat(AssetFormats.DRL);
        rule3.updateContent("package testRuleAndDSLAndFunction\n rule 'rule3' \n when \n Board() \n then \n System.err.println(42); end");
        rule3.checkin("");

        repo.save();

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.hasErrors());
        Package bin = asm.getBinaryPackage();
        assertNotNull(bin);
        assertEquals(3,
                bin.getRules().length);
        assertEquals(1,
                bin.getFunctions().size());

    }


    @Test
    public void testSkipDisabledPackageStuff() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage("testSkipDisabledPackageStuff",
                "");
        repo.save();

        AssetItem assertRule1 = pkg.addAsset("model1",
                "");
        assertRule1.updateFormat(AssetFormats.DRL_MODEL);
        assertRule1.updateContent("garbage");
        assertRule1.updateDisabled(true);
        assertRule1.checkin("");

        assertRule1 = pkg.addAsset("function1",
                "");
        assertRule1.updateFormat(AssetFormats.FUNCTION);
        assertRule1.updateContent("garbage");
        assertRule1.updateDisabled(true);
        assertRule1.checkin("");

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.hasErrors());

    }


    @Test
    public void testXLSDecisionTable() throws Exception {
        RulesRepository repo = rulesRepository;

        //first, setup the package correctly:
        PackageItem pkg = repo.createPackage("testXLSDecisionTable",
                "");

        DroolsHeader.updateDroolsHeader("import org.acme.insurance.Policy\n import org.acme.insurance.Driver",
                pkg);
        repo.save();

        InputStream xls = this.getClass().getResourceAsStream("/SampleDecisionTable.xls");
        assertNotNull(xls);

        AssetItem asset = pkg.addAsset("MyDT",
                "");
        asset.updateFormat(AssetFormats.DECISION_SPREADSHEET_XLS);
        asset.updateBinaryContentAttachment(xls);
        asset.checkin("");

        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        if (asm.hasErrors()) {
            System.err.println(asm.getErrors().get(0).getErrorReport());
        }
        assertFalse(asm.hasErrors());

        Package bin = asm.getBinaryPackage();

        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage(bin);

        WorkingMemory wm = rb.newStatefulSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        wm.insert(driver);
        wm.insert(policy);

        wm.fireAllRules();

        assertEquals(120,
                policy.getBasePrice());

        asset.updateBinaryContentAttachment(this.getClass().getResourceAsStream("/SampleDecisionTable_WithError.xls"));
        asset.checkin("");
        asm = new PackageAssembler(pkg);
        asm.compile();
        assertTrue(asm.hasErrors());
        assertEquals(asset.getName(),
                asm.getErrors().get(0).getName());
        asm = new PackageAssembler(pkg);
        assertFalse(asm.hasErrors());
    }

    @Test
    public void testBRXMLWithDSLMixedIn() throws Exception {
        RulesRepository repo = rulesRepository;

        //create our package
        PackageItem pkg = repo.createPackage("testBRLWithDSLMixedIn",
                "");
        DroolsHeader.updateDroolsHeader("import org.drools.Person",
                pkg);
        AssetItem rule1 = pkg.addAsset("rule2",
                "");
        rule1.updateFormat(AssetFormats.BUSINESS_RULE);

        AssetItem dsl = pkg.addAsset("MyDSL",
                "");
        dsl.updateFormat(AssetFormats.DSL);
        dsl.updateContent("[when]This is a sentence=Person()\n[then]say {hello}=System.err.println({hello});");
        dsl.checkin("");

        RuleModel model = new RuleModel();
        model.name = "rule2";
        FactPattern pattern = new FactPattern("Person");
        pattern.setBoundName("p");
        ActionSetField action = new ActionSetField("p");
        ActionFieldValue value = new ActionFieldValue("age",
                "42",
                SuggestionCompletionEngine.TYPE_NUMERIC);
        action.addFieldValue(value);

        model.addLhsItem(pattern);
        model.addRhsItem(action);

        DSLSentence dslCondition = new DSLSentence();
        dslCondition.setDefinition( "This is a sentence" );

        model.addLhsItem(dslCondition);

        DSLSentence dslAction = new DSLSentence();
        dslAction.setDefinition( "say {42}" );

        model.addRhsItem(dslAction);

        rule1.updateContent(BRXMLPersistence.getInstance().marshal(model));
        rule1.checkin("");
        repo.save();

        //now add a rule with no DSL
        model = new RuleModel();
        model.name = "ruleNODSL";
        pattern = new FactPattern("Person");
        pattern.setBoundName("p");
        action = new ActionSetField("p");
        value = new ActionFieldValue("age",
                "42",
                SuggestionCompletionEngine.TYPE_NUMERIC);
        action.addFieldValue(value);

        model.addLhsItem(pattern);
        model.addRhsItem(action);

        AssetItem ruleNODSL = pkg.addAsset("ruleNoDSL",
                "");
        ruleNODSL.updateFormat(AssetFormats.BUSINESS_RULE);

        ruleNODSL.updateContent(BRXMLPersistence.getInstance().marshal(model));
        ruleNODSL.checkin("");

        pkg = repo.loadPackage("testBRLWithDSLMixedIn");
        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        assertFalse(asm.hasErrors());
        Package bpkg = asm.getBinaryPackage();
        assertEquals(2,
                bpkg.getRules().length);

    }

    @Test
    public void testCustomSelector() throws Exception {
        RulesRepository repo = rulesRepository;

        //create our package
        PackageItem pkg = repo.createPackage("testCustomSelector",
                "");
        DroolsHeader.updateDroolsHeader("import org.drools.Person",
                pkg);
        AssetItem rule1 = pkg.addAsset("rule1",
                "");
        rule1.updateFormat(AssetFormats.DRL);

        rule1.updateContent("when \n Person() \n then \n System.out.println(\"yeah\");\n");
        rule1.checkin("");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DRL);
        rule2.updateContent("when \n Person() \n then \n System.out.println(\"yeah\");\n");
        rule2.checkin("");

        SelectorManager sm = SelectorManager.getInstance();
        sm.selectors.put("testSelect",
                new AssetSelector() {
                    public boolean isAssetAllowed(AssetItem asset) {
                        return asset.getName().equals("rule2");
                    }
                });

        PackageAssemblerConfiguration configuration = new PackageAssemblerConfiguration();
        configuration.setBuildMode("customSelector");
        configuration.setCustomSelectorConfigName("testSelect");

        PackageAssembler asm = new PackageAssembler(pkg,
                configuration);
        asm.compile();

        Package pk = asm.getBinaryPackage();
        assertEquals(1,
                pk.getRules().length);
        assertEquals("rule2",
                pk.getRules()[0].getName());

        asm = new PackageAssembler(pkg);
        asm.compile();
        pk = asm.getBinaryPackage();
        assertEquals(2,
                pk.getRules().length);

        configuration = new PackageAssemblerConfiguration();
        configuration.setBuildMode("customSelector");
        configuration.setCustomSelectorConfigName("nothing valid");
        asm = new PackageAssembler(pkg,
                configuration);
        asm.compile();
        assertTrue(asm.hasErrors());
        assertEquals(1,
                asm.getErrors().size());
        assertEquals(pkg.getName(),
                asm.getErrors().get(0).getName());
        assertTrue(asm.getErrors().get(0).isPackageItem());
        assertEquals(pkg.getUUID(),
                asm.getErrors().get(0).getUUID());


        configuration = new PackageAssemblerConfiguration();
        configuration.setBuildMode("customSelector");
        configuration.setCustomSelectorConfigName("");
        asm = new PackageAssembler(pkg,
                configuration);
        asm.compile();
        pk = asm.getBinaryPackage();
        assertEquals(2,
                pk.getRules().length);
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
        PackageItem pkg = repo.createPackage("testBuiltInSelector",
                "");
        DroolsHeader.updateDroolsHeader("import org.drools.Person",
                pkg);
        AssetItem rule1 = pkg.addAsset("rule1",
                "");
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateCategoryList(new String[]{"testBuiltInSelectorCategory1/testBuiltInSelectorCategory1Child"});

        rule1.updateContent("when \n Person() \n then \n System.out.println(\"yeah\");\n");
        rule1.checkin("");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DRL);
        rule2.updateCategoryList(new String[]{"testBuiltInSelectorCategory2/testBuiltInSelectorCategory2Child"});
        rule2.updateContent("when \n Person() \n then \n System.out.println(\"yeah\");\n");
        rule2.checkin("");

        SelectorManager sm = SelectorManager.getInstance();
        sm.selectors.put("testSelect",
                new AssetSelector() {
                    public boolean isAssetAllowed(AssetItem asset) {
                        return asset.getName().equals("rule2");
                    }
                });

        PackageAssemblerConfiguration packageAssemblerConfiguration = new PackageAssemblerConfiguration();
        packageAssemblerConfiguration.setBuildMode("BuiltInSelector");
        packageAssemblerConfiguration.setEnableStatusSelector(false);
        packageAssemblerConfiguration.setCategoryOperator( "=" );
        packageAssemblerConfiguration.setCategoryValue( "testBuiltInSelectorCategory1/testBuiltInSelectorCategory1Child" );
        packageAssemblerConfiguration.setEnableCategorySelector(true);

        //without selector
        PackageAssembler asm = new PackageAssembler(pkg);
        asm.compile();
        Package pk = asm.getBinaryPackage();
        assertEquals(2, pk.getRules().length);
        
        //with built-in selector
        asm = new PackageAssembler(pkg, packageAssemblerConfiguration);
        asm.compile();
        pk = asm.getBinaryPackage();
        assertEquals(1, pk.getRules().length);
        assertEquals("rule1", pk.getRules()[0].getName());

        packageAssemblerConfiguration = new PackageAssemblerConfiguration();
        packageAssemblerConfiguration.setBuildMode("BuiltInSelector");
        packageAssemblerConfiguration.setEnableStatusSelector(false);
        packageAssemblerConfiguration.setCategoryOperator( "!=" );
        packageAssemblerConfiguration.setCategoryValue( "testBuiltInSelectorCategory1/testBuiltInSelectorCategory1Child" );
        packageAssemblerConfiguration.setEnableCategorySelector(true);

        //with built-in selector
        asm = new PackageAssembler(pkg, packageAssemblerConfiguration);
        asm.compile();
        pk = asm.getBinaryPackage();
        assertEquals(1, pk.getRules().length);
        assertEquals("rule2", pk.getRules()[0].getName());
    }
    
    private void assertNotEmpty(String s) {
        if (s == null) fail("should not be null");
        if (s.trim().equals("")) fail("should not be empty string");
    }
}
