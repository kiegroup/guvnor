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
import org.drools.guvnor.client.widgets.tables.SortDirection;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A column that retrieves it's cell value from an indexed position in a List
 * holding the row data. Normally the row type is defined as a statically typed
 * Class and columns retrieve their cell values from discrete members. A
 * Decision Table's row contains dynamic (i.e. a List) of elements.
 * 
 * @param <T>
 *            The type of domain columns represented
 */
public class DynamicColumn<T> extends DynamicBaseColumn
    implements
    HasValueChangeHandlers<SortConfiguration> {

    private int               columnIndex        = 0;
    private T                 modelColumn;
    private Boolean           isVisible          = new Boolean( true );
    private Boolean           isSystemControlled = new Boolean( false );
    private SortConfiguration sortConfig         = new SortConfiguration();
    private int               width              = 100;

    // Event handling using GWT's EventBus
    private EventBus          eventBus;

    public DynamicColumn(T modelColumn,
                         EventBus eventBus) {
        this( modelColumn,
              null,
              0,
              false,
              true,
              eventBus );
    }

    public DynamicColumn(T modelColumn,
                         DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell,
                             EventBus eventBus) {
        this( modelColumn,
              cell,
              0,
              false,
              true,
              eventBus );
    }

    public DynamicColumn(T modelColumn,
                         DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell,
                         int columnIndex,
                         EventBus eventBus) {
        this( modelColumn,
              cell,
              columnIndex,
              false,
              true,
              eventBus );
    }

    public DynamicColumn(T modelColumn,
                         DecoratedGridCellValueAdaptor< ? extends Comparable< ? >> cell,
                         int columnIndex,
                         boolean isSystemControlled,
                         boolean isSortable,
                         EventBus eventBus) {
        super( cell );
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null" );
        }
        if ( columnIndex < 0 ) {
            throw new IllegalArgumentException( "columnIndex cannot be less than zero" );
        }
        this.modelColumn = modelColumn;
        this.columnIndex = columnIndex;
        this.sortConfig.setSortable( isSortable );
        this.isSystemControlled = isSystemControlled;
        this.eventBus = eventBus;
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null ) {
            return false;
        }
        if ( !(o instanceof DynamicColumn) ) {
            return false;
        }
        @SuppressWarnings("unchecked")
        DynamicColumn<T> c = (DynamicColumn<T>) o;
        return c.columnIndex == this.columnIndex
               && c.modelColumn.equals( this.modelColumn )
               && c.isVisible.equals( this.isVisible )
               && c.isSystemControlled.equals( this.isSystemControlled )
               && c.sortConfig.getSortDirection() == this.sortConfig.getSortDirection()
               && c.sortConfig.isSortable() == this.sortConfig.isSortable()
               && c.sortConfig.getSortIndex() == this.sortConfig.getSortIndex()
               && c.width == this.width;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public T getModelColumn() {
        return this.modelColumn;
    }

    public SortConfiguration getSortConfiguration() {
        return this.sortConfig;
    }

    public SortDirection getSortDirection() {
        return this.sortConfig.getSortDirection();
    }

    public int getSortIndex() {
        return this.sortConfig.getSortIndex();
    }

    @Override
    public CellValue< ? > getValue(DynamicDataRow object) {
        return (CellValue< ? >) object.get( columnIndex );
    }

    public int getWidth() {
        return width;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31
               * hash
               + columnIndex;
        hash = 31
               * hash
               + modelColumn.hashCode();
        hash = 31
               * hash
               + isVisible.hashCode();
        hash = 31
               * hash
               + isSystemControlled.hashCode();
        hash = 31
               * hash
               + sortConfig.getSortDirection().hashCode();
        hash = 31
               * hash
               + sortConfig.isSortable().hashCode();
        hash = 31
               * hash
               + sortConfig.getSortIndex();
        hash = 31
               * hash
               + width;
        return hash;
    }

    public boolean isSortable() {
        return this.sortConfig.isSortable();
    }

    public boolean isSystemControlled() {
        return isSystemControlled;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setColumnIndex(int columnIndex) {
        if ( columnIndex < 0 ) {
            throw new IllegalArgumentException( "columnIndex cannot be less than zero" );
        }
        this.columnIndex = columnIndex;
        this.sortConfig.setColumnIndex( columnIndex );
    }

    public void setSortable(boolean isSortable) {
        this.sortConfig.setSortable( isSortable );
        ValueChangeEvent.fire( this,
                               sortConfig );
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortConfig.setSortDirection( sortDirection );
        if ( sortDirection == SortDirection.NONE ) {
            this.sortConfig.setSortIndex( -1 );
        }
        ValueChangeEvent.fire( this,
                               sortConfig );
    }

    public void setSortIndex(int sortIndex) {
        if ( sortIndex < 0 ) {
            throw new IllegalArgumentException( "sortIndex cannot be less than zero" );
        }
        this.sortConfig.setSortIndex( sortIndex );
        ValueChangeEvent.fire( this,
                               sortConfig );
    }

    public void clearSortIndex() {
        this.sortConfig.setSortIndex( -1 );
        ValueChangeEvent.fire( this,
                               sortConfig );
    }

    public void setSystemControlled(boolean isSystemControlled) {
        this.isSystemControlled = isSystemControlled;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setWidth(int width) {
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }
        this.width = width;
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SortConfiguration> handler) {
        if ( handler == null ) {
            throw new IllegalArgumentException( "handler cannot be null" );
        }
        return eventBus.addHandler( ValueChangeEvent.getType(),
                                    handler );
    }

    public void fireEvent(GwtEvent< ? > event) {
        if ( event == null ) {
            throw new IllegalArgumentException( "event cannot be null" );
        }
        eventBus.fireEvent( event );
    }

}
