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
package org.drools.guvnor.client.decisiontable.widget;

import java.math.BigDecimal;
import java.util.Date;

import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.widgets.decoratedgrid.AbstractCellValueFactory;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue;
import org.drools.guvnor.client.widgets.decoratedgrid.CellValue.CellState;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public class DecisionTableCellValueFactory extends AbstractCellValueFactory<DTColumnConfig52> {

    // Model used to determine data-types etc for cells
    private GuidedDecisionTable52 model;

    /**
     * Construct a Cell Value Factory for a specific Decision Table
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param model
     *            The Decision Table model to assist data-type derivation
     */
    public DecisionTableCellValueFactory(SuggestionCompletionEngine sce,
                                         GuidedDecisionTable52 model) {
        super( sce );
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        this.model = model;
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
    public DTCellValue52 convertToDTModelCell(DTColumnConfig52 column,
                                              CellValue< ? > cell) {
        DTDataTypes52 dt = getDataType( column );
        DTCellValue52 dtCell = null;

        switch ( dt ) {
            case BOOLEAN :
                dtCell = new DTCellValue52( (Boolean) cell.getValue() );
                break;
            case DATE :
                dtCell = new DTCellValue52( (Date) cell.getValue() );
                break;
            case NUMERIC :
                dtCell = new DTCellValue52( (BigDecimal) cell.getValue() );
                break;
            default :
                dtCell = new DTCellValue52( (String) cell.getValue() );
        }
        dtCell.setOtherwise( cell.isOtherwise() );
        return dtCell;
    }

    /**
     * Make a CellValue applicable for the column
     * 
     * @param column
     *            The model column
     * @param iRow
     *            Row coordinate for initialisation
     * @param iCol
     *            Column coordinate for initialisation
     * @param dcv
     *            The Model cell containing the value
     * @return A CellValue
     */
    public CellValue< ? extends Comparable< ? >> makeCellValue(DTColumnConfig52 column,
                                                               int iRow,
                                                               int iCol,
                                                               DTCellValue52 dcv) {
        DTDataTypes52 dataType = getDataType( column );
        CellValue< ? extends Comparable< ? >> cell = null;

        //If this is a legacy Decision Table values are always String 
        //so ensure that the appropriate DTCellValue field is populated
        assertDTCellValue( dataType,
                           dcv );

        switch ( dataType ) {
            case BOOLEAN :
                cell = makeNewBooleanCellValue( iRow,
                                                iCol,
                                                dcv.getBooleanValue() );
                break;
            case DATE :
                cell = makeNewDateCellValue( iRow,
                                             iCol,
                                             dcv.getDateValue() );
                break;
            case NUMERIC :
                if ( column instanceof RowNumberCol52 ) {
                    cell = makeNewRowNumberCellValue( iRow,
                                                      iCol );
                } else {
                    cell = makeNewNumericCellValue( iRow,
                                                    iCol,
                                                    dcv.getNumericValue() );
                    if ( column instanceof AttributeCol52 ) {
                        AttributeCol52 at = (AttributeCol52) column;
                        if ( at.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                            if ( at.isUseRowNumber() ) {
                                cell = makeNewRowNumberCellValue( iRow,
                                                                  iCol );
                            }
                        }
                    }
                }
                break;
            default :
                cell = makeNewStringCellValue( iRow,
                                               iCol,
                                               dcv.getStringValue() );
                if ( column instanceof AttributeCol52 ) {
                    AttributeCol52 ac = (AttributeCol52) column;
                    if ( ac.getAttribute().equals( RuleAttributeWidget.DIALECT_ATTR ) ) {
                        cell = makeNewDialectCellValue( iRow,
                                                        iCol,
                                                        dcv.getStringValue() );
                    }
                }
        }

        if ( dcv.isOtherwise() ) {
            cell.addState( CellState.OTHERWISE );
        }

        return cell;
    }

    //If the Decision Table model has been converted from the legacy text based
    //class then all values are held in the DTCellValue's StringValue. This
    //function attempts to set the correct DTCellValue property based on
    //the DTCellValue's data type.
    private void assertDTCellValue(DTDataTypes52 dataType,
                                   DTCellValue52 dcv) {
        //If already converted exit
        if ( dcv.getDataType().equals( dataType ) ) {
            return;
        }

        String text = dcv.getStringValue();
        switch ( dataType ) {
            case BOOLEAN :
                dcv.setBooleanValue( (text == null ? null : Boolean.valueOf( text )) );
                break;
            case DATE :
                Date d = null;
                try {
                    if ( text != null ) {
                        if ( DATE_CONVERTOR == null ) {
                            throw new IllegalArgumentException( "DATE_CONVERTOR has not been initialised." );
                        }
                        d = DATE_CONVERTOR.parse( text );
                    }
                } catch ( IllegalArgumentException e ) {
                }
                dcv.setDateValue( d );
                break;
            case NUMERIC :
                BigDecimal bd = null;
                try {
                    if ( text != null ) {
                        bd = new BigDecimal( text );
                    }
                } catch ( NumberFormatException e ) {
                }
                dcv.setNumericValue( bd );
                break;
        }

    }

    // Get the Data Type corresponding to a given column
    protected DTDataTypes52 getDataType(DTColumnConfig52 column) {

        //Limited Entry are simply boolean
        if ( column instanceof LimitedEntryCol ) {
            return DTDataTypes52.BOOLEAN;
        }

        //Extended Entry...
        return model.getTypeSafeType( column,
                                      sce );
    }

    protected CellValue<BigDecimal> makeNewRowNumberCellValue(int iRow,
                                                              int iCol) {
        // Rows are 0-based internally but 1-based in the UI
        CellValue<BigDecimal> cv = new CellValue<BigDecimal>( new BigDecimal( iRow + 1 ),
                                                              iRow,
                                                              iCol );
        return cv;
    }

}
