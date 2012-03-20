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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.decisiontable.cells.PopupDateEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericBigDecimalEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericBigIntegerEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericByteEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericDoubleEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericFloatEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericIntegerEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericLongEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericShortEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupTextEditCell;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * A Factory to provide the Cells.
 */
public abstract class AbstractCellFactory<T> {

    private static final String                           DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    protected final SuggestionCompletionEngine            sce;

    protected final CellTableDropDownDataValueMapProvider dropDownManager;

    protected final boolean                               isReadOnly;

    protected final EventBus                              eventBus;

    /**
     * Construct a Cell Factory for a specific grid widget
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param dropDownManager
     *            DropDownManager for dependent cells
     * @param isReadOnly
     *            Should cells be created for a read-only mode of operation
     * @param eventBus
     *            EventBus to which cells can send update events
     */
    public AbstractCellFactory(final SuggestionCompletionEngine sce,
                               final CellTableDropDownDataValueMapProvider dropDownManager,
                               final boolean isReadOnly,
                               final EventBus eventBus) {
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        if ( dropDownManager == null ) {
            throw new IllegalArgumentException( "dropDownManager cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        this.sce = sce;
        this.dropDownManager = dropDownManager;
        this.isReadOnly = isReadOnly;
        this.eventBus = eventBus;
    }

    /**
     * Create a Cell for the given Column
     * 
     * @param column
     *            The Decision Table model column
     * @return A Cell
     */
    public abstract DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> getCell(T column);

    // Make a new Cell for Boolean columns
    protected DecoratedGridCellValueAdaptor<Boolean> makeBooleanCell() {
        return new DecoratedGridCellValueAdaptor<Boolean>( new CheckboxCellImpl( isReadOnly ),
                                                           eventBus );
    }

    // Make a new Cell for Date columns
    protected DecoratedGridCellValueAdaptor<Date> makeDateCell() {
        return new DecoratedGridCellValueAdaptor<Date>( new PopupDateEditCell( DateTimeFormat.getFormat( DATE_FORMAT ),
                                                                               isReadOnly ),
                                                        eventBus );
    }

    // Make a new Cell for Numeric columns
    protected DecoratedGridCellValueAdaptor<BigDecimal> makeNumericCell() {
        return new DecoratedGridCellValueAdaptor<BigDecimal>( new PopupNumericEditCell( isReadOnly ),
                                                              eventBus );
    }

    // Make a new Cell for BigDecimal columns
    protected DecoratedGridCellValueAdaptor<BigDecimal> makeNumericBigDecimalCell() {
        return new DecoratedGridCellValueAdaptor<BigDecimal>( new PopupNumericBigDecimalEditCell( isReadOnly ),
                                                              eventBus );
    }

    // Make a new Cell for BigInteger columns
    protected DecoratedGridCellValueAdaptor<BigInteger> makeNumericBigIntegerCell() {
        return new DecoratedGridCellValueAdaptor<BigInteger>( new PopupNumericBigIntegerEditCell( isReadOnly ),
                                                              eventBus );
    }

    // Make a new Cell for Byte columns
    protected DecoratedGridCellValueAdaptor<Byte> makeNumericByteCell() {
        return new DecoratedGridCellValueAdaptor<Byte>( new PopupNumericByteEditCell( isReadOnly ),
                                                        eventBus );
    }

    // Make a new Cell for Double columns
    protected DecoratedGridCellValueAdaptor<Double> makeNumericDoubleCell() {
        return new DecoratedGridCellValueAdaptor<Double>( new PopupNumericDoubleEditCell( isReadOnly ),
                                                          eventBus );
    }

    // Make a new Cell for Float columns
    protected DecoratedGridCellValueAdaptor<Float> makeNumericFloatCell() {
        return new DecoratedGridCellValueAdaptor<Float>( new PopupNumericFloatEditCell( isReadOnly ),
                                                         eventBus );
    }

    // Make a new Cell for Integer columns
    protected DecoratedGridCellValueAdaptor<Integer> makeNumericIntegerCell() {
        return new DecoratedGridCellValueAdaptor<Integer>( new PopupNumericIntegerEditCell( isReadOnly ),
                                                           eventBus );
    }

    // Make a new Cell for Long columns
    protected DecoratedGridCellValueAdaptor<Long> makeNumericLongCell() {
        return new DecoratedGridCellValueAdaptor<Long>( new PopupNumericLongEditCell( isReadOnly ),
                                                        eventBus );
    }

    // Make a new Cell for Short columns
    protected DecoratedGridCellValueAdaptor<Short> makeNumericShortCell() {
        return new DecoratedGridCellValueAdaptor<Short>( new PopupNumericShortEditCell( isReadOnly ),
                                                         eventBus );
    }

    // Make a new Cell for a Text columns
    protected DecoratedGridCellValueAdaptor<String> makeTextCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell( isReadOnly ),
                                                          eventBus );
    }

}
