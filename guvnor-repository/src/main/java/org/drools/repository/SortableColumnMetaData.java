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
package org.drools.repository;

/**
 * Sort Order MetaData used by the guvnor-repository module
 */
public class SortableColumnMetaData {

    private String columnName;
    private SortDirection sortDirection;

    public SortableColumnMetaData() {

    }

    public SortableColumnMetaData( final String columnName,
                                   final SortDirection sortDirection ) {
        this.columnName = columnName;
        this.sortDirection = sortDirection;
    }

    public String getColumnName() {
        return columnName;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }
}
