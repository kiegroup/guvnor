/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.decisiontable.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.cell.client.Cell.Context;

public class DecisionTableDropDownManagerTests {

    private DynamicData                  data;
    private GuidedDecisionTable52        model;
    private DecisionTableDropDownManager manager;

    @Before
    public void setup() {

        //---Setup model---
        model = new GuidedDecisionTable52();

        //Setup LHS
        Pattern52 p0 = new Pattern52();
        p0.setBoundName( "$f0" );
        p0.setFactType( "F0" );

        ConditionCol52 c0p0 = new ConditionCol52();
        c0p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c0p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        c0p0.setFactField( "c0p0" );
        c0p0.setOperator( "==" );
        p0.getChildColumns().add( c0p0 );

        ConditionCol52 c1p0 = new ConditionCol52();
        c1p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        c1p0.setFactField( "c1p0" );
        c1p0.setOperator( "==" );
        p0.getChildColumns().add( c1p0 );

        model.getConditions().add( p0 );

        //Setup RHS
        ActionSetFieldCol52 asf0 = new ActionSetFieldCol52();
        asf0.setBoundName( "$f0" );
        asf0.setType( SuggestionCompletionEngine.TYPE_STRING );
        asf0.setFactField( "asf0" );
        model.getActionCols().add( asf0 );

        ActionSetFieldCol52 asf1 = new ActionSetFieldCol52();
        asf1.setBoundName( "$f0" );
        asf1.setType( SuggestionCompletionEngine.TYPE_STRING );
        asf1.setFactField( "asf1" );
        model.getActionCols().add( asf1 );

        //---Setup data---
        data = new DynamicData();
        data.addRow();
        data.addRow();
        data.addColumn( 0,
                        makeColumnData( new String[]{"0", "1"} ),
                        true );
        data.addColumn( 1,
                        makeColumnData( new String[]{"desc0", "desc1"} ),
                        true );
        data.addColumn( 2,
                        makeColumnData( new String[]{"r0c2", "r1c2"} ),
                        true );
        data.addColumn( 3,
                        makeColumnData( new String[]{"r0c3", "r1c3"} ),
                        true );
        data.addColumn( 4,
                        makeColumnData( new String[]{"r0c4", "r1c4"} ),
                        true );
        data.addColumn( 5,
                        makeColumnData( new String[]{"r0c5", "r1c5"} ),
                        true );

        //---Setup manager---
        manager = new DecisionTableDropDownManager( model,
                                                    data );
    }

    private List<CellValue< ? extends Comparable< ? >>> makeColumnData(final String[] values) {
        final List<CellValue< ? extends Comparable< ? >>> columnData = new ArrayList<CellValue< ? extends Comparable< ? >>>();
        for ( String value : values ) {
            columnData.add( new CellValue<String>( value ) );
        }
        return columnData;
    }

    @Test
    public void testConstraints() {
        Context context;
        Map<String, String> values;

        //Row 0, Column 2
        context = new Context( 0,
                               2,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "c0p0" ) );
        assertNotNull( values.get( "c0p0" ) );
        assertEquals( "r0c2",
                      values.get( "c0p0" ) );

        assertTrue( values.containsKey( "c1p0" ) );
        assertNotNull( values.get( "c1p0" ) );
        assertEquals( "r0c3",
                      values.get( "c1p0" ) );

        //Row 1, Column 2
        context = new Context( 1,
                               2,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "c0p0" ) );
        assertNotNull( values.get( "c0p0" ) );
        assertEquals( "r1c2",
                      values.get( "c0p0" ) );

        assertTrue( values.containsKey( "c1p0" ) );
        assertNotNull( values.get( "c1p0" ) );
        assertEquals( "r1c3",
                      values.get( "c1p0" ) );

        //Row 0, Column 3
        context = new Context( 0,
                               3,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "c0p0" ) );
        assertNotNull( values.get( "c0p0" ) );
        assertEquals( "r0c2",
                      values.get( "c0p0" ) );

        assertTrue( values.containsKey( "c1p0" ) );
        assertNotNull( values.get( "c1p0" ) );
        assertEquals( "r0c3",
                      values.get( "c1p0" ) );

        //Row 1, Column 3
        context = new Context( 1,
                               3,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "c0p0" ) );
        assertNotNull( values.get( "c0p0" ) );
        assertEquals( "r1c2",
                      values.get( "c0p0" ) );

        assertTrue( values.containsKey( "c1p0" ) );
        assertNotNull( values.get( "c1p0" ) );
        assertEquals( "r1c3",
                      values.get( "c1p0" ) );
    }

    @Test
    public void testActions() {
        Context context;
        Map<String, String> values;

        //Row 0, Column 4
        context = new Context( 0,
                               4,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "asf0" ) );
        assertNotNull( values.get( "asf0" ) );
        assertEquals( "r0c4",
                      values.get( "asf0" ) );

        assertTrue( values.containsKey( "asf1" ) );
        assertNotNull( values.get( "asf1" ) );
        assertEquals( "r0c5",
                      values.get( "asf1" ) );

        //Row 1, Column 4
        context = new Context( 1,
                               4,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "asf0" ) );
        assertNotNull( values.get( "asf0" ) );
        assertEquals( "r1c4",
                      values.get( "asf0" ) );

        assertTrue( values.containsKey( "asf1" ) );
        assertNotNull( values.get( "asf1" ) );
        assertEquals( "r1c5",
                      values.get( "asf1" ) );

        //Row 0, Column 5
        context = new Context( 0,
                               5,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "asf0" ) );
        assertNotNull( values.get( "asf0" ) );
        assertEquals( "r0c4",
                      values.get( "asf0" ) );

        assertTrue( values.containsKey( "asf1" ) );
        assertNotNull( values.get( "asf1" ) );
        assertEquals( "r0c5",
                      values.get( "asf1" ) );

        //Row 1, Column 5
        context = new Context( 1,
                               5,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "asf0" ) );
        assertNotNull( values.get( "asf0" ) );
        assertEquals( "r1c4",
                      values.get( "asf0" ) );

        assertTrue( values.containsKey( "asf1" ) );
        assertNotNull( values.get( "asf1" ) );
        assertEquals( "r1c5",
                      values.get( "asf1" ) );
    }

}
