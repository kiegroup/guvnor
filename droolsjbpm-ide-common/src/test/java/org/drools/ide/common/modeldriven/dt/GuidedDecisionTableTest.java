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

package org.drools.ide.common.modeldriven.dt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;

import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.Pattern;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable52;
import org.junit.Test;

public class GuidedDecisionTableTest {

    @Test
    public void testValueLists() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        // add cols for LHS
        ConditionCol52 c1 = new ConditionCol52();
        Pattern p1 = new Pattern();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( c1 );
        dt.getConditionPatterns().add( p1 );

        ConditionCol52 c1_ = new ConditionCol52();
        Pattern p1_ = new Pattern();
        p1_.setBoundName( "c1" );
        p1_.setFactType( "Driver" );
        c1_.setFactField( "name" );
        p1_.getConditions().add( c1_ );
        c1_.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        dt.getConditionPatterns().add( p1_ );

        ConditionCol52 c1__ = new ConditionCol52();
        Pattern p1__ = new Pattern();
        p1__.setBoundName( "c1" );
        p1__.setFactType( "Driver" );
        c1__.setFactField( "name" );
        c1__.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1__.setValueList( "one,two,three" );
        p1__.getConditions().add( c1__ );
        dt.getConditionPatterns().add( p1__ );

        ConditionCol52 c2 = new ConditionCol52();
        Pattern p2 = new Pattern();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );
        c2.setFactField( "nothing" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getConditions().add( c2 );
        dt.getConditionPatterns().add( p2 );

        ActionSetFieldCol asf = new ActionSetFieldCol();
        asf.setBoundName( "c1" );
        asf.setFactField( "name" );
        dt.getActionCols().add( asf );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.setBoundName( "x" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        dt.getActionCols().add( ins );

        ActionInsertFactCol ins_ = new ActionInsertFactCol();
        ins_.setBoundName( "x" );
        ins_.setFactField( "rating" );
        ins_.setFactType( "Person" );
        ins_.setValueList( "one,two,three" );
        dt.getActionCols().add( ins_ );

        ActionSetFieldCol asf_ = new ActionSetFieldCol();
        asf_.setBoundName( "c1" );
        asf_.setFactField( "goo" );
        dt.getActionCols().add( asf_ );

        ActionSetFieldCol asf__ = new ActionSetFieldCol();
        asf__.setBoundName( "c1" );
        asf__.setFactField( "goo" );
        asf__.setValueList( "one,two,three" );
        dt.getActionCols().add( asf__ );

        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.putDataEnumList( "Driver.name",
                             new String[]{"bob", "michael"} );
        sce.putDataEnumList( "Person.rating",
                             new String[]{"1", "2"} );

        String[] r = dt.getValueList( c1,
                                      sce );
        assertEquals( 2,
                      r.length );
        assertEquals( "bob",
                      r[0] );
        assertEquals( "michael",
                      r[1] );

        assertEquals( 0,
                      dt.getValueList( c1_,
                                       sce ).length );

        r = dt.getValueList( c1__,
                             sce );
        assertEquals( 3,
                      r.length );
        assertEquals( "one",
                      r[0] );
        assertEquals( "two",
                      r[1] );
        assertEquals( "three",
                      r[2] );

        assertEquals( 0,
                      dt.getValueList( c2,
                                       sce ).length );

        r = dt.getValueList( asf,
                             sce );
        assertEquals( 2,
                      r.length );
        assertEquals( "bob",
                      r[0] );
        assertEquals( "michael",
                      r[1] );

        r = dt.getValueList( ins,
                             sce );
        assertEquals( 2,
                      r.length );
        assertEquals( "1",
                      r[0] );
        assertEquals( "2",
                      r[1] );

        r = dt.getValueList( ins_,
                             sce );
        assertEquals( 3,
                      r.length );
        assertEquals( "one",
                      r[0] );
        assertEquals( "two",
                      r[1] );
        assertEquals( "three",
                      r[2] );

        assertEquals( 0,
                      dt.getValueList( asf_,
                                       sce ).length );

        r = dt.getValueList( asf__,
                             sce );
        assertEquals( 3,
                      r.length );
        assertEquals( "one",
                      r[0] );
        assertEquals( "two",
                      r[1] );
        assertEquals( "three",
                      r[2] );

        AttributeCol at = new AttributeCol();
        at.setAttribute( "no-loop" );
        dt.getAttributeCols().add( at );

        r = dt.getValueList( at,
                             sce );
        assertEquals( 2,
                      r.length );
        assertEquals( "true",
                      r[0] );
        assertEquals( "false",
                      r[1] );

        at.setAttribute( "enabled" );
        assertEquals( 2,
                      dt.getValueList( at,
                                       sce ).length );

        at.setAttribute( "salience" );
        assertEquals( 0,
                      dt.getValueList( at,
                                       sce ).length );

    }

    @Test
    @SuppressWarnings("serial")
    public void testNumeric() {
        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Driver",
                        new ModelField[]{
                                new ModelField( "age",
                                                Integer.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_NUMERIC ),
                                new ModelField( "name",
                                                String.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_STRING )
                        } );
            }
        } );

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        AttributeCol at = new AttributeCol();
        at.setAttribute( "salience" );
        AttributeCol at_ = new AttributeCol();
        at_.setAttribute( "enabled" );

        dt.getAttributeCols().add( at );
        dt.getAttributeCols().add( at_ );

        ConditionCol52 c1 = new ConditionCol52();
        Pattern p1 = new Pattern();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( c1 );
        dt.getConditionPatterns().add( p1 );

        ConditionCol52 c1_ = new ConditionCol52();
        Pattern p1_ = new Pattern();
        p1_.setBoundName( "c1" );
        p1_.setFactType( "Driver" );
        c1_.setFactField( "age" );
        c1_.setOperator( "==" );
        c1_.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1_.getConditions().add( c1_ );
        dt.getConditionPatterns().add( p1_ );

        ConditionCol52 c2 = new ConditionCol52();
        Pattern p2 = new Pattern();
        p2.setBoundName( "c1" );
        p2.setFactType( "Driver" );
        c2.setFactField( "age" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getConditions().add( c2 );
        dt.getConditionPatterns().add( p2 );

        ActionSetFieldCol a = new ActionSetFieldCol();
        a.setBoundName( "c1" );
        a.setFactField( "name" );
        dt.getActionCols().add( a );

        ActionSetFieldCol a2 = new ActionSetFieldCol();
        a2.setBoundName( "c1" );
        a2.setFactField( "age" );
        dt.getActionCols().add( a2 );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.setBoundName( "x" );
        ins.setFactType( "Driver" );
        ins.setFactField( "name" );
        dt.getActionCols().add( ins );

        ActionInsertFactCol ins_ = new ActionInsertFactCol();
        ins_.setBoundName( "x" );
        ins_.setFactType( "Driver" );
        ins_.setFactField( "age" );
        dt.getActionCols().add( ins_ );

        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( at,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( c1_,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( a2,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( ins_,
                                  sce ) );

        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      dt.getType( at_,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      dt.getType( c1,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      dt.getType( a,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      dt.getType( ins,
                                  sce ) );

        assertEquals( null,
                      dt.getType( c2,
                                  sce ) );
    }

    @Test
    @SuppressWarnings("serial")
    public void testGetType() {
        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Driver",
                        new ModelField[]{
                                new ModelField( "age",
                                                Integer.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_NUMERIC ),
                                new ModelField( "name",
                                                String.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_STRING ),
                                new ModelField( "date",
                                                Date.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE ),
                                new ModelField( "approved",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_BOOLEAN )
                       } );
            }
        } );

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        AttributeCol salienceAttribute = new AttributeCol();
        salienceAttribute.setAttribute( "salience" );
        AttributeCol enabledAttribute = new AttributeCol();
        enabledAttribute.setAttribute( "enabled" );

        dt.getAttributeCols().add( salienceAttribute );
        dt.getAttributeCols().add( enabledAttribute );

        Pattern p1 = new Pattern();

        ConditionCol52 conditionColName = new ConditionCol52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );
        conditionColName.setFactField( "name" );
        conditionColName.setOperator( "==" );
        conditionColName.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( conditionColName );

        ConditionCol52 conditionColAge = new ConditionCol52();
        conditionColAge.setFactField( "age" );
        conditionColAge.setOperator( "==" );
        conditionColAge.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( conditionColAge );

        ConditionCol52 conditionColDate = new ConditionCol52();
        conditionColDate.setFactField( "date" );
        conditionColDate.setOperator( "==" );
        conditionColDate.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( conditionColDate );

        ConditionCol52 conditionColApproved = new ConditionCol52();
        conditionColApproved.setFactField( "approved" );
        conditionColApproved.setOperator( "==" );
        conditionColApproved.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( conditionColApproved );

        ConditionCol52 conditionColAge2 = new ConditionCol52();
        conditionColAge2.setFactField( "age" );
        conditionColAge2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( conditionColAge2 );

        dt.getConditionPatterns().add( p1 );

        ActionSetFieldCol a = new ActionSetFieldCol();
        a.setBoundName( "c1" );
        a.setFactField( "name" );
        dt.getActionCols().add( a );

        ActionSetFieldCol a2 = new ActionSetFieldCol();
        a2.setBoundName( "c1" );
        a2.setFactField( "age" );
        dt.getActionCols().add( a2 );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.setBoundName( "x" );
        ins.setFactType( "Driver" );
        ins.setFactField( "name" );
        dt.getActionCols().add( ins );

        ActionInsertFactCol ins_ = new ActionInsertFactCol();
        ins_.setBoundName( "x" );
        ins_.setFactType( "Driver" );
        ins_.setFactField( "age" );
        dt.getActionCols().add( ins_ );

        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( salienceAttribute,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      dt.getType( enabledAttribute,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      dt.getType( conditionColName,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( conditionColAge,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_DATE,
                      dt.getType( conditionColDate,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_BOOLEAN,
                      dt.getType( conditionColApproved,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      dt.getType( a,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( a2,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      dt.getType( ins,
                                  sce ) );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      dt.getType( ins_,
                                  sce ) );
        assertEquals( null,
                      dt.getType( conditionColAge2,
                                  sce ) );
    }

    @Test
    public void testNoConstraintLists() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        // add cols for LHS
        ConditionCol52 c1 = new ConditionCol52();
        Pattern p1 = new Pattern();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( c1 );
        dt.getConditionPatterns().add( p1 );

        ConditionCol52 c2 = new ConditionCol52();
        Pattern p2 = new Pattern();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "a,b,c" );
        p2.getConditions().add( c2 );
        dt.getConditionPatterns().add( p1 );

        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.putDataEnumList( "Driver.name",
                             new String[]{"bob", "michael"} );

        assertEquals( 0,
                      dt.getValueList( c1,
                                       sce ).length );
        assertEquals( 3,
                      dt.getValueList( c2,
                                       sce ).length );

    }

    @SuppressWarnings("serial")
    @Test
    public void testNoConstraints() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        // add cols for LHS
        RowNumberCol rnc = new RowNumberCol();
        DescriptionCol dc = new DescriptionCol();

        MetadataCol mdc = new MetadataCol();
        mdc.setMetadata( "cheese" );

        AttributeCol ac = new AttributeCol();
        ac.setAttribute( "salience" );

        ActionSetFieldCol asfc = new ActionSetFieldCol();
        asfc.setBoundName( "d1" );
        asfc.setFactField( "age" );

        ActionInsertFactCol aifc = new ActionInsertFactCol();
        aifc.setBoundName( "d2" );
        aifc.setFactType( "Driver" );
        aifc.setFactField( "age" );

        ConditionCol52 c1 = new ConditionCol52();
        Pattern p1 = new Pattern();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( c1 );
        dt.getConditionPatterns().add( p1 );

        ConditionCol52 c2 = new ConditionCol52();
        Pattern p2 = new Pattern();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );
        c2.setFactField( "age" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getConditions().add( c2 );
        dt.getConditionPatterns().add( p2 );

        ConditionCol52 c3 = new ConditionCol52();
        Pattern p3 = new Pattern();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getConditions().add( c3 );
        dt.getConditionPatterns().add( p3 );

        ConditionCol52 c4 = new ConditionCol52();
        Pattern p4 = new Pattern();
        p4.setBoundName( "c4" );
        p4.setFactType( "Driver" );
        c4.setFactField( "age" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p4.getConditions().add( c4 );
        dt.getConditionPatterns().add( p4 );

        ConditionCol52 c5 = new ConditionCol52();
        Pattern p5 = new Pattern();
        p5.setBoundName( "c5" );
        p5.setFactType( "Driver" );
        c5.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        p5.getConditions().add( c5 );
        dt.getConditionPatterns().add( p5 );

        ConditionCol52 c6 = new ConditionCol52();
        Pattern p6 = new Pattern();
        p6.setBoundName( "c6" );
        p6.setFactType( "Driver" );
        c6.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        p6.getConditions().add( c6 );
        dt.getConditionPatterns().add( p6 );

        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Driver",
                        new ModelField[]{
                                new ModelField( "age",
                                                Integer.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_NUMERIC ),
                                new ModelField( "name",
                                                String.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_STRING )
                        } );
            }
        } );

        assertTrue( dt.isConstraintValid( rnc,
                                          sce ) );
        assertTrue( dt.isConstraintValid( dc,
                                          sce ) );
        assertTrue( dt.isConstraintValid( mdc,
                                          sce ) );
        assertTrue( dt.isConstraintValid( ac,
                                          sce ) );
        assertTrue( dt.isConstraintValid( asfc,
                                          sce ) );
        assertTrue( dt.isConstraintValid( aifc,
                                          sce ) );

        assertFalse( dt.isConstraintValid( c1,
                                           sce ) );
        assertFalse( dt.isConstraintValid( c2,
                                           sce ) );
        assertFalse( dt.isConstraintValid( c3,
                                           sce ) );
        assertTrue( dt.isConstraintValid( c4,
                                          sce ) );
        assertTrue( dt.isConstraintValid( c5,
                                          sce ) );
        assertTrue( dt.isConstraintValid( c6,
                                          sce ) );

    }

}
