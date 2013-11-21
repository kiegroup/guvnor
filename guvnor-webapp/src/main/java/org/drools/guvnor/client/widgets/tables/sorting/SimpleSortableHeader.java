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

import com.google.gwt.user.cellview.client.Column;

/**
 * Header for columns that sort content without external helpers
 */
public class SimpleSortableHeader<T, C extends Comparable> extends AbstractSortableHeader<T> {

    private final Column<T, C> column;

    public SimpleSortableHeader( AbstractSortableHeaderGroup sortableHeaderGroup,
                                 String text,
                                 Column<T, C> column ) {
        super( sortableHeaderGroup,
               text );
        this.column = column;
    }

    public Column<T, C> getColumn() {
        return column;
    }

}
