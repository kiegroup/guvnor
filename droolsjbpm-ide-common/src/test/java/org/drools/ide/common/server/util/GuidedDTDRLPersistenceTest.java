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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;
import org.junit.Test;

public class GuidedDTDRLPersistenceTest {

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
        p1.getConditions().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        p1.getConditions().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getConditions().add( con3 );

        dt.getConditionPatterns().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "f2" );
        p2.setFactType( "Driver" );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        p2.getConditions().add( con4 );

        dt.getConditionPatterns().add( p2 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        ret.setBoundName( "f2" );
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

        dt.setData( RepositoryUpgradeHelper.makeDataLists( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", null},
                new String[]{"2", "desc", "", "39", "bob", "age * 0.3", "age > 7", "6.60", "", "gooVal1", ""}
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "from row number" ) > -1 );
        assertTrue( drl.indexOf( "rating == ( age * 0.2 )" ) > 0 );
        assertTrue( drl.indexOf( "f2 : Driver( eval( age > 7 ))" ) > 0 );
        assertTrue( drl.indexOf( "rating == ( age * 0.3 )" ) > drl.indexOf( "rating == ( age * 0.2 )" ) );
        assertTrue( drl.indexOf( "f1.setGoo2( \"whee\" )" ) > 0 ); // for default
        assertTrue( drl.indexOf( "salience 66" ) > 0 ); // for default

    }

    @Test
    public void testAttribs() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", ""};

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<AttributeCol52> attributeCols = new ArrayList<AttributeCol52>();

        RuleModel rm = new RuleModel();
        RuleAttribute[] orig = rm.attributes;
        p.doAttribs( allColumns,
                     attributeCols,
                     RepositoryUpgradeHelper.makeDataRowList( row ),
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
                     RepositoryUpgradeHelper.makeDataRowList( row ),
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
                     RepositoryUpgradeHelper.makeDataRowList( row ),
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
        p1.getConditions().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "in" );
        p1.getConditions().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getConditions().add( con3 );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        p1.getConditions().add( con4 );

        dt.getConditionPatterns().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        ret.setBoundName( "f2" );
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

        dt.setData( RepositoryUpgradeHelper.makeDataLists( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael, manik", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", null},
                new String[]{"2", "desc", "", "39", "bob, frank", "age * 0.3", "age > 7", "6.60", "", "gooVal1", ""}
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
        p1.getConditions().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        p1.getConditions().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getConditions().add( con3 );

        dt.getConditionPatterns().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "f2" );
        p2.setFactType( "Driver" );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "this.hasSomething($param)" );
        p2.getConditions().add( con4 );

        dt.getConditionPatterns().add( p2 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        ret.setBoundName( "f2" );
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

        dt.setData( RepositoryUpgradeHelper.makeDataLists( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "BAM", "6.60", "true", "gooVal1", null},
                new String[]{"2", "desc", "", "39", "bob", "age * 0.3", "BAM", "6.60", "", "gooVal1", ""}
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "from row number" ) > -1 );
        assertTrue( drl.indexOf( "rating == ( age * 0.2 )" ) > 0 );
        // assertTrue(drl.indexOf("f2 : Driver( eval( age > 7 ))") > 0);
        assertTrue( drl.indexOf( "f2 : Driver( eval( this.hasSomething(BAM) ))" ) > 0 );
        assertTrue( drl.indexOf( "rating == ( age * 0.3 )" ) > drl.indexOf( "rating == ( age * 0.2 )" ) );
        assertTrue( drl.indexOf( "f1.setGoo2( \"whee\" )" ) > 0 ); // for
                                                                   // default
        assertTrue( drl.indexOf( "salience 66" ) > 0 ); // for default

    }

    @Test
    public void testLHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton"};
        String[][] data = new String[1][];
        data[0] = row;

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        List<Pattern52> allPatterns = new ArrayList<Pattern52>();
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
        p1.getConditions().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        p1.getConditions().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        p1.getConditions().add( col3 );
        allColumns.add( col3 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c" );
        p2.setFactType( "Cheese" );
        allPatterns.add( p2 );

        ConditionCol52 col4 = new ConditionCol52();
        col4.setFactField( "type" );
        col4.setOperator( "==" );
        col4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getConditions().add( col4 );
        allColumns.add( col4 );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        RepositoryUpgradeHelper.makeDataRowList( row ),
                        RepositoryUpgradeHelper.makeDataLists( data ),
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
    public void testLHSNotPattern() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton"};
        String[][] data = new String[1][];
        data[0] = row;

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        List<Pattern52> allPatterns = new ArrayList<Pattern52>();
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
        p1.getConditions().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        p1.getConditions().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        p1.getConditions().add( col3 );
        allColumns.add( col3 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c" );
        p2.setFactType( "Cheese" );
        allPatterns.add( p2 );

        ConditionCol52 col4 = new ConditionCol52();
        col4.setFactField( "type" );
        col4.setOperator( "==" );
        col4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getConditions().add( col4 );
        allColumns.add( col4 );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        RepositoryUpgradeHelper.makeDataRowList( row ),
                        RepositoryUpgradeHelper.makeDataLists( data ),
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
        List<DTCellValue52> rowDTModel0 = RepositoryUpgradeHelper.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel1 = RepositoryUpgradeHelper.makeDataRowList( row[1] );
        rowDTModel1.get( 2 ).setOtherwise( true );
        rowDTModel1.get( 3 ).setOtherwise( true );
        data[1] = row[1];

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        List<Pattern52> allPatterns = new ArrayList<Pattern52>();
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
        p1.getConditions().add( col );
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
        p2.getConditions().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel0,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel1,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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
        List<DTCellValue52> rowDTModel0 = RepositoryUpgradeHelper.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"2", "desc2", "01-Feb-1981", "21-Jun-1986"};
        List<DTCellValue52> rowDTModel1 = RepositoryUpgradeHelper.makeDataRowList( row[1] );
        data[1] = row[1];

        row[2] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel2 = RepositoryUpgradeHelper.makeDataRowList( row[2] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[2] = row[2];

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        List<Pattern52> allPatterns = new ArrayList<Pattern52>();
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
        p1.getConditions().add( col );
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
        p2.getConditions().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel0,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel1,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel2,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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
        List<DTCellValue52> rowDTModel0 = RepositoryUpgradeHelper.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"2", "desc2", "2", "2"};
        List<DTCellValue52> rowDTModel1 = RepositoryUpgradeHelper.makeDataRowList( row[1] );
        data[1] = row[1];

        row[2] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel2 = RepositoryUpgradeHelper.makeDataRowList( row[2] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[2] = row[2];

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        List<Pattern52> allPatterns = new ArrayList<Pattern52>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "age" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC );
        col.setOperator( "==" );
        p1.getConditions().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( SuggestionCompletionEngine.TYPE_NUMERIC );
        col2.setOperator( "!=" );
        p2.getConditions().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel0,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel1,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel2,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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
        List<DTCellValue52> rowDTModel0 = RepositoryUpgradeHelper.makeDataRowList( row[0] );
        data[0] = row[0];

        row[1] = new String[]{"2", "desc2", "Michael2", "Michael2"};
        List<DTCellValue52> rowDTModel1 = RepositoryUpgradeHelper.makeDataRowList( row[1] );
        data[1] = row[1];

        row[2] = new String[]{"3", "desc3", null, null};
        List<DTCellValue52> rowDTModel2 = RepositoryUpgradeHelper.makeDataRowList( row[2] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[2] = row[2];

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        List<Pattern52> allPatterns = new ArrayList<Pattern52>();
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
        p1.getConditions().add( col );
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
        p2.getConditions().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel0,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel1,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        p.doConditions( allColumns,
                        allPatterns,
                        rowDTModel2,
                        RepositoryUpgradeHelper.makeDataLists( data ),
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

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<MetadataCol52> metadataCols = new ArrayList<MetadataCol52>();

        RuleModel rm = new RuleModel();
        RuleMetadata[] orig = rm.metadataList;
        // RuleAttribute[] orig = rm.attributes;
        p.doMetadata( allColumns,
                      metadataCols,
                      RepositoryUpgradeHelper.makeDataRowList( row ),
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
                      RepositoryUpgradeHelper.makeDataRowList( row ),
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
                      RepositoryUpgradeHelper.makeDataRowList( row ),
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
        p1.getConditions().add( c );

        dt.getConditionPatterns().add( p1 );

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( SuggestionCompletionEngine.TYPE_STRING );

        dt.getActionCols().add( asf );

        String[][] data = new String[][]{
                new String[]{"1", "desc", "y", "old"}
        };
        dt.setData( RepositoryUpgradeHelper.makeDataLists( data ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "Context( )" ) > -1 );
        assertTrue( drl.indexOf( "x.setAge" ) > drl.indexOf( "Context( )" ) );
        assertFalse( drl.indexOf( "update( x );" ) > -1 );

        dt.setData( RepositoryUpgradeHelper.makeDataLists( new String[][]{
                new String[]{"1", "desc", "", "old"}
            } ) );
        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        assertEquals( -1,
                      drl.indexOf( "Context( )" ) );

    }

    @Test
    public void testNoOperator() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "> 42", "33 + 1", "age > 6", "stilton"};
        String[][] data = new String[1][];
        data[0] = row;

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
        List<Pattern52> allPatterns = new ArrayList<Pattern52>();
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
        p1.getConditions().add( col1 );
        allColumns.add( col1 );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        RepositoryUpgradeHelper.makeDataRowList( row ),
                        RepositoryUpgradeHelper.makeDataLists( data ),
                        rm );

        String drl = BRDRLPersistence.getInstance().marshal( rm );
        assertTrue( drl.indexOf( "age > \"42\"" ) > 0 );

    }

    @Test
    public void testRHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "a condition", "actionsetfield1", "actionsetfield2", "retract", "actioninsertfact1", "actioninsertfact2"};

        List<DTColumnConfig52> allColumns = new ArrayList<DTColumnConfig52>();
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
        asf2.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        cols.add( asf2 );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        ret.setBoundName( "ret" );
        cols.add( ret );

        ActionInsertFactCol52 ins1 = new ActionInsertFactCol52();
        ins1.setBoundName( "ins" );
        ins1.setFactType( "Cheese" );
        ins1.setFactField( "price" );
        ins1.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        cols.add( ins1 );

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setBoundName( "ins" );
        ins2.setFactType( "Cheese" );
        ins2.setFactField( "type" );
        ins2.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        cols.add( ins2 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        p.doActions( allColumns,
                     cols,
                     RepositoryUpgradeHelper.makeDataRowList( row ),
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
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      a1.fieldValues[1].type );

        // examine the retract
        ActionRetractFact a2 = (ActionRetractFact) rm.rhs[1];
        assertEquals( "ret",
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
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      a3.fieldValues[0].type );

        assertEquals( "type",
                      a3.fieldValues[1].field );
        assertEquals( "actioninsertfact2",
                      a3.fieldValues[1].value );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
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
        p1.getConditions().add( c );
        dt.getConditionPatterns().add( p1 );

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        asf.setUpdate( true );

        dt.getActionCols().add( asf );

        String[][] data = new String[][]{
                new String[]{"1", "desc", "y", "old"}
        };
        dt.setData( RepositoryUpgradeHelper.makeDataLists( data ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "Context( )" ) > -1 );
        assertTrue( drl.indexOf( "x.setAge" ) > drl.indexOf( "Context( )" ) );

        dt.setData( RepositoryUpgradeHelper.makeDataLists( new String[][]{
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
        p1.getConditions().add( c );
        dt.getConditionPatterns().add( p1 );

        //With provided value
        String[][] data = new String[][]{
                new String[]{"1", "desc", "edam"},
        };
        dt.setData( RepositoryUpgradeHelper.makeDataLists( data ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertFalse( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"edam\" )" ) == -1 );

        //Without provided value #1
        data = new String[][]{
               new String[]{"1", "desc", null},
        };
        dt.setData( RepositoryUpgradeHelper.makeDataLists( data ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"cheddar\" )" ) == -1 );

        //Without provided value #2
        data = new String[][]{
               new String[]{"1", "desc", ""},
        };
        dt.setData( RepositoryUpgradeHelper.makeDataLists( data ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"cheddar\" )" ) == -1 );

    }
    
}
