/**
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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.server.util.BRDRLPersistence;
import org.drools.ide.common.server.util.GuidedDTDRLPersistence;

public class GuidedDTDRLPersistenceTest {

    @Test
    public void test2Rules() throws Exception {
        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.setTableName( "michael" );

        AttributeCol attr = new AttributeCol();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        ConditionCol con = new ConditionCol();
        con.setBoundName( "f1" );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setFactType( "Driver" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        dt.getConditionCols().add( con );

        ConditionCol con2 = new ConditionCol();
        con2.setBoundName( "f1" );
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setFactType( "Driver" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        dt.getConditionCols().add( con2 );

        ConditionCol con3 = new ConditionCol();
        con3.setBoundName( "f1" );
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setFactType( "Driver" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        dt.getConditionCols().add( con3 );

        ConditionCol con4 = new ConditionCol();
        con4.setBoundName( "f2" );
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setFactType( "Driver" );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        dt.getConditionCols().add( con4 );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        dt.getActionCols().add( ins );

        ActionRetractFactCol ret = new ActionRetractFactCol();
        ret.setBoundName( "f2" );
        dt.getActionCols().add( ret );

        ActionSetFieldCol set = new ActionSetFieldCol();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol set2 = new ActionSetFieldCol();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( "whee" );
        set2.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", null},
                new String[]{"2", "desc", "", "39", "bob", "age * 0.3", "age > 7", "6.60", "", "gooVal1", ""}
        } );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "from row number" ) > -1 );
        assertTrue( drl.indexOf( "rating == ( age * 0.2 )" ) > 0 );
        assertTrue( drl.indexOf( "f2 : Driver( eval( age > 7 ))" ) > 0 );
        assertTrue( drl.indexOf( "rating == ( age * 0.3 )" ) > drl.indexOf( "rating == ( age * 0.2 )" ) );
        assertTrue( drl.indexOf( "f1.setGoo2( \"whee\" )" ) > 0 ); // for
                                                                   // default
        assertTrue( drl.indexOf( "salience 66" ) > 0 ); // for default

    }

    @Test
    public void testInterpolate() {
        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.setTableName( "michael" );

        AttributeCol attr = new AttributeCol();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        ConditionCol con = new ConditionCol();
        con.setBoundName( "f1" );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setFactType( "Driver" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        dt.getConditionCols().add( con );

        ConditionCol con2 = new ConditionCol();
        con2.setBoundName( "f1" );
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setFactType( "Driver" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        dt.getConditionCols().add( con2 );

        ConditionCol con3 = new ConditionCol();
        con3.setBoundName( "f1" );
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setFactType( "Driver" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        dt.getConditionCols().add( con3 );

        ConditionCol con4 = new ConditionCol();
        con4.setBoundName( "f2" );
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setFactType( "Driver" );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "this.hasSomething($param)" );
        dt.getConditionCols().add( con4 );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        dt.getActionCols().add( ins );

        ActionRetractFactCol ret = new ActionRetractFactCol();
        ret.setBoundName( "f2" );
        dt.getActionCols().add( ret );

        ActionSetFieldCol set = new ActionSetFieldCol();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol set2 = new ActionSetFieldCol();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( "whee" );
        set2.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael", "age * 0.2", "BAM", "6.60", "true", "gooVal1", null},
                new String[]{"2", "desc", "", "39", "bob", "age * 0.3", "BAM", "6.60", "", "gooVal1", ""}
        } );

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
    public void testInOperator() {
        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.setTableName( "michael" );

        AttributeCol attr = new AttributeCol();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        ConditionCol con = new ConditionCol();
        con.setBoundName( "f1" );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setFactType( "Driver" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        dt.getConditionCols().add( con );

        ConditionCol con2 = new ConditionCol();
        con2.setBoundName( "f1" );
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setFactType( "Driver" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "in" );
        dt.getConditionCols().add( con2 );

        ConditionCol con3 = new ConditionCol();
        con3.setBoundName( "f1" );
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setFactType( "Driver" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        dt.getConditionCols().add( con3 );

        ConditionCol con4 = new ConditionCol();
        con4.setBoundName( "f2" );
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setFactType( "Driver" );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        dt.getConditionCols().add( con4 );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        dt.getActionCols().add( ins );

        ActionRetractFactCol ret = new ActionRetractFactCol();
        ret.setBoundName( "f2" );
        dt.getActionCols().add( ret );

        ActionSetFieldCol set = new ActionSetFieldCol();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol set2 = new ActionSetFieldCol();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( "whee" );
        set2.setType( SuggestionCompletionEngine.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( new String[][]{
                new String[]{"1", "desc", "42", "33", "michael, manik", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", null},
                new String[]{"2", "desc", "", "39", "bob, frank", "age * 0.3", "age > 7", "6.60", "", "gooVal1", ""}
        } );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "name in (\"michael\"," ) > 0 );

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
    public void testName() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertEquals( "Row 42 XXX",
                      p.getName( "XXX",
                                 "42" ) );
        assertEquals( "Row 42 YYY",
                      p.getName( "YYY",
                                 "42" ) );
    }

    @Test
    public void testAttribs() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", ""};

        List<AttributeCol> attributeCols = new ArrayList<AttributeCol>();
        RuleModel rm = new RuleModel();
        RuleAttribute[] orig = rm.attributes;
        p.doAttribs( 0,
                     attributeCols,
                     row,
                     rm );

        assertSame( orig,
                    rm.attributes );

        AttributeCol col1 = new AttributeCol();
        col1.setAttribute( "salience" );
        AttributeCol col2 = new AttributeCol();
        col2.setAttribute( "agenda-group" );
        attributeCols.add( col1 );
        attributeCols.add( col2 );

        p.doAttribs( 0,
                     attributeCols,
                     row,
                     rm );

        assertEquals( 1,
                      rm.attributes.length );
        assertEquals( "salience",
                      rm.attributes[0].attributeName );
        assertEquals( "a",
                      rm.attributes[0].value );

        row = new String[]{"1", "desc", "a", "b"};
        p.doAttribs( 0,
                     attributeCols,
                     row,
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
    public void testMetaData() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "bar", ""};

        List<MetadataCol> metadataCols = new ArrayList<MetadataCol>();
        RuleModel rm = new RuleModel();
        RuleMetadata[] orig = rm.metadataList;
        // RuleAttribute[] orig = rm.attributes;
        p.doMetadata( metadataCols,
                      row,
                      rm );
        // p.doAttribs(0,metadataCols, row, rm);

        assertSame( orig,
                    rm.metadataList );

        MetadataCol col1 = new MetadataCol();
        col1.setMetadata( "foo" );
        MetadataCol col2 = new MetadataCol();
        col2.setMetadata( "foo2" );
        metadataCols.add( col1 );
        metadataCols.add( col2 );

        p.doMetadata( metadataCols,
                      row,
                      rm );
        // p.doAttribs(0, metadataCols, row, rm);

        assertEquals( 1,
                      rm.metadataList.length );
        assertEquals( "foo",
                      rm.metadataList[0].attributeName );
        assertEquals( "bar",
                      rm.metadataList[0].value );

        row = new String[]{"1", "desc", "bar1", "bar2"};
        p.doMetadata( metadataCols,
                      row,
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
    public void testLHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton"};

        List<ConditionCol> cols = new ArrayList<ConditionCol>();
        ConditionCol col = new ConditionCol();
        col.setBoundName( "p1" );
        col.setFactType( "Person" );
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setOperator( "==" );
        cols.add( col );

        ConditionCol col2 = new ConditionCol();
        col2.setBoundName( "p1" );
        col2.setFactType( "Person" );
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        cols.add( col2 );

        ConditionCol col3 = new ConditionCol();
        col3.setBoundName( "p1" );
        col3.setFactType( "Person" );
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        cols.add( col3 );

        ConditionCol col4 = new ConditionCol();
        col4.setBoundName( "c" );
        col4.setFactType( "Cheese" );
        col4.setFactField( "type" );
        col4.setOperator( "==" );
        col4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cols.add( col4 );

        RuleModel rm = new RuleModel();

        p.doConditions( 1,
                        cols,
                        row,
                        rm );
        assertEquals( 2,
                      rm.lhs.length );

        assertEquals( "Person",
                      ((FactPattern) rm.lhs[0]).factType );
        assertEquals( "p1",
                      ((FactPattern) rm.lhs[0]).boundName );

        assertEquals( "Cheese",
                      ((FactPattern) rm.lhs[1]).factType );
        assertEquals( "c",
                      ((FactPattern) rm.lhs[1]).boundName );

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
    public void testRHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "a condition", "actionsetfield1", "actionsetfield2", "retract", "actioninsertfact1", "actioninsertfact2"};

        List<ActionCol> cols = new ArrayList<ActionCol>();
        ActionSetFieldCol asf1 = new ActionSetFieldCol();
        asf1.setBoundName( "a" );
        asf1.setFactField( "field1" );

        asf1.setType( SuggestionCompletionEngine.TYPE_STRING );
        cols.add( asf1 );

        ActionSetFieldCol asf2 = new ActionSetFieldCol();
        asf2.setBoundName( "a" );
        asf2.setFactField( "field2" );
        asf2.setUpdate( true );
        asf2.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        cols.add( asf2 );

        ActionRetractFactCol ret = new ActionRetractFactCol();
        ret.setBoundName( "ret" );
        cols.add( ret );

        ActionInsertFactCol ins1 = new ActionInsertFactCol();
        ins1.setBoundName( "ins" );
        ins1.setFactType( "Cheese" );
        ins1.setFactField( "price" );
        ins1.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        cols.add( ins1 );

        ActionInsertFactCol ins2 = new ActionInsertFactCol();
        ins2.setBoundName( "ins" );
        ins2.setFactType( "Cheese" );
        ins2.setFactField( "type" );
        ins2.setType( SuggestionCompletionEngine.TYPE_NUMERIC );
        cols.add( ins2 );

        RuleModel rm = new RuleModel();
        p.doActions( 2,
                     cols,
                     row,
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
    public void testNoConstraints() {
        GuidedDecisionTable dt = new GuidedDecisionTable();
        ConditionCol c = new ConditionCol();
        c.setBoundName( "x" );
        c.setFactType( "Context" );
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        dt.getConditionCols().add( c );
        ActionSetFieldCol asf = new ActionSetFieldCol();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( "String" );

        dt.getActionCols().add( asf );

        String[][] data = new String[][]{
                new String[]{"1", "desc", "y", "old"}
        };
        dt.setData( data );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        // System.err.println(drl);

        assertTrue( drl.indexOf( "Context( )" ) > -1 );
        assertTrue( drl.indexOf( "x.setAge" ) > drl.indexOf( "Context( )" ) );
        assertFalse( drl.indexOf( "update( x );" ) > -1 );

        dt.setData( new String[][]{
                new String[]{"1", "desc", "", "old"}
            } );
        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        assertEquals( -1,
                      drl.indexOf( "Context( )" ) );

    }

    @Test
    public void testUpdateModify() {
        GuidedDecisionTable dt = new GuidedDecisionTable();
        ConditionCol c = new ConditionCol();
        c.setBoundName( "x" );
        c.setFactType( "Context" );
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        dt.getConditionCols().add( c );
        ActionSetFieldCol asf = new ActionSetFieldCol();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( "String" );
        asf.setUpdate( true );

        dt.getActionCols().add( asf );

        String[][] data = new String[][]{
                new String[]{"1", "desc", "y", "old"}
        };
        dt.setData( data );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        System.err.println( drl );

        assertTrue( drl.indexOf( "Context( )" ) > -1 );
        assertTrue( drl.indexOf( "x.setAge" ) > drl.indexOf( "Context( )" ) );

        dt.setData( new String[][]{
                new String[]{"1", "desc", "", "old"}
            } );
        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        assertEquals( -1,
                      drl.indexOf( "Context( )" ) );

        assertTrue( drl.indexOf( "update( x );" ) > -1 );

    }

    @Test
    public void testNoOperator() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{"1", "desc", "a", "> 42", "33 + 1", "age > 6", "stilton"};

        List<ConditionCol> cols = new ArrayList<ConditionCol>();

        ConditionCol col2 = new ConditionCol();
        col2.setBoundName( "p1" );
        col2.setFactType( "Person" );
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setOperator( "" );
        cols.add( col2 );

        RuleModel rm = new RuleModel();

        p.doConditions( 1,
                        cols,
                        row,
                        rm );

        String drl = BRDRLPersistence.getInstance().marshal( rm );
        assertTrue( drl.indexOf( "age > \"42\"" ) > 0 );

    }

}
