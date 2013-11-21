/*
 * Copyright 2013 JBoss Inc
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
package org.drools.guvnor.client.widgets.tables.sorting;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * Abstract Header Group for sortable columns
 */
public abstract class AbstractSortableHeaderGroup<T extends Comparable> {

    protected final CellTable<T> cellTable;
    protected List<AbstractSortableHeader<T>> sortOrderList = new LinkedList<AbstractSortableHeader<T>>();

    public AbstractSortableHeaderGroup( CellTable<T> cellTable ) {
        this.cellTable = cellTable;
    }

    public void headerClicked( AbstractSortableHeader<T> header ) {
        updateSortOrder( header );
        cellTable.redrawHeaders();
        updateData();
    }

    private void updateSortOrder( AbstractSortableHeader<T> header ) {
        int index = sortOrderList.indexOf( header );
        if ( index == 0 ) {
            if ( header.getSortDirection() != SortDirection.ASCENDING ) {
                header.setSortDirection( SortDirection.ASCENDING );
            } else {
                header.setSortDirection( SortDirection.DESCENDING );
            }
        } else {
            // Remove it if it's already sorted on this header later
            if ( index > 0 ) {
                sortOrderList.remove( index );
            }
            header.setSortDirection( SortDirection.ASCENDING );
            // Bring this header to front // Deque.addFirst(sortableHeader)
            sortOrderList.add( 0, header );
            // Update sortIndexes
            int sortIndex = 0;
            for ( AbstractSortableHeader<T> sortableHeader : sortOrderList ) {
                sortableHeader.setSortIndex( sortIndex );
                sortIndex++;
            }
        }
    }

    public abstract void updateData();
}
