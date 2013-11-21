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

import java.util.List;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;

/**
 * Header Group for columns that need external help to sort content
 * @param <T>
 */
public class SortableHeaderGroup<T extends Comparable> extends AbstractSortableHeaderGroup<T> {

    public SortableHeaderGroup( CellTable<T> cellTable ) {
        super( cellTable );
    }

    @Override
    public void updateData() {
        //Raise event to force DataProvider to refresh content for display. Since Guvnor supports
        //sorting a CellTable on multiple columns we don't use GWT's support for sortable columns
        //nor ColumnSortList maintained withing a CellTable.
        ColumnSortEvent.fire( cellTable,
                              new ColumnSortList() );
    }

    public List<AbstractSortableHeader<T>> getSortOrderList() {
        return sortOrderList;
    }

}
