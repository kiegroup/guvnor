/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.ide.common.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionExecuteWorkItem;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionWorkItemFieldValue;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AnalysisCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionColumn;
import org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn;
import org.drools.ide.common.client.modeldriven.dt52.BaseColumn;
import org.drools.ide.common.client.modeldriven.dt52.CompositeColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;
import org.drools.ide.common.server.util.upgrade.GuidedDecisionTableUpgradeHelper1;
import org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableFloatParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableIntegerParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableStringParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;
import org.junit.Test;

public class GuidedDTDRLPersistenceTest {

    private GuidedDecisionTableUpgradeHelper1 upgrader = new GuidedDecisionTableUpgradeHelper1();

    @Test
    public void test2Rules() throws Exception {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        dt.setTableName( "michael" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "f1" );
        p1.setFactType( "Driver" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        p1.getChildColumns().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getChildColumns().add( con3 );

        dt.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "f2" );
        p2.setFactType( "Driver" );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        p2.getChildColumns().add( con4 );

        dt.getConditions().add( p2 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add( ret );

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( "whee" );
        set2.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( upgrader.makeDataLists( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", "f2"},
                new String[]{"2", "desc", "66", "39", "bob", "age * 0.3", "age > 7", "6.60", "", "gooVal1", "whee"}
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "from row number" ) > -1 );
        assertTrue( drl.indexOf( "rating == ( age * 0.2 )" ) > 0 );
        assertTrue( drl.indexOf( "f2 : Driver( eval( age > 7 ))" ) > 0 );
        assertTrue( drl.indexOf( "rating == ( age * 0.3 )" ) > drl.indexOf( "rating == ( age * 0.2 )" ) );
        assertTrue( drl.indexOf( "f1.setGoo2( \"whee\" )" ) > 0 );
        assertTrue( drl.indexOf( "salience 66" ) > 0 );

    }

    @Test
    public void testAttribs() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", ""};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<AttributeCol52> attributeCols = new ArrayList<AttributeCol52>();

        RuleModel rm = new RuleModel();
        RuleAttribute[] orig = rm.attributes;
        p.doAttribs( allColumns,
                     attributeCols,
                     upgrader.makeDataRowList( row ),
                     rm );

        assertSame( orig,
                    rm.attributes );

        AttributeCol52 col1 = new AttributeCol52();
        col1.setAttribute( "salience" );
        AttributeCol52 col2 = new AttributeCol52();
        col2.setAttribute( "agenda-group" );
        attributeCols.add( col1 );
        attributeCols.add( col2 );
        allColumns.addAll( attributeCols );

        p.doAttribs( allColumns,
                     attributeCols,
                     upgrader.makeDataRowList( row ),
                     rm );

        assertEquals( 1,
                      rm.attributes.length );
        assertEquals( "salience",
                      rm.attributes[0].attributeName );
        assertEquals( "a",
                      rm.attributes[0].value );

        row = new String[]{"1", "desc", "a", "b"};
        p.doAttribs( allColumns,
                     attributeCols,
                     upgrader.makeDataRowList( row ),
                     rm );
        assertEquals( 2,
                      rm.attributes.length );
        assertEquals( "salience",
                      rm.attributes[0].attributeName );
        assertEquals( "a",
                      rm.attributes[0].value );
        assertEquals( "agenda-group",
                      rm.attributes[1].attributeName );
        assertEquals( "b",
                      rm.attributes[1].value );

    }

