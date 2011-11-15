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

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;

/**
 * An abstract "Sidebar" widget to decorate a <code>DecoratedGridWidget</code>
 * 
 * @param <T>
 */
public abstract class DecoratedGridSidebarWidget<T> extends Composite
    implements
    HasRows<List<CellValue< ? extends Comparable< ? >>>> {

    protected HasRows<List<CellValue< ? extends Comparable< ? >>>> hasRows;

    protected AbstractCellValueFactory<T, ? >                      cellValueFactory;

    // Resources
    protected ResourcesProvider<T>                                 resources;

    protected EventBus                                             eventBus;

    /**
     * Construct a "Sidebar" for the provided DecoratedGridWidget. The sidebar
     * will call upon the <code>HasRows</code> to facilitate addition and
     * removal of rows.
     * 
     * @param resources
     * @param eventBus
     * @param hasRows
     */
    public DecoratedGridSidebarWidget(ResourcesProvider<T> resources,
                                      EventBus eventBus,
                                      HasRows<List<CellValue< ? extends Comparable< ? >>>> hasRows) {
        if ( resources == null ) {
            throw new IllegalArgumentException( "resources cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        if ( hasRows == null ) {
            throw new IllegalArgumentException( "hasRows cannot be null" );
        }
        this.resources = resources;
        this.eventBus = eventBus;
        this.hasRows = hasRows;
    }

    /**
     * Resize the sidebar.
     * 
     * @param height
     */
    abstract void resizeSidebar(int height);

    /**
     * Set scroll position to enable some degree of synchronisation between
     * DecisionTable and DecisionTableSidebar
     * 
     * @param position
     */
    abstract void setScrollPosition(int position);

    /**
     * Redraw the sidebar, this involves clearing any content before calling to
     * addSelector for each row in the grid's data
     */
    abstract void redraw();

}
