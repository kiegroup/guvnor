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
package org.drools.guvnor.client.widgets.decoratedgrid;

import java.math.BigDecimal;
import java.util.Date;

import org.drools.guvnor.client.decisiontable.cells.PopupDateEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupNumericEditCell;
import org.drools.guvnor.client.decisiontable.cells.PopupTextEditCell;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * A Factory to provide the Cells.
 */
public abstract class AbstractCellFactory<T> {

    // The containing MergableGridWidget to which cells will send their updates
    protected MergableGridWidget<T>     grid;

    protected SuggestionCompletionEngine sce;

    /**
     * Construct a Cell Factory for a specific grid widget
     * 
     * @param sce
     *            SuggestionCompletionEngine to assist with drop-downs
     * @param grid
     *            MergableGridWidget to which cells will send their updates
     */
    public AbstractCellFactory(SuggestionCompletionEngine sce,
                               MergableGridWidget<T> grid) {

        if ( sce == null ) {
            throw new IllegalArgumentException( "sce cannot be null" );
        }
        if ( grid == null ) {
            throw new IllegalArgumentException( "grid cannot be null" );
        }
        this.sce = sce;
        this.grid = grid;
    }

    /**
     * Create a Cell for the given Column
     * 
     * @param column
     *            The Decision Table model column
     * @return A Cell
     */
    public abstract DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, T> getCell(T column);

    // Make a new Cell for Boolean columns
    protected DecoratedGridCellValueAdaptor<Boolean, T> makeBooleanCell() {
        return new DecoratedGridCellValueAdaptor<Boolean, T>( new CheckboxCell() );
    }

    // Make a new Cell for Date columns
    protected DecoratedGridCellValueAdaptor<Date, T> makeDateCell() {
        return new DecoratedGridCellValueAdaptor<Date, T>( new PopupDateEditCell( DateTimeFormat.getFormat( PredefinedFormat.DATE_SHORT ) ) );
    }

    // Make a new Cell for Numeric columns
    protected DecoratedGridCellValueAdaptor<BigDecimal, T> makeNumericCell() {
        return new DecoratedGridCellValueAdaptor<BigDecimal, T>( new PopupNumericEditCell() );
    }

    // Make a new Cell for a Text columns
    protected DecoratedGridCellValueAdaptor<String, T> makeTextCell() {
        return new DecoratedGridCellValueAdaptor<String, T>( new PopupTextEditCell() );
    }

}
