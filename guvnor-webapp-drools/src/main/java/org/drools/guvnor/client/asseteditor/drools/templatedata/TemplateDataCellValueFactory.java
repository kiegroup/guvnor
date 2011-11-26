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
package org.drools.guvnor.client.asseteditor.drools.templatedata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractCellValueFactory;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicDataRow;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel;
import org.drools.ide.common.client.modeldriven.dt.TemplateModel.InterpolationVariable;
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
                BigDecimal bd = null;
                try {
                    bd = new BigDecimal( dcv );
                } catch ( Exception e ) {
                }
                cell = makeNewNumericCellValue( bd );
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

        //Strip field name, if it is fully qualified
        if ( factField != null ) {
            if ( factField.contains( "." ) ) {
                factField = factField.substring( factField.indexOf( "." ) + 1 );
            }

            //Check for enumerations
            if ( factType != null ) {
                vals = sce.getEnumValues( factType,
                                          factField );
                if ( vals != null && vals.length > 0 ) {
                    return DTDataTypes52.STRING;
                }
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

    //Convert an interpolation variable to a column
    private TemplateDataColumn makeModelColumn(InterpolationVariable var) {
        return new TemplateDataColumn( var.getVarName(),
                                       var.getDataType(),
                                       var.getFactType(),
                                       var.getFactField() );
    }

}
