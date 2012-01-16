/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.RowExpander.ColumnValues;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.RowExpander.RowIterator;
import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.server.rules.SuggestionCompletionLoader;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.junit.Test;

public class RowExpanderTests {

    @Test
    @SuppressWarnings("serial")
    public void testExpansionNoExpansion() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE ),
                                new ModelField( "approved",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_BOOLEAN )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        Pattern52 p4 = new Pattern52();
        p4.setBoundName( "c4" );
        p4.setFactType( "Driver" );

        ConditionCol52 c4 = new ConditionCol52();
        c4.setFactField( "approved" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p4.getChildColumns().add( c4 );
        dtable.getConditions().add( p4 );

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName( "c1" );
        a1.setFactField( "name" );
        dtable.getActionCols().add( a1 );

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName( "a2" );
        a2.setFactType( "Driver" );
        a2.setFactField( "name" );
        dtable.getActionCols().add( a2 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        List<ColumnValues> columns = re.getColumns();
        assertEquals( 8,
                      columns.size() );
        assertEquals( 1,
                      columns.get( 0 ).values.size() );
        assertEquals( 1,
                      columns.get( 1 ).values.size() );
        assertEquals( 1,
                      columns.get( 2 ).values.size() );
        assertEquals( 1,
                      columns.get( 3 ).values.size() );
        assertEquals( 1,
                      columns.get( 4 ).values.size() );
        assertEquals( 1,
                      columns.get( 5 ).values.size() );
        assertEquals( 1,
                      columns.get( 6 ).values.size() );
        assertEquals( 1,
                      columns.get( 7 ).values.size() );

        RowIterator ri = re.iterator();
        assertFalse( ri.hasNext() );

    }

    @Test
    @SuppressWarnings("serial")
    public void testExpansionWithValuesList() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE ),
                                new ModelField( "approved",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_BOOLEAN )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c3.setValueList( "c3a,c3b" );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        Pattern52 p4 = new Pattern52();
        p4.setBoundName( "c4" );
        p4.setFactType( "Driver" );

        ConditionCol52 c4 = new ConditionCol52();
        c4.setFactField( "approved" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c4.setValueList( "c4a,c4b" );
        p4.getChildColumns().add( c4 );
        dtable.getConditions().add( p4 );

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName( "c1" );
        a1.setFactField( "name" );
        a1.setValueList( "a1a,a1b" );
        dtable.getActionCols().add( a1 );

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName( "a2" );
        a2.setFactType( "Driver" );
        a2.setFactField( "name" );
        a2.setValueList( "a2a,a2b" );
        dtable.getActionCols().add( a2 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        List<ColumnValues> columns = re.getColumns();
        assertEquals( 8,
                      columns.size() );

        assertEquals( 1,
                      columns.get( 0 ).values.size() );
        assertEquals( 1,
                      columns.get( 1 ).values.size() );
        assertEquals( 2,
                      columns.get( 2 ).values.size() );
        assertEquals( 2,
                      columns.get( 3 ).values.size() );
        assertEquals( 2,
                      columns.get( 4 ).values.size() );
        assertEquals( 2,
                      columns.get( 5 ).values.size() );
        assertEquals( 1,
                      columns.get( 6 ).values.size() );
        assertEquals( 1,
                      columns.get( 7 ).values.size() );

        assertEquals( "c1a",
                      columns.get( 2 ).values.get( 0 ) );
        assertEquals( "c1b",
                      columns.get( 2 ).values.get( 1 ) );

        assertEquals( "c2a",
                      columns.get( 3 ).values.get( 0 ) );
        assertEquals( "c2b",
                      columns.get( 3 ).values.get( 1 ) );

        assertEquals( "c3a",
                      columns.get( 4 ).values.get( 0 ) );
        assertEquals( "c3b",
                      columns.get( 4 ).values.get( 1 ) );

        assertEquals( "c4a",
                      columns.get( 5 ).values.get( 0 ) );
        assertEquals( "c4b",
                      columns.get( 5 ).values.get( 1 ) );

        assertNull( columns.get( 6 ).values.get( 0 ) );

        assertNull( columns.get( 7 ).values.get( 0 ) );

        RowIterator ri = re.iterator();
        assertTrue( ri.hasNext() );

    }

    @Test
    public void testExpansionWithGuvnorEnums() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        String pkg = "package org.test\n"
                     + "declare Driver\n"
                     + "name: String\n"
                     + "age: Integer\n"
                     + "dateOfBirth: Date\n"
                     + "approved: Boolean\n"
                     + "end\n";

        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        List<String> enums = new ArrayList<String>();

        enums.add( "'Driver.name' : ['f1a', 'f1b'], 'Driver.age' : ['f2a', 'f2b'], 'Driver.dateOfBirth' : ['f3a', 'f3b'], 'Driver.approved' : ['f4a', 'f4b']" );

        SuggestionCompletionEngine sce = loader.getSuggestionEngine( pkg,
                                                                     new ArrayList<JarInputStream>(),
                                                                     new ArrayList<DSLTokenizedMappingFile>(),
                                                                     enums );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        Pattern52 p4 = new Pattern52();
        p4.setBoundName( "c4" );
        p4.setFactType( "Driver" );

        ConditionCol52 c4 = new ConditionCol52();
        c4.setFactField( "approved" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p4.getChildColumns().add( c4 );
        dtable.getConditions().add( p4 );

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName( "c1" );
        a1.setFactField( "name" );
        dtable.getActionCols().add( a1 );

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName( "a2" );
        a2.setFactType( "Driver" );
        a2.setFactField( "name" );
        dtable.getActionCols().add( a2 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        List<ColumnValues> columns = re.getColumns();
        assertEquals( 8,
                      columns.size() );

        assertEquals( 1,
                      columns.get( 0 ).values.size() );
        assertEquals( 1,
                      columns.get( 1 ).values.size() );
        assertEquals( 2,
                      columns.get( 2 ).values.size() );
        assertEquals( 2,
                      columns.get( 3 ).values.size() );
        assertEquals( 2,
                      columns.get( 4 ).values.size() );
        assertEquals( 2,
                      columns.get( 5 ).values.size() );
        assertEquals( 1,
                      columns.get( 6 ).values.size() );
        assertEquals( 1,
                      columns.get( 7 ).values.size() );

        assertEquals( "f1a",
                      columns.get( 2 ).values.get( 0 ) );
        assertEquals( "f1b",
                      columns.get( 2 ).values.get( 1 ) );

        assertEquals( "f2a",
                      columns.get( 3 ).values.get( 0 ) );
        assertEquals( "f2b",
                      columns.get( 3 ).values.get( 1 ) );

        assertEquals( "f3a",
                      columns.get( 4 ).values.get( 0 ) );
        assertEquals( "f3b",
                      columns.get( 4 ).values.get( 1 ) );

        assertEquals( "f4a",
                      columns.get( 5 ).values.get( 0 ) );
        assertEquals( "f4b",
                      columns.get( 5 ).values.get( 1 ) );

        assertNull( columns.get( 6 ).values.get( 0 ) );

        assertNull( columns.get( 7 ).values.get( 0 ) );

        RowIterator ri = re.iterator();
        assertTrue( ri.hasNext() );

    }

    @SuppressWarnings("serial")
    @Test
    public void testColumnValues() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Driver",
                        new ModelField[]{
                                new ModelField( "name",
                                                String.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_STRING )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        List<ColumnValues> columns = re.getColumns();
        assertEquals( 3,
                      columns.size() );

        assertNull( columns.get( 0 ).getCurrentValue() );
        assertNull( columns.get( 1 ).getCurrentValue() );
        assertEquals( "c1a",
                      columns.get( 2 ).getCurrentValue() );
        columns.get( 2 ).advanceColumnValue();
        assertEquals( "c1b",
                      columns.get( 2 ).getCurrentValue() );
        columns.get( 2 ).advanceColumnValue();
        assertEquals( "c1a",
                      columns.get( 2 ).getCurrentValue() );
        columns.get( 2 ).advanceColumnValue();
        assertEquals( "c1b",
                      columns.get( 2 ).getCurrentValue() );

    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesList1() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes( new HashMap<String, ModelField[]>() {
            {
                put( "Driver",
                        new ModelField[]{
                                new ModelField( "name",
                                                String.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_STRING )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        assertEquals( 3,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 2,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 0 ).get( 2 ) );
        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 1 ).get( 2 ) );

    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesList2() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        assertEquals( 4,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 4,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 0 ).get( 2 ) );
        assertEquals( "c2a",
                      rows.get( 0 ).get( 3 ) );
        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 1 ).get( 2 ) );
        assertEquals( "c2a",
                      rows.get( 1 ).get( 3 ) );
        assertNull( rows.get( 2 ).get( 0 ) );
        assertNull( rows.get( 2 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 2 ).get( 2 ) );
        assertEquals( "c2b",
                      rows.get( 2 ).get( 3 ) );
        assertNull( rows.get( 3 ).get( 0 ) );
        assertNull( rows.get( 3 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 3 ).get( 2 ) );
        assertEquals( "c2b",
                      rows.get( 3 ).get( 3 ) );
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesList3() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        assertEquals( 5,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 4,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 0 ).get( 2 ) );
        assertEquals( "c2a",
                      rows.get( 0 ).get( 3 ) );
        assertNull( rows.get( 0 ).get( 4 ) );

        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 1 ).get( 2 ) );
        assertEquals( "c2a",
                      rows.get( 1 ).get( 3 ) );
        assertNull( rows.get( 1 ).get( 4 ) );

        assertNull( rows.get( 2 ).get( 0 ) );
        assertNull( rows.get( 2 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 2 ).get( 2 ) );
        assertEquals( "c2b",
                      rows.get( 2 ).get( 3 ) );
        assertNull( rows.get( 2 ).get( 4 ) );

        assertNull( rows.get( 3 ).get( 0 ) );
        assertNull( rows.get( 3 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 3 ).get( 2 ) );
        assertEquals( "c2b",
                      rows.get( 3 ).get( 3 ) );
        assertNull( rows.get( 3 ).get( 4 ) );
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndDefaultValues() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        c1.setDefaultValue( "c1default" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        c2.setDefaultValue( "c2default" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c3.setDefaultValue( "c3default" );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        assertEquals( 5,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 4,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 0 ).get( 2 ) );
        assertEquals( "c2a",
                      rows.get( 0 ).get( 3 ) );
        assertEquals( "c3default",
                      rows.get( 0 ).get( 4 ) );

        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 1 ).get( 2 ) );
        assertEquals( "c2a",
                      rows.get( 1 ).get( 3 ) );
        assertEquals( "c3default",
                      rows.get( 1 ).get( 4 ) );

        assertNull( rows.get( 2 ).get( 0 ) );
        assertNull( rows.get( 2 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 2 ).get( 2 ) );
        assertEquals( "c2b",
                      rows.get( 2 ).get( 3 ) );
        assertEquals( "c3default",
                      rows.get( 2 ).get( 4 ) );

        assertNull( rows.get( 3 ).get( 0 ) );
        assertNull( rows.get( 3 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 3 ).get( 2 ) );
        assertEquals( "c2b",
                      rows.get( 3 ).get( 3 ) );
        assertEquals( "c3default",
                      rows.get( 3 ).get( 4 ) );
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabled1() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c3.setValueList( "c3a,c3b" );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        RowExpander re = new RowExpander( dtable,
                                          sce );
        re.setExpandColumn( c1,
                            false );
        re.setExpandColumn( c2,
                            false );
        re.setExpandColumn( c3,
                            false );

        assertEquals( 5,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        assertFalse( i.hasNext() );

    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabled2() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c3.setValueList( "c3a,c3b" );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        RowExpander re = new RowExpander( dtable,
                                          sce );
        re.setExpandColumn( c1,
                            false );
        re.setExpandColumn( c2,
                            false );

        assertEquals( 5,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 2,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertNull( rows.get( 0 ).get( 2 ) );
        assertNull( rows.get( 0 ).get( 3 ) );
        assertEquals( "c3a",
                      rows.get( 0 ).get( 4 ) );

        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertNull( rows.get( 1 ).get( 2 ) );
        assertNull( rows.get( 1 ).get( 3 ) );
        assertEquals( "c3b",
                      rows.get( 1 ).get( 4 ) );

    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabled3() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c3.setValueList( "c3a,c3b" );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        RowExpander re = new RowExpander( dtable,
                                          sce );
        re.setExpandColumn( c2,
                            false );

        assertEquals( 5,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 4,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 0 ).get( 2 ) );
        assertNull( rows.get( 0 ).get( 3 ) );
        assertEquals( "c3a",
                      rows.get( 0 ).get( 4 ) );

        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 1 ).get( 2 ) );
        assertNull( rows.get( 1 ).get( 3 ) );
        assertEquals( "c3a",
                      rows.get( 1 ).get( 4 ) );

        assertNull( rows.get( 2 ).get( 0 ) );
        assertNull( rows.get( 2 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 2 ).get( 2 ) );
        assertNull( rows.get( 2 ).get( 3 ) );
        assertEquals( "c3b",
                      rows.get( 2 ).get( 4 ) );

        assertNull( rows.get( 3 ).get( 0 ) );
        assertNull( rows.get( 3 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 3 ).get( 2 ) );
        assertNull( rows.get( 3 ).get( 3 ) );
        assertEquals( "c3b",
                      rows.get( 3 ).get( 4 ) );

    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabledAndDefaultValues() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValueList( "c1a,c1b" );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c2.setValueList( "c2a,c2b" );
        c2.setDefaultValue( "c2default" );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c3.setValueList( "c3a,c3b" );
        p3.getChildColumns().add( c3 );
        dtable.getConditions().add( p3 );

        RowExpander re = new RowExpander( dtable,
                                          sce );
        re.setExpandColumn( c2,
                            false );

        assertEquals( 5,
                      re.getColumns().size() );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 4,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 0 ).get( 2 ) );
        assertEquals( "c2default",
                      rows.get( 0 ).get( 3 ) );
        assertEquals( "c3a",
                      rows.get( 0 ).get( 4 ) );

        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 1 ).get( 2 ) );
        assertEquals( "c2default",
                      rows.get( 1 ).get( 3 ) );
        assertEquals( "c3a",
                      rows.get( 1 ).get( 4 ) );

        assertNull( rows.get( 2 ).get( 0 ) );
        assertNull( rows.get( 2 ).get( 1 ) );
        assertEquals( "c1a",
                      rows.get( 2 ).get( 2 ) );
        assertEquals( "c2default",
                      rows.get( 2 ).get( 3 ) );
        assertEquals( "c3b",
                      rows.get( 2 ).get( 4 ) );

        assertNull( rows.get( 3 ).get( 0 ) );
        assertNull( rows.get( 3 ).get( 1 ) );
        assertEquals( "c1b",
                      rows.get( 3 ).get( 2 ) );
        assertEquals( "c2default",
                      rows.get( 3 ).get( 3 ) );
        assertEquals( "c3b",
                      rows.get( 3 ).get( 4 ) );

    }

    @Test
    @SuppressWarnings("serial")
    public void testExpansionWithLimitedEntry() {
        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();
        dtable.setTableFormat( TableFormat.LIMITED_ENTRY );
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
                                new ModelField( "dateOfBirth",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_DATE ),
                                new ModelField( "approved",
                                                Boolean.class.getName(),
                                                FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                SuggestionCompletionEngine.TYPE_BOOLEAN )
                        } );
            }
        } );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        LimitedEntryConditionCol52 c1 = new LimitedEntryConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValue( new DTCellValue52( "Mike" ) );
        p1.getChildColumns().add( c1 );
        dtable.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        LimitedEntryConditionCol52 c2 = new LimitedEntryConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setValue( new DTCellValue52( 25 ) );
        p2.getChildColumns().add( c2 );
        dtable.getConditions().add( p2 );

        LimitedEntryActionSetFieldCol52 a1 = new LimitedEntryActionSetFieldCol52();
        a1.setBoundName( "c1" );
        a1.setFactField( "name" );
        a1.setValue( new DTCellValue52( "a1name" ) );
        dtable.getActionCols().add( a1 );

        LimitedEntryActionInsertFactCol52 a2 = new LimitedEntryActionInsertFactCol52();
        a2.setBoundName( "a2" );
        a2.setFactType( "Driver" );
        a2.setFactField( "name" );
        a2.setValue( new DTCellValue52( "a2name" ) );
        dtable.getActionCols().add( a2 );

        RowExpander re = new RowExpander( dtable,
                                          sce );

        List<ColumnValues> columns = re.getColumns();
        assertEquals( 6,
                      columns.size() );

        assertEquals( 1,
                      columns.get( 0 ).values.size() );
        assertEquals( 1,
                      columns.get( 1 ).values.size() );
        assertEquals( 2,
                      columns.get( 2 ).values.size() );
        assertEquals( 2,
                      columns.get( 3 ).values.size() );
        assertEquals( 1,
                      columns.get( 4 ).values.size() );
        assertEquals( 1,
                      columns.get( 5 ).values.size() );

        assertEquals( "true",
                      columns.get( 2 ).values.get( 0 ) );
        assertEquals( "false",
                      columns.get( 2 ).values.get( 1 ) );

        assertEquals( "true",
                      columns.get( 3 ).values.get( 0 ) );
        assertEquals( "false",
                      columns.get( 3 ).values.get( 1 ) );

        assertEquals( "false",
                      columns.get( 4 ).values.get( 0 ) );

        assertEquals( "false",
                      columns.get( 5 ).values.get( 0 ) );

        RowIterator i = re.iterator();
        List<List<String>> rows = new ArrayList<List<String>>();
        while ( i.hasNext() ) {
            List<String> row = i.next();
            rows.add( row );
        }

        assertEquals( 4,
                      rows.size() );

        assertNull( rows.get( 0 ).get( 0 ) );
        assertNull( rows.get( 0 ).get( 1 ) );
        assertEquals( "true",
                      rows.get( 0 ).get( 2 ) );
        assertEquals( "true",
                      rows.get( 0 ).get( 3 ) );
        assertEquals( "false",
                      rows.get( 0 ).get( 4 ) );
        assertEquals( "false",
                      rows.get( 0 ).get( 5 ) );

        assertNull( rows.get( 1 ).get( 0 ) );
        assertNull( rows.get( 1 ).get( 1 ) );
        assertEquals( "false",
                      rows.get( 1 ).get( 2 ) );
        assertEquals( "true",
                      rows.get( 1 ).get( 3 ) );
        assertEquals( "false",
                      rows.get( 1 ).get( 4 ) );
        assertEquals( "false",
                      rows.get( 1 ).get( 5 ) );

        assertNull( rows.get( 2 ).get( 0 ) );
        assertNull( rows.get( 2 ).get( 1 ) );
        assertEquals( "true",
                      rows.get( 2 ).get( 2 ) );
        assertEquals( "false",
                      rows.get( 2 ).get( 3 ) );
        assertEquals( "false",
                      rows.get( 2 ).get( 4 ) );
        assertEquals( "false",
                      rows.get( 2 ).get( 5 ) );

        assertNull( rows.get( 3 ).get( 0 ) );
        assertNull( rows.get( 3 ).get( 1 ) );
        assertEquals( "false",
                      rows.get( 3 ).get( 2 ) );
        assertEquals( "false",
                      rows.get( 3 ).get( 3 ) );
        assertEquals( "false",
                      rows.get( 3 ).get( 4 ) );
        assertEquals( "false",
                      rows.get( 3 ).get( 5 ) );

    }

}
