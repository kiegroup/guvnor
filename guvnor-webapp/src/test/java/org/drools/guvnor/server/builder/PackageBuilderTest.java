/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server.builder;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.Iterator;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.ResourceFactory;
import org.junit.Test;

public class PackageBuilderTest {

    @Test
    public void testSimplePackageAttributesSingleDrl() throws Exception {

        String drl1 = "package test\nno-loop true\ndeclare Album\n genre: String \n end\nrule \"rule1\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl1 ) ),
                      ResourceType.DRL );
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();

        assertEquals( 1,
                      kbase.getKnowledgePackages().size() );

        KnowledgePackage kp1 = kbase.getKnowledgePackages().iterator().next();
        KnowledgePackageImp kpImpl1 = (KnowledgePackageImp) kp1;

        assertEquals( "test",
                      kp1.getName() );

        assertEquals( 1,
                      kp1.getRules().size() );

        Rule r1 = kp1.getRules().iterator().next();
        String rName = r1.getName();
        assertEquals( "rule1",
                      rName );

        org.drools.rule.Rule rr1 = (org.drools.rule.Rule) kpImpl1.getRule( rName );

        assertEquals( true,
                      rr1.isNoLoop() );

    }

    @Test
    public void testSimplePackageAttributesSingleDrlDirect() throws Exception {

        String drl1 = "package test\n no-loop true\ndeclare Album\n genre: String \n end\nrule \"rule1\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader(drl1) );
        org.drools.rule.Package p1 = builder.getPackage();

        assertEquals( "test",
                      p1.getName() );

        assertEquals( 1,
                      p1.getRules().length );

        org.drools.rule.Rule r1 = p1.getRules()[0];

        assertEquals( true,
                      r1.isNoLoop() );

    }
    
    @Test
    public void testSimplePackageAttributesMultipleDrls() throws Exception {

        String drl1 = "package test\nno-loop true\ndeclare Album\n genre: String \n end\nrule \"rule1\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        String drl2 = "package test\nno-loop false\ndeclare Album\n genre: String \n end\nrule \"rule2\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl1 ) ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl2 ) ),
                      ResourceType.DRL );
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();

        assertEquals( 1,
                      kbase.getKnowledgePackages().size() );

        KnowledgePackage kp1 = kbase.getKnowledgePackages().iterator().next();
        KnowledgePackageImp kpImpl1 = (KnowledgePackageImp) kp1;

        assertEquals( "test",
                      kp1.getName() );

        assertEquals( 2,
                      kp1.getRules().size() );

        Iterator<Rule> ruleItr = kp1.getRules().iterator();
        Rule r1 = ruleItr.next();
        Rule r2 = ruleItr.next();

        String rName1 = r1.getName();
        assertEquals( "rule1",
                      rName1 );

        String rName2 = r2.getName();
        assertEquals( "rule2",
                      rName2 );

        org.drools.rule.Rule rr1 = (org.drools.rule.Rule) kpImpl1.getRule( rName1 );
        org.drools.rule.Rule rr2 = (org.drools.rule.Rule) kpImpl1.getRule( rName2 );

        assertEquals( true,
                      rr1.isNoLoop() );

        assertEquals( false,
                      rr2.isNoLoop() );

    }
    
    @Test
    public void testComplexPackageAttributesMultipleDrls() throws Exception {

        String drl1 = "package test\nno-loop true\ndeclare Album\n genre: String \n end\nrule \"rule1\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        String drl2 = "package test\nagenda-group \"test\"\ndeclare Album\n genre: String \n end\nrule \"rule2\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl1 ) ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl2 ) ),
                      ResourceType.DRL );
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();

        assertEquals( 1,
                      kbase.getKnowledgePackages().size() );

        KnowledgePackage kp1 = kbase.getKnowledgePackages().iterator().next();
        KnowledgePackageImp kpImpl1 = (KnowledgePackageImp) kp1;

        assertEquals( "test",
                      kp1.getName() );

        assertEquals( 2,
                      kp1.getRules().size() );

        Iterator<Rule> ruleItr = kp1.getRules().iterator();
        Rule r1 = ruleItr.next();
        Rule r2 = ruleItr.next();

        String rName1 = r1.getName();
        assertEquals( "rule1",
                      rName1 );

        String rName2 = r2.getName();
        assertEquals( "rule2",
                      rName2 );

        org.drools.rule.Rule rr1 = (org.drools.rule.Rule) kpImpl1.getRule( rName1 );
        org.drools.rule.Rule rr2 = (org.drools.rule.Rule) kpImpl1.getRule( rName2 );

        assertEquals( true,
                      rr1.isNoLoop() );
        assertEquals( "MAIN",
                      rr1.getAgendaGroup() );

        assertEquals( true,
                      rr2.isNoLoop() );
        assertEquals( "test",
                      rr2.getAgendaGroup() );

    }
    
    @Test
    public void testComplexPackageAttributesMultipleDrlsDirect() throws Exception {

        String drl1 = "package test\nno-loop true\ndeclare Album\n genre: String \n end\nrule \"rule1\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        String drl2 = "package test\nagenda-group\"test\"\ndeclare Album\n genre: String \n end\nrule \"rule2\"\nwhen Album() \n then \nAlbum a = new Album(); \n end";
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader(drl1) );
        builder.addPackageFromDrl( new StringReader(drl2) );
        org.drools.rule.Package p1 = builder.getPackage();

        assertEquals( "test",
                      p1.getName() );

        assertEquals( 2,
                      p1.getRules().length );

        org.drools.rule.Rule r1 = p1.getRules()[0];

        assertEquals( true,
                      r1.isNoLoop() );
        assertEquals( "MAIN",
                      r1.getAgendaGroup() );

        org.drools.rule.Rule r2 = p1.getRules()[1];

        assertEquals( true,
                      r2.isNoLoop() );
        assertEquals( "test",
                      r2.getAgendaGroup() );

    }
    
}
