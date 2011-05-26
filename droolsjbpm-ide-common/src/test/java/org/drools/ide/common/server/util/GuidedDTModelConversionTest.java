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
package org.drools.ide.common.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt.DTCellValue;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable52;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class GuidedDTModelConversionTest {

    @Test
    public void testConversion() {

        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.setTableName( "michael" );

        MetadataCol md = new MetadataCol();
        md.setMetadata( "legacy" );
        md.setDefaultValue( "yes" );
        dt.getMetadataCols().add( md );

        AttributeCol attr = new AttributeCol();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        ConditionCol52 con = new ConditionCol52();
        con.setBoundName( "f1" );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setFactType( "Driver" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        dt.getConditionCols().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setBoundName( "f1" );
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setFactType( "Driver" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        dt.getConditionCols().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setBoundName( "f1" );
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setFactType( "Driver" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        dt.getConditionCols().add( con3 );

        ConditionCol52 con4 = new ConditionCol52();
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
                new String[]{"1", "desc", "metar1", "saliencer1", "c1r1", "c2r1", "c3r1", "c4r1", "a1r1", "a2r1", "a3r1", "a4r1"},
                new String[]{"2", "desc", "metar2", "saliencer2", "c1r2", "c2r2", "c3r2", "c4r2", "a1r2", "a2r2", "a3r2", "a4r2"}
        } );

        GuidedDecisionTable52 tsdt = RepositoryUpgradeHelper.convertGuidedDTModel( dt );

        assertEquals( "michael",
                      tsdt.getTableName() );

        assertEquals( 1,
                      tsdt.getMetadataCols().size() );
        assertEquals( "legacy",
                      tsdt.getMetadataCols().get( 0 ).getMetadata() );
        assertEquals( "yes",
                      tsdt.getMetadataCols().get( 0 ).getDefaultValue() );

        assertEquals( 1,
                      tsdt.getAttributeCols().size() );
        assertEquals( "salience",
                      tsdt.getAttributeCols().get( 0 ).getAttribute() );
        assertEquals( "66",
                      tsdt.getAttributeCols().get( 0 ).getDefaultValue() );

        assertEquals( 2,
                      tsdt.getConditionPatterns().size() );

        assertEquals( "f1",
                      tsdt.getConditionPattern( "f1" ).getBoundName() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getFactType() );

        assertEquals( "f2",
                      tsdt.getConditionPattern( "f2" ).getBoundName() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f2" ).getFactType() );

        assertEquals( 3,
                      tsdt.getConditionPattern( "f1" ).getConditions().size() );

        assertEquals( 1,
                      tsdt.getConditionPattern( "f2" ).getConditions().size() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getConstraintValueType() );
        assertEquals( "age",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getFactType() );
        assertEquals( "Driver f1 age",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getConstraintValueType() );
        assertEquals( "name",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getFactType() );
        assertEquals( "Driver f1 name",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 2 ).getConstraintValueType() );
        assertEquals( "rating",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 2 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 2 ).getFactType() );
        assertEquals( "Driver rating",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 2 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 2 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getConstraintValueType() );
        assertEquals( "(not needed)",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getFactType() );
        assertEquals( "Driver 2 pimp",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getHeader() );

        assertEquals( 4,
                      tsdt.getActionCols().size() );

        ActionInsertFactCol a1 = (ActionInsertFactCol) tsdt.getActionCols().get( 0 );
        assertEquals( "ins",
                      a1.getBoundName() );
        assertEquals( "Cheese",
                      a1.getFactType() );
        assertEquals( "price",
                      a1.getFactField() );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      a1.getType() );

        ActionRetractFactCol a2 = (ActionRetractFactCol) tsdt.getActionCols().get( 1 );
        assertEquals( "f2",
                      a2.getBoundName() );

        ActionSetFieldCol a3 = (ActionSetFieldCol) tsdt.getActionCols().get( 2 );
        assertEquals( "f1",
                      a3.getBoundName() );
        assertEquals( "goo1",
                      a3.getFactField() );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      a3.getType() );

        ActionSetFieldCol a4 = (ActionSetFieldCol) tsdt.getActionCols().get( 3 );
        assertEquals( "f1",
                      a4.getBoundName() );
        assertEquals( "goo2",
                      a4.getFactField() );
        assertEquals( "whee",
                      a4.getDefaultValue() );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      a4.getType() );

        assertEquals( 2,
                      tsdt.getData().size() );
        isRowEquivalent( tsdt.getData().get( 0 ),
                         dt.getData()[0] );
        isRowEquivalent( tsdt.getData().get( 1 ),
                         dt.getData()[1] );

    }

    @Test
    public void testConversionPatternGrouping() {

        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.setTableName( "michael" );

        MetadataCol md = new MetadataCol();
        md.setMetadata( "legacy" );
        md.setDefaultValue( "yes" );
        dt.getMetadataCols().add( md );

        AttributeCol attr = new AttributeCol();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( "66" );
        dt.getAttributeCols().add( attr );

        ConditionCol52 con = new ConditionCol52();
        con.setBoundName( "f1" );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setFactType( "Driver" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        dt.getConditionCols().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setBoundName( "f2" );
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setFactType( "Person" );
        con2.setHeader( "Person f2 name" );
        con2.setOperator( "==" );
        dt.getConditionCols().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setBoundName( "f1" );
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setFactType( "Driver" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        dt.getConditionCols().add( con3 );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setBoundName( "f2" );
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setFactType( "Person" );
        con4.setHeader( "Person f2 not needed" );
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
                new String[]{"1", "desc", "metar1", "saliencer1", "f1c1r1", "f2c1r1", "f1c2r1", "f2c2r1", "a1r1", "a2r1", "a3r1", "a4r1"},
                new String[]{"2", "desc", "metar2", "saliencer2", "f1c1r2", "f2c1r2", "f1c2r2", "f2c2r2", "a1r2", "a2r2", "a3r2", "a4r2"}
        } );

        GuidedDecisionTable52 tsdt = RepositoryUpgradeHelper.convertGuidedDTModel( dt );

        assertEquals( "michael",
                      tsdt.getTableName() );

        assertEquals( 1,
                      tsdt.getMetadataCols().size() );
        assertEquals( "legacy",
                      tsdt.getMetadataCols().get( 0 ).getMetadata() );
        assertEquals( "yes",
                      tsdt.getMetadataCols().get( 0 ).getDefaultValue() );

        assertEquals( 1,
                      tsdt.getAttributeCols().size() );
        assertEquals( "salience",
                      tsdt.getAttributeCols().get( 0 ).getAttribute() );
        assertEquals( "66",
                      tsdt.getAttributeCols().get( 0 ).getDefaultValue() );

        assertEquals( 2,
                      tsdt.getConditionPatterns().size() );

        assertEquals( "f1",
                      tsdt.getConditionPattern( "f1" ).getBoundName() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getFactType() );

        assertEquals( "f2",
                      tsdt.getConditionPattern( "f2" ).getBoundName() );
        assertEquals( "Person",
                      tsdt.getConditionPattern( "f2" ).getFactType() );

        assertEquals( 2,
                      tsdt.getConditionPattern( "f1" ).getConditions().size() );

        assertEquals( 2,
                      tsdt.getConditionPattern( "f2" ).getConditions().size() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getConstraintValueType() );
        assertEquals( "age",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getFactType() );
        assertEquals( "Driver f1 age",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getConstraintValueType() );
        assertEquals( "rating",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getFactType() );
        assertEquals( "Driver rating",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getConditions().get( 1 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getConstraintValueType() );
        assertEquals( "name",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getFactField() );
        assertEquals( "Person",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getFactType() );
        assertEquals( "Person f2 name",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 1 ).getConstraintValueType() );
        assertEquals( "(not needed)",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 1 ).getFactField() );
        assertEquals( "Person",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 1 ).getFactType() );
        assertEquals( "Person f2 not needed",
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 1 ).getHeader() );
        assertEquals( null,
                      tsdt.getConditionPattern( "f2" ).getConditions().get( 1 ).getOperator() );

        assertEquals( 4,
                      tsdt.getActionCols().size() );

        ActionInsertFactCol a1 = (ActionInsertFactCol) tsdt.getActionCols().get( 0 );
        assertEquals( "ins",
                      a1.getBoundName() );
        assertEquals( "Cheese",
                      a1.getFactType() );
        assertEquals( "price",
                      a1.getFactField() );
        assertEquals( SuggestionCompletionEngine.TYPE_NUMERIC,
                      a1.getType() );

        ActionRetractFactCol a2 = (ActionRetractFactCol) tsdt.getActionCols().get( 1 );
        assertEquals( "f2",
                      a2.getBoundName() );

        ActionSetFieldCol a3 = (ActionSetFieldCol) tsdt.getActionCols().get( 2 );
        assertEquals( "f1",
                      a3.getBoundName() );
        assertEquals( "goo1",
                      a3.getFactField() );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      a3.getType() );

        ActionSetFieldCol a4 = (ActionSetFieldCol) tsdt.getActionCols().get( 3 );
        assertEquals( "f1",
                      a4.getBoundName() );
        assertEquals( "goo2",
                      a4.getFactField() );
        assertEquals( "whee",
                      a4.getDefaultValue() );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING,
                      a4.getType() );

        assertEquals( 2,
                      tsdt.getData().size() );

        assertEquals( new BigDecimal( 1 ),
                      tsdt.getData().get( 0 ).get( 0 ).getNumericValue() );
        assertEquals( "desc",
                      tsdt.getData().get( 0 ).get( 1 ).getStringValue() );
        assertEquals( "metar1",
                      tsdt.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( "saliencer1",
                      tsdt.getData().get( 0 ).get( 3 ).getStringValue() );
        assertEquals( "f2c1r1",
                      tsdt.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "f2c2r1",
                      tsdt.getData().get( 0 ).get( 5 ).getStringValue() );
        assertEquals( "f1c1r1",
                      tsdt.getData().get( 0 ).get( 6 ).getStringValue() );
        assertEquals( "f1c2r1",
                      tsdt.getData().get( 0 ).get( 7 ).getStringValue() );
        assertEquals( "a1r1",
                      tsdt.getData().get( 0 ).get( 8 ).getStringValue() );
        assertEquals( "a2r1",
                      tsdt.getData().get( 0 ).get( 9 ).getStringValue() );
        assertEquals( "a3r1",
                      tsdt.getData().get( 0 ).get( 10 ).getStringValue() );
        assertEquals( "a4r1",
                      tsdt.getData().get( 0 ).get( 11 ).getStringValue() );

        assertEquals( new BigDecimal( 2 ),
                      tsdt.getData().get( 1 ).get( 0 ).getNumericValue() );
        assertEquals( "desc",
                      tsdt.getData().get( 1 ).get( 1 ).getStringValue() );
        assertEquals( "metar2",
                      tsdt.getData().get( 1 ).get( 2 ).getStringValue() );
        assertEquals( "saliencer2",
                      tsdt.getData().get( 1 ).get( 3 ).getStringValue() );
        assertEquals( "f2c1r2",
                      tsdt.getData().get( 1 ).get( 4 ).getStringValue() );
        assertEquals( "f2c2r2",
                      tsdt.getData().get( 1 ).get( 5 ).getStringValue() );
        assertEquals( "f1c1r2",
                      tsdt.getData().get( 1 ).get( 6 ).getStringValue() );
        assertEquals( "f1c2r2",
                      tsdt.getData().get( 1 ).get( 7 ).getStringValue() );
        assertEquals( "a1r2",
                      tsdt.getData().get( 1 ).get( 8 ).getStringValue() );
        assertEquals( "a2r2",
                      tsdt.getData().get( 1 ).get( 9 ).getStringValue() );
        assertEquals( "a3r2",
                      tsdt.getData().get( 1 ).get( 10 ).getStringValue() );
        assertEquals( "a4r2",
                      tsdt.getData().get( 1 ).get( 11 ).getStringValue() );

        isRowEquivalent( tsdt.getData().get( 0 ),
                         dt.getData()[0] );
        isRowEquivalent( tsdt.getData().get( 1 ),
                         dt.getData()[1] );

    }

    private void isRowEquivalent(List<DTCellValue> row,
                                    String[] array) {
        assertEquals( row.size(),
                      array.length );

        BigDecimal newRowNum = row.get( 0 ).getNumericValue();
        BigDecimal oldRowNum = new BigDecimal( array[0] );
        assertEquals( newRowNum,
                      oldRowNum );

        for ( int iCol = 1; iCol < row.size(); iCol++ ) {
            DTCellValue cell = row.get( iCol );
            String v1 = cell.getStringValue();
            String v2 = array[iCol];
            assertTrue( isEqualOrNull( v1,
                                       v2 ) );
            assertEquals( v1,
                          v2 );
        }
    }

    private boolean isEqualOrNull(Object v1,
                                  Object v2) {
        if ( v1 == null && v2 == null ) {
            return true;
        }
        if ( v1 != null && v2 == null ) {
            return false;
        }
        if ( v1 == null && v2 != null ) {
            return false;
        }
        return v1.equals( v2 );
    }

}
