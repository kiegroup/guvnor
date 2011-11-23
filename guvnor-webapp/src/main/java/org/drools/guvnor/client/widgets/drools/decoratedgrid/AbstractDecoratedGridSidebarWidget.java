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

import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.AppendRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.RowGroupingChangeEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetInternalModelEvent;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;

/**
 * An abstract "Sidebar" widget to decorate a <code>DecoratedGridWidget</code>
 * 
 * @param <T>
 *            Column data-type
 */
public abstract class AbstractDecoratedGridSidebarWidget<M, T> extends Composite
    implements
    DeleteRowEvent.Handler,
    InsertRowEvent.Handler,
    AppendRowEvent.Handler,
    SetInternalModelEvent.Handler<M, T>,
    RowGroupingChangeEvent.Handler {

    protected ResourcesProvider<T> resources;
    protected EventBus             eventBus;

    /**
     * Construct a "Sidebar" for the provided DecoratedGridWidget. The sidebar
     * will call upon the <code>HasRows</code> to facilitate addition and
     * removal of rows.
     * 
     * @param resources
     * @param eventBus
     */
    public AbstractDecoratedGridSidebarWidget(ResourcesProvider<T> resources,
                                              EventBus eventBus) {
        if ( resources == null ) {
            throw new IllegalArgumentException( "resources cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        this.resources = resources;
        this.eventBus = eventBus;

        //Wire-up event handlers
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( RowGroupingChangeEvent.TYPE,
                             this );
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
    public abstract void setScrollPosition(int position);

    /**
     * Redraw the sidebar, this involves clearing any content before calling to
     * addSelector for each row in the grid's data
     */
    abstract void redraw();

}
