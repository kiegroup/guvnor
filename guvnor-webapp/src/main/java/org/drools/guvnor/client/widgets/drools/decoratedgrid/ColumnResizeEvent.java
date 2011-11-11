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

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a column resize event.
 */
public class ColumnResizeEvent extends GwtEvent<ColumnResizeHandler> {

    // Parameters for the Event
    private final DynamicColumn< ? >         column;
    private final int                        width;

    /**
     * Handler type.
     */
    private static Type<ColumnResizeHandler> TYPE = new Type<ColumnResizeHandler>();

    /**
     * Fires a value change event on all registered handlers in the handler
     * manager. If no such handlers exist, this method will do nothing.
     * 
     * @param source
     *            the source of the handlers
     * @param column
     *            the column resized
     * @param width
     *            the columns width (px)
     */
    public static void fire(HasColumnResizeHandlers source,
                            DynamicColumn< ? > column,
                            int width) {
        if ( source == null ) {
            throw new IllegalArgumentException( "source cannot be null" );
        }
        if ( column == null ) {
            throw new IllegalArgumentException( "column cannot be null" );
        }
        ColumnResizeEvent event = new ColumnResizeEvent( column,
                                                             width );
        source.fireEvent( event );
    }

    /**
     * Gets the type of Handlers that can handle the event
     * 
     * @return
     */
    public static Type<ColumnResizeHandler> getType() {
        return TYPE;
    }

    /**
     * Creates a value change event.
     * 
     * @param column
     *            The column on which the resize event was triggered
     * @param width
     *            The new width of the column
     */
    protected ColumnResizeEvent(DynamicColumn< ? > column,
                                int width) {
        if ( column == null ) {
            throw new IllegalArgumentException( "column cannot be null" );
        }
        this.column = column;
        this.width = width;
    }

    @Override
    public final Type<ColumnResizeHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the column to which the resize event relates.
     * 
     * @return the column
     */
    public DynamicColumn< ? > getColumn() {
        return this.column;
    }

    /**
     * Gets the width of the column
     * 
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    @Override
    public String toDebugString() {
        return super.toDebugString()
               + "column = "
               + getColumn().toString()
                + ", width = "
               + getWidth();
    }

    @Override
    protected void dispatch(ColumnResizeHandler handler) {
        handler.onColumnResize( this );
    }
}
