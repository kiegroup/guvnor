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
package org.drools.guvnor.client.widgets.drools.decoratedgrid.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a change in the grouping of rows.
 */
public class RowGroupingChangeEvent extends GwtEvent<RowGroupingChangeHandler> {

    /**
     * Handler type.
     */
    private static Type<RowGroupingChangeHandler> TYPE = new Type<RowGroupingChangeHandler>();

    /**
     * Fires a value change event on all registered handlers in the handler
     * manager. If no such handlers exist, this method will do nothing.
     * 
     * @param source
     *            the source of the handlers
     */
    public static void fire(HasRowGroupingChangeHandlers source) {
        if ( source == null ) {
            throw new IllegalArgumentException( "source cannot be null" );
        }
        RowGroupingChangeEvent event = new RowGroupingChangeEvent();
        source.fireEvent( event );
    }

    /**
     * Gets the type of Handlers that can handle the event
     * 
     * @return
     */
    public static Type<RowGroupingChangeHandler> getType() {
        return TYPE;
    }

    /**
     * Creates a redraw event.
     */
    protected RowGroupingChangeEvent() {
    }

    @Override
    public final Type<RowGroupingChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    public String toDebugString() {
        return super.toDebugString();
    }

    @Override
    protected void dispatch(RowGroupingChangeHandler handler) {
        handler.onRowGroupingChange( this );
    }
}
