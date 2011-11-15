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

import org.drools.guvnor.client.widgets.drools.decoratedgrid.MergableGridWidget.CellSelectionDetail;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a change in the selected cell.
 */
public class SelectedCellChangeEvent extends GwtEvent<SelectedCellChangeHandler> {

    // Parameters for the Event
    private final CellSelectionDetail              cellDetails;

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
     * @param cellDetails
     *            details of selected cell
     */
    public static void fire(HasSelectedCellChangeHandlers source,
                            CellSelectionDetail cellDetails) {
        if ( source == null ) {
            throw new IllegalArgumentException( "source cannot be null" );
        }
        if ( cellDetails == null ) {
            throw new IllegalArgumentException( "cellDetails cannot be null" );
        }
        SelectedCellChangeEvent event = new SelectedCellChangeEvent( cellDetails );
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
     *            details of selected cell
     */
    protected SelectedCellChangeEvent(CellSelectionDetail cellDetails) {
        if ( cellDetails == null ) {
            throw new IllegalArgumentException( "cellDetails cannot be null" );
        }
        this.cellDetails = cellDetails;
    }

    @Override
    public final Type<SelectedCellChangeHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the details of the selected cell
     * 
     * @return the details
     */
    public CellSelectionDetail getCellSelectionDetail() {
        return this.cellDetails;
    }

    @Override
    public String toDebugString() {
        return super.toDebugString()
               + "cellDetails = "
               + getCellSelectionDetail().toString();
    }

    @Override
    protected void dispatch(SelectedCellChangeHandler handler) {
        handler.onSelectedCellChange( this );
    }
}
