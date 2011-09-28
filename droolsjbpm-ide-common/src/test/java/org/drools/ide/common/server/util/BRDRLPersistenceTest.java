/*
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.ExpressionField;
import org.drools.ide.common.client.modeldriven.brl.ExpressionText;
import org.drools.ide.common.client.modeldriven.brl.ExpressionUnboundFact;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;
import org.junit.Before;
import org.junit.Test;

public class BRDRLPersistenceTest {

    private BRLPersistence brlPersistence;

    @Before
    public void setUp() throws Exception {
        brlPersistence = BRDRLPersistence.getInstance();
    }

    @Test
    public void testGenerateEmptyDRL() {
        String expected = "rule \"null\"\n\tdialect \"mvel\"\n\twhen\n\tthen\nend\n";

        final String drl = brlPersistence.marshal( new RuleModel() );

        assertNotNull( drl );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testFreeForm() {
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

        String drl = brlPersistence.marshal( m );
        assertNotNull( drl );
        assertTrue( drl.indexOf( "Person()" ) > 0 );
        assertTrue( drl.indexOf( "fun()" ) > drl.indexOf( "Person()" ) );
    }

    @Test
    public void testBasics() {
        String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
                          + "\t\tAccident( )\n\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testInsertLogical() {
        String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
                          + "\t\tAccident( )\n\tthen\n\t\tinsertLogical( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertLogicalFact( "Report" ) );

        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testAttr() {
        RuleModel m = new RuleModel();
        m.attributes = new RuleAttribute[1];
        m.attributes[0] = new RuleAttribute( "enabled",
                                             "true" );
        final String drl = brlPersistence.marshal( m );

        assertTrue( drl.indexOf( "enabled true" ) > 0 );

    }

    @Test
    public void testEnumNoType() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( type == \"CheeseType.CHEDDAR\" )\n"
                          + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "type" );
        con.setOperator( "==" );
        con.setValue( "CheeseType.CHEDDAR" );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testEnumTypeString() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( type == \"CHEDDAR\" )\n"
                          + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "type" );
        con.setOperator( "==" );
        con.setValue( "CHEDDAR" );
        con.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testEnumTypeNumeric() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( age == 100 )\n"
                          + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "age" );
        con.setOperator( "==" );
        con.setValue( "100" );
        con.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testEnumTypeBoolean() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( smelly == true )\n"
                          + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "smelly" );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setFieldType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testEnumTypeDate() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( dateMade == \"31-Jan-2010\" )\n"
                          + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "dateMade" );
        con.setOperator( "==" );
        con.setValue( "31-Jan-2010" );
        con.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testEnumTypeComparable() {
        //Java 1.5+ "true" enums are of type Comparable
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( type == Cheese.CHEDDAR )\n"
                          + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "type" );
        con.setOperator( "==" );
        con.setValue( "Cheese.CHEDDAR" );
        con.setFieldType( SuggestionCompletionEngine.TYPE_COMPARABLE );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testMoreComplexRendering() {
        final RuleModel m = getComplexModel();
        String expected = "rule \"Complex Rule\"\n" + "\tno-loop true\n"
                          + "\tsalience -10\n" + "\tagenda-group \"aGroup\"\n"
                          + "\tdialect \"mvel\"\n" + "\twhen\n"
                          + "\t\t>p1 : Person( f1 : age < 42 )\n"
                          + "\t\t>not (Cancel( )) \n" + "\tthen\n"
                          + "\t\t>p1.setStatus( \"rejected\" );\n"
                          + "\t\t>update( p1 );\n" + "\t\t>retract( p1 );\n"
                          + "\t\tSend an email to administrator\n" + "end\n";

        final String drl = brlPersistence.marshal( m );

        assertEquals( expected,
                      drl );

    }

    @Test
    public void testFieldBindingWithNoConstraints() {
        // to satisfy JBRULES-850
        RuleModel m = getModelWithNoConstraints();
        String s = BRDRLPersistence.getInstance().marshal( m );
        // System.out.println(s);
        assertTrue( s.indexOf( "Person( f1 : age)" ) != -1 );
    }

    @Test
    public void textIsNullOperator() {
        final RuleModel m = new RuleModel();
        m.name = "IsNullOperator";
        final FactPattern pat = new FactPattern( "Person" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "age" );
        con.setOperator( "== null" );
        pat.addConstraint( con );

        m.addLhsItem( pat );

        String s = BRDRLPersistence.getInstance().marshal( m );
        assertTrue( s.indexOf( "Person( age == null )" ) != -1 );
    }

    @Test
    public void textIsNotNullOperator() {
        final RuleModel m = new RuleModel();
        m.name = "IsNotNullOperator";
        final FactPattern pat = new FactPattern( "Person" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "age" );
        con.setOperator( "!= null" );
        pat.addConstraint( con );

        m.addLhsItem( pat );

        String s = BRDRLPersistence.getInstance().marshal( m );
        assertTrue( s.indexOf( "Person( age != null )" ) != -1 );
    }

    //
    // public void testRoundTrip() {
    // final RuleModel m = getComplexModel();
    //
    // final String xml = BRXMLPersistence.getInstance().marshal( m );
    //
    // final RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( xml );
    // assertNotNull( m2 );
    // assertEquals( m.name,
    // m2.name );
    // assertEquals( m.lhs.length,
    // m2.lhs.length );
    // assertEquals( m.rhs.length,
    // m2.rhs.length );
    // assertEquals( 1,
    // m.attributes.length );
    //
    // final RuleAttribute at = m.attributes[0];
    // assertEquals( "no-loop",
    // at.attributeName );
    // assertEquals( "true",
    // at.value );
    //
    // final String newXML = BRXMLPersistence.getInstance().marshal( m2 );
    // assertEquals( xml,
    // newXML );
    //
    // }
    //

    private RuleModel getModelWithNoConstraints() {
        final RuleModel m = new RuleModel();
        m.name = "Complex Rule";
        final FactPattern pat = new FactPattern( "Person" );
        pat.setBoundName( "p1" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldBinding( "f1" );
        con.setFieldName( "age" );
        // con.operator = "<";
        // con.value = "42";
        pat.addConstraint( con );

        m.addLhsItem( pat );

        return m;
    }

    private RuleModel getComplexModel() {
        final RuleModel m = new RuleModel();
        m.name = "Complex Rule";

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );
        m.addAttribute( new RuleAttribute( "salience",
                                           "-10" ) );
        m.addAttribute( new RuleAttribute( "agenda-group",
                                           "aGroup" ) );

        final FactPattern pat = new FactPattern( "Person" );
        pat.setBoundName( "p1" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldBinding( "f1" );
        con.setFieldName( "age" );
        con.setOperator( "<" );
        con.setValue( "42" );
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
        sen.setDefinition( "Send an email to {administrator}" );

        m.addRhsItem( sen );
        return m;
    }

    @Test
    public void testOrComposite() throws Exception {
        RuleModel m = new RuleModel();
        m.name = "or";
        CompositeFactPattern cp = new CompositeFactPattern(
                                                            CompositeFactPattern.COMPOSITE_TYPE_OR );
        FactPattern p1 = new FactPattern( "Person" );
        SingleFieldConstraint sf1 = new SingleFieldConstraint( "age" );
        sf1.setOperator( "==" );
        sf1.setValue( "42" );
        p1.addConstraint( sf1 );

        cp.addFactPattern( p1 );

        FactPattern p2 = new FactPattern( "Person" );
        SingleFieldConstraint sf2 = new SingleFieldConstraint( "age" );
        sf2.setOperator( "==" );
        sf2.setValue( "43" );
        p2.addConstraint( sf2 );

        cp.addFactPattern( p2 );

        m.addLhsItem( cp );

        String result = BRDRLPersistence.getInstance().marshal( m );
        assertTrue( result
                .indexOf( "( Person( age == 42 ) or Person( age == 43 ) )" ) > 0 );

    }

    @Test
    public void testExistsMultiPatterns() throws Exception {
        String result = getCompositeFOL( CompositeFactPattern.COMPOSITE_TYPE_EXISTS );
        assertTrue( result
                .indexOf( "exists (Person( age == 42 ) and Person( age == 43 ))" ) > 0 );
    }

    @Test
    public void testNotMultiPatterns() throws Exception {
        String result = getCompositeFOL( CompositeFactPattern.COMPOSITE_TYPE_NOT );
        assertTrue( result
                .indexOf( "not (Person( age == 42 ) and Person( age == 43 ))" ) > 0 );
    }

    @Test
    public void testSingleExists() throws Exception {
        RuleModel m = new RuleModel();
        m.name = "or";
        CompositeFactPattern cp = new CompositeFactPattern( CompositeFactPattern.COMPOSITE_TYPE_EXISTS );
        FactPattern p1 = new FactPattern( "Person" );
        SingleFieldConstraint sf1 = new SingleFieldConstraint( "age" );
        sf1.setOperator( "==" );
        sf1.setValue( "42" );
        p1.addConstraint( sf1 );

        cp.addFactPattern( p1 );

        m.addLhsItem( cp );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertTrue( result.indexOf( "exists (Person( age == 42 )) " ) > 0 );

    }

    private String getCompositeFOL(String type) {
        RuleModel m = new RuleModel();
        m.name = "or";
        CompositeFactPattern cp = new CompositeFactPattern( type );
        FactPattern p1 = new FactPattern( "Person" );
        SingleFieldConstraint sf1 = new SingleFieldConstraint( "age" );
        sf1.setOperator( "==" );
        sf1.setValue( "42" );
        p1.addConstraint( sf1 );

        cp.addFactPattern( p1 );

        FactPattern p2 = new FactPattern( "Person" );
        SingleFieldConstraint sf2 = new SingleFieldConstraint( "age" );
        sf2.setOperator( "==" );
        sf2.setValue( "43" );
        p2.addConstraint( sf2 );

        cp.addFactPattern( p2 );

        m.addLhsItem( cp );

        String result = BRDRLPersistence.getInstance().marshal( m );

        return result;
    }

    // public void testLoadEmpty() {
    // RuleModel m = BRXMLPersistence.getInstance().unmarshal( null );
    // assertNotNull( m );
    //
    // m = BRXMLPersistence.getInstance().unmarshal( "" );
    // assertNotNull( m );
    // }

    @Test
    public void testCompositeConstraints() {
        RuleModel m = new RuleModel();
        m.name = "with composite";

        FactPattern p1 = new FactPattern( "Person" );
        p1.setBoundName( "p1" );
        m.addLhsItem( p1 );

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;
        p.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "goo" );
        X.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        X.setValue( "foo" );
        X.setOperator( "==" );
        X.connectives = new ConnectiveConstraint[1];
        X.connectives[0] = new ConnectiveConstraint();
        X.connectives[0].setConstraintValueType( ConnectiveConstraint.TYPE_LITERAL );
        X.connectives[0].setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        X.connectives[0].setOperator( "|| ==" );
        X.connectives[0].setValue( "bar" );
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "goo2" );
        Y.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Y.setValue( "foo" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;
        final SingleFieldConstraint Q1 = new SingleFieldConstraint();
        Q1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        Q1.setFieldName( "goo" );
        Q1.setOperator( "==" );
        Q1.setValue( "whee" );
        Q1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q1 );

        final SingleFieldConstraint Q2 = new SingleFieldConstraint();
        Q2.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        Q2.setFieldName( "gabba" );
        Q2.setOperator( "==" );
        Q2.setValue( "whee" );
        Q2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q2 );

        // now nest it
        comp.addConstraint( comp2 );

        final SingleFieldConstraint Z = new SingleFieldConstraint();
        Z.setFieldName( "goo3" );
        Z.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Z.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        Z.setValue( "foo" );
        Z.setOperator( "==" );

        p.addConstraint( Z );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = BRDRLPersistence.getInstance().marshal( m );
        String expected = "rule \"with composite\" "
                          + " \tdialect \"mvel\"\n when "
                          + "p1 : Person( ) "
                          + "Goober( goo == \"foo\"  || == \"bar\" || goo2 == \"foo\" || ( goo == \"whee\" && gabba == \"whee\" ), goo3 == \"foo\" )"
                          + " then " + "insert( new Whee() );" + "end";
        assertEqualsIgnoreWhitespace( expected,
                                      actual );

    }

    @Test
    public void testFieldsDeclaredButNoConstraints() {
        RuleModel m = new RuleModel();
        m.name = "boo";

        FactPattern p = new FactPattern( "Person" );

        // this isn't an effective constraint, so it should be ignored.
        p.addConstraint( new SingleFieldConstraint( "field1" ) );

        m.addLhsItem( p );

        String actual = BRDRLPersistence.getInstance().marshal( m );

        String expected = "rule \"boo\" \tdialect \"mvel\"\n when Person() then end";

        assertEqualsIgnoreWhitespace( expected,
                                      actual );

        SingleFieldConstraint con = (SingleFieldConstraint) p.constraintList.constraints[0];
        con.setFieldBinding( "q" );

        // now it should appear, as we are binding a var to it

        actual = BRDRLPersistence.getInstance().marshal( m );

        expected = "rule \"boo\" dialect \"mvel\" when Person(q : field1) then end";

        assertEqualsIgnoreWhitespace( expected,
                                      actual );

    }

    @Test
    public void testLiteralStrings() {

        RuleModel m = new RuleModel();
        m.name = "test literal strings";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "goo" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test literal strings\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person(field1 == \"goo\", field2 == variableHere)"
                                              + " then " + "end",
                                      result );

    }

    @Test
    public void testLHSExpressionString1() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsString1";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionText( "field1" ) );
        con.setOperator( "==" );
        con.setValue( "goo" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsString1\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( field1 == \"goo\" )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionString2() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsString2";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.lang.String",
                                                                     SuggestionCompletionEngine.TYPE_STRING ) );
        con.setOperator( "==" );
        con.setValue( "Cheddar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsString2\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( field1 == \"Cheddar\" )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionJavaEnum() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsJavaEnum";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "CHEESE",
                                                                     SuggestionCompletionEngine.TYPE_COMPARABLE ) );
        con.setOperator( "==" );
        con.setValue( "CHEESE.Cheddar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsJavaEnum\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( field1 == CHEESE.Cheddar )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionNumber() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNumber";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.lang.Integer",
                                                                     SuggestionCompletionEngine.TYPE_NUMERIC ) );
        con.setOperator( "==" );
        con.setValue( "55" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsNumber\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( field1 == 55 )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionDate() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsDate";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.util.Date",
                                                                     SuggestionCompletionEngine.TYPE_DATE ) );
        con.setOperator( "==" );
        con.setValue( "27-Jun-2011" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsDate\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( field1 == \"27-Jun-2011\" )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionBoolean() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsBoolean";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.lang.Boolean",
                                                                     SuggestionCompletionEngine.TYPE_BOOLEAN ) );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsBoolean\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( field1 == true )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionNestedString() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedString";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     SuggestionCompletionEngine.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "name",
                                                                     "java.lang.String",
                                                                     SuggestionCompletionEngine.TYPE_STRING ) );
        con.setOperator( "==" );
        con.setValue( "Cheedar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsNestedString\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( favouriteCheese.name == \"Cheedar\" )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionNestedNumber() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedNumber";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     SuggestionCompletionEngine.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "age",
                                                                     "java.lang.Integer",
                                                                     SuggestionCompletionEngine.TYPE_NUMERIC ) );
        con.setOperator( "==" );
        con.setValue( "55" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsNestedNumber\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( favouriteCheese.age == 55 )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionNestedDate() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedDate";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     SuggestionCompletionEngine.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "dateBrought",
                                                                     "java.util.Date",
                                                                     SuggestionCompletionEngine.TYPE_DATE ) );
        con.setOperator( "==" );
        con.setValue( "27-Jun-2011" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsNestedDate\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( favouriteCheese.dateBrought == \"27-Jun-2011\" )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionNestedJavaEnum() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedJavaEnum";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     SuggestionCompletionEngine.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "genericName",
                                                                     "CHEESE",
                                                                     SuggestionCompletionEngine.TYPE_COMPARABLE ) );
        con.setOperator( "==" );
        con.setValue( "CHEESE.Cheddar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsNestedJavaEnum\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( favouriteCheese.genericName == CHEESE.Cheddar )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLHSExpressionNestedBoolean() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedBoolean";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     SuggestionCompletionEngine.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "smelly",
                                                                     "java.lang.Boolean",
                                                                     SuggestionCompletionEngine.TYPE_BOOLEAN ) );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test expressionsNestedBoolean\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person( favouriteCheese.smelly == true )"
                                              + " then " + "end",
                                      result );
    }

    @Test
    public void testLiteralNumerics() {

        RuleModel m = new RuleModel();
        m.name = "test literal numerics";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "44" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test literal numerics\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person(field1 == 44, field2 == variableHere)"
                                              + " then " + "end",
                                      result );

    }

    @Test
    public void testLiteralBooleans() {

        RuleModel m = new RuleModel();
        m.name = "test literal booleans";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test literal booleans\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person(field1 == true, field2 == variableHere)"
                                              + " then " + "end",
                                      result );

    }

    @Test
    public void testLiteralDates() {

        RuleModel m = new RuleModel();
        m.name = "test literal dates";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "31-Jan-2010" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test literal dates\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person(field1 == \"31-Jan-2010\", field2 == variableHere)"
                                              + " then " + "end",
                                      result );

    }

    @Test
    public void testLiteralNoType() {

        RuleModel m = new RuleModel();
        m.name = "test literal no type";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "bananna" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        assertEqualsIgnoreWhitespace( "rule \"test literal no type\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person(field1 == \"bananna\", field2 == variableHere)"
                                              + " then " + "end",
                                      result );

    }

    @Test
    public void testRHSDateInsertAction() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS Date";

            FactPattern p = new FactPattern( "Person" );
            SingleFieldConstraint con = new SingleFieldConstraint();
            con.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
            con.setFieldName( "dateOfBirth" );
            con.setOperator( "==" );
            con.setValue( "31-Jan-2000" );
            con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
            p.addConstraint( con );

            m.addLhsItem( p );

            ActionInsertFact ai = new ActionInsertFact( "Birthday" );
            ai.addFieldValue( new ActionFieldValue( "dob",
                                                    "31-Jan-2000",
                                                    SuggestionCompletionEngine.TYPE_DATE ) );
            m.addRhsItem( ai );

            String result = BRDRLPersistence.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "fact0.setDob( sdf.parse(\"31-Jan-2000\"" ) != -1 );

        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }

    }

    @Test
    public void testRHSDateModifyAction() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS Date";

            FactPattern p = new FactPattern( "Person" );
            p.setBoundName( "$p" );
            SingleFieldConstraint con = new SingleFieldConstraint();
            con.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
            con.setFieldName( "dateOfBirth" );
            con.setOperator( "==" );
            con.setValue( "31-Jan-2000" );
            con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
            p.addConstraint( con );

            m.addLhsItem( p );

            ActionUpdateField am = new ActionUpdateField( "$p" );
            am.addFieldValue( new ActionFieldValue( "dob",
                                                    "31-Jan-2000",
                                                    SuggestionCompletionEngine.TYPE_DATE ) );
            m.addRhsItem( am );

            String result = BRDRLPersistence.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "$p.setDob( sdf.parse(\"31-Jan-2000\"" ) != -1 );
            assertTrue( result.indexOf( "update( $p );" ) != -1 );

        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }

    }

    @Test
    public void testRHSDateUpdateAction() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS Date";

            FactPattern p = new FactPattern( "Person" );
            p.setBoundName( "$p" );
            SingleFieldConstraint con = new SingleFieldConstraint();
            con.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
            con.setFieldName( "dateOfBirth" );
            con.setOperator( "==" );
            con.setValue( "31-Jan-2000" );
            con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
            p.addConstraint( con );

            m.addLhsItem( p );

            ActionSetField au = new ActionSetField( "$p" );
            au.addFieldValue( new ActionFieldValue( "dob",
                                                    "31-Jan-2000",
                                                    SuggestionCompletionEngine.TYPE_DATE ) );
            m.addRhsItem( au );

            String result = BRDRLPersistence.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "$p.setDob( sdf.parse(\"31-Jan-2000\"" ) != -1 );
            assertTrue( result.indexOf( "update( $p );" ) == -1 );

        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }
    }

    @Test
    public void testSubConstraints() {

        RuleModel m = new RuleModel();
        m.name = "test sub constraints";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "field1" );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        con2.setParent( con );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );
        assertEqualsIgnoreWhitespace( "rule \"test sub constraints\""
                                              + "\tdialect \"mvel\"\n when "
                                              + "     Person(field1.field2 == variableHere)" + " then "
                                              + "end",
                                      result );

    }

    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

    @Test
    public void testReturnValueConstraint() {
        RuleModel m = new RuleModel();
        m.name = "yeah";

        FactPattern p = new FactPattern( "Goober" );

        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setConstraintValueType( SingleFieldConstraint.TYPE_RET_VALUE );
        con.setValue( "someFunc(x)" );
        con.setOperator( "==" );
        con.setFieldName( "goo" );

        p.addConstraint( con );
        m.addLhsItem( p );

        String actual = BRDRLPersistence.getInstance().marshal( m );
        // System.err.println(actual);

        String expected = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
                          + "Goober( goo == ( someFunc(x) ) )" + " then " + "end";
        assertEqualsIgnoreWhitespace( expected,
                                      actual );
    }

    @Test
    public void testPredicateConstraint() {
        RuleModel m = new RuleModel();
        m.name = "yeah";

        FactPattern p = new FactPattern( "Goober" );

        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setConstraintValueType( SingleFieldConstraint.TYPE_PREDICATE );
        con.setValue( "field soundslike 'poo'" );

        p.addConstraint( con );
        m.addLhsItem( p );

        String actual = BRDRLPersistence.getInstance().marshal( m );
        // System.err.println(actual);

        String expected = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
                          + "Goober( eval( field soundslike 'poo' ) )" + " then " + "end";
        assertEqualsIgnoreWhitespace( expected,
                                      actual );
    }

    @Test
    public void testConnective() {

        RuleModel m = new RuleModel();
        m.name = "test literal strings";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "goo" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        connective.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "blah" );

        con.connectives = new ConnectiveConstraint[1];
        con.connectives[0] = connective;

        m.addLhsItem( p );

        String result = BRDRLPersistence.getInstance().marshal( m );

        String expected = "rule \"test literal strings\" "
                          + "\tdialect \"mvel\"\n when "
                          + "Person( field1 == goo  || == \"blah\" )" + " then " + "end";
        assertEqualsIgnoreWhitespace( expected,
                                      result );

    }

    @Test
    public void testInvalidComposite() throws Exception {
        RuleModel m = new RuleModel();
        CompositeFactPattern com = new CompositeFactPattern( "not" );
        m.addLhsItem( com );

        String s = BRDRLPersistence.getInstance().marshal( m );
        assertNotNull( s );

        m.addLhsItem( new CompositeFactPattern( "or" ) );
        m.addLhsItem( new CompositeFactPattern( "exists" ) );
        s = BRDRLPersistence.getInstance().marshal( m );
        assertNotNull( s );
    }

    @Test
    public void testAssertWithDSL() throws Exception {
        RuleModel m = new RuleModel();
        DSLSentence sen = new DSLSentence();
        sen.setDefinition( "I CAN HAS DSL" );
        m.addRhsItem( sen );
        ActionInsertFact ins = new ActionInsertFact( "Shizzle" );
        ActionFieldValue val = new ActionFieldValue( "goo",
                                                     "42",
                                                     "Numeric" );
        ins.fieldValues = new ActionFieldValue[1];
        ins.fieldValues[0] = val;
        m.addRhsItem( ins );

        ActionInsertLogicalFact insL = new ActionInsertLogicalFact( "Shizzle" );
        ActionFieldValue valL = new ActionFieldValue( "goo",
                                                      "42",
                                                      "Numeric" );
        insL.fieldValues = new ActionFieldValue[1];
        insL.fieldValues[0] = valL;
        m.addRhsItem( insL );

        String result = BRDRLPersistence.getInstance().marshal( m );
        assertTrue( result.indexOf( ">insert" ) > -1 );

        assertTrue( result.indexOf( ">insertLogical" ) > -1 );
    }

    @Test
    public void testDefaultMVEL() {
        RuleModel m = new RuleModel();

        String s = BRDRLPersistence.getInstance().marshal( m );
        assertTrue( s.indexOf( "mvel" ) > -1 );

        m.addAttribute( new RuleAttribute( "dialect",
                                           "goober" ) );
        s = BRDRLPersistence.getInstance().marshal( m );
        assertFalse( s.indexOf( "mvel" ) > -1 );
        assertTrue( s.indexOf( "goober" ) > -1 );

    }

    @Test
    public void testLockOnActive() {
        RuleModel m = new RuleModel();

        m.addAttribute( new RuleAttribute( "lock-on-active",
                                           "true" ) );
        m.addAttribute( new RuleAttribute( "auto-focus",
                                           "true" ) );
        m.addAttribute( new RuleAttribute( "duration",
                                           "42" ) );

        String s = BRDRLPersistence.getInstance().marshal( m );

        assertTrue( s.indexOf( "lock-on-active true" ) > -1 );
        assertTrue( s.indexOf( "auto-focus true" ) > -1 );
        assertTrue( s.indexOf( "duration 42" ) > -1 );

    }

    @Test
    public void testAddGlobal() {
        String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
                          + "\t\tAccident( )\n\tthen\n\t\tinsert( new Report() );\n\t\tresults.add(f);\nend\n";
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        ActionGlobalCollectionAdd add = new ActionGlobalCollectionAdd();
        add.globalName = "results";
        add.factName = "f";
        m.addRhsItem( add );
        m.name = "my rule";

        final String drl = brlPersistence.marshal( m );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testCompositeOrConstraints() {
        RuleModel m = new RuleModel();
        m.name = "or composite";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( SuggestionCompletionEngine.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFieldName( "Foo.barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = BRDRLPersistence.getInstance().marshal( m );
        String expected = "rule \"or composite\""
            + "dialect \"mvel\"\n"
            + "when\n"
            + "Goober( gooField == \"gooValue\" || fooField != null || fooField.barField == \"barValue\" )\n"
            + "then\n"
            + "insert( new Whee() );\n"
            + "end";

        assertEqualsIgnoreWhitespace( expected,
                                      actual );
    }

    @Test
    public void testCompositeOrConstraintsComplex() {
        RuleModel m = new RuleModel();
        m.name = "or composite complex";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( SuggestionCompletionEngine.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFieldName( "Foo.barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );

        final SingleFieldConstraint sfc4 = new SingleFieldConstraint();
        sfc4.setFieldName( "zooField" );
        sfc4.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc4.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc4.setValue( "zooValue" );
        sfc4.setOperator( "==" );
        p.addConstraint( sfc4 );
        
        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = BRDRLPersistence.getInstance().marshal( m );
        String expected = "rule \"or composite complex\""
            + "dialect \"mvel\"\n"
            + "when\n"
            + "Goober( gooField == \"gooValue\" || fooField != null || fooField.barField == \"barValue\", zooField == \"zooValue\" )\n"
            + "then\n"
            + "insert( new Whee() );\n"
            + "end";

        assertEqualsIgnoreWhitespace( expected,
                                      actual );
    }
    
    @Test
    public void testCompositeAndConstraints() {
        RuleModel m = new RuleModel();
        m.name = "and composite";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( SuggestionCompletionEngine.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFieldName( "Foo.barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = BRDRLPersistence.getInstance().marshal( m );
        String expected = "rule \"and composite\""
            + "dialect \"mvel\"\n"
            + "when\n"
            + "Goober( gooField == \"gooValue\" && fooField != null && fooField.barField == \"barValue\" )\n"
            + "then\n"
            + "insert( new Whee() );\n"
            + "end";

        assertEqualsIgnoreWhitespace( expected,
                                      actual );
    }

    @Test
    public void testCompositeAndConstraintsComplex() {
        RuleModel m = new RuleModel();
        m.name = "and composite complex";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( SuggestionCompletionEngine.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFieldName( "Foo.barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );
        
        final SingleFieldConstraint sfc4 = new SingleFieldConstraint();
        sfc4.setFieldName( "zooField" );
        sfc4.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc4.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc4.setValue( "zooValue" );
        sfc4.setOperator( "==" );
        p.addConstraint( sfc4 );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = BRDRLPersistence.getInstance().marshal( m );
        String expected = "rule \"and composite complex\""
            + "dialect \"mvel\"\n"
            + "when\n"
            + "Goober( gooField == \"gooValue\" && fooField != null && fooField.barField == \"barValue\", zooField == \"zooValue\" )\n"
            + "then\n"
            + "insert( new Whee() );\n"
            + "end";

        assertEqualsIgnoreWhitespace( expected,
                                      actual );
    }
    
}
