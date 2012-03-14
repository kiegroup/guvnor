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
import java.util.Set;
import java.util.jar.JarInputStream;

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
import org.drools.ide.common.server.rules.SuggestionCompletionLoader;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
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
        model.lhs = new IPattern[3];

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

        //Dependent enumerations
        FactPattern fp2 = new FactPattern();
        fp2.setFactType( "Fact" );

        SingleFieldConstraint sfc0p2 = new SingleFieldConstraint();
        sfc0p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc0p2.setFieldBinding( "$sfc0p2" );
        sfc0p2.setFactType( "Fact" );
        sfc0p2.setFieldName( "field1" );
        sfc0p2.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc0p2.setOperator( "==" );
        sfc0p2.setValue( "enum1" );
        fp2.addConstraint( sfc0p2 );

        SingleFieldConstraint sfc1p2 = new SingleFieldConstraint();
        sfc1p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc1p2.setFieldBinding( "$sfc1p2" );
        sfc1p2.setFactType( "Fact" );
        sfc1p2.setFieldName( "field2" );
        sfc1p2.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc1p2.setOperator( "==" );
        sfc1p2.setValue( "enum2" );
        fp2.addConstraint( sfc1p2 );

        SingleFieldConstraint sfc2p2 = new SingleFieldConstraint();
        sfc2p2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        sfc2p2.setFieldBinding( "$sfc2p2" );
        sfc2p2.setFactType( "Fact" );
        sfc2p2.setFieldName( "field3" );
        sfc2p2.setFieldType( SuggestionCompletionEngine.TYPE_STRING );
        sfc2p2.setOperator( "==" );
        sfc2p2.setValue( "enum3" );
        fp2.addConstraint( sfc2p2 );

        model.lhs[2] = fp2;

        //Setup RHS
        model.rhs = new IAction[2];

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

        //Dependent enumerations
        ActionInsertFact aif1 = new ActionInsertFact( "Fact" );
        ActionFieldValue aif1f0 = new ActionFieldValue( "field1",
                                                        "AIF1F0Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif1f0.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif1.addFieldValue( aif1f0 );
        ActionFieldValue aif1f1 = new ActionFieldValue( "field2",
                                                        "AIF1F1Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif1f1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif1.addFieldValue( aif1f1 );
        ActionFieldValue aif1f2 = new ActionFieldValue( "field3",
                                                        "AIF1F2Value",
                                                        SuggestionCompletionEngine.TYPE_STRING );
        aif1f2.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        aif1.addFieldValue( aif1f2 );
        model.rhs[1] = aif1;

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
                        makeColumnData( new String[]{"val1", "val1"} ),
                        true );
        data.addColumn( 4,
                        makeColumnData( new String[]{"val1a", "val1b"} ),
                        true );
        data.addColumn( 5,
                        makeColumnData( new String[]{"val1a1", "val1b1"} ),
                        true );
        data.addColumn( 6,
                        makeColumnData( new String[]{"r0c3", "r1c3"} ),
                        true );
        data.addColumn( 7,
                        makeColumnData( new String[]{"r0c4", "r1c4"} ),
                        true );
        data.addColumn( 8,
                        makeColumnData( new String[]{"val1", "val1"} ),
                        true );
        data.addColumn( 9,
                        makeColumnData( new String[]{"val1a", "val1b"} ),
                        true );
        data.addColumn( 10,
                        makeColumnData( new String[]{"val1a1", "val1b1"} ),
                        true );

        //---Setup SCE---
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        List<String> enums = new ArrayList<String>();
        final String enumDefinition = "'Fact.field1' : ['val1', 'val2'], "
                                      + "'Fact.field2[field1=val1]' : ['val1a', 'val1b'], "
                                      + "'Fact.field3[field2=val1a]' : ['val1a1', 'val1a2'], "
                                      + "'Fact.field3[field2=val1b]' : ['val1b1', 'val1b2']";
        enums.add( enumDefinition );

        sce = loader.getSuggestionEngine( "",
                                          new ArrayList<JarInputStream>(),
                                          new ArrayList<DSLTokenizedMappingFile>(),
                                          enums );

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

        //Row 0, Column 6
        context = new Context( 0,
                               6,
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

        //Row 1, Column 6
        context = new Context( 1,
                               6,
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

        //Row 0, Column 7
        context = new Context( 0,
                               7,
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

        //Row 1, Column 7
        context = new Context( 1,
                               7,
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

    @Test
    public void testConstraintsEnumDependencies() {

        Context context;
        Set<Integer> columns;

        context = new Context( 0,
                               3,
                               null );
        columns = manager.getDependentColumnIndexes( context );
        assertNotNull( columns );
        assertEquals( 2,
                      columns.size() );
        assertTrue( columns.contains( new Integer( 4 ) ) );
        assertTrue( columns.contains( new Integer( 5 ) ) );

        context = new Context( 0,
                               4,
                               null );
        columns = manager.getDependentColumnIndexes( context );
        assertNotNull( columns );
        assertEquals( 1,
                      columns.size() );
        assertTrue( columns.contains( new Integer( 5 ) ) );
    }

    @Test
    public void testActionsEnumDependencies() {

        Context context;
        Set<Integer> columns;

        context = new Context( 0,
                               8,
                               null );
        columns = manager.getDependentColumnIndexes( context );
        assertNotNull( columns );
        assertEquals( 2,
                      columns.size() );
        assertTrue( columns.contains( new Integer( 9 ) ) );
        assertTrue( columns.contains( new Integer( 10 ) ) );

        context = new Context( 0,
                               9,
                               null );
        columns = manager.getDependentColumnIndexes( context );
        assertNotNull( columns );
        assertEquals( 1,
                      columns.size() );
        assertTrue( columns.contains( new Integer( 10 ) ) );
    }

}