    @Test
    public void testCellCSV() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertEquals( "(\"Michael\", \"Mark\", \"Peter\")",
                      p.makeInList( "Michael, Mark, Peter" ) );
        assertEquals( "(\"Michael\")",
                      p.makeInList( "Michael" ) );
        assertEquals( "(\"Michael\")",
                      p.makeInList( "\"Michael\"" ) );
        assertEquals( "(\"Michael\", \"Ma rk\", \"Peter\")",
                      p.makeInList( "Michael, \"Ma rk\", Peter" ) );
        assertEquals( "(WEE WAAH)",
                      p.makeInList( "(WEE WAAH)" ) );
    }

    @Test
    public void testCellVal() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertFalse( p.validCell( null ) );
        assertFalse( p.validCell( "" ) );
        assertFalse( p.validCell( "  " ) );

    }

    @Test
    public void testInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName( "michael" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "f1" );
        p1.setFactType( "Driver" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "in" );
        p1.getChildColumns().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getChildColumns().add( con3 );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        p1.getChildColumns().add( con4 );

        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add( ret );

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( "whee" );
        set2.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( upgrader.makeDataLists( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael, manik", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", "f2"},
                new String[]{"2", "desc", "", "39", "bob, frank", "age * 0.3", "age > 7", "6.60", "", "gooVal1", null}
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "name in (\"michael\"," ) > 0 );

    }

    @Test
    public void testInterpolate() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName( "michael" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "f1" );
        p1.setFactType( "Driver" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        p1.getChildColumns().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getChildColumns().add( con3 );

        dt.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "f2" );
        p2.setFactType( "Driver" );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "this.hasSomething($param)" );
        p2.getChildColumns().add( con4 );

        dt.getConditions().add( p2 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add( ret );

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( "whee" );
        set2.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( upgrader.makeDataLists( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "BAM", "6.60", "true", "gooVal1", "f2"},
                new String[]{"2", "desc", "66", "39", "bob", "age * 0.3", "BAM", "6.60", "", "gooVal1", "whee"}
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "from row number" ) > -1 );
        assertTrue( drl.indexOf( "rating == ( age * 0.2 )" ) > 0 );
        assertTrue( drl.indexOf( "f2 : Driver( eval( this.hasSomething(BAM) ))" ) > 0 );
        assertTrue( drl.indexOf( "rating == ( age * 0.3 )" ) > drl.indexOf( "rating == ( age * 0.2 )" ) );
        assertTrue( drl.indexOf( "f1.setGoo2( \"whee\" )" ) > 0 );
        assertTrue( drl.indexOf( "salience 66" ) > 0 );
    }

    @Test
    public void testLHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton"};
        String[][] data = new String[1][];
        data[0] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        p1.getChildColumns().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        p1.getChildColumns().add( col3 );
        allColumns.add( col3 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c" );
        p2.setFactType( "Cheese" );
        allPatterns.add( p2 );

        ConditionCol52 col4 = new ConditionCol52();
        col4.setFactField( "type" );
        col4.setOperator( "==" );
        col4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( col4 );
        allColumns.add( col4 );

        RuleModel rm = new RuleModel();

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        upgrader.makeDataLists( data ),
                        rm );
        assertEquals( 2,
                      rm.lhs.length );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Cheese",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "c",
                      ((FactPattern) rm.lhs[1]).getBoundName() );

        // examine the first pattern
        FactPattern person = (FactPattern) rm.lhs[0];
        assertEquals( 3,
                      person.constraintList.constraints.length );
        SingleFieldConstraint cons = (SingleFieldConstraint) person.constraintList.constraints[0];
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
        assertEquals( "name",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "mike",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.constraintList.constraints[1];
        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      cons.getConstraintValueType() );
        assertEquals( "age",
                      cons.getFieldName() );
        assertEquals( "<",
                      cons.getOperator() );
        assertEquals( "33 + 1",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.constraintList.constraints[2];
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      cons.getConstraintValueType() );
        assertEquals( "age > 6",
                      cons.getValue() );

        // examine the second pattern
        FactPattern cheese = (FactPattern) rm.lhs[1];
        assertEquals( 1,
                      cheese.constraintList.constraints.length );
        cons = (SingleFieldConstraint) cheese.constraintList.constraints[0];
        assertEquals( "type",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "stilton",
                      cons.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
    }

    @Test
    public void testLHSBindings() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "mike", "33 + 1", "age > 6"};
        String[][] data = new String[1][];
        data[0] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setOperator( "==" );
        col.setBinding( "$name" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        col2.setBinding( "$name" );
        p1.getChildColumns().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        col3.setBinding( "$name" );
        p1.getChildColumns().add( col3 );
        allColumns.add( col3 );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        upgrader.makeDataLists( data ),
                        rm );
        assertEquals( 1,
                      rm.lhs.length );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        // examine the first pattern
        FactPattern person = (FactPattern) rm.lhs[0];
        assertEquals( 3,
                      person.constraintList.constraints.length );

        SingleFieldConstraint cons = (SingleFieldConstraint) person.constraintList.constraints[0];
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
        assertEquals( "name",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "mike",
                      cons.getValue() );
        assertEquals( "$name",
                      cons.getFieldBinding() );

        cons = (SingleFieldConstraint) person.constraintList.constraints[1];
        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      cons.getConstraintValueType() );
        assertEquals( "age",
                      cons.getFieldName() );
        assertEquals( "<",
                      cons.getOperator() );
        assertEquals( "33 + 1",
                      cons.getValue() );
        assertNull( cons.getFieldBinding() );

        cons = (SingleFieldConstraint) person.constraintList.constraints[2];
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      cons.getConstraintValueType() );
        assertEquals( "age > 6",
                      cons.getValue() );
        assertNull( cons.getFieldBinding() );

    }

    @Test
    public void testLHSNotPattern() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton"};
        String[][] data = new String[1][];
        data[0] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setNegated( true );
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        p1.getChildColumns().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        p1.getChildColumns().add( col3 );
        allColumns.add( col3 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c" );
        p2.setFactType( "Cheese" );
        allPatterns.add( p2 );

        ConditionCol52 col4 = new ConditionCol52();
        col4.setFactField( "type" );
        col4.setOperator( "==" );
        col4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( col4 );
        allColumns.add( col4 );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        upgrader.makeDataLists( data ),
                        rm );

        String drl = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Cheese",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "c",
                      ((FactPattern) rm.lhs[1]).getBoundName() );

        // examine the first pattern
        FactPattern person = (FactPattern) rm.lhs[0];
        assertEquals( 3,
                      person.constraintList.constraints.length );
        SingleFieldConstraint cons = (SingleFieldConstraint) person.constraintList.constraints[0];
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
        assertEquals( "name",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "mike",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.constraintList.constraints[1];
        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      cons.getConstraintValueType() );
        assertEquals( "age",
                      cons.getFieldName() );
        assertEquals( "<",
                      cons.getOperator() );
        assertEquals( "33 + 1",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.constraintList.constraints[2];
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      cons.getConstraintValueType() );
        assertEquals( "age > 6",
                      cons.getValue() );

        assertEquals( person.isNegated(),
                      true );

        assertTrue( drl.indexOf( "not Person(" ) > 0 );

        // examine the second pattern
        FactPattern cheese = (FactPattern) rm.lhs[1];
        assertEquals( 1,
                      cheese.constraintList.constraints.length );
        cons = (SingleFieldConstraint) cheese.constraintList.constraints[0];
        assertEquals( "type",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "stilton",
                      cons.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );

        assertEquals( cheese.isNegated(),
                      false );

        assertTrue( drl.indexOf( "c : Cheese(" ) > 0 );

    }

    @Test
    public void testLHSOtherwisePatternBoolean() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[2][];
        String[][] data = new String[2][];
        row[0] = new String[]{"1", "desc1", "true", "false"};
        List<DTCellValue52> rowDTModel0 = upgrader.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel1 = upgrader.makeDataRowList( row[1] );
        rowDTModel1.get( 2 ).setOtherwise( true );
        rowDTModel1.get( 3 ).setOtherwise( true );
        data[1] = row[1];

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "alive" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "alive" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl0 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( alive == true )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( alive != false )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl1 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( alive not in ( true )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( alive in ( false )" ) > 0 );

    }

    @Test
    public void testLHSOtherwisePatternDate() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[3][];
        String[][] data = new String[3][];
        row[0] = new String[]{"1", "desc1", "01-Jan-1980", "20-Jun-1985"};
        List<DTCellValue52> rowDTModel0 = upgrader.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"2", "desc2", "01-Feb-1981", "21-Jun-1986"};
        List<DTCellValue52> rowDTModel1 = upgrader.makeDataRowList( row[1] );
        data[1] = row[1];

        row[2] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel2 = upgrader.makeDataRowList( row[2] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[2] = row[2];

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "dateOfBirth" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "dateOfBirth" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl0 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( dateOfBirth == \"01-Jan-1980\" )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( dateOfBirth != \"20-Jun-1985\" )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl1 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( dateOfBirth == \"01-Feb-1981\" )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( dateOfBirth != \"21-Jun-1986\" )" ) > 0 );

        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel2 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        rowDTModel2,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl2 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl2.indexOf( "p1 : Person( dateOfBirth not in ( \"01-Jan-1980\", \"01-Feb-1981\" )" ) > 0 );
        assertTrue( drl2.indexOf( "p2 : Person( dateOfBirth in ( \"20-Jun-1985\", \"21-Jun-1986\" )" ) > 0 );

    }

    @Test
    public void testLHSOtherwisePatternNumeric() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[3][];
        String[][] data = new String[3][];
        row[0] = new String[]{"1", "desc1", "1", "1"};
        List<DTCellValue52> rowDTModel0 = upgrader.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"2", "desc2", "2", "2"};
        List<DTCellValue52> rowDTModel1 = upgrader.makeDataRowList( row[1] );
        data[1] = row[1];

        row[2] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel2 = upgrader.makeDataRowList( row[2] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[2] = row[2];

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "age" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl0 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( age == 1 )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( age != 1 )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl1 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( age == 2 )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( age != 2 )" ) > 0 );

        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel2 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        rowDTModel2,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl2 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl2.indexOf( "p1 : Person( age not in ( 1, 2 )" ) > 0 );
        assertTrue( drl2.indexOf( "p2 : Person( age in ( 1, 2 )" ) > 0 );

    }

    @Test
    public void testLHSOtherwisePatternString() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[3][];
        String[][] data = new String[3][];
        row[0] = new String[]{"1", "desc1", "Michael1", "Michael1"};
        List<DTCellValue52> rowDTModel0 = upgrader.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"2", "desc2", "Michael2", "Michael2"};
        List<DTCellValue52> rowDTModel1 = upgrader.makeDataRowList( row[1] );
        data[1] = row[1];

        row[2] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel2 = upgrader.makeDataRowList( row[2] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[2] = row[2];

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "name" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl0 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( name == \"Michael1\" )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( name != \"Michael1\" )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl1 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( name == \"Michael2\" )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( name != \"Michael2\" )" ) > 0 );

        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel2 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        rowDTModel2,
                        upgrader.makeDataLists( data ),
                        rm );
        String drl2 = BRDRLPersistence.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).getBoundName() );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[1]).getFactType() );
        assertEquals( "p2",
                      ((FactPattern) rm.lhs[1]).getBoundName() );
        assertTrue( drl2.indexOf( "p1 : Person( name not in ( \"Michael1\", \"Michael2\" )" ) > 0 );
        assertTrue( drl2.indexOf( "p2 : Person( name in ( \"Michael1\", \"Michael2\" )" ) > 0 );

    }

    @Test
    public void testMetaData() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "bar", ""};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<MetadataCol52> metadataCols = new ArrayList<MetadataCol52>();

        RuleModel rm = new RuleModel();
        RuleMetadata[] orig = rm.metadataList;
        // RuleAttribute[] orig = rm.attributes;
        p.doMetadata( allColumns,
                      metadataCols,
                      upgrader.makeDataRowList( row ),
                      rm );
        // p.doAttribs(allColumns, metadataCols, row, rm);

        assertSame( orig,
                    rm.metadataList );

        MetadataCol52 col1 = new MetadataCol52();
        col1.setMetadata( "foo" );
        MetadataCol52 col2 = new MetadataCol52();
        col2.setMetadata( "foo2" );
        metadataCols.add( col1 );
        metadataCols.add( col2 );
        allColumns.addAll( metadataCols );

        p.doMetadata( allColumns,
                      metadataCols,
                      upgrader.makeDataRowList( row ),
                      rm );
        // p.doAttribs(allColumns, metadataCols, row, rm);

        assertEquals( 1,
                      rm.metadataList.length );
        assertEquals( "foo",
                      rm.metadataList[0].attributeName );
        assertEquals( "bar",
                      rm.metadataList[0].value );

        row = new String[]{"1", "desc", "bar1", "bar2"};
        p.doMetadata( allColumns,
                      metadataCols,
                      upgrader.makeDataRowList( row ),
                      rm );
        assertEquals( 2,
                      rm.metadataList.length );
        assertEquals( "foo",
                      rm.metadataList[0].attributeName );
        assertEquals( "bar1",
                      rm.metadataList[0].value );
        assertEquals( "foo2",
                      rm.metadataList[1].attributeName );
        assertEquals( "bar2",
                      rm.metadataList[1].value );

    }

    @Test
    public void testName() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertEquals( "Row 42 XXX",
                      p.getName( "XXX",
                                 42 ) );
        assertEquals( "Row 42 YYY",
                      p.getName( "YYY",
                                 42 ) );
    }

    @Test
    public void testNoConstraints() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );

        dt.getConditions().add( p1 );

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( SuggestionCompletionEngine.TYPE_STRING );

        dt.getActionCols().add( asf );

        String[][] data = new String[][]{
                new String[]{"1", "desc", "y", "old"}
        };
        dt.setData( upgrader.makeDataLists( data ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "Context( )" ) > -1 );
        assertTrue( drl.indexOf( "x.setAge" ) > drl.indexOf( "Context( )" ) );
        assertFalse( drl.indexOf( "update( x );" ) > -1 );

        dt.setData( upgrader.makeDataLists( new String[][]{
                new String[]{"1", "desc", "", "old"}
            } ) );
        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        assertEquals( -1,
                      drl.indexOf( "Context( )" ) );

    }

    @Test
    public void testNoOperator() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "> 42"};
        String[][] data = new String[1][];
        data[0] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn< ? >> allPatterns = new ArrayList<CompositeColumn< ? >>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col1 = new ConditionCol52();
        col1.setFactField( "age" );
        col1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col1.setOperator( "" );
        p1.getChildColumns().add( col1 );
        allColumns.add( col1 );

        RuleModel rm = new RuleModel();

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        upgrader.makeDataLists( data ),
                        rm );

        String drl = BRDRLPersistence.getInstance().marshal( rm );
        assertTrue( drl.indexOf( "age > \"42\"" ) > 0 );

    }

    @Test
    public void testRHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "a condition", "actionsetfield1", "actionsetfield2", "retract", "actioninsertfact1", "actioninsertfact2"};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );
        allColumns.add( new ConditionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionSetFieldCol52 asf1 = new ActionSetFieldCol52();
        asf1.setBoundName( "a" );
        asf1.setFactField( "field1" );

        asf1.setType( SuggestionCompletionEngine.TYPE_STRING );
        cols.add( asf1 );

        ActionSetFieldCol52 asf2 = new ActionSetFieldCol52();
        asf2.setBoundName( "a" );
        asf2.setFactField( "field2" );
        asf2.setUpdate( true );
        asf2.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        cols.add( asf2 );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        cols.add( ret );

        ActionInsertFactCol52 ins1 = new ActionInsertFactCol52();
        ins1.setBoundName( "ins" );
        ins1.setFactType( "Cheese" );
        ins1.setFactField( "price" );
        ins1.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        cols.add( ins1 );

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setBoundName( "ins" );
        ins2.setFactType( "Cheese" );
        ins2.setFactField( "type" );
        ins2.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        cols.add( ins2 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 3,
                      rm.rhs.length );

        // examine the set field action that is produced
        ActionSetField a1 = (ActionSetField) rm.rhs[0];
        assertEquals( "a",
                      a1.variable );
        assertEquals( 2,
                      a1.fieldValues.length );

        assertEquals( "field1",
                      a1.fieldValues[0].field );
        assertEquals( "actionsetfield1",
                      a1.fieldValues[0].value );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      a1.fieldValues[0].type );

        assertEquals( "field2",
                      a1.fieldValues[1].field );
        assertEquals( "actionsetfield2",
                      a1.fieldValues[1].value );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                      a1.fieldValues[1].type );

        // examine the retract
        ActionRetractFact a2 = (ActionRetractFact) rm.rhs[1];
        assertEquals( "retract",
                      a2.variableName );

        // examine the insert
        ActionInsertFact a3 = (ActionInsertFact) rm.rhs[2];
        assertEquals( "Cheese",
                      a3.factType );
        assertEquals( 2,
                      a3.fieldValues.length );

        assertEquals( "price",
                      a3.fieldValues[0].field );
        assertEquals( "actioninsertfact1",
                      a3.fieldValues[0].value );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                      a3.fieldValues[0].type );

        assertEquals( "type",
                      a3.fieldValues[1].field );
        assertEquals( "actioninsertfact2",
                      a3.fieldValues[1].value );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                      a3.fieldValues[1].type );

    }

    @Test
    public void testUpdateModify() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        asf.setUpdate( true );

        dt.getActionCols().add( asf );

        String[][] data = new String[][]{
                new String[]{"1", "desc", "y", "old"}
        };
        dt.setData( upgrader.makeDataLists( data ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "Context( )" ) > -1 );
        assertTrue( drl.indexOf( "x.setAge" ) > drl.indexOf( "Context( )" ) );

        dt.setData( upgrader.makeDataLists( new String[][]{
                new String[]{"1", "desc", "", "old"}
            } ) );
        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        assertEquals( -1,
                      drl.indexOf( "Context( )" ) );

        assertTrue( drl.indexOf( "update( x );" ) > -1 );

    }

    @Test
    public void testDefaultValue() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "$c" );
        p1.setFactType( "CheeseLover" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c.setFactField( "favouriteCheese" );
        c.setDefaultValue( "cheddar" );
        c.setOperator( "==" );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        //With provided value
        String[][] data = new String[][]{
                new String[]{"1", "desc", "edam"},
        };
        dt.setData( upgrader.makeDataLists( data ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertFalse( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"edam\" )" ) == -1 );

        //Without provided value #1
        data = new String[][]{
                new String[]{"1", "desc", null},
        };
        dt.setData( upgrader.makeDataLists( data ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"cheddar\" )" ) == -1 );

        //Without provided value #2
        data = new String[][]{
                new String[]{"1", "desc", ""},
        };
        dt.setData( upgrader.makeDataLists( data ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"cheddar\" )" ) == -1 );

    }

    @Test
    public void testLimitedEntryAttributes() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        dt.getAttributeCols().add( attr );

        dt.setData( upgrader.makeDataLists( new String[][]{
                                                           new String[]{"1", "desc", "100"},
                                                           new String[]{"2", "desc", "200"}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "salience 100" ) > -1 );
        assertTrue( drl.indexOf( "salience 200" ) > -1 );

    }

    @Test
    public void testLimitedEntryMetadata() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        MetadataCol52 md = new MetadataCol52();
        md.setMetadata( "metadata" );
        dt.getMetadataCols().add( md );

        dt.setData( upgrader.makeDataLists( new String[][]{
                                                           new String[]{"1", "desc", "md1"},
                                                           new String[]{"2", "desc", "md2"}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "@metadata(md1)" ) > -1 );
        assertTrue( drl.indexOf( "@metadata(md2)" ) > -1 );

    }

    @Test
    public void testLimitedEntryConditionsNoConstraints() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        // This is a hack consistent with how the Expanded Form decision table 
        // works. I wouldn't be too surprised if this changes at some time, but 
        // GuidedDTDRLPersistence.marshal does not support empty patterns at
        // present.
        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setValue( new DTCellValue52( "y" ) );
        p1.getChildColumns().add( cc1 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true},
                                                           new Object[]{2l, "desc", false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );

    }

    @Test
    public void testLimitedEntryConditionsConstraints1() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "==" );
        cc1.setValue( new DTCellValue52( "Pupa" ) );
        p1.getChildColumns().add( cc1 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true},
                                                           new Object[]{2l, "desc", false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == \"Pupa\" )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( name == \"Pupa\" )",
                             index + 1 );
        assertFalse( index > -1 );

    }

    @Test
    public void testLimitedEntryConditionsConstraints2() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "==" );
        cc1.setValue( new DTCellValue52( "Pupa" ) );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc2.setFactField( "name" );
        cc2.setOperator( "==" );
        cc2.setValue( new DTCellValue52( "Smurfette" ) );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc3.setFactField( "colour" );
        cc3.setOperator( "==" );
        cc3.setValue( new DTCellValue52( "Blue" ) );
        p1.getChildColumns().add( cc3 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true, false, true},
                                                           new Object[]{2l, "desc", false, true, true},
                                                           new Object[]{3l, "desc", false, false, true}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == \"Pupa\" , colour == \"Blue\" )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( name == \"Smurfette\" , colour == \"Blue\" )",
                             index + 1 );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( colour == \"Blue\" )",
                             index + 1 );
        assertTrue( index > -1 );

    }

    @Test
    public void testLimitedEntryActionSet() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        cc1.setFactField( "isSmurf" );
        cc1.setOperator( "==" );
        cc1.setValue( new DTCellValue52( "true" ) );
        p1.getChildColumns().add( cc1 );

        LimitedEntryActionSetFieldCol52 asf1 = new LimitedEntryActionSetFieldCol52();
        asf1.setBoundName( "p1" );
        asf1.setFactField( "colour" );
        asf1.setValue( new DTCellValue52( "Blue" ) );

        dt.getActionCols().add( asf1 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true, true},
                                                           new Object[]{2l, "desc", true, false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( isSmurf == true )" );
        assertTrue( index > -1 );
        index = drl.indexOf( "p1.setColour( \"Blue\" )",
                             index + 1 );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( isSmurf == true )",
                             index + 1 );
        assertTrue( index > -1 );
        index = drl.indexOf( "p1.setColour( \"Blue\" )",
                             index + 1 );
        assertFalse( index > -1 );

    }

    @Test
    public void testLimitedEntryActionInsert() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryActionInsertFactCol52 asf1 = new LimitedEntryActionInsertFactCol52();
        asf1.setFactType( "Smurf" );
        asf1.setBoundName( "s1" );
        asf1.setFactField( "colour" );
        asf1.setValue( new DTCellValue52( "Blue" ) );

        dt.getActionCols().add( asf1 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true},
                                                           new Object[]{2l, "desc", false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf s1 = new Smurf();" );
        assertTrue( index > -1 );
        index = drl.indexOf( "s1.setColour( \"Blue\" );",
                             index + 1 );
        assertTrue( index > -1 );
        index = drl.indexOf( "insert( s1 );",
                             index + 1 );
        assertTrue( index > -1 );

        int indexRule2 = index;
        indexRule2 = drl.indexOf( "Smurf s1 = new Smurf();",
                                  index + 1 );
        assertFalse( indexRule2 > -1 );
        indexRule2 = drl.indexOf( "s1.setColour( \"Blue\" );",
                                  index + 1 );
        assertFalse( indexRule2 > -1 );
        indexRule2 = drl.indexOf( "insert(s1 );",
                                  index + 1 );
        assertFalse( indexRule2 > -1 );
    }

    @Test
    public void testLHSIsNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "== null" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "== null" );
        p1.getChildColumns().add( cc2 );

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "== null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true, true, true},
                                                           new Object[]{2l, "desc", false, false, false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == null , age == null , dateOfBirth == null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLHSIsNotNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "!= null" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "!= null" );
        p1.getChildColumns().add( cc2 );

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "!= null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true, true, true},
                                                           new Object[]{2l, "desc", false, false, false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name != null , age != null , dateOfBirth != null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSIsNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "== null" );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "== null" );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "== null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true, true, true},
                                                           new Object[]{2l, "desc", false, false, false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == null , age == null , dateOfBirth == null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSIsNotNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "!= null" );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "!= null" );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( SuggestionCompletionEngine.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "!= null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( upgrader.makeDataLists( new Object[][]{
                                                           new Object[]{1l, "desc", true, true, true},
                                                           new Object[]{2l, "desc", false, false, false}
                                                           } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name != null , age != null , dateOfBirth != null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testRHSExecuteWorkItem() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "true"};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "work-item" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setValue( Boolean.TRUE );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setValue( 123.456f );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setValue( 123 );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setValue( "hello" );
        pwd.addParameter( p4 );

        cols.add( awi );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 1,
                      rm.rhs.length );

        //Examine RuleModel action
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[0];
        assertNotNull( aw );

        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getParameters().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getParameter( "BooleanParameter" );
        assertNotNull( mp1 );
        assertEquals( Boolean.TRUE,
                      mp1.getValue() );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getParameter( "FloatParameter" );
        assertNotNull( mp2 );
        assertEquals( new Float( 123.456f ),
                      mp2.getValue() );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getParameter( "IntegerParameter" );
        assertNotNull( mp3 );
        assertEquals( new Integer( 123 ),
                      mp3.getValue() );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getParameter( "StringParameter" );
        assertNotNull( mp4 );
        assertEquals( "hello",
                      mp4.getValue() );

    }

    @Test
    public void testRHSExecuteWorkItemWithBindings() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "true"};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "work-item" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setValue( Boolean.TRUE );
        p1.setBinding( "$b" );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setValue( 123.456f );
        p2.setBinding( "$f" );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setValue( 123 );
        p3.setBinding( "$i" );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setValue( "hello" );
        p4.setBinding( "$s" );
        pwd.addParameter( p4 );

        cols.add( awi );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 1,
                      rm.rhs.length );

        //Examine RuleModel action
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[0];
        assertNotNull( aw );

        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getParameters().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getParameter( "BooleanParameter" );
        assertNotNull( mp1 );
        assertEquals( Boolean.TRUE,
                      mp1.getValue() );
        assertEquals( "$b",
                      mp1.getBinding() );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getParameter( "FloatParameter" );
        assertNotNull( mp2 );
        assertEquals( new Float( 123.456f ),
                      mp2.getValue() );
        assertEquals( "$f",
                      mp2.getBinding() );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getParameter( "IntegerParameter" );
        assertNotNull( mp3 );
        assertEquals( new Integer( 123 ),
                      mp3.getValue() );
        assertEquals( "$i",
                      mp3.getBinding() );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getParameter( "StringParameter" );
        assertNotNull( mp4 );
        assertEquals( "hello",
                      mp4.getValue() );
        assertEquals( "$s",
                      mp4.getBinding() );

    }

    @Test
    //Test all Actions setting fields are correctly converted to RuleModel
    public void testRHSActionWorkItemSetFields1() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "true", "true", "true", "true", "true"};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerResult" );
        pwd.addResult( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringResult" );
        pwd.addResult( p4 );

        cols.add( awi );

        ActionWorkItemSetFieldCol52 asf1 = new ActionWorkItemSetFieldCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemSetFieldCol52 asf2 = new ActionWorkItemSetFieldCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        ActionWorkItemSetFieldCol52 asf3 = new ActionWorkItemSetFieldCol52();
        asf3.setBoundName( "$r" );
        asf3.setFactField( "ResultIntegerField" );
        asf3.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        asf3.setWorkItemName( "WorkItem" );
        asf3.setWorkItemResultParameterName( "IntegerResult" );
        asf3.setParameterClassName( Integer.class.getName() );
        cols.add( asf3 );

        ActionWorkItemSetFieldCol52 asf4 = new ActionWorkItemSetFieldCol52();
        asf4.setBoundName( "$r" );
        asf4.setFactField( "ResultStringField" );
        asf4.setType( SuggestionCompletionEngine.TYPE_STRING );
        asf4.setWorkItemName( "WorkItem" );
        asf4.setWorkItemResultParameterName( "StringResult" );
        asf4.setParameterClassName( String.class.getName() );
        cols.add( asf4 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[0];
        assertNotNull( aw );

        ActionSetField asf = (ActionSetField) rm.rhs[1];
        assertNotNull( asf );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getResult( "IntegerResult" );
        assertNotNull( mp3 );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getResult( "StringResult" );
        assertNotNull( mp4 );

        //Check ActionSetField
        assertEquals( asf.variable,
                      "$r" );
        assertEquals( 4,
                      asf.fieldValues.length );

        ActionFieldValue fv1 = asf.fieldValues[0];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.field );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      wifv1.type );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

        ActionFieldValue fv2 = asf.fieldValues[1];
        assertNotNull( fv2 );
        assertTrue( fv2 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv2 = (ActionWorkItemFieldValue) fv2;
        assertEquals( "ResultFloatField",
                      wifv2.field );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT,
                      wifv2.type );
        assertEquals( "WorkItem",
                      wifv2.getWorkItemName() );
        assertEquals( "FloatResult",
                      wifv2.getWorkItemParameterName() );
        assertEquals( Float.class.getName(),
                      wifv2.getWorkItemParameterClassName() );

        ActionFieldValue fv3 = asf.fieldValues[2];
        assertNotNull( fv3 );
        assertTrue( fv3 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv3 = (ActionWorkItemFieldValue) fv3;
        assertEquals( "ResultIntegerField",
                      wifv3.field );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                      wifv3.type );
        assertEquals( "WorkItem",
                      wifv3.getWorkItemName() );
        assertEquals( "IntegerResult",
                      wifv3.getWorkItemParameterName() );
        assertEquals( Integer.class.getName(),
                      wifv3.getWorkItemParameterClassName() );

        ActionFieldValue fv4 = asf.fieldValues[3];
        assertNotNull( fv4 );
        assertTrue( fv4 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv4 = (ActionWorkItemFieldValue) fv4;
        assertEquals( "ResultStringField",
                      wifv4.field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      wifv4.type );
        assertEquals( "WorkItem",
                      wifv4.getWorkItemName() );
        assertEquals( "StringResult",
                      wifv4.getWorkItemParameterName() );
        assertEquals( String.class.getName(),
                      wifv4.getWorkItemParameterClassName() );

    }

    @Test
    //Test only Actions set to "true" are correctly converted to RuleModel
    public void testRHSActionWorkItemSetFields2() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "true", "true", "false"};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        cols.add( awi );

        ActionWorkItemSetFieldCol52 asf1 = new ActionWorkItemSetFieldCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemSetFieldCol52 asf2 = new ActionWorkItemSetFieldCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[0];
        assertNotNull( aw );

        ActionSetField asf = (ActionSetField) rm.rhs[1];
        assertNotNull( asf );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 2,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        //Check ActionSetField
        assertEquals( asf.variable,
                      "$r" );
        assertEquals( 1,
                      asf.fieldValues.length );

        ActionFieldValue fv1 = asf.fieldValues[0];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.field );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      wifv1.type );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

    }

    @Test
    //Test all Actions inserting Facts are correctly converted to RuleModel
    public void testRHSActionWorkItemInsertFacts1() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "true", "true", "true", "true", "true"};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerResult" );
        pwd.addResult( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringResult" );
        pwd.addResult( p4 );

        cols.add( awi );

        ActionWorkItemInsertFactCol52 asf1 = new ActionWorkItemInsertFactCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemInsertFactCol52 asf2 = new ActionWorkItemInsertFactCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        ActionWorkItemInsertFactCol52 asf3 = new ActionWorkItemInsertFactCol52();
        asf3.setBoundName( "$r" );
        asf3.setFactField( "ResultIntegerField" );
        asf3.setType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        asf3.setWorkItemName( "WorkItem" );
        asf3.setWorkItemResultParameterName( "IntegerResult" );
        asf3.setParameterClassName( Integer.class.getName() );
        cols.add( asf3 );

        ActionWorkItemInsertFactCol52 asf4 = new ActionWorkItemInsertFactCol52();
        asf4.setBoundName( "$r" );
        asf4.setFactField( "ResultStringField" );
        asf4.setType( SuggestionCompletionEngine.TYPE_STRING );
        asf4.setWorkItemName( "WorkItem" );
        asf4.setWorkItemResultParameterName( "StringResult" );
        asf4.setParameterClassName( String.class.getName() );
        cols.add( asf4 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[0];
        assertNotNull( aw );

        ActionInsertFact aif = (ActionInsertFact) rm.rhs[1];
        assertNotNull( aif );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getResult( "IntegerResult" );
        assertNotNull( mp3 );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getResult( "StringResult" );
        assertNotNull( mp4 );

        //Check ActionInsertFact
        assertEquals( aif.getBoundName(),
                      "$r" );
        assertEquals( 4,
                      aif.fieldValues.length );

        ActionFieldValue fv1 = aif.fieldValues[0];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.field );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      wifv1.type );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

        ActionFieldValue fv2 = aif.fieldValues[1];
        assertNotNull( fv2 );
        assertTrue( fv2 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv2 = (ActionWorkItemFieldValue) fv2;
        assertEquals( "ResultFloatField",
                      wifv2.field );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT,
                      wifv2.type );
        assertEquals( "WorkItem",
                      wifv2.getWorkItemName() );
        assertEquals( "FloatResult",
                      wifv2.getWorkItemParameterName() );
        assertEquals( Float.class.getName(),
                      wifv2.getWorkItemParameterClassName() );

        ActionFieldValue fv3 = aif.fieldValues[2];
        assertNotNull( fv3 );
        assertTrue( fv3 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv3 = (ActionWorkItemFieldValue) fv3;
        assertEquals( "ResultIntegerField",
                      wifv3.field );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                      wifv3.type );
        assertEquals( "WorkItem",
                      wifv3.getWorkItemName() );
        assertEquals( "IntegerResult",
                      wifv3.getWorkItemParameterName() );
        assertEquals( Integer.class.getName(),
                      wifv3.getWorkItemParameterClassName() );

        ActionFieldValue fv4 = aif.fieldValues[3];
        assertNotNull( fv4 );
        assertTrue( fv4 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv4 = (ActionWorkItemFieldValue) fv4;
        assertEquals( "ResultStringField",
                      wifv4.field );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      wifv4.type );
        assertEquals( "WorkItem",
                      wifv4.getWorkItemName() );
        assertEquals( "StringResult",
                      wifv4.getWorkItemParameterName() );
        assertEquals( String.class.getName(),
                      wifv4.getWorkItemParameterClassName() );

    }

    @Test
    //Test only Actions set to "true" are correctly converted to RuleModel
    public void testRHSActionWorkItemInsertFacts2() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "true", "true", "false"};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        cols.add( awi );

        ActionWorkItemInsertFactCol52 asf1 = new ActionWorkItemInsertFactCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( SuggestionCompletionEngine.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemInsertFactCol52 asf2 = new ActionWorkItemInsertFactCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        //When using a TemplateDataProvider the assumption is that we 
        //have a "complete" decision table including AnalysisCol52
        allColumns.add( new AnalysisCol52() );

        List<DTCellValue52> rowData = upgrader.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[0];
        assertNotNull( aw );

        ActionInsertFact aif = (ActionInsertFact) rm.rhs[1];
        assertNotNull( aif );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 2,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        //Check ActionInsertFact
        assertEquals( aif.getBoundName(),
                      "$r" );
        assertEquals( 1,
                      aif.fieldValues.length );

        ActionFieldValue fv1 = aif.fieldValues[0];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.field );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      wifv1.type );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into a RuleModel
    public void testLHSWithBRLColumn_ParseToRuleModel() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Gargamel", "Pupa", "50"},
                new String[]{"2", "desc", "Gargamel", "", "50"},
                new String[]{"3", "desc", "Gargamel", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Condition
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Baddie" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "name" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        dtable.getConditions().add( p1 );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Smurf" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "$name" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        brl1DefinitionFactPattern1Constraint2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint2.setFieldName( "age" );
        brl1DefinitionFactPattern1Constraint2.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint2.setValue( "$age" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint2 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "$name",
                                                                                   SuggestionCompletionEngine.TYPE_STRING,
                                                                                   "Person",
                                                                                   "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn( "$age",
                                                                                   SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                                                                                   "Person",
                                                                                   "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getConditions().add( brl1 );

        //Now to test conversion
        RuleModel rm = new RuleModel();
        List<BaseColumn> allColumns = dtable.getExpandedColumns();
        List<CompositeColumn< ? >> allPatterns = dtable.getConditions();
        List<List<DTCellValue52>> dtData = upgrader.makeDataLists( data );

        //Row 0
        List<DTCellValue52> dtRowData0 = upgrader.makeDataRowList( data[0] );
        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData0 );
        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        dtRowData0,
                        dtData,
                        rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Baddie",
                      ((FactPattern) rm.lhs[0]).getFactType() );
        assertEquals( "Smurf",
                      ((FactPattern) rm.lhs[1]).getFactType() );

        // examine the first pattern
        FactPattern result0Fp1 = (FactPattern) rm.lhs[0];
        assertEquals( 1,
                      result0Fp1.constraintList.constraints.length );

        SingleFieldConstraint result0Fp1Con1 = (SingleFieldConstraint) result0Fp1.constraintList.constraints[0];
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      result0Fp1Con1.getConstraintValueType() );
        assertEquals( "name",
                      result0Fp1Con1.getFieldName() );
        assertEquals( "==",
                      result0Fp1Con1.getOperator() );
        assertEquals( "Gargamel",
                      result0Fp1Con1.getValue() );

        // examine the second pattern
        FactPattern result0Fp2 = (FactPattern) rm.lhs[1];
        assertEquals( 2,
                      result0Fp2.constraintList.constraints.length );

        SingleFieldConstraint result0Fp2Con1 = (SingleFieldConstraint) result0Fp2.constraintList.constraints[0];
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result0Fp2Con1.getConstraintValueType() );
        assertEquals( "name",
                      result0Fp2Con1.getFieldName() );
        assertEquals( "==",
                      result0Fp2Con1.getOperator() );
        assertEquals( "$name",
                      result0Fp2Con1.getValue() );

        SingleFieldConstraint result0Fp2Con2 = (SingleFieldConstraint) result0Fp2.constraintList.constraints[1];
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result0Fp2Con2.getConstraintValueType() );
        assertEquals( "age",
                      result0Fp2Con2.getFieldName() );
        assertEquals( "==",
                      result0Fp2Con2.getOperator() );
        assertEquals( "$age",
                      result0Fp2Con2.getValue() );

        //Row 1
        List<DTCellValue52> dtRowData1 = upgrader.makeDataRowList( data[1] );
        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData1 );
        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        dtRowData1,
                        dtData,
                        rm );

        assertEquals( 1,
                      rm.lhs.length );
        assertEquals( "Baddie",
                      ((FactPattern) rm.lhs[0]).getFactType() );

        // examine the first pattern
        FactPattern result1Fp1 = (FactPattern) rm.lhs[0];
        assertEquals( 1,
                      result1Fp1.constraintList.constraints.length );

        SingleFieldConstraint result1Fp1Con1 = (SingleFieldConstraint) result1Fp1.constraintList.constraints[0];
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      result1Fp1Con1.getConstraintValueType() );
        assertEquals( "name",
                      result1Fp1Con1.getFieldName() );
        assertEquals( "==",
                      result1Fp1Con1.getOperator() );
        assertEquals( "Gargamel",
                      result1Fp1Con1.getValue() );

        //Row 2
        List<DTCellValue52> dtRowData2 = upgrader.makeDataRowList( data[2] );
        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData2 );
        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        dtRowData2,
                        dtData,
                        rm );

        assertEquals( 1,
                      rm.lhs.length );
        assertEquals( "Baddie",
                      ((FactPattern) rm.lhs[0]).getFactType() );

        // examine the first pattern
        FactPattern result2Fp1 = (FactPattern) rm.lhs[0];
        assertEquals( 1,
                      result2Fp1.constraintList.constraints.length );

        SingleFieldConstraint result2Fp1Con1 = (SingleFieldConstraint) result2Fp1.constraintList.constraints[0];
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      result2Fp1Con1.getConstraintValueType() );
        assertEquals( "name",
                      result2Fp1Con1.getFieldName() );
        assertEquals( "==",
                      result2Fp1Con1.getOperator() );
        assertEquals( "Gargamel",
                      result2Fp1Con1.getValue() );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Gargamel", "Pupa", "50"},
                new String[]{"2", "desc", "Gargamel", "", "50"},
                new String[]{"3", "desc", "Gargamel", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Condition
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Baddie" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "name" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        dtable.getConditions().add( p1 );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Smurf" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "$name" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        brl1DefinitionFactPattern1Constraint2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint2.setFieldName( "age" );
        brl1DefinitionFactPattern1Constraint2.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint2.setValue( "$age" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint2 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "$name",
                                                                                   SuggestionCompletionEngine.TYPE_STRING,
                                                                                   "Person",
                                                                                   "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn( "$age",
                                                                                   SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                                                                                   "Person",
                                                                                   "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getConditions().add( brl1 );
        dtable.setData( upgrader.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        int pattern2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "#from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "#from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertTrue( pattern2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "#from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertTrue( pattern2StartIndex == -1 );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_MultiplePatterns() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Pupa", "50"},
                new String[]{"2", "desc", "", "50"},
                new String[]{"3", "desc", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Baddie" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "Gargamel" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        FactPattern brl1DefinitionFactPattern2 = new FactPattern( "Smurf" );

        SingleFieldConstraint brl1DefinitionFactPattern2Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern2Constraint1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionFactPattern2Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern2Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern2Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern2Constraint1.setValue( "$name" );
        brl1DefinitionFactPattern2.addConstraint( brl1DefinitionFactPattern2Constraint1 );

        SingleFieldConstraint brl1DefinitionFactPattern2Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern2Constraint2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        brl1DefinitionFactPattern2Constraint2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern2Constraint2.setFieldName( "age" );
        brl1DefinitionFactPattern2Constraint2.setOperator( "==" );
        brl1DefinitionFactPattern2Constraint2.setValue( "$age" );
        brl1DefinitionFactPattern2.addConstraint( brl1DefinitionFactPattern2Constraint2 );

        brl1Definition.add( brl1DefinitionFactPattern2 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "$name",
                                                                                   SuggestionCompletionEngine.TYPE_STRING,
                                                                                   "Person",
                                                                                   "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn( "$age",
                                                                                   SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                                                                                   "Person",
                                                                                   "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getConditions().add( brl1 );
        dtable.setData( upgrader.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        int pattern2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "#from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "#from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertTrue( pattern2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "#from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertTrue( pattern2StartIndex == -1 );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_NoVariables() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        Object[][] data = new Object[][]{
                new Object[]{"1", "desc", Boolean.TRUE},
                new Object[]{"2", "desc", Boolean.FALSE}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Baddie" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "Gargamel" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "",
                                                                                   SuggestionCompletionEngine.TYPE_BOOLEAN );
        brl1.getChildColumns().add( brl1Variable1 );

        dtable.getConditions().add( brl1 );
        dtable.setData( upgrader.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "#from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "#from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into a RuleModel
    public void testRHSWithBRLColumn_ParseToRuleModel() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Gargamel", "Pupa", "50"},
                new String[]{"2", "desc", "Gargamel", "", "50"},
                new String[]{"3", "desc", "Gargamel", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Action
        ActionInsertFactCol52 a1 = new ActionInsertFactCol52();
        a1.setBoundName( "$b" );
        a1.setFactType( "Baddie" );
        a1.setFactField( "name" );
        a1.setType( SuggestionCompletionEngine.TYPE_STRING );

        dtable.getActionCols().add( a1 );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Smurf" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "$name",
                                                                                  SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        ActionFieldValue brl1DefinitionAction1FieldValue2 = new ActionFieldValue( "age",
                                                                                  "$age",
                                                                                  SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        brl1DefinitionAction1FieldValue2.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue2 );
        brl1Definition.add( brl1DefinitionAction1 );
        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "$name",
                                                                             SuggestionCompletionEngine.TYPE_STRING,
                                                                             "Person",
                                                                             "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn( "$age",
                                                                             SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                                                                             "Person",
                                                                             "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getActionCols().add( brl1 );

        //Now to test conversion
        RuleModel rm = new RuleModel();
        List<BaseColumn> allColumns = dtable.getExpandedColumns();
        List<ActionCol52> allActions = dtable.getActionCols();

        //Row 0
        List<DTCellValue52> dtRowData0 = upgrader.makeDataRowList( data[0] );
        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData0 );
        p.doActions( allColumns,
                     allActions,
                     rowDataProvider0,
                     dtRowData0,
                     rm );

        assertEquals( 2,
                      rm.rhs.length );
        assertEquals( "Baddie",
                      ((ActionInsertFact) rm.rhs[0]).factType );
        assertEquals( "Smurf",
                      ((ActionInsertFact) rm.rhs[1]).factType );

        // examine the first action
        ActionInsertFact result0Action1 = (ActionInsertFact) rm.rhs[0];
        assertEquals( 1,
                      result0Action1.fieldValues.length );

        ActionFieldValue result0Action1FieldValue1 = (ActionFieldValue) result0Action1.fieldValues[0];
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      result0Action1FieldValue1.type );
        assertEquals( "name",
                      result0Action1FieldValue1.field );
        assertEquals( "Gargamel",
                      result0Action1FieldValue1.value );

        // examine the second action
        ActionInsertFact result0Action2 = (ActionInsertFact) rm.rhs[1];
        assertEquals( 2,
                      result0Action2.fieldValues.length );

        ActionFieldValue result0Action2FieldValue1 = (ActionFieldValue) result0Action2.fieldValues[0];
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      result0Action2FieldValue1.type );
        assertEquals( "name",
                      result0Action2FieldValue1.field );
        assertEquals( "$name",
                      result0Action2FieldValue1.value );

        ActionFieldValue result0Action2FieldValue2 = (ActionFieldValue) result0Action2.fieldValues[1];
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                      result0Action2FieldValue2.type );
        assertEquals( "age",
                      result0Action2FieldValue2.field );
        assertEquals( "$age",
                      result0Action2FieldValue2.value );

        //Row 1
        List<DTCellValue52> dtRowData1 = upgrader.makeDataRowList( data[1] );
        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData1 );
        p.doActions( allColumns,
                     allActions,
                     rowDataProvider1,
                     dtRowData1,
                     rm );

        assertEquals( 1,
                      rm.rhs.length );
        assertEquals( "Baddie",
                      ((ActionInsertFact) rm.rhs[0]).factType );

        // examine the first action
        ActionInsertFact result1Action1 = (ActionInsertFact) rm.rhs[0];
        assertEquals( 1,
                      result1Action1.fieldValues.length );

        ActionFieldValue result1Action1FieldValue1 = (ActionFieldValue) result1Action1.fieldValues[0];
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      result1Action1FieldValue1.type );
        assertEquals( "name",
                      result1Action1FieldValue1.field );
        assertEquals( "Gargamel",
                      result1Action1FieldValue1.value );

        //Row 2
        List<DTCellValue52> dtRowData2 = upgrader.makeDataRowList( data[2] );
        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData2 );
        p.doActions( allColumns,
                     allActions,
                     rowDataProvider2,
                     dtRowData2,
                     rm );

        assertEquals( 1,
                      rm.rhs.length );
        assertEquals( "Baddie",
                      ((ActionInsertFact) rm.rhs[0]).factType );

        // examine the first action
        ActionInsertFact result2Action1 = (ActionInsertFact) rm.rhs[0];
        assertEquals( 1,
                      result2Action1.fieldValues.length );

        ActionFieldValue result2Action1FieldValue1 = (ActionFieldValue) result2Action1.fieldValues[0];
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      result2Action1FieldValue1.type );
        assertEquals( "name",
                      result2Action1FieldValue1.field );
        assertEquals( "Gargamel",
                      result2Action1FieldValue1.value );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Gargamel", "Pupa", "50"},
                new String[]{"2", "desc", "Gargamel", "", "50"},
                new String[]{"3", "desc", "Gargamel", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Action
        ActionInsertFactCol52 a1 = new ActionInsertFactCol52();
        a1.setBoundName( "$b" );
        a1.setFactType( "Baddie" );
        a1.setFactField( "name" );
        a1.setType( SuggestionCompletionEngine.TYPE_STRING );

        dtable.getActionCols().add( a1 );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Smurf" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "$name",
                                                                                  SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        ActionFieldValue brl1DefinitionAction1FieldValue2 = new ActionFieldValue( "age",
                                                                                  "$age",
                                                                                  SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        brl1DefinitionAction1FieldValue2.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue2 );
        brl1Definition.add( brl1DefinitionAction1 );
        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "$name",
                                                                             SuggestionCompletionEngine.TYPE_STRING,
                                                                             "Person",
                                                                             "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn( "$age",
                                                                             SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                                                                             "Person",
                                                                             "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getActionCols().add( brl1 );
        dtable.setData( upgrader.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        int action2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "#from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie $b = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "$b.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( $b );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact0 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setName( \"Pupa\" );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setAge( 50 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact0 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "#from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie $b = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "$b.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( $b );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact0 = new Smurf();",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setName( \"Pupa\" );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setAge( 50 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact0 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "#from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie $b = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "$b.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( $b );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact0 = new Smurf();",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setName( \"Pupa\" );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setAge( 50 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact0 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_MultipleActions() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{"1", "desc", "Pupa", "50"},
                new String[]{"2", "desc", "", "50"},
                new String[]{"3", "desc", "Pupa", ""}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Baddie" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "Gargamel",
                                                                                  SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        brl1Definition.add( brl1DefinitionAction1 );

        ActionInsertFact brl1DefinitionAction2 = new ActionInsertFact( "Smurf" );
        ActionFieldValue brl1DefinitionAction2FieldValue1 = new ActionFieldValue( "name",
                                                                                  "$name",
                                                                                  SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionAction2FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction2.addFieldValue( brl1DefinitionAction2FieldValue1 );
        ActionFieldValue brl1DefinitionAction2FieldValue2 = new ActionFieldValue( "age",
                                                                                  "$age",
                                                                                  SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER );
        brl1DefinitionAction2FieldValue2.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction2.addFieldValue( brl1DefinitionAction2FieldValue2 );
        brl1Definition.add( brl1DefinitionAction2 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "$name",
                                                                             SuggestionCompletionEngine.TYPE_STRING,
                                                                             "Person",
                                                                             "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn( "$age",
                                                                             SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER,
                                                                             "Person",
                                                                             "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getActionCols().add( brl1 );
        dtable.setData( upgrader.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        int action2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "#from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact1 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setName( \"Pupa\" );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setAge( 50 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact1 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "#from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact1 = new Smurf();",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setName( \"Pupa\" );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setAge( 50 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact1 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "#from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact1 = new Smurf();",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setName( \"Pupa\" );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setAge( 50 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact1 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_NoVariables() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        Object[][] data = new Object[][]{
                new Object[]{"1", "desc", Boolean.TRUE},
                new Object[]{"2", "desc", Boolean.FALSE}
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Baddie" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "Gargamel",
                                                                                  SuggestionCompletionEngine.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        brl1Definition.add( brl1DefinitionAction1 );
        
        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "",
                                                                             SuggestionCompletionEngine.TYPE_BOOLEAN );
        brl1.getChildColumns().add( brl1Variable1 );

        dtable.getActionCols().add( brl1 );
        dtable.setData( upgrader.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "#from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "#from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertTrue( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         ruleStartIndex );
        assertTrue( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         ruleStartIndex );
        assertTrue( action1StartIndex == -1 );

    }

}
