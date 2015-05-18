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

package org.guvnor.m2repo.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.paging.PageRequest;

/**
 * A request for GuvnorM2Repository content
 */
@Portable
public class JarListPageRequest extends PageRequest {

    public static final String COLUMN_NAME = "org.guvnor.m2repo.model.name";

    public static final String COLUMN_PATH = "org.guvnor.m2repo.model.path";

    public static final String COLUMN_LAST_MODIFIED = "org.guvnor.m2repo.model.last.modified";

    private String filters;
    private String dataSourceName;
    private boolean isAscending;

    public JarListPageRequest() {
        //Errai marshalling
    }

    public JarListPageRequest( final int startRowIndex,
                               final Integer pageSize,
                               final String filters,
                               final String dataSourceName,
                               final boolean isAscending ) {
        super( startRowIndex,
               pageSize );
        this.filters = filters;
        this.dataSourceName = dataSourceName;
        this.isAscending = isAscending;
    }

    public String getFilters() {
        return filters;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public boolean isAscending() {
        return isAscending;
    }
}
