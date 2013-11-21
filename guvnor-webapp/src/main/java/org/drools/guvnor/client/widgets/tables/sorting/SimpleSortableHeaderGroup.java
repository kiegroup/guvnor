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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/**
 * Header Group for columns that sort content without external helpers
 */
public class SimpleSortableHeaderGroup<T extends Comparable> extends AbstractSortableHeaderGroup<T> {

    public SimpleSortableHeaderGroup( CellTable<T> cellTable ) {
        super( cellTable );
    }

    @Override
    public void updateData() {
        List<T> displayedItems = new ArrayList<T>( cellTable.getDisplayedItems() );
        Collections.sort( displayedItems, new Comparator<T>() {
            public int compare( T leftRow,
                                T rightRow ) {
                for ( AbstractSortableHeader<T> sortableHeader : sortOrderList ) {
                    Column<T, T> column = ( (SimpleSortableHeader) sortableHeader ).getColumn();
                    Comparable leftColumnValue = column.getValue( leftRow );
                    Comparable rightColumnValue = column.getValue( rightRow );
                    int comparison = ( leftColumnValue == rightColumnValue ) ? 0
                            : ( leftColumnValue == null ) ? -1
                            : ( rightColumnValue == null ) ? 1
                            : leftColumnValue.compareTo( rightColumnValue );
                    if ( comparison != 0 ) {
                        switch ( sortableHeader.getSortDirection() ) {
                            case ASCENDING:
                                break;
                            case DESCENDING:
                                comparison = -comparison;
                                break;
                            default:
                                throw new IllegalStateException( "Sorting can only be enabled for ASCENDING or" +
                                                                         " DESCENDING, not sortDirection (" + sortableHeader.getSortDirection() + ") ." );
                        }
                        return comparison;
                    }
                }
                return leftRow.compareTo( rightRow );
            }
        } );
        cellTable.setRowData( 0, displayedItems );
        cellTable.redraw();
    }

}
