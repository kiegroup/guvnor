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

import java.math.BigDecimal;
import java.util.Calendar;
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
import org.drools.ide.common.client.modeldriven.dt.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt.DTCellValue;
import org.drools.ide.common.client.modeldriven.dt.DTDataTypes;
import org.drools.ide.common.client.modeldriven.dt.Pattern;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable52;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for CellValueFactory
 */
public class CellValueFactoryTests {

    private SuggestionCompletionEngine    sce     = null;
    private GuidedDecisionTable52   dt      = null;
    private DecisionTableCellValueFactory factory = null;

    private AttributeCol                  at1     = null;
    private AttributeCol                  at2     = null;
    private ConditionCol52                  c1      = null;
    private ConditionCol52                  c2      = null;
    private ConditionCol52                  c3      = null;
    private ConditionCol52                  c4      = null;
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

        dt = new GuidedDecisionTable52();

        at1 = new AttributeCol();
        at1.setAttribute( "salience" );
        at2 = new AttributeCol();
        at2.setAttribute( "enabled" );

        dt.getAttributeCols().add( at1 );
        dt.getAttributeCols().add( at2 );

        Pattern p1 = new Pattern();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( c1 );
        dt.getConditionPatterns().add( p1 );

        Pattern p2 = new Pattern();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getConditions().add( c2 );
        dt.getConditionPatterns().add( p2 );

        Pattern p3 = new Pattern();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getConditions().add( c3 );
        dt.getConditionPatterns().add( p3 );

        Pattern p4 = new Pattern();
        p4.setBoundName( "c4" );
        p4.setFactType( "Driver" );

        c4 = new ConditionCol52();
        c4.setFactField( "approved" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p4.getConditions().add( c4 );
        dt.getConditionPatterns().add( p4 );

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
        DecisionTableCellValueFactory.injectDateConvertor( JVMDateConverter.getInstance() );

    }

    @Test
    public void testDataTypes() {

        Calendar cdob = Calendar.getInstance();
        cdob.clear();
        cdob.set( 2000,
                  0,
                  1 );
        Date dob = cdob.getTime();

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
    public void testEmptyCells() {

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
    public void testTypedValues() {

        Calendar cdob = Calendar.getInstance();
        cdob.clear();
        cdob.set( 2000,
                  0,
                  1 );
        Date dob = cdob.getTime();

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
    public void testStringValues() {

        Calendar cdob = Calendar.getInstance();
        cdob.clear();
        cdob.set( 2000,
                  0,
                  1 );
        Date dob = cdob.getTime();

        DTCellValue dcv1 = new DTCellValue( "1" );
        DTCellValue dcv2 = new DTCellValue( "true" );
        DTCellValue dcv3 = new DTCellValue( "Michael" );
        DTCellValue dcv4 = new DTCellValue( "11" );
        DTCellValue dcv5 = new DTCellValue( "01-JAN-2000" );
        DTCellValue dcv6 = new DTCellValue( "true" );
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
