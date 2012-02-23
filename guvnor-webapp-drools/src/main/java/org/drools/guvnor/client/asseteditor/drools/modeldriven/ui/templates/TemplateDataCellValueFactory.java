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
package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.templates;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractCellValueFactory;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicDataRow;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.templates.InterpolationVariable;
import org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public class TemplateDataCellValueFactory extends AbstractCellValueFactory<TemplateDataColumn, String> {

    //Template model
    private TemplateModel model;

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
     * Set the model for which CellValues will be created
     * 
     * @param model
     */
    public void setModel(TemplateModel model) {
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
    }

    /**
     * Construct a new row of data for the underlying model
     * 
     * @return
     */
    public List<String> makeRowData() {
        List<String> data = new ArrayList<String>();
        InterpolationVariable[] variables = model.getInterpolationVariablesList();
        for ( InterpolationVariable var : variables ) {
            TemplateDataColumn column = makeModelColumn( var );
            String dcv = makeModelCellValue( column );
            data.add( dcv );
        }
        return data;
    }

    /**
     * Construct a new row of data for the MergableGridWidget
     * 
     * @param cell
     * @return
     */
    @Override
    public DynamicDataRow makeUIRowData() {
        DynamicDataRow data = new DynamicDataRow();
        InterpolationVariable[] variables = model.getInterpolationVariablesList();
        for ( InterpolationVariable var : variables ) {
            TemplateDataColumn column = makeModelColumn( var );
            String dcv = makeModelCellValue( column );
            CellValue< ? extends Comparable< ? >> cell = convertModelCellValue( column,
                                                                                dcv );
            data.add( cell );
        }

        return data;
    }

    /**
     * Construct a new column of data for the underlying model
     * 
     * @return
     */
    public List<String> makeColumnData(TemplateDataColumn column) {
        List<String> data = new ArrayList<String>();
        for ( int iRow = 0; iRow < model.getRowsCount(); iRow++ ) {
            String cell = makeModelCellValue( column );
            data.add( cell );
        }
        return data;
    }

    /**
     * Convert a column of domain data to that suitable for the UI
     * 
     * @param column
     * @param columnData
     * @return
     */
    public List<CellValue< ? extends Comparable< ? >>> convertColumnData(TemplateDataColumn column,
                                                                         List<String> columnData) {
        List<CellValue< ? extends Comparable< ? >>> data = new ArrayList<CellValue< ? extends Comparable< ? >>>();
        for ( int iRow = 0; iRow < model.getRowsCount(); iRow++ ) {
            String dcv = columnData.get( iRow );
            CellValue< ? extends Comparable< ? >> cell = convertModelCellValue( column,
                                                                                dcv );
            data.add( cell );
        }
        return data;
    }

    /**
     * Make a Model cell for the given column
     * 
     * @param column
     * @return
     */
    @Override
    public String makeModelCellValue(TemplateDataColumn column) {
        return new String();
    }

    /**
     * Convert a Model cell to one that can be used in the UI
     * 
     * @param cell
     * @return
     */
    @Override
    public CellValue< ? extends Comparable< ? >> convertModelCellValue(TemplateDataColumn column,
                                                                       String dcv) {

        DTDataTypes52 dataType = getDataType( column );
        CellValue< ? extends Comparable< ? >> cell = null;

        switch ( dataType ) {
            case BOOLEAN :
                Boolean b = Boolean.FALSE;
                try {
                    b = Boolean.valueOf( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewBooleanCellValue( b );
                break;
            case DATE :
                Date d = null;
                try {
                    if ( DATE_CONVERTOR == null ) {
                        throw new IllegalArgumentException( "DATE_CONVERTOR has not been initialised." );
                    }
                    d = DATE_CONVERTOR.parse( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewDateCellValue( d );
                break;
            case NUMERIC :
                BigDecimal numericValue = null;
                try {
                    numericValue = new BigDecimal( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewNumericCellValue( numericValue );
                break;
            case NUMERIC_BIGDECIMAL :
                BigDecimal bigDecimalValue = null;
                try {
                    bigDecimalValue = new BigDecimal( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewBigDecimalCellValue( bigDecimalValue );
                break;
            case NUMERIC_BIGINTEGER :
                BigInteger bigIntegerValue = null;
                try {
                    bigIntegerValue = new BigInteger( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewBigIntegerCellValue( bigIntegerValue );
                break;
            case NUMERIC_BYTE :
                Byte byteValue = null;
                try {
                    byteValue = new Byte( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewByteCellValue( byteValue );
                break;
            case NUMERIC_DOUBLE :
                Double doubleValue = null;
                try {
                    doubleValue = new Double( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewDoubleCellValue( doubleValue );
                break;
            case NUMERIC_FLOAT :
                Float floatValue = null;
                try {
                    floatValue = new Float( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewFloatCellValue( floatValue );
                break;
            case NUMERIC_INTEGER :
                Integer integerValue = null;
                try {
                    integerValue = new Integer( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewIntegerCellValue( integerValue );
                break;
            case NUMERIC_LONG :
                Long longValue = null;
                try {
                    longValue = new Long( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewLongCellValue( longValue );
                break;
            case NUMERIC_SHORT :
                Short shortValue = null;
                try {
                    shortValue = new Short( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewShortCellValue( shortValue );
                break;
            default :
                cell = makeNewStringCellValue( dcv );
        }

        return cell;
    }

    // Get the Data Type corresponding to a given column
    protected DTDataTypes52 getDataType(TemplateDataColumn column) {

        // Columns with lists of values, enums etc are always Text (for now)
        String[] vals = null;
        String factType = column.getFactType();
        String factField = column.getFactField();

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
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGDECIMAL ) ) {
            return DTDataTypes52.NUMERIC_BIGDECIMAL;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BIGINTEGER ) ) {
            return DTDataTypes52.NUMERIC_BIGINTEGER;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_BYTE ) ) {
            return DTDataTypes52.NUMERIC_BYTE;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE ) ) {
            return DTDataTypes52.NUMERIC_DOUBLE;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_FLOAT ) ) {
            return DTDataTypes52.NUMERIC_FLOAT;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_INTEGER ) ) {
            return DTDataTypes52.NUMERIC_INTEGER;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_LONG ) ) {
            return DTDataTypes52.NUMERIC_LONG;
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC_SHORT ) ) {
            return DTDataTypes52.NUMERIC_SHORT;
        } else {
            return DTDataTypes52.STRING;
        }
    }

    //Convert an interpolation variable to a column
    private TemplateDataColumn makeModelColumn(InterpolationVariable var) {
        return new TemplateDataColumn( var.getVarName(),
                                       var.getDataType(),
                                       var.getFactType(),
                                       var.getFactField() );
    }

    /**
     * Convert a type-safe UI CellValue into a type-safe Model CellValue
     * 
     * @param column
     *            Model column from which data-type can be derived
     * @param cell
     *            UI CellValue to convert into Model CellValue
     * @return
     */
    public String convertToModelCell(TemplateDataColumn column,
                                     CellValue< ? > cv) {
        DTDataTypes52 dataType = getDataType( column );

        switch ( dataType ) {
            case BOOLEAN :
                return convertBooleanValueToString( cv );
            case DATE :
                return convertDateValueToString( cv );
            case NUMERIC :
                return convertNumericValueToString( cv );
            case NUMERIC_BIGDECIMAL :
                return convertBigDecimalValueToString( cv );
            case NUMERIC_BIGINTEGER :
                return convertBigIntegerValueToString( cv );
            case NUMERIC_BYTE :
                return convertByteValueToString( cv );
            case NUMERIC_DOUBLE :
                return convertDoubleValueToString( cv );
            case NUMERIC_FLOAT :
                return convertFloatValueToString( cv );
            case NUMERIC_INTEGER :
                return convertIntegerValueToString( cv );
            case NUMERIC_LONG :
                return convertLongValueToString( cv );
            case NUMERIC_SHORT :
                return convertShortValueToString( cv );
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
            if ( DATE_CONVERTOR == null ) {
                throw new IllegalArgumentException( "DATE_CONVERTOR has not been initialised." );
            }
            result = DATE_CONVERTOR.format( (Date) value.getValue() );
        }
        return result;
    }

    //Convert a Generic Numeric (BigDecimal) value to a String
    private String convertNumericValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((BigDecimal) value.getValue()).toPlainString());
    }

    //Convert a BigDecimal value to a String
    private String convertBigDecimalValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((BigDecimal) value.getValue()).toPlainString());
    }

    //Convert a BigInteger value to a String
    private String convertBigIntegerValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((BigInteger) value.getValue()).toString());
    }

    //Convert a Byte value to a String
    private String convertByteValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Byte) value.getValue()).toString());
    }

    //Convert a Double value to a String
    private String convertDoubleValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Double) value.getValue()).toString());
    }

    //Convert a Float value to a String
    private String convertFloatValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Float) value.getValue()).toString());
    }

    //Convert a Integer value to a String
    private String convertIntegerValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Integer) value.getValue()).toString());
    }

    //Convert a Long value to a String
    private String convertLongValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Long) value.getValue()).toString());
    }

    //Convert a Short value to a String
    private String convertShortValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : ((Short) value.getValue()).toString());
    }

    //Convert a String value to a String
    private String convertStringValueToString(CellValue< ? > value) {
        return (value.getValue() == null ? null : (String) value.getValue());
    }

}
