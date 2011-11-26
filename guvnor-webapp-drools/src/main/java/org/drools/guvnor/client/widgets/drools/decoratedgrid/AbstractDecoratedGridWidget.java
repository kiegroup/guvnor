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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.widgets.drools.decoratedgrid.AbstractMergableGridWidget.CellSelectionDetail;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.AppendRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.ColumnResizeEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.DeleteRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertColumnEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.InsertRowEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SelectedCellChangeEvent;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.events.SetModelEvent;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Abstract grid, decorated with DecoratedGridHeaderWidget and
 * DecoratedGridSidebarWidget encapsulating basic operation: keyboard navigation
 * and column resizing.
 * 
 * @param <M>
 *            The domain model represented by the Grid
 * @param <T>
 *            The type of domain columns represented by the Grid
 * @param <C>
 *            The type of domain cell represented by the Grid
 */
public abstract class AbstractDecoratedGridWidget<M, T, C> extends Composite
    implements
    SelectedCellValueUpdater,
    ColumnResizeEvent.Handler,
    SelectedCellChangeEvent.Handler,
    DeleteRowEvent.Handler,
    InsertRowEvent.Handler,
    AppendRowEvent.Handler,
    SetModelEvent.Handler<M>,
    DeleteColumnEvent.Handler,
    InsertColumnEvent.Handler<T, C> {

    // Widgets for UI
    protected Panel                                    mainPanel;
    protected Panel                                    bodyPanel;
    protected ScrollPanel                              scrollPanel;
    protected AbstractMergableGridWidget<M, T>         gridWidget;
    protected AbstractDecoratedGridHeaderWidget<M, T>  headerWidget;
    protected AbstractDecoratedGridSidebarWidget<M, T> sidebarWidget;

    protected int                                      height;
    protected int                                      width;

    // Resources
    protected final ResourcesProvider<T>               resources;

    //Event Bus
    protected final EventBus                           eventBus;

    /**
     * Construct at empty DecoratedGridWidget, without DecoratedGridHeaderWidget
     * or DecoratedGridSidebarWidget These should be set before the grid is
     * displayed using setHeaderWidget and setSidebarWidget respectively.
     */
    public AbstractDecoratedGridWidget(ResourcesProvider<T> resources,
                                       EventBus eventBus,
                                       Panel mainPanel,
                                       Panel bodyPanel,
                                       AbstractMergableGridWidget<M, T> gridWidget,
                                       AbstractDecoratedGridHeaderWidget<M, T> headerWidget,
                                       AbstractDecoratedGridSidebarWidget<M, T> sidebarWidget) {

        if ( resources == null ) {
            throw new IllegalArgumentException( "resources cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        if ( mainPanel == null ) {
            throw new IllegalArgumentException( "mainPanel cannot be null" );
        }
        if ( bodyPanel == null ) {
            throw new IllegalArgumentException( "bodyPanel cannot be null" );
        }
        if ( gridWidget == null ) {
            throw new IllegalArgumentException( "gridWidget cannot be null" );
        }
        if ( headerWidget == null ) {
            throw new IllegalArgumentException( "headerWidget cannot be null" );
        }
        if ( sidebarWidget == null ) {
            throw new IllegalArgumentException( "sidebarWidget cannot be null" );
        }

        this.resources = resources;
        this.eventBus = eventBus;
        this.mainPanel = mainPanel;
        this.bodyPanel = bodyPanel;
        this.gridWidget = gridWidget;
        this.headerWidget = headerWidget;
        this.sidebarWidget = sidebarWidget;

        scrollPanel = new ScrollPanel();
        scrollPanel.add( gridWidget );
        scrollPanel.addScrollHandler( getScrollHandler() );

        initialiseHeaderWidget();
        initialiseSidebarWidget();

        initWidget( mainPanel );

        //Wire-up event handlers
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( SelectedCellChangeEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteColumnEvent.TYPE,
                             this );
    }

    /**
     * Resize the DecoratedGridHeaderWidget and DecoratedGridSidebarWidget when
     * DecoratedGridWidget shows scrollbars
     */
    protected void assertDimensions() {
        headerWidget.setWidth( scrollPanel.getElement().getClientWidth()
                               + "px" );
        sidebarWidget.setHeight( scrollPanel.getElement().getClientHeight()
                                 + "px" );
    }

    /**
     * Return the ScrollPanel in which the DecoratedGridWidget "grid" is nested.
     * This allows ScrollEvents to be hooked up to other defendant controls
     * (e.g. the Header).
     * 
     * @return
     */
    protected abstract ScrollHandler getScrollHandler();

    //Initialise the Header Widget and attach resize handlers to GridWidget to support
    //column resizing and to resize GridWidget's ScrollPanel when header resizes.
    private void initialiseHeaderWidget() {
        eventBus.addHandler( ColumnResizeEvent.TYPE,
                             this );
        this.headerWidget.addResizeHandler( new ResizeHandler() {

            public void onResize(ResizeEvent event) {
                scrollPanel.setHeight( (height - event.getHeight())
                                       + "px" );
                assertDimensions();
            }
        } );
        bodyPanel.add( headerWidget );
        bodyPanel.add( scrollPanel );
    }

    //Set the SidebarWidget and attach a ResizeEvent handler to the Sidebar for when the header changes 
    //size and the Sidebar needs to be redrawn to align correctly. Also attach a RowGroupingChangeEvent 
    //handler to the MergableGridWidget so the Sidebar can redraw itself when rows are merged, grouped, 
    //ungrouped or unmerged.
    private void initialiseSidebarWidget() {
        this.headerWidget.addResizeHandler( new ResizeHandler() {

            public void onResize(ResizeEvent event) {
                sidebarWidget.resizeSidebar( event.getHeight() );
            }

        } );

        this.mainPanel.add( sidebarWidget );
        this.mainPanel.add( bodyPanel );
    }

    /**
     * This should be used instead of setHeight(String) and setWidth(String) as
     * various child Widgets of the DecisionTable need to have their sizes set
     * relative to the outermost Widget (i.e. this).
     */
    @Override
    public void setPixelSize(int width,
                             int height) {
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }
        if ( height < 0 ) {
            throw new IllegalArgumentException( "height cannot be less than zero" );
        }
        super.setPixelSize( width,
                            height );
        this.height = height;
        setHeight( height );
        setWidth( width );
    }

    /**
     * Sort data based upon information stored in Columns
     */
    public void sort() {

        //TODO {manstis} Fix sorting
        //Extract list of sort information
        List<SortConfiguration> sortConfig = new ArrayList<SortConfiguration>();
        //        List<DynamicColumn<T>> columns = gridWidget.getColumns();
        //        for ( DynamicColumn<T> column : columns ) {
        //            SortConfiguration sc = column.getSortConfiguration();
        //            if ( sc.getSortIndex() != -1 ) {
        //                sortConfig.add( sc );
        //            }
        //        }

        //        gridWidget.getData().sort( sortConfig );

        //Redraw whole table
        gridWidget.redraw();
    }

    //Ensure the selected cell is visible
    private void cellSelected(CellSelectionDetail ce) {

        //No selection
        if ( ce == null ) {
            return;
        }

        //Left extent
        if ( ce.getOffsetX() < scrollPanel.getHorizontalScrollPosition() ) {
            scrollPanel.setHorizontalScrollPosition( ce.getOffsetX() );
        }

        //Right extent
        int scrollWidth = scrollPanel.getElement().getClientWidth();
        if ( ce.getOffsetX() + ce.getWidth() > scrollWidth + scrollPanel.getHorizontalScrollPosition() ) {
            int delta = ce.getOffsetX() + ce.getWidth() - scrollPanel.getHorizontalScrollPosition() - scrollWidth;
            scrollPanel.setHorizontalScrollPosition( scrollPanel.getHorizontalScrollPosition() + delta );
        }

        //Top extent
        if ( ce.getOffsetY() < scrollPanel.getVerticalScrollPosition() ) {
            scrollPanel.setVerticalScrollPosition( ce.getOffsetY() );
        }

        //Bottom extent
        int scrollHeight = scrollPanel.getElement().getClientHeight();
        if ( ce.getOffsetY() + ce.getHeight() > scrollHeight + scrollPanel.getVerticalScrollPosition() ) {
            int delta = ce.getOffsetY() + ce.getHeight() - scrollPanel.getVerticalScrollPosition() - scrollHeight;
            scrollPanel.setVerticalScrollPosition( scrollPanel.getVerticalScrollPosition() + delta );
        }

    }

    // Set height of outer most Widget and related children
    private void setHeight(final int height) {
        mainPanel.setHeight( height
                             + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleFinally( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    // Set width of outer most Widget and related children
    private void setWidth(int width) {
        mainPanel.setWidth( width
                            + "px" );
        scrollPanel.setWidth( (width - resources.sidebarWidth())
                              + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleFinally( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    /**
     * Return an immutable list of selected cells
     * 
     * @return The selected cells
     */
    public List<CellValue< ? >> getSelectedCells() {
        return this.gridWidget.getSelectedCells();
    }

    /**
     * Set the value of the selected cells
     * 
     * @param value
     */
    public void setSelectedCellsValue(Object value) {
        this.gridWidget.setSelectedCellsValue( value );
    }

    public void onColumnResize(final ColumnResizeEvent event) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onSelectedCellChange(SelectedCellChangeEvent event) {
        cellSelected( event.getCellSelectionDetail() );
    }

    public void onDeleteRow(DeleteRowEvent event) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onInsertRow(InsertRowEvent event) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onAppendRow(AppendRowEvent event) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onDeleteColumn(DeleteColumnEvent event) {
        if ( event.redraw() ) {
            Scheduler.get().scheduleDeferred( new Command() {

                public void execute() {
                    assertDimensions();
                }

            } );
        }
    }

}
