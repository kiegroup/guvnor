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

package org.drools.guvnor.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;

import org.drools.ide.common.client.modeldriven.FieldNature;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;
import org.drools.ide.common.server.util.BRLPersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BRDRTPersistenceTest {
    private static final Logger log = LoggerFactory.getLogger(BRDRTPersistenceTest.class);
    private BRLPersistence p;

    @Before
    public void setUp() throws Exception {
        p = BRDRTPersistence.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        p = null;
    }

    @Test
    public void testGenerateEmptyDRL() {
        String expected =
                "rule \"null_0\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\tthen\n" +
                "end";

        final String drl = p.marshal(new TemplateModel());
        log.info("drl :\n{}", drl);

        assertNotNull(drl);
        assertEquals(expected, drl);
    }

    @Test
    public void testEmptyData() {
        String expected =
                "rule \"with composite_0\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\t$p : Person( name == \"name_na\" )\n" +
                "\tthen\n" +
                "end";
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[0];

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc.setFieldName("name");
        sfc.setValue("name");
        sfc.setOperator("==");

        sfc.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;
        final String drl = p.marshal(m);
        log.info("drl :\n{}", drl);
        
        assertNotNull(drl);
        assertEquals(expected, drl);
    }

    @Test
    public void testFreeFormLine() {
        String expected =
            "rule \"with composite_1\"\n" +
            "\tdialect \"mvel\"\n" +
            "\twhen\n" +
            "\t\t$p : Person( name == \"diegoll\" )\n" +
            "\t\tCheese(type == \"Gouda\", price < 17)\n" +
            "\tthen\n" +
            "\t\tPerson fact0 = new Person();\n" +
            "\t\tfact0.setAge( 87 );\n" +
            "\t\tinsert( fact0 );\n" +
            "end\n" +
            "\n" +
            "rule \"with composite_0\"\n" +
            "\tdialect \"mvel\"\n" +
            "\twhen\n" +
            "\t\t$p : Person( name == \"baunax\" )\n" +
            "\t\tCheese(type == \"Cheddar\", price < 23)\n" +
            "\tthen\n" +
            "\t\tPerson fact0 = new Person();\n" +
            "\t\tfact0.setAge( 34 );\n" +
            "\t\tinsert( fact0 );\n" +
            "end";

        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[2];
        m.rhs = new IAction[1];

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");

        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc.setFieldName("name");
        sfc.setValue("name");
        sfc.setOperator("==");

        sfc.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        fp.addConstraint(sfc);

        m.lhs[0] = fp;

        FreeFormLine ffl = new FreeFormLine();
        ffl.text = "Cheese(type == @{type}, price < @{price})";

        m.lhs[1] = ffl;

        ActionInsertFact aif = new ActionInsertFact("Person");
        ActionFieldValue afv = new ActionFieldValue("age", "age", "");
        afv.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        afv.nature = FieldNature.TYPE_TEMPLATE;

        aif.addFieldValue(afv);
        m.rhs[0] = aif;

        m.addRow(new String[] {"baunax", "\"Cheddar\"", "23", "34"});
        m.addRow(new String[] {"diegoll", "\"Gouda\"", "17", "87"});
        final String drl = p.marshal(m);
        log.info("drl :\n{}", drl);

        assertNotNull(drl);
        assertEquals(expected, drl);
    }

    @Test
    public void testEmptyDataWithRHS() {
        String expected =
                "rule \"with composite_1\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\t$p : Person( name == \"diegoll\" )\n" +
                "\tthen\n" +
                "\t\tPerson fact0 = new Person();\n" +
                "\t\tfact0.setAge( 87 );\n" +
                "\t\tinsert( fact0 );\n" +
                "end\n" +
                "\n" +
                "rule \"with composite_0\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\t$p : Person( name == \"baunax\" )\n" +
                "\tthen\n" +
                "\t\tPerson fact0 = new Person();\n" +
                "\t\tfact0.setAge( 34 );\n" +
                "\t\tinsert( fact0 );\n" +
                "end";

        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[1];

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc.setFieldName("name");
        sfc.setValue("name");
        sfc.setOperator("==");

        sfc.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;
        
        ActionInsertFact aif = new ActionInsertFact("Person");
        ActionFieldValue afv = new ActionFieldValue("age", "age", "");
        afv.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        afv.nature = FieldNature.TYPE_TEMPLATE;
        
        aif.addFieldValue(afv);
        m.rhs[0] = aif;
        
        m.addRow(new String[] {"baunax", "34"});
        m.addRow(new String[] {"diegoll", "87"});
        final String drl = p.marshal(m);
        log.info("drl :\n{}", drl);

        assertNotNull(drl);
        assertEquals(expected, drl);
    }

    @Test
    public void testWithData() {
        String expected =
                "rule \"with composite_1\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\t$p : Person( name == \"diegoll\" )\n" +
                "\tthen\n" +
                "end\n" +
                "\n" +
                "rule \"with composite_0\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\t$p : Person( name == \"baunax\" )\n" +
                "\tthen\n" +
                "end";

        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[0];

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc.setFieldName("name");
        sfc.setValue("name");
        sfc.setOperator("==");

        sfc.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;
        
        m.addRow(new String[] {"baunax"});
        m.addRow(new String[] {"diegoll"});
        
        final String drl = p.marshal(m);
        log.info("drl :\n{}", drl);
        assertNotNull(drl);
        assertEquals(expected, drl);

    }

    @Test
    public void testRemoveWithData() {
        String expected =
                "rule \"with composite_1\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\t$p : Person( name == \"diegoll\" )\n" +
                "\tthen\n" +
                "end\n" +
                "\n" +
                "rule \"with composite_0\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\t$p : Person( name == \"baunax\" )\n" +
                "\tthen\n" +
                "end";

        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[0];

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc.setFieldName("name");
        sfc.setValue("name");
        sfc.setOperator("==");

        sfc.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;
        
        m.addRow(new String[] {"baunax"});
        m.addRow(new String[] {"diegoll"});
        String id1 = m.addRow(new String[] {"diegoll1"});
        String id2 = m.addRow(new String[] {"diegoll2"});
        
        m.removeRowById(id1);
        m.removeRowById(id2);
        
        final String drl = p.marshal(m);
        log.info("drl :\n{}", drl);
        assertNotNull(drl);
        assertEquals(expected, drl);

    }

    @Test
    public void testWithDataAndSync() {
        TemplateModel m = new TemplateModel();
        m.name = "with composite";
        m.lhs = new IPattern[1];
        m.rhs = new IAction[0];

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        
        SingleFieldConstraint sfc = new SingleFieldConstraint("name");
        sfc.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc.setFieldName("name");
        sfc.setValue("name");
        sfc.setOperator("==");
        sfc.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        
        fp.addConstraint(sfc);
        
        sfc = new SingleFieldConstraint("age");
        sfc.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        sfc.setFieldName("age");
        sfc.setValue("age");
        sfc.setOperator("==");
        sfc.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        
        fp.addConstraint(sfc);
        
        m.lhs[0] = fp;

        m.putInSync();
        HashSet<String> expected = new HashSet<String>();
        expected.add("name");
        expected.add("age");
        expected.add(TemplateModel.ID_COLUMN_NAME);
        assertEquals(expected, m.getTable().keySet());
        
        fp.removeConstraint(1);
        m.putInSync();
        
        expected.remove("age");
        expected.add(TemplateModel.ID_COLUMN_NAME);
        assertEquals(expected, m.getTable().keySet());
        
        fp.addConstraint(sfc);
        m.putInSync();
        
        expected.add("age");
        expected.add(TemplateModel.ID_COLUMN_NAME);
        assertEquals(expected, m.getTable().keySet());
        
    }
}
