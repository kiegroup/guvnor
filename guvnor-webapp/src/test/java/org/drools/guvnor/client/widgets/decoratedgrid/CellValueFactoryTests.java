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
package org.drools.guvnor.client.widgets.decoratedgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.drools.guvnor.client.decisiontable.widget.DecisionTableCellValueFactory;
import org.drools.guvnor.server.util.JVMDateConverter;
import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTCellValue;
import org.drools.ide.common.client.modeldriven.dt.DTDataTypes;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for CellValueFactory
 */
public class CellValueFactoryTests {

    private SuggestionCompletionEngine    sce     = null;
    private TypeSafeGuidedDecisionTable   dt      = null;
    private DecisionTableCellValueFactory factory = null;

    private AttributeCol                  at1     = null;
    private AttributeCol                  at2     = null;
    private ConditionCol                  c1      = null;
    private ConditionCol                  c2      = null;
    private ConditionCol                  c3      = null;
    private ConditionCol                  c4      = null;
    private ActionSetFieldCol             a1      = null;
    private ActionInsertFactCol           a2      = null;

    @Before
    @SuppressWarnings("serial")
    public void setup() {
        sce = new SuggestionCompletionEngine();

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

        dt = new TypeSafeGuidedDecisionTable();

        at1 = new AttributeCol();
        at1.setAttribute( "salience" );
        at2 = new AttributeCol();
        at2.setAttribute( "enabled" );

        dt.getAttributeCols().add( at1 );
        dt.getAttributeCols().add( at2 );

        c1 = new ConditionCol();
        c1.setBoundName( "c1" );
        c1.setFactType( "Driver" );
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        dt.getConditionCols().add( c1 );

        c2 = new ConditionCol();
        c2.setBoundName( "c2" );
        c2.setFactType( "Driver" );
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        dt.getConditionCols().add( c2 );

        c3 = new ConditionCol();
        c3.setBoundName( "c3" );
        c3.setFactType( "Driver" );
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        dt.getConditionCols().add( c3 );

        c4 = new ConditionCol();
        c4.setBoundName( "c4" );
        c4.setFactType( "Driver" );
        c4.setFactField( "approved" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        dt.getConditionCols().add( c4 );

        a1 = new ActionSetFieldCol();
        a1.setBoundName( "c1" );
        a1.setFactField( "name" );
        dt.getActionCols().add( a1 );

        a2 = new ActionInsertFactCol();
        a2.setBoundName( "a2" );
        a2.setFactType( "Driver" );
        a2.setFactField( "name" );
        dt.getActionCols().add( a2 );

        factory = new DecisionTableCellValueFactory( sce,
                                                                                   dt );
        factory.injectDateConvertor( JVMDateConverter.getInstance() );

    }

    @Test
    public void testConversionDataTypes() {

        @SuppressWarnings("deprecation")
        Date dob = new Date( 2000,
                             1,
                             1 );

        DTCellValue dcv1 = new DTCellValue( Boolean.TRUE );
        DTCellValue dcv2 = new DTCellValue( dob );
        DTCellValue dcv3 = new DTCellValue( 1 );
        DTCellValue dcv4 = new DTCellValue( 1.0 );
        DTCellValue dcv5 = new DTCellValue( new BigDecimal( 1 ) );
        DTCellValue dcv6 = new DTCellValue( "Smurf" );

        assertEquals( dcv1.getDataType(),
                      DTDataTypes.BOOLEAN );
        assertEquals( dcv2.getDataType(),
                      DTDataTypes.DATE );
        assertEquals( dcv3.getDataType(),
                      DTDataTypes.NUMERIC );
        assertEquals( dcv4.getDataType(),
                      DTDataTypes.NUMERIC );
        assertEquals( dcv5.getDataType(),
                      DTDataTypes.NUMERIC );
        assertEquals( dcv6.getDataType(),
                      DTDataTypes.STRING );

    }

    @Test
    public void testEmptyValues() {

        CellValue< ? extends Comparable< ? >> cell1 = factory.makeCellValue( at1,
                                                                             0,
                                                                             0 );
        CellValue< ? extends Comparable< ? >> cell2 = factory.makeCellValue( at2,
                                                                             0,
                                                                             1 );
        CellValue< ? extends Comparable< ? >> cell3 = factory.makeCellValue( c1,
                                                                             0,
                                                                             2 );
        CellValue< ? extends Comparable< ? >> cell4 = factory.makeCellValue( c2,
                                                                             0,
                                                                             3 );
        CellValue< ? extends Comparable< ? >> cell5 = factory.makeCellValue( c3,
                                                                             0,
                                                                             4 );
        CellValue< ? extends Comparable< ? >> cell6 = factory.makeCellValue( c4,
                                                                             0,
                                                                             4 );
        CellValue< ? extends Comparable< ? >> cell7 = factory.makeCellValue( a1,
                                                                             0,
                                                                             5 );
        CellValue< ? extends Comparable< ? >> cell8 = factory.makeCellValue( a2,
                                                                             0,
                                                                             6 );

        assertEquals( cell1.getValue(),
                      null );
        assertEquals( cell2.getValue(),
                      Boolean.FALSE );
        assertEquals( cell3.getValue(),
                      null );
        assertEquals( cell4.getValue(),
                      null );
        assertEquals( cell5.getValue(),
                      null );
        assertEquals( cell6.getValue(),
                      Boolean.FALSE );
        assertEquals( cell7.getValue(),
                      null );
        assertEquals( cell8.getValue(),
                      null );
    }

    @Test
    public void testWithValues() {

        @SuppressWarnings("deprecation")
        Date dob = new Date( 2000,
                             1,
                             1 );

        DTCellValue dcv1 = new DTCellValue( 1 );
        DTCellValue dcv2 = new DTCellValue( Boolean.TRUE );
        DTCellValue dcv3 = new DTCellValue( "Michael" );
        DTCellValue dcv4 = new DTCellValue( 11 );
        DTCellValue dcv5 = new DTCellValue( dob );
        DTCellValue dcv6 = new DTCellValue( Boolean.TRUE );
        DTCellValue dcv7 = new DTCellValue( "Mike" );
        DTCellValue dcv8 = new DTCellValue( "Mike" );

        CellValue< ? extends Comparable< ? >> cell1 = factory.makeCellValue( at1,
                                                                             0,
                                                                             0,
                                                                             dcv1 );
        CellValue< ? extends Comparable< ? >> cell2 = factory.makeCellValue( at2,
                                                                             0,
                                                                             1,
                                                                             dcv2 );
        CellValue< ? extends Comparable< ? >> cell3 = factory.makeCellValue( c1,
                                                                             0,
                                                                             2,
                                                                             dcv3 );
        CellValue< ? extends Comparable< ? >> cell4 = factory.makeCellValue( c2,
                                                                             0,
                                                                             3,
                                                                             dcv4 );
        CellValue< ? extends Comparable< ? >> cell5 = factory.makeCellValue( c3,
                                                                             0,
                                                                             4,
                                                                             dcv5 );
        CellValue< ? extends Comparable< ? >> cell6 = factory.makeCellValue( c4,
                                                                             0,
                                                                             4,
                                                                             dcv6 );
        CellValue< ? extends Comparable< ? >> cell7 = factory.makeCellValue( a1,
                                                                             0,
                                                                             5,
                                                                             dcv7 );
        CellValue< ? extends Comparable< ? >> cell8 = factory.makeCellValue( a2,
                                                                             0,
                                                                             6,
                                                                             dcv8 );

        assertTrue( cell1.getValue() instanceof BigDecimal );
        assertTrue( cell2.getValue() instanceof Boolean );
        assertTrue( cell3.getValue() instanceof String );
        assertTrue( cell4.getValue() instanceof BigDecimal );
        assertTrue( cell5.getValue() instanceof Date );
        assertTrue( cell6.getValue() instanceof Boolean );
        assertTrue( cell7.getValue() instanceof String );
        assertTrue( cell8.getValue() instanceof String );

        assertEquals( cell1.getValue(),
                      new BigDecimal( 1 ) );
        assertEquals( cell2.getValue(),
                      Boolean.TRUE );
        assertEquals( cell3.getValue(),
                      "Michael" );
        assertEquals( cell4.getValue(),
                      new BigDecimal( 11 ) );
        assertEquals( cell5.getValue(),
                      dob );
        assertEquals( cell6.getValue(),
                      Boolean.TRUE );
        assertEquals( cell7.getValue(),
                      "Mike" );
        assertEquals( cell8.getValue(),
                      "Mike" );

    }

    @Test
    public void testConversionEmptyValues() {

        DTCellValue dcv1 = new DTCellValue( "" );
        DTCellValue dcv2 = new DTCellValue( "" );
        DTCellValue dcv3 = new DTCellValue( "" );
        DTCellValue dcv4 = new DTCellValue( "" );
        DTCellValue dcv5 = new DTCellValue( "" );
        DTCellValue dcv6 = new DTCellValue( "" );
        DTCellValue dcv7 = new DTCellValue( "" );
        DTCellValue dcv8 = new DTCellValue( "" );

        CellValue< ? extends Comparable< ? >> cell1 = factory.makeCellValue( at1,
                                                                             0,
                                                                             0,
                                                                             dcv1 );
        CellValue< ? extends Comparable< ? >> cell2 = factory.makeCellValue( at2,
                                                                             0,
                                                                             1,
                                                                             dcv2 );
        CellValue< ? extends Comparable< ? >> cell3 = factory.makeCellValue( c1,
                                                                             0,
                                                                             2,
                                                                             dcv3 );
        CellValue< ? extends Comparable< ? >> cell4 = factory.makeCellValue( c2,
                                                                             0,
                                                                             3,
                                                                             dcv4 );
        CellValue< ? extends Comparable< ? >> cell5 = factory.makeCellValue( c3,
                                                                             0,
                                                                             4,
                                                                             dcv5 );
        CellValue< ? extends Comparable< ? >> cell6 = factory.makeCellValue( c4,
                                                                             0,
                                                                             4,
                                                                             dcv6 );
        CellValue< ? extends Comparable< ? >> cell7 = factory.makeCellValue( a1,
                                                                             0,
                                                                             5,
                                                                             dcv7 );
        CellValue< ? extends Comparable< ? >> cell8 = factory.makeCellValue( a2,
                                                                             0,
                                                                             6,
                                                                             dcv8 );

        assertEquals( cell1.getValue(),
                      null );
        assertEquals( cell2.getValue(),
                      Boolean.FALSE );
        assertEquals( cell3.getValue(),
                      null );
        assertEquals( cell4.getValue(),
                      null );
        assertEquals( cell5.getValue(),
                      null );
        assertEquals( cell6.getValue(),
                      Boolean.FALSE );
        assertEquals( cell7.getValue(),
                      null );
        assertEquals( cell8.getValue(),
                      null );

        assertEquals( dcv1.getDataType(),
                      DTDataTypes.NUMERIC );
        assertEquals( dcv2.getDataType(),
                      DTDataTypes.BOOLEAN );
        assertEquals( dcv3.getDataType(),
                      DTDataTypes.STRING );
        assertEquals( dcv4.getDataType(),
                      DTDataTypes.NUMERIC );
        assertEquals( dcv5.getDataType(),
                      DTDataTypes.DATE );
        assertEquals( dcv6.getDataType(),
                      DTDataTypes.BOOLEAN );
        assertEquals( dcv7.getDataType(),
                      DTDataTypes.STRING );
        assertEquals( dcv8.getDataType(),
                      DTDataTypes.STRING );

    }

}
