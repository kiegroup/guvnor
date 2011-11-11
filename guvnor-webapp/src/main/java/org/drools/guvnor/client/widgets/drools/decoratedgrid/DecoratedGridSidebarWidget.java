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
package org.drools.guvnor.client.widgets.drools.decoratedgrid;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.data.DynamicDataRow;

import com.google.gwt.user.client.ui.Composite;

/**
 * An abstract "Sidebar" widget to decorate a <code>DecoratedGridWidget</code>
 * 
 * @param <T>
 *            The type of domain columns represented by the Header
 */
public abstract class DecoratedGridSidebarWidget<T> extends Composite {

    protected DecoratedGridWidget<T> grid;
    protected HasRows                hasRows;

    // Resources
    protected ResourcesProvider<T>   resources;

    /**
     * Construct a "Sidebar" for the provided DecoratedGridWidget. The sidebar
     * will call upon the <code>HasRows</code> to facilitate addition and
     * removal of rows.
     * 
     * @param resources
     * @param grid
     * @param hasRows
     */
    public DecoratedGridSidebarWidget(ResourcesProvider<T> resources,
                                      DecoratedGridWidget<T> grid,
                                      HasRows hasRows) {
        if ( resources == null ) {
            throw new IllegalArgumentException( "resources cannot be null" );
        }
        if ( grid == null ) {
            throw new IllegalArgumentException( "grid cannot be null" );
        }
        if ( hasRows == null ) {
            throw new IllegalArgumentException( "hasRows cannot be null" );
        }
        this.resources = resources;
        this.grid = grid;
        this.hasRows = hasRows;
    }

    /**
     * Delete a Selector for the given row.
     * 
     * @param row
     */
    public abstract void deleteSelector(DynamicDataRow row);

    /**
     * Insert a Selector for the given row.
     * 
     * @param row
     *            The row for which the selector will be added
     */
    public abstract void insertSelector(DynamicDataRow row);

    /**
     * Redraw the sidebar, this involves clearing any content before calling to
     * addSelector for each row in the grid's data
     */
    public abstract void redraw();

    /**
     * Resize the sidebar.
     * 
     * @param height
     */
    public abstract void resizeSidebar(int height);

    /**
     * Set scroll position to enable some degree of synchronisation between
     * DecisionTable and DecisionTableSidebar
     * 
     * @param position
     */
    public abstract void setScrollPosition(int position);

}
