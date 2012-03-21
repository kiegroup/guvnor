/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.modeldriven.ui;

import java.math.BigDecimal;
import java.util.Date;

import org.drools.guvnor.client.widgets.decoratedgrid.AbstractCellValueFactory;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public class TemplateDataCellValueFactory extends AbstractCellValueFactory<TemplateDataColumn> {

    /**
     * Construct a Cell Value Factory for a specific Template data editor
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     */
    public TemplateDataCellValueFactory(SuggestionCompletionEngine sce) {
        super( sce );
    }

    /**
     * Serialise value to a String
     * 
     * @param column
     *            The model column
     * @param cv
     *            CellValue for which value will be serialised
     * @return String representation of value
     */
    public String convertValueToString(TemplateDataColumn column,
                                       CellValue< ? > cv) {
        DTDataTypes52 dataType = getDataType( column );

        switch ( dataType ) {
            case BOOLEAN :
                return convertBooleanValueToString( cv );
            case DATE :
                return convertDateValueToString( cv );
            case NUMERIC :
                return convertNumericValueToString( cv );
            default :
                return convertStringValueToString( cv );
        }

    }

    /**
     * Make a CellValue applicable for the column. This is used by legacy UI
     * Models (Template Data Editor and legacy Guided Decision Tables) that
     * store values in a two-dimensional array of Strings.
     * 
     * @param column
     *            The model column
     * @param iRow
     *            Row coordinate for initialisation
     * @param iCol
     *            Column coordinate for initialisation
     * @param initialValue
     *            The initial value of the cell
     * @return A CellValue
     */
    public CellValue< ? extends Comparable< ? >> makeCellValue(TemplateDataColumn column,
                                                               int iRow,
                                                               int iCol,
                                                               String initialValue) {
        DTDataTypes52 dataType = getDataType( column );
        CellValue< ? extends Comparable< ? >> cell = null;

        switch ( dataType ) {
            case BOOLEAN :
                Boolean b = Boolean.FALSE;
                try {
                    b = Boolean.valueOf( initialValue );
                } catch ( Exception e ) {
                }
                cell = makeNewBooleanCellValue( iRow,
                                                iCol,
                                                b );
                break;
            case DATE :
                Date d = null;
                try {
                    if ( DATE_CONVERTOR == null ) {
                        throw new IllegalArgumentException( "DATE_CONVERTOR has not been initialised." );
                    }
                    d = DATE_CONVERTOR.parse( initialValue );
                } catch ( Exception e ) {
                }
                cell = makeNewDateCellValue( iRow,
                                             iCol,
                                             d );
                break;
            case NUMERIC :
                BigDecimal bd = null;
                try {
                    bd = new BigDecimal( initialValue );
                } catch ( Exception e ) {
                }
                cell = makeNewNumericCellValue( iRow,
                                                iCol,
                                                bd );
                break;
            default :
                cell = makeNewStringCellValue( iRow,
                                               iCol,
                                               initialValue );
        }

        return cell;
    }

    //Convert a Boolean value to a String
    private String convertBooleanValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Boolean) value.getValue()).toString());
    }

    //Convert a Date value to a String
    private String convertDateValueToString(CellValue< ? > value) {
        String result = null;
        if ( value.getValue() != null ) {
            if ( DATE_CONVERTOR == null ) {
                throw new IllegalArgumentException( "DATE_CONVERTOR has not been initialised." );
            }
            result = DATE_CONVERTOR.format( (Date) value.getValue() );
        }
        return result;
    }

    //Convert a BigDecimal value to a String
    private String convertNumericValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((BigDecimal) value.getValue()).toPlainString());
    }

    //Convert a String value to a String
    private String convertStringValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : (String) value.getValue());
    }

    // Get the Data Type corresponding to a given column
    protected DTDataTypes52 getDataType(TemplateDataColumn column) {

        // Columns with lists of values, enums etc are always Text (for now)
        String[] vals = null;
        String factType = column.getFactType();
        String factField = column.getFactField();

        //Strip field name, if it is fully qualified
        if ( factField.contains( "." ) ) {
            factField = factField.substring( factField.indexOf( "." ) + 1 );
        }

        //Check for enumerations
        if ( factType != null && factField != null ) {
            vals = sce.getEnumValues( factType,
                                      factField );
            if ( vals != null && vals.length > 0 ) {
                return DTDataTypes52.STRING;
            }
        }

        //Otherwise use data type extracted from model
        String dataType = column.getDataType();
        if ( dataType.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            return DTDataTypes52.BOOLEAN;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            return DTDataTypes52.DATE;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            return DTDataTypes52.NUMERIC;
        } else {
            return DTDataTypes52.STRING;
        }
    }

}
