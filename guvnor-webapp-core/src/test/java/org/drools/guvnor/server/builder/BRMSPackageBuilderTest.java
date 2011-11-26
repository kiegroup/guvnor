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


import org.drools.builder.conf.DefaultPackageNameOption;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarInputStream;

import static org.junit.Assert.*;

public class BRMSPackageBuilderTest {

    @Before
    public void setUp() {
        System.getProperties().remove("drools.dialect.java.compiler");
    }

    @After
    public void tearDown() {
        System.getProperties().remove("drools.dialect.java.compiler");
    }

    // @FIXME rule "abc" is null and the Packge has no namespace
    @Test
    public void testPartialPackage() throws Exception {

        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/billasurf.jar"));
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add(jis);

        Properties ps = new Properties();
        ps.setProperty(DefaultPackageNameOption.PROPERTY_NAME, "foo.bar");

        BRMSPackageBuilder builder = new BRMSPackageBuilder(ps, new ClassLoaderBuilder(jarInputStreams).buildClassLoader());

        //PackageDescr pc = new PackageDescr("foo.bar");
        //builder.addPackage( pc );

        String header = "import com.billasurf.Person\n import com.billasurf.Board";
        builder.addPackageFromDrl(new StringReader(header));
        assertFalse(builder.hasErrors());

        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration("java");
        assertEquals(JavaDialectConfiguration.ECLIPSE, javaConf.getCompiler());

        String ruleAtom = "rule foo \n when \n Person() \n then \n System.out.println(42); end";
        builder.addPackageFromDrl(new StringReader(ruleAtom));
        if (builder.hasErrors()) {
            System.err.println(builder.getErrors().getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());

        ruleAtom = "rule foo2 \n when \n Person() \n then \n System.out.println(42); end";
        builder.addPackageFromDrl(new StringReader(ruleAtom));
        if (builder.hasErrors()) {
            System.err.println(builder.getErrors().getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());

        assertEquals("foo.bar", builder.getPackage().getName());


        String functionAtom = "function int fooBar(String x) { return 42; }";
        builder.addPackageFromDrl(new StringReader(functionAtom));
        if (builder.hasErrors()) {
            System.err.println(builder.getErrors().getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());

        Package p = builder.getPackage();
        assertEquals(2, p.getRules().length);
        assertEquals(1, p.getFunctions().size());
        assertNotNull(p.getRule("foo2"));

        functionAtom = "xxx";
        builder.addPackageFromDrl(new StringReader(functionAtom));
        assertTrue(builder.hasErrors());
        builder.clearErrors();
        assertFalse(builder.hasErrors());
    }

    @Test
    public void testGeneratedBeans() throws Exception {

        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/billasurf.jar"));
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add(jis);
        BRMSPackageBuilder builder = new BRMSPackageBuilder(new Properties(), new ClassLoaderBuilder(jarInputStreams).buildClassLoader());
        builder.getPackageBuilderConfiguration().setDefaultPackageName( "foo.bar" );

        PackageDescr pc = new PackageDescr("foo.bar");
        builder.addPackage(pc);

        String header = "import com.billasurf.Person\n import com.billasurf.Board\n declare GenBean \n name: String \n end";
        builder.addPackageFromDrl(new StringReader(header));
        assertFalse(builder.hasErrors());

        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration("java");
        assertEquals(JavaDialectConfiguration.ECLIPSE, javaConf.getCompiler());

        String ruleAtom = "rule foo \n when \n Person() \n GenBean(name=='mike')\n then \n System.out.println(42); end";
        builder.addPackageFromDrl(new StringReader(ruleAtom));
        if (builder.hasErrors()) {
            System.err.println(builder.getErrors().getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());

        ruleAtom = "rule foo2 \n when \n Person() \n then \n System.out.println(42); end";
        builder.addPackageFromDrl(new StringReader(ruleAtom));
        if (builder.hasErrors()) {
            System.err.println(builder.getErrors().getErrors()[0].getMessage());
        }
        assertFalse(builder.hasErrors());

        assertEquals("foo.bar", builder.getPackage().getName());
    }

    @Test
    public void testGeneratedBeansExtendsSimple() throws Exception {

        BRMSPackageBuilder builder = new BRMSPackageBuilder();
        builder.getPackageBuilderConfiguration().setDefaultPackageName( "foo.bar" );

        PackageDescr pc = new PackageDescr( "foo.bar" );
        builder.addPackage( pc );

        String header = "declare Bean1 \n"
                        + "name: String\n"
                        + "end\n"
                        + "declare Bean2 extends Bean1 \n"
                        + "age: int\n" +
                        "end";
        builder.addPackageFromDrl( new StringReader( header ) );
        assertFalse( builder.hasErrors() );

        String rule1 = "rule foo1 \n"
                          + "when \n"
                          + "Bean1(name=='mike') \n"
                          + "then \n"
                          + "System.out.println(42);\n"
                          + "end";
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        if ( builder.hasErrors() ) {
            System.err.println( builder.getErrors().getErrors()[0].getMessage() );
        }
        assertFalse( builder.hasErrors() );

        String rule2 = "rule foo2 \n"
                       + "when \n"
                       + "Bean2(age==27, name=='mike') \n"
                       + "then \n"
                       + "System.out.println(42);\n"
                       + "end";

        builder.addPackageFromDrl( new StringReader( rule2 ) );
        if ( builder.hasErrors() ) {
            System.err.println( builder.getErrors().getErrors()[0].getMessage() );
        }
        assertFalse( builder.hasErrors() );

        assertEquals( "foo.bar",
                      builder.getPackage().getName() );
    }

    @Test
    public void testGeneratedBeansExtendsPOJOSimple() throws Exception {

        JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add( jis );
        BRMSPackageBuilder builder = new BRMSPackageBuilder( new Properties(),
                                                             new ClassLoaderBuilder( jarInputStreams ).buildClassLoader() );

        PackageDescr pc = new PackageDescr( "foo.bar" );
        builder.addPackage( pc );

        String header = "import com.billasurf.Person\n"
                        + "declare Person \n"
                        + "end\n";

        builder.addPackageFromDrl( new StringReader( header ) );
        assertFalse( builder.hasErrors() );

        String rule1 = "rule foo \n"
            + "when \n"
            + "Person(age==27, name=='mike') \n"
            + "then \n"
            + "System.out.println(42);\n"
            + "end";
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        if ( builder.hasErrors() ) {
            System.err.println( builder.getErrors().getErrors()[0].getMessage() );
        }
        assertFalse( builder.hasErrors() );

        assertEquals( "foo.bar",
                      builder.getPackage().getName() );
    }
    
    @Test
    public void testGeneratedBeansExtendsPOJOComplex1() throws Exception {

        JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add( jis );
        BRMSPackageBuilder builder = new BRMSPackageBuilder( new Properties(),
                                                             new ClassLoaderBuilder( jarInputStreams ).buildClassLoader() );

        PackageDescr pc = new PackageDescr( "foo.bar" );
        builder.addPackage( pc );

        String header = "import com.billasurf.Person\n"
                        + "declare Person \n"
                        + "end\n"
                        + "declare Person2 \n"
                        + "board : String\n"
                        + "end\n";

        builder.addPackageFromDrl( new StringReader( header ) );
        assertFalse( builder.hasErrors() );

        String rule1 = "rule foo1 \n"
                       + "when \n"
                       + "Person2(age==27, name=='mike', board=='regular') \n"
                       + "then \n"
                       + "System.out.println(42);\n"
                       + "end";
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        if ( builder.hasErrors() ) {
            System.err.println( builder.getErrors().getErrors()[0].getMessage() );
        }
        assertFalse( builder.hasErrors() );

        assertEquals( "foo.bar",
                      builder.getPackage().getName() );
    }

    @Test
    public void testGeneratedBeansExtendsPOJOComplex2() throws Exception {

        JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/billasurf.jar" ) );
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add( jis );
        BRMSPackageBuilder builder = new BRMSPackageBuilder( new Properties(),
                                                             new ClassLoaderBuilder( jarInputStreams ).buildClassLoader() );

        PackageDescr pc = new PackageDescr( "foo.bar" );
        builder.addPackage( pc );

        String header = "import com.billasurf.Person\n"
                        + "declare Person \n"
                        + "end\n"
                        + "declare Person2 \n"
                        + "board : String\n"
                        + "end\n";

        builder.addPackageFromDrl( new StringReader( header ) );
        assertFalse( builder.hasErrors() );

        String rule1 = "rule foo1 \n"
                       + "when \n"
                       + "Person(age==27, name=='mike') \n"
                       + "then \n"
                       + "System.out.println(42);\n"
                       + "end";
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        if ( builder.hasErrors() ) {
            System.err.println( builder.getErrors().getErrors()[0].getMessage() );
        }
        assertFalse( builder.hasErrors() );

        String rule2 = "rule foo2 \n"
                       + "when \n"
                       + "Person2(age==27, name=='mike', board=='regular') \n"
                       + "then \n"
                       + "System.out.println(42);\n"
                       + "end";
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        if ( builder.hasErrors() ) {
            System.err.println( builder.getErrors().getErrors()[0].getMessage() );
        }
        assertFalse( builder.hasErrors() );

        assertEquals( "foo.bar",
                      builder.getPackage().getName() );
    }

    @Test
    public void testHasDSL() {
        BRMSPackageBuilder builder = new BRMSPackageBuilder();
        assertFalse(builder.hasDSL());
    }

    @Test
    public void testGetExpander() {
        BRMSPackageBuilder builder = new BRMSPackageBuilder();
        List<DSLTokenizedMappingFile> files = new ArrayList<DSLTokenizedMappingFile>();
        files.add(new DSLTokenizedMappingFile());
        builder.setDSLFiles(files);
        assertTrue(builder.hasDSL());
        assertNotNull(builder.getDSLExpander());
    }

    @Test
    public void testDefaultCompiler() throws Exception {

        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/billasurf.jar"));
        List<JarInputStream> l = new ArrayList<JarInputStream>();
        l.add(jis);
        Properties properties = new Properties();
        properties.setProperty("drools.accumulate.function.groupCount", "org.drools.base.accumulators.MaxAccumulateFunction");
        BRMSPackageBuilder builder = new BRMSPackageBuilder(properties, new ClassLoaderBuilder(l).buildClassLoader());
        assertEquals("org.drools.base.accumulators.MaxAccumulateFunction", builder.getPackageBuilderConfiguration().getAccumulateFunction("groupCount").getClass().getName());

        PackageDescr pc = new PackageDescr("foo.bar");
        builder.addPackage(pc);

        String header = "import com.billasurf.Person\n import com.billasurf.Board";
        builder.addPackageFromDrl(new StringReader(header));
        assertFalse(builder.hasErrors());

        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration("java");
        assertEquals(JavaDialectConfiguration.ECLIPSE, javaConf.getCompiler());
    }

    @Test
    public void testEclipseCompiler() throws Exception {

        System.setProperty("drools.dialect.java.compiler", "ECLIPSE");
        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/billasurf.jar"));
        List<JarInputStream> l = new ArrayList<JarInputStream>();
        l.add(jis);
        BRMSPackageBuilder builder = new BRMSPackageBuilder(new Properties(), new ClassLoaderBuilder(l).buildClassLoader());

        PackageDescr pc = new PackageDescr("foo.bar");
        builder.addPackage(pc);

        String header = "import com.billasurf.Person\n import com.billasurf.Board";
        builder.addPackageFromDrl(new StringReader(header));
        assertFalse(builder.hasErrors());

        JavaDialectConfiguration javaConf = (JavaDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration("java");
        assertEquals(JavaDialectConfiguration.ECLIPSE, javaConf.getCompiler());
    }

    @Test
    public void testNamespaceSingle() throws Exception {

        System.setProperty("drools.dialect.java.compiler", "ECLIPSE");
        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/billasurf.jar"));
        List<JarInputStream> l = new ArrayList<JarInputStream>();
        l.add(jis);
        BRMSPackageBuilder builder = new BRMSPackageBuilder(new Properties(), new ClassLoaderBuilder(l).buildClassLoader());

        assertFalse(builder.getPackageBuilderConfiguration().isAllowMultipleNamespaces());
    }

    @Test
    public void testRuleFlow() throws Exception {
        BRMSPackageBuilder builder = new BRMSPackageBuilder(new Properties(), new ClassLoaderBuilder(new ArrayList<JarInputStream>()).buildClassLoader());
        builder.addProcessFromXml(new InputStreamReader(this.getClass().getResourceAsStream("evaluation.rf")));
        assertFalse(builder.hasErrors());
    }

    @Test
    public void testBPMN2Process() throws Exception {
        BRMSPackageBuilder builder = new BRMSPackageBuilder(new Properties(), new ClassLoaderBuilder(new ArrayList<JarInputStream>()).buildClassLoader());
        builder.addProcessFromXml(new InputStreamReader(this.getClass().getResourceAsStream("Hello.bpmn")));
        assertFalse(builder.hasErrors());
    }

}
