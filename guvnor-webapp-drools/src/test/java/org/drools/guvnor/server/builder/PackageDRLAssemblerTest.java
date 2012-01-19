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

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class PackageDRLAssemblerTest extends GuvnorTestBase {

    @Test
    public void testSimplePackageWithDeclaredTypes() throws Exception {

        ModuleItem pkg = rulesRepository.createModule("testSimplePackageWithDeclaredTypes2",
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

        rulesRepository.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        String drl = asm.getCompiledSource();

        assertTrue(drl.indexOf("declare Album") > -1);
    }

    @Test
    public void testSimplePackageWithDeclaredTypesUsingDependency() throws Exception {

        ModuleItem pkg = rulesRepository.createModule("testSimplePackageWithDeclaredTypesUsingDependency",
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
        rulesRepository.save();

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        assertFalse(asm.getErrors().toString(),
                asm.hasErrors());

        asm = new PackageAssembler();
        asm.init(pkg, null);
        String drl = asm.getCompiledSource();

        assertTrue(drl.indexOf("genre2") == -1);
        assertTrue(drl.indexOf("genre3") > -1);

        pkg.updateDependency("model?version=2");
        pkg.checkin("Update dependency");

        PackageAssembler asm2 = new PackageAssembler();
        asm2.init(pkg, null);
        assertFalse(asm2.getErrors().toString(),
                asm2.hasErrors());

        asm2 = new PackageAssembler();
        asm2.init(pkg, null);
        String drl2 = asm2.getCompiledSource();

        assertTrue(drl2.indexOf("genre2") > -1);
        assertTrue(drl2.indexOf("genre3") == -1);

    }

    @Test
    public void testGetHistoryPackageSource() throws Exception {
        //Package version 1(Initial version)
        ModuleItem pkg = rulesRepository.createModule("testGetHistoryPackageSource",
                "");

        //Package version 2
        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer1",
                pkg);

        AssetItem func = pkg.addAsset("func",
                "");
        func.updateFormat(AssetFormats.FUNCTION);
        func.updateContent("function void foo() { System.out.println(version 1); }");
        func.checkin("version 1");

        AssetItem dsl = pkg.addAsset("myDSL",
                "");
        dsl.updateFormat(AssetFormats.DSL);
        dsl.updateContent("[then]call a func=foo();\n[when]foo=FooBarBaz1()");
        dsl.checkin("version 1");

        AssetItem rule = pkg.addAsset("rule1",
                "");
        rule.updateFormat(AssetFormats.DRL);
        rule.updateContent("rule 'foo' when Goo1() then end");
        rule.checkin("version 1");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
        rule2.updateContent("when \n foo \n then \n call a func");
        rule2.checkin("version 1");

        AssetItem rule3 = pkg.addAsset("model1",
                "");
        rule3.updateFormat(AssetFormats.DRL_MODEL);
        rule3.updateContent("declare Album1\n genre1: String \n end");
        rule3.checkin("version 1");

        pkg.checkin("version2");

        //Package version 3
        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer2",
                pkg);
        func.updateContent("function void foo() { System.out.println(version 2); }");
        func.checkin("version 2");
        dsl.updateContent("[then]call a func=foo();\n[when]foo=FooBarBaz2()");
        dsl.checkin("version 2");
        rule.updateContent("rule 'foo' when Goo2() then end");
        rule.checkin("version 2");
        rule2.updateContent("when \n foo \n then \n call a func");
        rule2.checkin("version 2");
        rule3.updateContent("declare Album2\n genre2: String \n end");
        rule3.checkin("version 2");
        //impl.buildPackage(pkg.getUUID(), true);
        pkg.checkin("version3");

        //Verify the latest version
        ModuleItem item = rulesRepository.loadModule("testGetHistoryPackageSource");
        PackageAssembler asm = new PackageAssembler();
        asm.init(item, null);
        String drl = asm.getCompiledSource();

        System.out.println(drl);

        assertEquals("version3",
                item.getCheckinComment());
        assertTrue(drl.indexOf("global com.billasurf.Person customer2") >= 0);
        assertTrue(drl.indexOf("System.out.println(version 2)") >= 0);
        assertTrue(drl.indexOf("FooBarBaz2()") >= 0);
        assertTrue(drl.indexOf("rule 'foo' when Goo2() then end") >= 0);
        assertTrue(drl.indexOf("foo") >= 0);
        assertTrue(drl.indexOf("declare Album2") >= 0);
        //assertEquals(12, item.getCompiledPackageBytes().length);

        //Verify version 2
        ModuleItem item2 = rulesRepository.loadModule("testGetHistoryPackageSource",
                2);
        PackageAssembler asm2 = new PackageAssembler();
        asm2.init(item2, null);
        String drl2 = asm2.getCompiledSource();

        System.out.println(drl2);

        assertEquals("version2",
                item2.getCheckinComment());
        assertTrue(drl2.indexOf("global com.billasurf.Person customer1") >= 0);
        assertTrue(drl2.indexOf("System.out.println(version 1)") >= 0);
        assertTrue(drl2.indexOf("FooBarBaz1()") >= 0);
        assertTrue(drl2.indexOf("rule 'foo' when Goo1() then end") >= 0);
        assertTrue(drl2.indexOf("foo") >= 0);
        assertTrue(drl2.indexOf("declare Album1") >= 0);
    }

    @Test
    public void testShowSource() throws Exception {

        //first, setup the package correctly:
        ModuleItem pkg = rulesRepository.createModule("testShowSource",
                "");

        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);
        rulesRepository.save();

        AssetItem func = pkg.addAsset("func",
                "");
        func.updateFormat(AssetFormats.FUNCTION);
        func.updateContent("function void foo() { System.out.println(42); }");
        func.checkin("");

        AssetItem dsl = pkg.addAsset("myDSL",
                "");
        dsl.updateFormat(AssetFormats.DSL);
        dsl.updateContent("[then]call a func=foo();\n[when]foo=FooBarBaz()");
        dsl.checkin("");

        AssetItem rule = pkg.addAsset("rule1",
                "");
        rule.updateFormat(AssetFormats.DRL);
        rule.updateContent("rule 'foo' when Goo() then end");
        rule.checkin("");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
        rule2.updateContent("when \n foo \n then \n call a func");
        rule2.checkin("");

        AssetItem rule3 = pkg.addAsset("model1",
                "");
        rule3.updateFormat(AssetFormats.DRL_MODEL);
        rule3.updateContent("garbage");
        rule3.updateDisabled(true);
        rule3.checkin("");

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        String drl = asm.getCompiledSource();

        assertNotNull(drl);

        assertContains("import com.billasurf.Board\n global com.billasurf.Person customer",
                drl);
        assertContains("package testShowSource",
                drl);
        assertContains("function void foo() { System.out.println(42); }",
                drl);
        assertContains("foo();",
                drl);
        assertContains("FooBarBaz()",
                drl);
        assertContains("rule 'foo' when Goo() then end",
                drl);

        assertEquals(-1,
                drl.indexOf("garbage"));

    }


    @Test
    public void testShowSourceUsingSpecifiedDependencies() throws Exception {
        //first, setup the package correctly:
        ModuleItem pkg = rulesRepository.createModule("testShowSourceUsingSpecifiedDependencies",
                "");

        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);
        rulesRepository.save();

        AssetItem func = pkg.addAsset("func",
                "");
        func.updateFormat(AssetFormats.FUNCTION);
        func.updateContent("function void foo() { System.out.println(version 1); }");
        func.checkin("version 1");
        func.updateContent("function void foo() { System.out.println(version 2); }");
        func.checkin("version 2");

        AssetItem dsl = pkg.addAsset("myDSL",
                "");
        dsl.updateFormat(AssetFormats.DSL);
        dsl.updateContent("[then]call a func=foo();\n[when]foo=FooBarBaz1()");
        dsl.checkin("version 1");
        dsl.updateContent("[then]call a func=foo();\n[when]foo=FooBarBaz2()");
        dsl.checkin("version 2");

        AssetItem rule = pkg.addAsset("rule1",
                "");
        rule.updateFormat(AssetFormats.DRL);
        rule.updateContent("rule 'foo' when Goo() then end");
        rule.checkin("version 1");
        rule.updateContent("rule 'foo' when Eoo() then end");
        rule.checkin("version 2");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
        rule2.updateContent("when \n foo \n then \n call a func");
        rule2.checkin("version 1");
        rule2.updateContent("when \n foo \n then \n call a func");
        rule2.checkin("version 2");

        AssetItem rule3 = pkg.addAsset("model1",
                "");
        rule3.updateFormat(AssetFormats.DRL_MODEL);
        rule3.updateContent("garbage");
        rule3.updateDisabled(true);
        rule3.checkin("version 1");
        rule3.updateContent("declare Album\n genre1: String \n end");
        rule3.checkin("version 2");

        rulesRepository.save();

        //NOTE: dont use version=0. Version 0 is the root node.
        pkg.updateDependency("func?version=1");
        pkg.updateDependency("myDSL?version=1");
        pkg.updateDependency("rule1?version=1");
        pkg.updateDependency("rule2?version=1");
        pkg.updateDependency("model1?version=1");
        pkg.checkin("Update dependency");

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        String drl = asm.getCompiledSource();

        assertNotNull(drl);

        assertContains("import com.billasurf.Board\n global com.billasurf.Person customer",
                drl);
        assertContains("package testShowSource",
                drl);
        assertContains("function void foo() { System.out.println(version 1); }",
                drl);
        assertContains("foo();",
                drl);
        assertContains("FooBarBaz1()",
                drl);
        assertContains("rule 'foo' when Goo() then end",
                drl);

        assertEquals(-1,
                drl.indexOf("garbage"));
        assertEquals(-1,
                drl.indexOf("Album"));
    }

    @Test
    public void testShowSourceForHistoricalPackage() throws Exception {
        ModuleItem pkg = rulesRepository.createModule("testShowSourceForHistoricalPackage",
                "");

        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\n global com.billasurf.Person customer",
                pkg);
        rulesRepository.save();

        AssetItem func = pkg.addAsset("func",
                "");
        func.updateFormat(AssetFormats.FUNCTION);
        func.updateContent("function void foo() { System.out.println(version 1); }");
        func.checkin("version 1");
        func.updateContent("function void foo() { System.out.println(version 2); }");
        func.checkin("version 2");

        AssetItem dsl = pkg.addAsset("myDSL",
                "");
        dsl.updateFormat(AssetFormats.DSL);
        dsl.updateContent("[then]call a func=foo();\n[when]foo=FooBarBaz1()");
        dsl.checkin("version 1");
        dsl.updateContent("[then]call a func=foo();\n[when]foo=FooBarBaz2()");
        dsl.checkin("version 2");

        AssetItem rule = pkg.addAsset("rule1",
                "");
        rule.updateFormat(AssetFormats.DRL);
        rule.updateContent("rule 'foo' when Goo() then end");
        rule.checkin("version 1");
        rule.updateContent("rule 'foo' when Eoo() then end");
        rule.checkin("version 2");

        AssetItem rule2 = pkg.addAsset("rule2",
                "");
        rule2.updateFormat(AssetFormats.DSL_TEMPLATE_RULE);
        rule2.updateContent("when \n foo \n then \n call a func");
        rule2.checkin("version 1");
        rule2.updateContent("when \n foo \n then \n call a func");
        rule2.checkin("version 2");

        AssetItem rule3 = pkg.addAsset("model1",
                "");
        rule3.updateFormat(AssetFormats.DRL_MODEL);
        rule3.updateContent("garbage");
        rule3.updateDisabled(true);
        rule3.checkin("version 1");
        rule3.updateContent("declare Album\n genre1: String \n end");
        rule3.checkin("version 2");

        rulesRepository.save();
        pkg.checkin("Version 2");
        pkg.checkout();
        pkg.checkin("Version 3");

        ModuleItem historicalPackage = rulesRepository.loadModule("testShowSourceForHistoricalPackage",
                2);

        PackageAssembler asm = new PackageAssembler();
        asm.init(historicalPackage, null);
        String drl = asm.getCompiledSource();

        assertNotNull(drl);
        System.out.println(drl);

        assertContains("import com.billasurf.Board\n global com.billasurf.Person customer",
                drl);
        assertContains("package testShowSource",
                drl);
        assertContains("function void foo() { System.out.println(version 2); }",
                drl);
        assertContains("FooBarBaz2()",
                drl);
        assertContains("rule 'foo' when Eoo() then end",
                drl);

        assertEquals(-1,
                drl.indexOf("garbage"));
        assertEquals(-1,
                drl.indexOf("Album"));
    }

    @Test
    public void testBuildPackageWithEmptyHeader() throws Exception {
        //first, setup the package correctly:
        ModuleItem pkg = rulesRepository.createModule("testBuildPackageWithEmptyHeader",
                "");

        DroolsHeader.updateDroolsHeader("\n",
                pkg);
        rulesRepository.save();

        PackageAssembler asm = null;
        try {
            asm = new PackageAssembler();
            asm.init(pkg, null);
        } catch (NullPointerException e) {
            // Possible cause: Header has only white spaces "\n\t".
            fail(e.toString());
        }
        String drl = asm.getCompiledSource();

        assertNotNull(drl);
        assertEquals("package testBuildPackageWithEmptyHeader",
                drl.trim());

    }

    @Test
    public void testSkipDisabledAssets() throws Exception {

        //first, setup the package correctly:
        ModuleItem pkg = rulesRepository.createModule("testSkipDisabledAssets",
                "");
        rulesRepository.save();

        AssetItem assertRule1 = pkg.addAsset("rule1",
                "");
        assertRule1.updateFormat(AssetFormats.DRL);
        assertRule1.updateContent("rule 'foo1' when then end");
        assertRule1.checkin("");
        assertRule1.updateDisabled(false);

        AssetItem assertRule2 = pkg.addAsset("rule2",
                "");
        assertRule2.updateFormat(AssetFormats.DRL);
        assertRule2.updateContent("rule 'foo2' when then end");
        assertRule2.checkin("");
        assertRule2.updateDisabled(true);

        AssetItem assertRule3 = pkg.addAsset("rule3",
                "");
        assertRule3.updateFormat(AssetFormats.DRL);
        assertRule3.updateContent("rule 'foo3' when then end");
        assertRule3.checkin("");

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        assertFalse(asm.hasErrors());

        String drl = asm.getCompiledSource();

        assertNotNull(drl);

        assertContains("rule 'foo1' when then end",
                drl);
        assertDoesNotContain("rule 'foo2' when then end",
                drl);
        assertContains("rule 'foo3' when then end",
                drl);

    }

    @Test
    public void testSkipDisabledImports() throws Exception {

        //first, setup the package correctly:
        ModuleItem pkg = rulesRepository.createModule("testXLSDecisionTableIgnoreImports",
                "");

        rulesRepository.save();

        InputStream xls = this.getClass().getResourceAsStream("Sample.xls");
        assertNotNull(xls);

        AssetItem asset = pkg.addAsset("MyDT",
                "");
        asset.updateFormat(AssetFormats.DECISION_SPREADSHEET_XLS);
        asset.updateBinaryContentAttachment(xls);
        asset.checkin("");

        PackageAssembler asm = new PackageAssembler();
        asm.init(pkg, null);
        String drl = asm.getCompiledSource();
        System.err.println(drl);

        assertTrue(drl.indexOf("package ",
                2) == -1); //skip a few, make sure we only have one instance of "package "
    }

    private void assertContains(String sub,
                                String text) {
        if (text.indexOf(sub) == -1) {
            fail("the text: '" + sub + "' was not found.");
        }
    }

    private void assertDoesNotContain(String sub,
                                      String text) {
        if (text.indexOf(sub) > -1) {
            fail("the text: '" + sub + "' was found.");
        }

    }
}
