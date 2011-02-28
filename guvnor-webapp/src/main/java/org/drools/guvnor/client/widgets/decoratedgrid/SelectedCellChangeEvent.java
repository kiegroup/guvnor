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
package org.drools.guvnor.client.widgets.decoratedgrid;

import org.drools.guvnor.client.widgets.decoratedgrid.MergableGridWidget.CellExtents;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a change in the selected cell.
 */
public class SelectedCellChangeEvent extends GwtEvent<SelectedCellChangeHandler> {

    // Parameters for the Event
    private final CellExtents         cellExtents;

    /**
     * Handler type.
     */
    private static Type<SelectedCellChangeHandler> TYPE = new Type<SelectedCellChangeHandler>();

    /**
     * Fires a value change event on all registered handlers in the handler
     * manager. If no such handlers exist, this method will do nothing.
     * 
     * @param source
     *            the source of the handlers
     * @param cellExtents
     *            dimensions of selected cell
     */
    public static void fire(HasSelectedCellChangeHandlers source,
                            CellExtents cellExtents) {
        if ( source == null ) {
            throw new IllegalArgumentException( "source cannot be null" );
        }
        if ( cellExtents == null ) {
            throw new IllegalArgumentException( "cellExtents cannot be null" );
        }
        SelectedCellChangeEvent event = new SelectedCellChangeEvent( cellExtents );
        source.fireEvent( event );
    }

    /**
     * Gets the type of Handlers that can handle the event
     * 
     * @return
     */
    public static Type<SelectedCellChangeHandler> getType() {
        return TYPE;
    }

    /**
     * Creates a value change event.
     * 
     * @param cellExtents
     *            dimensions of selected cell
     */
    protected SelectedCellChangeEvent(CellExtents cellExtents) {
        if ( cellExtents == null ) {
            throw new IllegalArgumentException( "cellExtents cannot be null" );
        }
        this.cellExtents=cellExtents;
    }

    // The instance knows its BeforeSelectionHandler is of type I, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @Override
    public final Type<SelectedCellChangeHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the dimensions of the selected cell
     * 
     * @return the dimensions
     */
    public CellExtents getCellExtents() {
        return this.cellExtents;
    }

    @Override
    public String toDebugString() {
        return super.toDebugString()
               + "cellExtents = "
               + getCellExtents().toString();
    }

    @Override
    protected void dispatch(SelectedCellChangeHandler handler) {
        handler.onSelectedCellChange( this );
    }
}
