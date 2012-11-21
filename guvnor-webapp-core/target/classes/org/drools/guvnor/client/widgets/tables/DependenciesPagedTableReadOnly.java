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

package org.drools.guvnor.client.widgets.tables;

import org.drools.guvnor.client.rpc.DependenciesPageRow;
import org.drools.guvnor.client.widgets.query.OpenItemCommand;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Widget with a table of Dependencies entries.
 * 
 */
public class DependenciesPagedTableReadOnly extends DependenciesPagedTable {
    // UI
    interface DependenciesPagedTablReadOnlyeBinder
        extends
        UiBinder<Widget, DependenciesPagedTableReadOnly> {
    }

    protected SingleSelectionModel<DependenciesPageRow> selectionModel;

    public DependenciesPagedTableReadOnly(String theUuid,
                                          OpenItemCommand openSelectedCommand) {
        super( theUuid,
               openSelectedCommand );
    }

    @Override
    protected void doCellTable() {
        ProvidesKey<DependenciesPageRow> providesKey = new ProvidesKey<DependenciesPageRow>() {
            public Object getKey(DependenciesPageRow row) {
                return row.getDependencyPath();
            }
        };

        cellTable = new CellTable<DependenciesPageRow>( providesKey );
        selectionModel = new SingleSelectionModel<DependenciesPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<DependenciesPageRow> columnPicker = new ColumnPicker<DependenciesPageRow>( cellTable );
        SortableHeaderGroup<DependenciesPageRow> sortableHeaderGroup = new SortableHeaderGroup<DependenciesPageRow>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

}
