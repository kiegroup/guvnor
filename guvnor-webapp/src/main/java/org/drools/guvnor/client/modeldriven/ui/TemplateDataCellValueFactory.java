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
import org.drools.ide.common.client.modeldriven.dt.DTDataTypes;

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
        DTDataTypes dataType = getDataType( column );

        switch ( dataType ) {
            case BOOLEAN :
                return convertBooleanValueToString( cv );
            case DATE :
                return convertDateValueToString( cv );
            case DIALECT :
                return convertStringValueToString( cv );
            case NUMERIC :
                return convertNumericValueToString( cv );
            case ROW_NUMBER :
                return convertNumericValueToString( cv );
            default :
                return convertStringValueToString( cv );
        }

    }

    //Convert a Boolean value to a String
    private String convertBooleanValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Boolean) value.getValue()).toString());
    }

    //Convert a Date value to a String
    private String convertDateValueToString(CellValue< ? > value) {
        String result = null;
        if ( value.getValue() != null ) {
            result = DATE_FORMAT.format( (Date) value.getValue() );
        }
        return result;
    }

    //Convert a BigDecimal value to a String
    private String convertNumericValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((BigDecimal) value.getValue()).toPlainString());
    }

    //TODO Convert a String value to a String
    private String convertStringValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : (String) value.getValue());
    }

    // Get the Data Type corresponding to a given column
    protected DTDataTypes getDataType(TemplateDataColumn column) {

        // Columns with lists of values, enums etc are always Text (for now)
        String[] vals = null;
        String factType = column.getFactType();
        String factField = column.getFactField();
        if ( factType != null && factField != null ) {
            vals = sce.getEnumValues( factType,
                                      factField );
            if ( vals != null && vals.length > 0 ) {
                return DTDataTypes.STRING;
            }
        }

        //Otherwise use data type extracted from model
        String dataType = column.getDataType();
        if ( dataType.equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            return DTDataTypes.BOOLEAN;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            return DTDataTypes.DATE;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            return DTDataTypes.NUMERIC;
        } else {
            return DTDataTypes.STRING;
        }
    }

}
