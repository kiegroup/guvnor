package org.drools.guvnor.server.util;

import java.util.HashSet;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertFact;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FreeFormLine;
import org.drools.guvnor.client.modeldriven.brl.IAction;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.dt.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BRDRTPersistenceTest extends TestCase {
	private static final Logger log = LoggerFactory.getLogger(BRDRTPersistenceTest.class); 
	private BRLPersistence p;

	@Override
	protected void setUp() throws Exception {
		p = BRDRTPersistence.getInstance();
	}

	@Override
	protected void tearDown() throws Exception {
		p = null;
	}
	
	public void testGenerateEmptyDRL() {
		String expected = 
				"rule \"null_0\"\n" + 
				"	dialect \"mvel\"\n" + 
				"	when\n" + 
				"	then\n" + 
				"end";

		final String drl = p.marshal(new TemplateModel());
		log.info("drl :\n{}", drl);

		assertNotNull(drl);
		assertEquals(expected, drl);
	}

	public void testEmptyData() {
		String expected = 
				"rule \"with composite_0\"\n" + 
				"	dialect \"mvel\"\n" + 
				"	when\n" + 
				"		$p : Person( name == \"name_na\" )\n" + 
				"	then\n" + 
				"end";
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[0];

        FactPattern fp = new FactPattern("Person");
        fp.boundName = "$p";
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.fieldName = "name";
        sfc.value = "name";
        sfc.operator = "==";
        	
        sfc.constraintValueType = ISingleFieldConstraint.TYPE_TEMPLATE;
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;
        final String drl = p.marshal(m);
		log.info("drl :\n{}", drl);
        assertNotNull(drl);
        assertEquals(expected, drl);
	}

	public void testFreeFormLine() {
		String expected = 
			"rule \"with composite_1\"\n" + 
			"	dialect \"mvel\"\n" + 
			"	when\n" + 
			"		$p : Person( name == \"diegoll\" )\n" + 
			"		Cheese(type == \"Gouda\", price < 17)\n" + 
			"	then\n" + 
			"		Person fact0 = new Person();\n" + 
			"		fact0.setAge( 87 );\n" + 
			"		insert(fact0 );\n" + 
			"end\n" + 
			"\n" + 
			"rule \"with composite_0\"\n" + 
			"	dialect \"mvel\"\n" + 
			"	when\n" + 
			"		$p : Person( name == \"baunax\" )\n" + 
			"		Cheese(type == \"Cheddar\", price < 23)\n" + 
			"	then\n" + 
			"		Person fact0 = new Person();\n" + 
			"		fact0.setAge( 34 );\n" + 
			"		insert(fact0 );\n" + 
			"end";

		TemplateModel m = new TemplateModel();
		m.name = "with composite";
		m.lhs = new IPattern[2];
		m.rhs = new IAction[1];

		FactPattern fp = new FactPattern("Person");
		fp.boundName = "$p";

		SingleFieldConstraint sfc = new SingleFieldConstraint("name");
		sfc.fieldName = "name";
		sfc.value = "name";
		sfc.operator = "==";

		sfc.constraintValueType = ISingleFieldConstraint.TYPE_TEMPLATE;
		fp.addConstraint(sfc);

		m.lhs[0] = fp;
		
		FreeFormLine ffl = new FreeFormLine();
		ffl.text = "Cheese(type == \"@{type}\", price < @{price})";
		
		m.lhs[1] = ffl;

		ActionInsertFact aif = new ActionInsertFact("Person");
		ActionFieldValue afv = new ActionFieldValue("age", "age", ""); 
		afv.nature = ActionFieldValue.TYPE_TEMPLATE;

		aif.addFieldValue(afv);
		m.rhs[0] = aif;

		m.addRow(new String[] {"baunax", "Cheddar", "23", "34"});
		m.addRow(new String[] {"diegoll", "Gouda", "17", "87"});
		final String drl = p.marshal(m);
		log.info("drl :\n{}", drl);

		assertNotNull(drl);
		assertEquals(expected, drl);
	}

	public void testEmptyDataWithRHS() {
		String expected = 
				"rule \"with composite_1\"\n" + 
				"	dialect \"mvel\"\n" + 
				"	when\n" + 
				"		$p : Person( name == \"diegoll\" )\n" + 
				"	then\n" + 
				"		Person fact0 = new Person();\n" + 
				"		fact0.setAge( 87 );\n" + 
				"		insert(fact0 );\n" + 
				"end\n" + 
				"\n" + 
				"rule \"with composite_0\"\n" + 
				"	dialect \"mvel\"\n" + 
				"	when\n" + 
				"		$p : Person( name == \"baunax\" )\n" + 
				"	then\n" + 
				"		Person fact0 = new Person();\n" + 
				"		fact0.setAge( 34 );\n" + 
				"		insert(fact0 );\n" + 
				"end";

		TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[1];

        FactPattern fp = new FactPattern("Person");
        fp.boundName = "$p";
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.fieldName = "name";
        sfc.value = "name";
        sfc.operator = "==";
        	
        sfc.constraintValueType = ISingleFieldConstraint.TYPE_TEMPLATE;
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;
        
        ActionInsertFact aif = new ActionInsertFact("Person");
        ActionFieldValue afv = new ActionFieldValue("age", "age", ""); 
        afv.nature = ActionFieldValue.TYPE_TEMPLATE;
        
        aif.addFieldValue(afv);
        m.rhs[0] = aif;
        
        m.addRow(new String[] {"baunax", "34"});
        m.addRow(new String[] {"diegoll", "87"});
        final String drl = p.marshal(m);
		log.info("drl :\n{}", drl);
		
        assertNotNull(drl);
        assertEquals(expected, drl);
	}
	
	public void testWithData() {
		String expected = 
				"rule \"with composite_1\"\n" + 
				"	dialect \"mvel\"\n" + 
				"	when\n" + 
				"		$p : Person( name == \"diegoll\" )\n" + 
				"	then\n" + 
				"end\n" + 
				"\n" + 
				"rule \"with composite_0\"\n" + 
				"	dialect \"mvel\"\n" + 
				"	when\n" + 
				"		$p : Person( name == \"baunax\" )\n" + 
				"	then\n" + 
				"end";
		
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[0];

        FactPattern fp = new FactPattern("Person");
        fp.boundName = "$p";
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.fieldName = "name";
        sfc.value = "name";
        sfc.operator = "==";
        	
        sfc.constraintValueType = ISingleFieldConstraint.TYPE_TEMPLATE;
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;
        
        m.addRow(new String[] {"baunax"});
        m.addRow(new String[] {"diegoll"});
        
        final String drl = p.marshal(m);
		log.info("drl :\n{}", drl);
        assertNotNull(drl);
        assertEquals(expected, drl);

	}
	
	public void testWithDataAndSync() {
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[0];

        FactPattern fp = new FactPattern("Person");
        fp.boundName = "$p";
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.fieldName = "name";
        sfc.value = "name";
        sfc.operator = "==";
        sfc.constraintValueType = ISingleFieldConstraint.TYPE_TEMPLATE;
        
        fp.addConstraint(sfc);
        
        sfc = new SingleFieldConstraint("age");
        sfc.fieldName = "age";
        sfc.value = "age";
        sfc.operator = "==";
        sfc.constraintValueType = ISingleFieldConstraint.TYPE_TEMPLATE;
        
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;

        m.putInSync();
        HashSet<String> expected = new HashSet<String>();
        expected.add("name");
        expected.add("age");
        assertEquals(expected, m.getTable().keySet());
        
        fp.removeConstraint(1);
        m.putInSync();
        
        expected.remove("age");
        assertEquals(expected, m.getTable().keySet());
        
        fp.addConstraint(sfc);
        m.putInSync();
        
        expected.add("age");
        assertEquals(expected, m.getTable().keySet());
        
	}
}
