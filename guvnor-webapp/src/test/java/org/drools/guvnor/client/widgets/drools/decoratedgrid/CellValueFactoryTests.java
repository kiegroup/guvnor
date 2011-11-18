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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableCellValueFactory;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.server.util.JVMDateConverter;
import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for CellValueFactory
 */
public class CellValueFactoryTests {

    private SuggestionCompletionEngine    sce     = null;
    private GuidedDecisionTable52         dt      = null;
    private DecisionTableCellValueFactory factory = null;

    private AttributeCol52                at1     = null;
    private AttributeCol52                at2     = null;
    private ConditionCol52                c1      = null;
    private ConditionCol52                c2      = null;
    private ConditionCol52                c3      = null;
    private ConditionCol52                c4      = null;
    private ActionSetFieldCol52           a1      = null;
    private ActionInsertFactCol52         a2      = null;

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

        at1 = new AttributeCol52();
        at1.setAttribute( "salience" );
        at2 = new AttributeCol52();
        at2.setAttribute( "enabled" );

        dt.getAttributeCols().add( at1 );
        dt.getAttributeCols().add( at2 );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "c1" );
        p1.setFactType( "Driver" );

        c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setOperator( "==" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getConditions().add( c1 );
        dt.getConditionPatterns().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c2" );
        p2.setFactType( "Driver" );

        c2 = new ConditionCol52();
        c2.setFactField( "age" );
        c2.setOperator( "==" );
        c2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getConditions().add( c2 );
        dt.getConditionPatterns().add( p2 );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "c3" );
        p3.setFactType( "Driver" );

        c3 = new ConditionCol52();
        c3.setFactField( "dateOfBirth" );
        c3.setOperator( "==" );
        c3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getConditions().add( c3 );
        dt.getConditionPatterns().add( p3 );

        Pattern52 p4 = new Pattern52();
        p4.setBoundName( "c4" );
        p4.setFactType( "Driver" );

        c4 = new ConditionCol52();
        c4.setFactField( "approved" );
        c4.setOperator( "==" );
        c4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p4.getConditions().add( c4 );
        dt.getConditionPatterns().add( p4 );

        a1 = new ActionSetFieldCol52();
        a1.setBoundName( "c1" );
        a1.setFactField( "name" );
        dt.getActionCols().add( a1 );

        a2 = new ActionInsertFactCol52();
        a2.setBoundName( "a2" );
        a2.setFactType( "Driver" );
        a2.setFactField( "name" );
        dt.getActionCols().add( a2 );

        factory = new DecisionTableCellValueFactory( sce );
        factory.setModel( dt );

        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT,
                         "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

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

        DTCellValue52 dcv1 = new DTCellValue52( Boolean.TRUE );
        DTCellValue52 dcv2 = new DTCellValue52( dob );
        DTCellValue52 dcv3 = new DTCellValue52( 1 );
        DTCellValue52 dcv4 = new DTCellValue52( 1.0 );
        DTCellValue52 dcv5 = new DTCellValue52( new BigDecimal( 1 ) );
        DTCellValue52 dcv6 = new DTCellValue52( "Smurf" );

        assertEquals( dcv1.getDataType(),
                      DTDataTypes52.BOOLEAN );
        assertEquals( dcv2.getDataType(),
                      DTDataTypes52.DATE );
        assertEquals( dcv3.getDataType(),
                      DTDataTypes52.NUMERIC );
        assertEquals( dcv4.getDataType(),
                      DTDataTypes52.NUMERIC );
        assertEquals( dcv5.getDataType(),
                      DTDataTypes52.NUMERIC );
        assertEquals( dcv6.getDataType(),
                      DTDataTypes52.STRING );

    }

    @Test
    public void testEmptyCells() {

        CellValue< ? extends Comparable< ? >> cell1 = factory.convertModelCellValue( at1,
                                                                                     new DTCellValue52() );
        CellValue< ? extends Comparable< ? >> cell2 = factory.convertModelCellValue( at2,
                                                                                     new DTCellValue52() );
        CellValue< ? extends Comparable< ? >> cell3 = factory.convertModelCellValue( c1,
                                                                                     new DTCellValue52() );
        CellValue< ? extends Comparable< ? >> cell4 = factory.convertModelCellValue( c2,
                                                                                     new DTCellValue52() );
        CellValue< ? extends Comparable< ? >> cell5 = factory.convertModelCellValue( c3,
                                                                                     new DTCellValue52() );
        CellValue< ? extends Comparable< ? >> cell6 = factory.convertModelCellValue( c4,
                                                                                     new DTCellValue52() );
        CellValue< ? extends Comparable< ? >> cell7 = factory.convertModelCellValue( a1,
                                                                                     new DTCellValue52() );
        CellValue< ? extends Comparable< ? >> cell8 = factory.convertModelCellValue( a2,
                                                                                     new DTCellValue52() );

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

        DTCellValue52 dcv1 = new DTCellValue52( 1 );
        DTCellValue52 dcv2 = new DTCellValue52( Boolean.TRUE );
        DTCellValue52 dcv3 = new DTCellValue52( "Michael" );
        DTCellValue52 dcv4 = new DTCellValue52( 11 );
        DTCellValue52 dcv5 = new DTCellValue52( dob );
        DTCellValue52 dcv6 = new DTCellValue52( Boolean.TRUE );
        DTCellValue52 dcv7 = new DTCellValue52( "Mike" );
        DTCellValue52 dcv8 = new DTCellValue52( "Mike" );

        CellValue< ? extends Comparable< ? >> cell1 = factory.convertModelCellValue( at1,
                                                                                     dcv1 );
        CellValue< ? extends Comparable< ? >> cell2 = factory.convertModelCellValue( at2,
                                                                                     dcv2 );
        CellValue< ? extends Comparable< ? >> cell3 = factory.convertModelCellValue( c1,
                                                                                     dcv3 );
        CellValue< ? extends Comparable< ? >> cell4 = factory.convertModelCellValue( c2,
                                                                                     dcv4 );
        CellValue< ? extends Comparable< ? >> cell5 = factory.convertModelCellValue( c3,
                                                                                     dcv5 );
        CellValue< ? extends Comparable< ? >> cell6 = factory.convertModelCellValue( c4,
                                                                                     dcv6 );
        CellValue< ? extends Comparable< ? >> cell7 = factory.convertModelCellValue( a1,
                                                                                     dcv7 );
        CellValue< ? extends Comparable< ? >> cell8 = factory.convertModelCellValue( a2,
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

        DTCellValue52 dcv1 = new DTCellValue52( "1" );
        DTCellValue52 dcv2 = new DTCellValue52( "true" );
        DTCellValue52 dcv3 = new DTCellValue52( "Michael" );
        DTCellValue52 dcv4 = new DTCellValue52( "11" );
        DTCellValue52 dcv5 = new DTCellValue52( "01-JAN-2000" );
        DTCellValue52 dcv6 = new DTCellValue52( "true" );
        DTCellValue52 dcv7 = new DTCellValue52( "Mike" );
        DTCellValue52 dcv8 = new DTCellValue52( "Mike" );

        CellValue< ? extends Comparable< ? >> cell1 = factory.convertModelCellValue( at1,
                                                                                     dcv1 );
        CellValue< ? extends Comparable< ? >> cell2 = factory.convertModelCellValue( at2,
                                                                                     dcv2 );
        CellValue< ? extends Comparable< ? >> cell3 = factory.convertModelCellValue( c1,
                                                                                     dcv3 );
        CellValue< ? extends Comparable< ? >> cell4 = factory.convertModelCellValue( c2,
                                                                                     dcv4 );
        CellValue< ? extends Comparable< ? >> cell5 = factory.convertModelCellValue( c3,
                                                                                     dcv5 );
        CellValue< ? extends Comparable< ? >> cell6 = factory.convertModelCellValue( c4,
                                                                                     dcv6 );
        CellValue< ? extends Comparable< ? >> cell7 = factory.convertModelCellValue( a1,
                                                                                     dcv7 );
        CellValue< ? extends Comparable< ? >> cell8 = factory.convertModelCellValue( a2,
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

        DTCellValue52 dcv1 = new DTCellValue52( "" );
        DTCellValue52 dcv2 = new DTCellValue52( "" );
        DTCellValue52 dcv3 = new DTCellValue52( "" );
        DTCellValue52 dcv4 = new DTCellValue52( "" );
        DTCellValue52 dcv5 = new DTCellValue52( "" );
        DTCellValue52 dcv6 = new DTCellValue52( "" );
        DTCellValue52 dcv7 = new DTCellValue52( "" );
        DTCellValue52 dcv8 = new DTCellValue52( "" );

        CellValue< ? extends Comparable< ? >> cell1 = factory.convertModelCellValue( at1,
                                                                                     dcv1 );
        CellValue< ? extends Comparable< ? >> cell2 = factory.convertModelCellValue( at2,
                                                                                     dcv2 );
        CellValue< ? extends Comparable< ? >> cell3 = factory.convertModelCellValue( c1,
                                                                                     dcv3 );
        CellValue< ? extends Comparable< ? >> cell4 = factory.convertModelCellValue( c2,
                                                                                     dcv4 );
        CellValue< ? extends Comparable< ? >> cell5 = factory.convertModelCellValue( c3,
                                                                                     dcv5 );
        CellValue< ? extends Comparable< ? >> cell6 = factory.convertModelCellValue( c4,
                                                                                     dcv6 );
        CellValue< ? extends Comparable< ? >> cell7 = factory.convertModelCellValue( a1,
                                                                                     dcv7 );
        CellValue< ? extends Comparable< ? >> cell8 = factory.convertModelCellValue( a2,
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
                      DTDataTypes52.NUMERIC );
        assertEquals( dcv2.getDataType(),
                      DTDataTypes52.BOOLEAN );
        assertEquals( dcv3.getDataType(),
                      DTDataTypes52.STRING );
        assertEquals( dcv4.getDataType(),
                      DTDataTypes52.NUMERIC );
        assertEquals( dcv5.getDataType(),
                      DTDataTypes52.DATE );
        assertEquals( dcv6.getDataType(),
                      DTDataTypes52.BOOLEAN );
        assertEquals( dcv7.getDataType(),
                      DTDataTypes52.STRING );
        assertEquals( dcv8.getDataType(),
                      DTDataTypes52.STRING );

    }

}
