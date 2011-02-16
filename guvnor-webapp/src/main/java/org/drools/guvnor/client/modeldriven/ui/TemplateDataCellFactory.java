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

import org.drools.guvnor.client.widgets.decoratedgrid.AbstractCellFactory;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridCellValueAdaptor;
import org.drools.guvnor.client.widgets.decoratedgrid.DecoratedGridWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

public class TemplateDataCellFactory extends AbstractCellFactory<TemplateDataColumn> {

    /**
     * Construct a Cell Factory for a specific Template Data Widget
     * 
     * @param grid
     *            DecoratedGridWidget to which cells will send their updates
     */
    public TemplateDataCellFactory(DecoratedGridWidget<TemplateDataColumn> grid) {
        super( grid );
    }

    /**
     * Create a Cell for the given TemplateDataColumn
     * 
     * @param column
     *            The Template Data Table model column
     * @return A Cell
     */
    public DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, TemplateDataColumn> getCell(TemplateDataColumn column) {

        DecoratedGridCellValueAdaptor< ? extends Comparable< ? >, TemplateDataColumn> cell = null;

        String dataType = column.getDataType();
        if ( column.getDataType().equals( SuggestionCompletionEngine.TYPE_BOOLEAN ) ) {
            cell = makeBooleanCell();
        } else if ( column.getDataType().equals( SuggestionCompletionEngine.TYPE_DATE ) ) {
            cell = makeDateCell();
        } else if ( dataType.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
            cell = makeNumericCell();
        } else {
            cell = makeTextCell();
        }

        cell.setDecoratedGridWidget( grid );
        return cell;

    }

}
