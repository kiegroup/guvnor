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

import org.drools.guvnor.client.decisiontable.cells.PopupTextEditCell;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;

public class TemplateDataCellFactory {

    // The containing DecoratedGridWidget to which cells will send their updates
    private DecoratedGridWidget<TemplateDataColumn> grid;

    /**
     * Construct a Cell Factory for a specific Template Data Widget
     * 
     * @param grid
     *            DecoratedGridWidget to which cells will send their updates
     */
    public TemplateDataCellFactory(DecoratedGridWidget<TemplateDataColumn> grid) {
        if ( grid == null ) {
            throw new IllegalArgumentException( "grid cannot be null" );
        }

        this.grid = grid;
    }

    /**
     * Create a Cell for the given TemplateDataColumn
     * 
     * @param column
     *            The Template Data Table model column
     * @return A Cell
     */
    public DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, TemplateDataColumn> getCell(TemplateDataColumn column) {

        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, TemplateDataColumn> cell = makeTextCell();
        cell.setDecoratedGridWidget( grid );
        return cell;

    }

    // Make a new Cell for a TextCol
    private DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, TemplateDataColumn> makeTextCell() {
        return new DecoratedGridCellValueAdaptor<String, TemplateDataColumn>(
                                                                              new PopupTextEditCell() );
    }

}
