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
package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gwt.cell.client.Cell.Context;

public class TemplateDropDownManagerTests {

    private DynamicData                data;
    private TemplateModel              model;
    private TemplateDropDownManager    manager;
    private SuggestionCompletionEngine sce;

    @Before
    public void setup() {

        //---Setup model---
        model = new TemplateModel();

        //Setup LHS
        model.lhs = new IPattern[2];

        //Both fields are Template Keys
        FactPattern fp0 = new FactPattern();
        fp0.setFactType( "FT0" );

        SingleFieldConstraint sfc0p0 = new SingleFieldConstraint();
        sfc0p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p0.setFieldBinding( "$sfc0p0" );
        sfc0p0.setFactType( "FT0" );
        sfc0p0.setFieldName( "sfc0p0" );
        sfc0p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p0.setOperator( "==" );
        sfc0p0.setValue( "sfc0p0Value" );
        fp0.addConstraint( sfc0p0 );

        SingleFieldConstraint sfc1p0 = new SingleFieldConstraint();
        sfc1p0.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc1p0.setFieldBinding( "$sfc1p0" );
        sfc1p0.setFactType( "FT0" );
        sfc1p0.setFieldName( "sfc1p0" );
        sfc1p0.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p0.setOperator( "==" );
        sfc1p0.setValue( "sfc1p0Value" );
        fp0.addConstraint( sfc1p0 );

        model.lhs[0] = fp0;

        //One field is a Template Key the other is a literal
        FactPattern fp1 = new FactPattern();
        fp1.setFactType( "FT1" );

        SingleFieldConstraint sfc0p1 = new SingleFieldConstraint();
        sfc0p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p1.setFieldBinding( "$sfc0p1" );
        sfc0p1.setFactType( "FT1" );
        sfc0p1.setFieldName( "sfc0p1" );
        sfc0p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p1.setOperator( "==" );
        sfc0p1.setValue( "sfc0p1Value" );
        fp1.addConstraint( sfc0p1 );

        SingleFieldConstraint sfc1p1 = new SingleFieldConstraint();
        sfc1p1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc1p1.setFieldBinding( "$sfc1p1" );
        sfc1p1.setFactType( "FT1" );
        sfc1p1.setFieldName( "sfc1p1" );
        sfc1p1.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p1.setOperator( "==" );
        sfc1p1.setValue( "sfc1p1Value" );
        fp1.addConstraint( sfc1p1 );

        model.lhs[1] = fp1;

        //Setup RHS
        model.rhs = new IAction[1];

        //Both fields are Template Keys
        ActionInsertFact aif0 = new ActionInsertFact( "AIF0" );
        ActionFieldValue aif0f0 = new ActionFieldValue( "AIF0F0",
                                                        "AIF0F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif0f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif0.addFieldValue( aif0f0 );
        ActionFieldValue aif0f1 = new ActionFieldValue( "AIF0F1",
                                                        "AIF0F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif0f1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif0.addFieldValue( aif0f1 );
        model.rhs[0] = aif0;

        //---Setup data---
        data = new DynamicData();
        data.addRow();
        data.addRow();
        data.addColumn( 0,
                        makeColumnData( new String[]{"r0c0", "r1c0"} ),
                        true );
        data.addColumn( 1,
                        makeColumnData( new String[]{"r0c1", "r1c1"} ),
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

        //---Setup SCE---
        sce = new SuggestionCompletionEngine();

        //---Setup manager---
        manager = new TemplateDropDownManager( model,
                                               data,
                                               sce );
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

        //Row 0, Column 0
        context = new Context( 0,
                               0,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "sfc0p0" ) );
        assertNotNull( values.get( "sfc0p0" ) );
        assertEquals( "r0c0",
                      values.get( "sfc0p0" ) );

        assertTrue( values.containsKey( "sfc1p0" ) );
        assertNotNull( values.get( "sfc1p0" ) );
        assertEquals( "r0c1",
                      values.get( "sfc1p0" ) );

        //Row 1, Column 0
        context = new Context( 1,
                               0,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "sfc0p0" ) );
        assertNotNull( values.get( "sfc0p0" ) );
        assertEquals( "r1c0",
                      values.get( "sfc0p0" ) );

        assertTrue( values.containsKey( "sfc1p0" ) );
        assertNotNull( values.get( "sfc1p0" ) );
        assertEquals( "r1c1",
                      values.get( "sfc1p0" ) );

        //Row 0, Column 1
        context = new Context( 0,
                               1,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "sfc0p0" ) );
        assertNotNull( values.get( "sfc0p0" ) );
        assertEquals( "r0c0",
                      values.get( "sfc0p0" ) );

        assertTrue( values.containsKey( "sfc1p0" ) );
        assertNotNull( values.get( "sfc1p0" ) );
        assertEquals( "r0c1",
                      values.get( "sfc1p0" ) );

        //Row 1, Column 1
        context = new Context( 1,
                               1,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "sfc0p0" ) );
        assertNotNull( values.get( "sfc0p0" ) );
        assertEquals( "r1c0",
                      values.get( "sfc0p0" ) );

