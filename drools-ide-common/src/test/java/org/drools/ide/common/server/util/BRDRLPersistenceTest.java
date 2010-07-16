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

import junit.framework.TestCase;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact;
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
import org.drools.ide.common.server.util.BRDRLPersistence;
import org.drools.ide.common.server.util.BRLPersistence;

public class BRDRLPersistenceTest extends TestCase {

	private BRLPersistence p;

	protected void setUp() throws Exception {
		super.setUp();
		p = BRDRLPersistence.getInstance();
	}

	public void testGenerateEmptyDRL() {
		String expected = "rule \"null\"\n\tdialect \"mvel\"\n\twhen\n\tthen\nend\n";

		final String drl = p.marshal(new RuleModel());

		assertNotNull(drl);
		assertEquals(expected, drl);
	}

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

        String drl = p.marshal(m);
        assertNotNull(drl);
        assertTrue(drl.indexOf("Person()") > 0);
        assertTrue(drl.indexOf("fun()") > drl.indexOf("Person()"));
	}

	public void testBasics() {
		String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
				+ "\t\tAccident( )\n\tthen\n\t\tinsert( new Report() );\nend\n";
		final RuleModel m = new RuleModel();
		m.addLhsItem(new FactPattern("Person"));
		m.addLhsItem(new FactPattern("Accident"));
		m.addAttribute(new RuleAttribute("no-loop", "true"));

		m.addRhsItem(new ActionInsertFact("Report"));
		m.name = "my rule";

		final String drl = p.marshal(m);
		assertEquals(expected, drl);
	}

	public void testInsertLogical() {
		String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
				+ "\t\tAccident( )\n\tthen\n\t\tinsertLogical( new Report() );\nend\n";
		final RuleModel m = new RuleModel();
		m.addLhsItem(new FactPattern("Person"));
		m.addLhsItem(new FactPattern("Accident"));
		m.addAttribute(new RuleAttribute("no-loop", "true"));

		m.addRhsItem(new ActionInsertLogicalFact("Report"));

		m.name = "my rule";

		final String drl = p.marshal(m);
		assertEquals(expected, drl);
	}

	public void testAttr() {
		RuleModel m = new RuleModel();
		m.attributes = new RuleAttribute[1];
		m.attributes[0] = new RuleAttribute("enabled", "true");
		final String drl = p.marshal(m);

		assertTrue(drl.indexOf("enabled true") > 0);

	}

	public void testMoreComplexRendering() {
		final RuleModel m = getComplexModel();
		String expected = "rule \"Complex Rule\"\n" + "\tno-loop true\n"
				+ "\tsalience -10\n" + "\tagenda-group \"aGroup\"\n"
				+ "\tdialect \"mvel\"\n" + "\twhen\n"
				+ "\t\t>p1 : Person( f1 : age < 42 )\n"
				+ "\t\t>not Cancel( )\n" + "\tthen\n"
				+ "\t\t>p1.setStatus( \"rejected\" );\n"
				+ "\t\t>update( p1 );\n" + "\t\t>retract( p1 );\n"
				+ "\t\tSend an email to administrator\n" + "end\n";

		final String drl = p.marshal(m);

		assertEquals(expected, drl);

	}

	public void testFieldBindingWithNoConstraints() {
		// to satisfy JBRULES-850
		RuleModel m = getModelWithNoConstraints();
		String s = BRDRLPersistence.getInstance().marshal(m);
		// System.out.println(s);
		assertTrue(s.indexOf("Person( f1 : age)") != -1);
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
		final FactPattern pat = new FactPattern();
		pat.boundName = "p1";
		pat.factType = "Person";
		final SingleFieldConstraint con = new SingleFieldConstraint();
		con.setFieldBinding("f1");
		con.setFieldName("age");
		// con.operator = "<";
		// con.value = "42";
		pat.addConstraint(con);

		m.addLhsItem(pat);

		return m;
	}

	private RuleModel getComplexModel() {
		final RuleModel m = new RuleModel();
		m.name = "Complex Rule";

		m.addAttribute(new RuleAttribute("no-loop", "true"));
		m.addAttribute(new RuleAttribute("salience", "-10"));
		m.addAttribute(new RuleAttribute("agenda-group", "aGroup"));

		final FactPattern pat = new FactPattern();
		pat.boundName = "p1";
		pat.factType = "Person";
		final SingleFieldConstraint con = new SingleFieldConstraint();
		con.setFieldBinding("f1");
		con.setFieldName("age");
		con.setOperator("<");
		con.setValue("42");
		pat.addConstraint(con);

		m.addLhsItem(pat);

		final CompositeFactPattern comp = new CompositeFactPattern("not");
		comp.addFactPattern(new FactPattern("Cancel"));
		m.addLhsItem(comp);

		final ActionUpdateField set = new ActionUpdateField();
		set.variable = "p1";
		set.addFieldValue(new ActionFieldValue("status", "rejected",
				SuggestionCompletionEngine.TYPE_STRING));
		m.addRhsItem(set);

		final ActionRetractFact ret = new ActionRetractFact("p1");
		m.addRhsItem(ret);

		final DSLSentence sen = new DSLSentence();
		sen.sentence = "Send an email to {administrator}";

		m.addRhsItem(sen);
		return m;
	}

	public void testOrComposite() throws Exception {
		RuleModel m = new RuleModel();
		m.name = "or";
		CompositeFactPattern cp = new CompositeFactPattern(
				CompositeFactPattern.COMPOSITE_TYPE_OR);
		FactPattern p1 = new FactPattern("Person");
		SingleFieldConstraint sf1 = new SingleFieldConstraint("age");
		sf1.setOperator("==");
		sf1.setValue("42");
		p1.addConstraint(sf1);

		cp.addFactPattern(p1);

		FactPattern p2 = new FactPattern("Person");
		SingleFieldConstraint sf2 = new SingleFieldConstraint("age");
		sf2.setOperator("==");
		sf2.setValue("43");
		p2.addConstraint(sf2);

		cp.addFactPattern(p2);

		m.addLhsItem(cp);

		String result = BRDRLPersistence.getInstance().marshal(m);
		assertTrue(result
				.indexOf("( Person( age == 42 ) or Person( age == 43 ) )") > 0);

	}

	public void testExistsMultiPatterns() throws Exception {
		String result = getCompositeFOL(CompositeFactPattern.COMPOSITE_TYPE_EXISTS);
		assertTrue(result
				.indexOf("exists (Person( age == 42 ) and Person( age == 43 ))") > 0);
	}

	public void testNotMultiPatterns() throws Exception {
		String result = getCompositeFOL(CompositeFactPattern.COMPOSITE_TYPE_NOT);
		assertTrue(result
				.indexOf("not (Person( age == 42 ) and Person( age == 43 ))") > 0);
	}

	public void testSingleExists() throws Exception {
		RuleModel m = new RuleModel();
		m.name = "or";
		CompositeFactPattern cp = new CompositeFactPattern(
				CompositeFactPattern.COMPOSITE_TYPE_EXISTS);
		FactPattern p1 = new FactPattern("Person");
		SingleFieldConstraint sf1 = new SingleFieldConstraint("age");
		sf1.setOperator("==");
		sf1.setValue("42");
		p1.addConstraint(sf1);

		cp.addFactPattern(p1);

		m.addLhsItem(cp);

		String result = BRDRLPersistence.getInstance().marshal(m);

		assertTrue(result.indexOf("exists Person( age == 42 )") > 0);

	}

	private String getCompositeFOL(String type) {
		RuleModel m = new RuleModel();
		m.name = "or";
		CompositeFactPattern cp = new CompositeFactPattern(type);
		FactPattern p1 = new FactPattern("Person");
		SingleFieldConstraint sf1 = new SingleFieldConstraint("age");
		sf1.setOperator("==");
		sf1.setValue("42");
		p1.addConstraint(sf1);

		cp.addFactPattern(p1);

		FactPattern p2 = new FactPattern("Person");
		SingleFieldConstraint sf2 = new SingleFieldConstraint("age");
		sf2.setOperator("==");
		sf2.setValue("43");
		p2.addConstraint(sf2);

		cp.addFactPattern(p2);

		m.addLhsItem(cp);

		String result = BRDRLPersistence.getInstance().marshal(m);

		return result;
	}

	// public void testLoadEmpty() {
	// RuleModel m = BRXMLPersistence.getInstance().unmarshal( null );
	// assertNotNull( m );
	//
	// m = BRXMLPersistence.getInstance().unmarshal( "" );
	// assertNotNull( m );
	// }

	public void testCompositeConstraints() {
		RuleModel m = new RuleModel();
		m.name = "with composite";

		FactPattern p1 = new FactPattern("Person");
		p1.boundName = "p1";
		m.addLhsItem(p1);

		FactPattern p = new FactPattern("Goober");
		m.addLhsItem(p);
		CompositeFieldConstraint comp = new CompositeFieldConstraint();
		comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;
		p.addConstraint(comp);

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
		comp.addConstraint(X);

		final SingleFieldConstraint Y = new SingleFieldConstraint();
		Y.setFieldName("goo2");
		Y.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
		Y.setValue("foo");
		Y.setOperator("==");
		comp.addConstraint(Y);

		CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
		comp2.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;
		final SingleFieldConstraint Q1 = new SingleFieldConstraint();
		Q1.setFieldName("goo");
		Q1.setOperator("==");
		Q1.setValue("whee");
		Q1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);

		comp2.addConstraint(Q1);

		final SingleFieldConstraint Q2 = new SingleFieldConstraint();
		Q2.setFieldName("gabba");
		Q2.setOperator("==");
		Q2.setValue("whee");
		Q2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);

		comp2.addConstraint(Q2);

		// now nest it
		comp.addConstraint(comp2);

		final SingleFieldConstraint Z = new SingleFieldConstraint();
		Z.setFieldName("goo3");
		Z.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
		Z.setValue("foo");
		Z.setOperator("==");

		p.addConstraint(Z);

		ActionInsertFact ass = new ActionInsertFact("Whee");
		m.addRhsItem(ass);

		String actual = BRDRLPersistence.getInstance().marshal(m);
		String expected = "rule \"with composite\" "
				+ " \tdialect \"mvel\"\n when "
				+ "p1 : Person( ) "
				+ "Goober( goo == \"foo\"  || == \"bar\" || goo2 == \"foo\" || ( goo == \"whee\" && gabba == \"whee\" ), goo3 == \"foo\" )"
				+ " then " + "insert( new Whee() );" + "end";
		assertEqualsIgnoreWhitespace(expected, actual);

	}

	public void testFieldsDeclaredButNoConstraints() {
		RuleModel m = new RuleModel();
		m.name = "boo";

		FactPattern p = new FactPattern();
		p.factType = "Person";

		// this isn't an effective constraint, so it should be ignored.
		p.addConstraint(new SingleFieldConstraint("field1"));

		m.addLhsItem(p);

		String actual = BRDRLPersistence.getInstance().marshal(m);

		String expected = "rule \"boo\" \tdialect \"mvel\"\n when Person() then end";

		assertEqualsIgnoreWhitespace(expected, actual);

		SingleFieldConstraint con = (SingleFieldConstraint) p.constraintList.constraints[0];
		con.setFieldBinding("q");

		// now it should appear, as we are binding a var to it

		actual = BRDRLPersistence.getInstance().marshal(m);

		expected = "rule \"boo\" dialect \"mvel\" when Person(q : field1) then end";

		assertEqualsIgnoreWhitespace(expected, actual);

	}

	public void testLiteralStrings() {

		RuleModel m = new RuleModel();
		m.name = "test literal strings";

		FactPattern p = new FactPattern("Person");
		SingleFieldConstraint con = new SingleFieldConstraint();
		con.setFieldName("field1");
		con.setOperator("==");
		con.setValue("goo");
		con.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
		p.addConstraint(con);

		SingleFieldConstraint con2 = new SingleFieldConstraint();
		con2.setFieldName("field2");
		con2.setOperator("==");
		con2.setValue("variableHere");
		con2.setConstraintValueType(SingleFieldConstraint.TYPE_VARIABLE);
		p.addConstraint(con2);

		m.addLhsItem(p);

		String result = BRDRLPersistence.getInstance().marshal(m);

		assertEqualsIgnoreWhitespace("rule \"test literal strings\""
				+ "\tdialect \"mvel\"\n when "
				+ "     Person(field1 == \"goo\", field2 == variableHere)"
				+ " then " + "end", result);

	}

	public void testSubConstraints() {

		RuleModel m = new RuleModel();
		m.name = "test sub constraints";

		FactPattern p = new FactPattern("Person");
		SingleFieldConstraint con = new SingleFieldConstraint();
		con.setFieldName("field1");
		p.addConstraint(con);

		SingleFieldConstraint con2 = new SingleFieldConstraint();
		con2.setFieldName("field2");
		con2.setOperator("==");
		con2.setValue("variableHere");
		con2.setConstraintValueType(SingleFieldConstraint.TYPE_VARIABLE);
		con2.setParent(con);
		p.addConstraint(con2);

		m.addLhsItem(p);

		String result = BRDRLPersistence.getInstance().marshal(m);
		assertEqualsIgnoreWhitespace("rule \"test sub constraints\""
				+ "\tdialect \"mvel\"\n when "
				+ "     Person(field1.field2 == variableHere)" + " then "
				+ "end", result);

	}

	private void assertEqualsIgnoreWhitespace(final String expected,
			final String actual) {
		final String cleanExpected = expected.replaceAll("\\s+", "");
		final String cleanActual = actual.replaceAll("\\s+", "");

		assertEquals(cleanExpected, cleanActual);
	}

	public void testReturnValueConstraint() {
		RuleModel m = new RuleModel();
		m.name = "yeah";

		FactPattern p = new FactPattern();

		SingleFieldConstraint con = new SingleFieldConstraint();
		con.setConstraintValueType(SingleFieldConstraint.TYPE_RET_VALUE);
		con.setValue("someFunc(x)");
		con.setOperator("==");
		con.setFieldName("goo");
		p.factType = "Goober";

		p.addConstraint(con);
		m.addLhsItem(p);

		String actual = BRDRLPersistence.getInstance().marshal(m);
		// System.err.println(actual);

		String expected = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
				+ "Goober( goo == ( someFunc(x) ) )" + " then " + "end";
		assertEqualsIgnoreWhitespace(expected, actual);
	}

	public void testPredicateConstraint() {
		RuleModel m = new RuleModel();
		m.name = "yeah";

		FactPattern p = new FactPattern();

		SingleFieldConstraint con = new SingleFieldConstraint();
		con.setConstraintValueType(SingleFieldConstraint.TYPE_PREDICATE);
		con.setValue("field soundslike 'poo'");

		p.factType = "Goober";

		p.addConstraint(con);
		m.addLhsItem(p);

		String actual = BRDRLPersistence.getInstance().marshal(m);
		// System.err.println(actual);

		String expected = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
				+ "Goober( eval( field soundslike 'poo' ) )" + " then " + "end";
		assertEqualsIgnoreWhitespace(expected, actual);
	}

	public void testConnective() {

		RuleModel m = new RuleModel();
		m.name = "test literal strings";

		FactPattern p = new FactPattern("Person");
		SingleFieldConstraint con = new SingleFieldConstraint();
		con.setFieldName("field1");
		con.setOperator("==");
		con.setValue("goo");
		con.setConstraintValueType(SingleFieldConstraint.TYPE_VARIABLE);
		p.addConstraint(con);

		ConnectiveConstraint connective = new ConnectiveConstraint();
		connective.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
		connective.operator = "|| ==";
		connective.setValue("blah");

		con.connectives = new ConnectiveConstraint[1];
		con.connectives[0] = connective;

		m.addLhsItem(p);

		String result = BRDRLPersistence.getInstance().marshal(m);

		String expected = "rule \"test literal strings\" "
				+ "\tdialect \"mvel\"\n when "
				+ "Person( field1 == goo  || == \"blah\" )" + " then " + "end";
		assertEqualsIgnoreWhitespace(expected, result);

	}

	public void testInvalidComposite() throws Exception {
		RuleModel m = new RuleModel();
		CompositeFactPattern com = new CompositeFactPattern("not");
		m.addLhsItem(com);

		String s = BRDRLPersistence.getInstance().marshal(m);
		assertNotNull(s);

		m.addLhsItem(new CompositeFactPattern("or"));
		m.addLhsItem(new CompositeFactPattern("exists"));
		s = BRDRLPersistence.getInstance().marshal(m);
		assertNotNull(s);
	}

	public void testAssertWithDSL() throws Exception {
		RuleModel m = new RuleModel();
		DSLSentence sen = new DSLSentence();
		sen.sentence = "I CAN HAS DSL";
		m.addRhsItem(sen);
		ActionInsertFact ins = new ActionInsertFact("Shizzle");
		ActionFieldValue val = new ActionFieldValue("goo", "42", "Numeric");
		ins.fieldValues = new ActionFieldValue[1];
		ins.fieldValues[0] = val;
		m.addRhsItem(ins);

		ActionInsertLogicalFact insL = new ActionInsertLogicalFact("Shizzle");
		ActionFieldValue valL = new ActionFieldValue("goo", "42", "Numeric");
		insL.fieldValues = new ActionFieldValue[1];
		insL.fieldValues[0] = valL;
		m.addRhsItem(insL);

		String result = BRDRLPersistence.getInstance().marshal(m);
		assertTrue(result.indexOf(">insert") > -1);

		assertTrue(result.indexOf(">insertLogical") > -1);
	}

	public void testDefaultMVEL() {
		RuleModel m = new RuleModel();

		String s = BRDRLPersistence.getInstance().marshal(m);
		assertTrue(s.indexOf("mvel") > -1);

		m.addAttribute(new RuleAttribute("dialect", "goober"));
		s = BRDRLPersistence.getInstance().marshal(m);
		assertFalse(s.indexOf("mvel") > -1);
		assertTrue(s.indexOf("goober") > -1);

	}

	public void testLockOnActive() {
		RuleModel m = new RuleModel();

		m.addAttribute(new RuleAttribute("lock-on-active", "true"));
		m.addAttribute(new RuleAttribute("auto-focus", "true"));
		m.addAttribute(new RuleAttribute("duration", "42"));

		String s = BRDRLPersistence.getInstance().marshal(m);

		assertTrue(s.indexOf("lock-on-active true") > -1);
		assertTrue(s.indexOf("auto-focus true") > -1);
		assertTrue(s.indexOf("duration 42") > -1);

	}


   public void testAddGlobal() {
		String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
				+ "\t\tAccident( )\n\tthen\n\t\tinsert( new Report() );\n\t\tresults.add(f);\nend\n";
		final RuleModel m = new RuleModel();
		m.addLhsItem(new FactPattern("Person"));
		m.addLhsItem(new FactPattern("Accident"));
		m.addAttribute(new RuleAttribute("no-loop", "true"));

		m.addRhsItem(new ActionInsertFact("Report"));
        ActionGlobalCollectionAdd add = new ActionGlobalCollectionAdd();
        add.globalName = "results";
        add.factName = "f";
        m.addRhsItem(add);
		m.name = "my rule";

		final String drl = p.marshal(m);
		assertEquals(expected, drl);
	}

}
