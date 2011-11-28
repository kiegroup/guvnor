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
import java.util.Date;

import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.decisiontable.cells.PopupDateEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupTextEditCell;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * A Factory to provide the Cells.
 */
public abstract class AbstractCellFactory<T> {

    private static final String                DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    protected final SuggestionCompletionEngine sce;

    protected final EventBus                   eventBus;

    /**
     * Construct a Cell Factory for a specific grid widget
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param eventBus
     *            EventBus to which cells can send update events
     */
    public AbstractCellFactory(final SuggestionCompletionEngine sce,
                               final EventBus eventBus) {
        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        this.sce = sce;
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
        CheckboxCellImpl cbc = GWT.create( CheckboxCellImpl.class );
        return new DecoratedGridCellValueAdaptor<Boolean>( cbc,
                                                           eventBus );
    }

    // Make a new Cell for Date columns
    protected DecoratedGridCellValueAdaptor<Date> makeDateCell() {
        return new DecoratedGridCellValueAdaptor<Date>( new PopupDateEditCell( DateTimeFormat.getFormat( DATE_FORMAT ) ),
                                                        eventBus );
    }

    // Make a new Cell for Numeric columns
    protected DecoratedGridCellValueAdaptor<BigDecimal> makeNumericCell() {
        return new DecoratedGridCellValueAdaptor<BigDecimal>( new PopupNumericEditCell(),
                                                              eventBus );
    }

    // Make a new Cell for a Text columns
    protected DecoratedGridCellValueAdaptor<String> makeTextCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell(),
                                                          eventBus );
    }

}