        assertTrue( values.containsKey( "sfc1p0" ) );
        assertNotNull( values.get( "sfc1p0" ) );
        assertEquals( "r1c1",
                      values.get( "sfc1p0" ) );

        //Row 0, Column 2
        context = new Context( 0,
                               2,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "sfc0p1" ) );
        assertNotNull( values.get( "sfc0p1" ) );
        assertEquals( "r0c2",
                      values.get( "sfc0p1" ) );

        assertTrue( values.containsKey( "sfc1p1" ) );
        assertNotNull( values.get( "sfc1p1" ) );
        assertEquals( "sfc1p1Value",
                      values.get( "sfc1p1" ) );

        //Row 1, Column 2
        context = new Context( 1,
                               2,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "sfc0p1" ) );
        assertNotNull( values.get( "sfc0p1" ) );
        assertEquals( "r1c2",
                      values.get( "sfc0p1" ) );

        assertTrue( values.containsKey( "sfc1p1" ) );
        assertNotNull( values.get( "sfc1p1" ) );
        assertEquals( "sfc1p1Value",
                      values.get( "sfc1p1" ) );
    }

    @Test
    public void testActions() {
        Context context;
        Map<String, String> values;

        //Row 0, Column 3
        context = new Context( 0,
                               3,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "AIF0F0" ) );
        assertNotNull( values.get( "AIF0F0" ) );
        assertEquals( "r0c3",
                      values.get( "AIF0F0" ) );

        assertTrue( values.containsKey( "AIF0F1" ) );
        assertNotNull( values.get( "AIF0F1" ) );
        assertEquals( "r0c4",
                      values.get( "AIF0F1" ) );

        //Row 1, Column 3
        context = new Context( 1,
                               3,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "AIF0F0" ) );
        assertNotNull( values.get( "AIF0F0" ) );
        assertEquals( "r1c3",
                      values.get( "AIF0F0" ) );

        assertTrue( values.containsKey( "AIF0F1" ) );
        assertNotNull( values.get( "AIF0F1" ) );
        assertEquals( "r1c4",
                      values.get( "AIF0F1" ) );

        //Row 0, Column 4
        context = new Context( 0,
                               4,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "AIF0F0" ) );
        assertNotNull( values.get( "AIF0F0" ) );
        assertEquals( "r0c3",
                      values.get( "AIF0F0" ) );

        assertTrue( values.containsKey( "AIF0F1" ) );
        assertNotNull( values.get( "AIF0F1" ) );
        assertEquals( "r0c4",
                      values.get( "AIF0F1" ) );

        //Row 1, Column 4
        context = new Context( 1,
                               4,
                               null );
        values = manager.getCurrentValueMap( context );
        assertNotNull( values );
        assertEquals( 2,
                      values.size() );

        assertTrue( values.containsKey( "AIF0F0" ) );
        assertNotNull( values.get( "AIF0F0" ) );
        assertEquals( "r1c3",
                      values.get( "AIF0F0" ) );

        assertTrue( values.containsKey( "AIF0F1" ) );
        assertNotNull( values.get( "AIF0F1" ) );
        assertEquals( "r1c4",
                      values.get( "AIF0F1" ) );
    }

}
