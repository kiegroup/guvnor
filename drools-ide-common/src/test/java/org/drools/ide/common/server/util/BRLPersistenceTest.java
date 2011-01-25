/**
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.server.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;

public class BRLPersistenceTest {

    @Test
    public void testGenerateEmptyXML() {
        final BRLPersistence p = BRXMLPersistence.getInstance();
        final String xml = p.marshal( new RuleModel() );
        assertNotNull( xml );
        assertFalse( xml.equals( "" ) );

        assertTrue( xml.startsWith( "<rule>" ) );
        assertTrue( xml.endsWith( "</rule>" ) );
    }

    @Test
    public void testBasics() {
        final BRLPersistence p = BRXMLPersistence.getInstance();
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        ActionGlobalCollectionAdd ag = new ActionGlobalCollectionAdd();
        ag.factName = "x";
        ag.globalName = "g";
        m.addRhsItem( ag);
        m.name = "my rule";
        final String xml = p.marshal( m );
        System.out.println(xml);
        assertTrue( xml.indexOf( "Person" ) > -1 );
        assertTrue( xml.indexOf( "Accident" ) > -1 );
        assertTrue( xml.indexOf( "no-loop" ) > -1 );
        assertTrue( xml.indexOf( "org.drools" ) == -1 );
        assertTrue( xml.indexOf( "addToGlobal" ) > -1 );


        RuleModel rm_ = BRXMLPersistence.getInstance().unmarshal(xml);
        assertEquals(2, rm_.rhs.length);

    }

    @Test
    public void testMoreComplexRendering() {
        final BRLPersistence p = BRXMLPersistence.getInstance();
        final RuleModel m = getComplexModel();

        final String xml = p.marshal( m );
        System.out.println( xml );

        assertTrue( xml.indexOf( "org.drools" ) == -1 );

    }

    @Test
    public void testRoundTrip() {
        final RuleModel m = getComplexModel();

        final String xml = BRXMLPersistence.getInstance().marshal( m );

        final RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull( m2 );
        assertEquals( m.name,
                      m2.name );
        assertEquals( m.lhs.length,
                      m2.lhs.length );
        assertEquals( m.rhs.length,
                      m2.rhs.length );
        assertEquals( 1,
                      m.attributes.length );

        final RuleAttribute at = m.attributes[0];
        assertEquals( "no-loop",
                      at.attributeName );
        assertEquals( "true",
                      at.value );

        final String newXML = BRXMLPersistence.getInstance().marshal( m2 );
        assertEquals( xml,
                      newXML );

    }

    @Test
    public void testCompositeConstraintsRoundTrip() throws Exception {
        RuleModel m = new RuleModel();
        m.name = "with composite";

        FactPattern p1 = new FactPattern("Person");
        p1.boundName = "p1";
        m.addLhsItem( p1 );

        FactPattern p = new FactPattern("Goober");
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;
        p.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("goo");
        X.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        X.setValue("foo");
        X.setOperator("==");
        X.connectives = new ConnectiveConstraint[1];
        X.connectives[0] = new ConnectiveConstraint();
        X.connectives[0].setConstraintValueType(ConnectiveConstraint.TYPE_LITERAL);
        X.connectives[0].operator = "|| ==";
        X.connectives[0].setValue("bar");
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("goo2");
        Y.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        Y.setValue("foo");
        Y.setOperator("==");
        comp.addConstraint( Y );

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;
        final SingleFieldConstraint Q1 = new SingleFieldConstraint();
        Q1.setFieldName("goo");
        Q1.setOperator("==");
        Q1.setValue("whee");
        Q1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);

        comp2.addConstraint( Q1 );

        final SingleFieldConstraint Q2 = new SingleFieldConstraint();
        Q2.setFieldName("gabba");
        Q2.setOperator("==");
        Q2.setValue("whee");
        Q2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);

        comp2.addConstraint( Q2 );

        //now nest it
        comp.addConstraint( comp2 );



        final SingleFieldConstraint Z = new SingleFieldConstraint();
        Z.setFieldName("goo3");
        Z.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        Z.setValue("foo");
        Z.setOperator("==");

        p.addConstraint( Z );

        ActionInsertFact ass = new ActionInsertFact("Whee");
        m.addRhsItem( ass );


        String xml = BRXMLPersistence.getInstance().marshal( m );
        //System.err.println(xml);

        RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull(m2);
        assertEquals("with composite", m2.name);

        assertEquals(m2.lhs.length, m.lhs.length);
        assertEquals(m2.rhs.length, m.rhs.length);




    }

    @Test
    public void testFreeFormLine() {
        RuleModel m = new RuleModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[1];

        FreeFormLine fl = new FreeFormLine();
        fl.text = "Person()";
        m.lhs[0] = fl;

        FreeFormLine fr = new FreeFormLine();
        fr.text = "fun()";
        m.rhs[0] = fr;

        String xml = BRXMLPersistence.getInstance().marshal(m);
        assertNotNull(xml);

        RuleModel m_  = BRXMLPersistence.getInstance().unmarshal(xml);
        assertEquals(1, m_.lhs.length);
        assertEquals(1, m_.rhs.length);

        assertEquals("Person()", ((FreeFormLine)m_.lhs[0]).text);
        assertEquals("fun()", ((FreeFormLine)m_.rhs[0]).text);

    }

    /**
     * This will verify that we can load an old BRL change. If this fails,
     * then backwards compatibility is broken.
     */
    @Test
    public void testBackwardsCompat() throws Exception {
        RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( loadResource( "existing_brl.xml" ) );

        assertNotNull(m2);
        assertEquals(3, m2.rhs.length);
    }

    public static String loadResource(final String name) throws Exception {

        //        System.err.println( getClass().getResource( name ) );
        final InputStream in = BRLPersistenceTest.class.getResourceAsStream( name );


        final Reader reader = new InputStreamReader( in );

        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }

        return text.toString();
    }

    private RuleModel getComplexModel() {
        final RuleModel m = new RuleModel();

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        final FactPattern pat = new FactPattern("Person");
        pat.boundName = "p1";
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldBinding("f1");
        con.setFieldName("age");
        con.setOperator("<");
        con.setValue("42");
        pat.addConstraint( con );

        m.addLhsItem( pat );

        final CompositeFactPattern comp = new CompositeFactPattern( "not" );
        comp.addFactPattern( new FactPattern( "Cancel" ) );
        m.addLhsItem( comp );

        final ActionUpdateField set = new ActionUpdateField();
        set.variable = "p1";
        set.addFieldValue( new ActionFieldValue( "status",
                                                 "rejected",
                                                 SuggestionCompletionEngine.TYPE_STRING ) );
        m.addRhsItem( set );

        final ActionRetractFact ret = new ActionRetractFact( "p1" );
        m.addRhsItem( ret );

        final DSLSentence sen = new DSLSentence();
        sen.sentence = "Send an email to {administrator}";

        m.addRhsItem( sen );
        return m;
    }

    @Test
    public void testLoadEmpty() {
        RuleModel m = BRXMLPersistence.getInstance().unmarshal( null );
        assertNotNull( m );

        m = BRXMLPersistence.getInstance().unmarshal( "" );
        assertNotNull( m );
    }

}
