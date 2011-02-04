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

package org.drools.guvnor.client.rulelist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.table.ColumnPicker;
import org.drools.guvnor.client.table.SelectionColumn;
import org.drools.guvnor.client.table.SortableHeader;
import org.drools.guvnor.client.table.SortableHeaderGroup;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;

/**
 * Widget that shows rows of AssetItems data. This makes the assumption that a
 * large number of columns are common to all grids displayed in Guvnor. Based
 * upon work by Geoffrey de Smet.
 * 
 * @author manstis
 */
public abstract class AbstractPagedTable<T extends AbstractPageRow> extends Composite {

    // UI
    @SuppressWarnings("rawtypes")
    interface PagedTableBinder
        extends
        UiBinder<Widget, AbstractPagedTable> {
    }

    // Usual suspects
    protected static final Constants constants         = GWT.create( Constants.class );

    // TODO use (C)DI
    protected RepositoryServiceAsync repositoryService = RepositoryServiceFactory.getService();
    protected Set<Command>           unloadListenerSet = new HashSet<Command>();
    protected MultiSelectionModel<T> selectionModel;
    protected final OpenItemCommand  editEvent;
    protected AsyncDataProvider<T>   dataProvider;

    protected String                 feedURL;

    protected int                    pageSize          = 5;

    private static PagedTableBinder  uiBinder          = GWT.create( PagedTableBinder.class );

    @UiField(provided = true)
    ToggleButton                     columnPickerButton;

    @UiField(provided = true)
    CellTable<T>                     cellTable;

    @UiField(provided = true)
    SimplePager                      pager;

    @UiField()
    Image                            feedImage;

    @UiField()
    HorizontalPanel                  toolbar;

    /**
     * Simple constructor that associates an OpenItemCommand with the "Open"
     * column and other buttons.
     * 
     * @param event
     */
    public AbstractPagedTable(int pageSize,
                              OpenItemCommand editEvent) {
        this( pageSize,
              editEvent,
              null );
    }

    /**
     * Simple constructor that associates an OpenItemCommand with the "Open"
     * column and other buttons.
     * 
     * @param event
     */
    public AbstractPagedTable(int pageSize,
                              OpenItemCommand editEvent,
                              String feedURL) {
        this.pageSize = pageSize;
        this.editEvent = editEvent;
        this.feedURL = feedURL;
        doCellTable();
        initWidget( uiBinder.createAndBindUi( this ) );

        if ( this.feedURL == null
                || "".equals( feedURL ) ) {
            this.feedImage.setVisible( false );
        }

    }

    /**
     * Register an UnloadListener
     * 
     * @param unloadListener
     */
    public void addUnloadListener(Command unloadListener) {
        unloadListenerSet.add( unloadListener );
    }

    /**
     * Return an array of selected UUIDs. API is maintained for backwards
     * compatibility of legacy code with AssetItemGrid's implementation
     * 
     * @return
     */
    public String[] getSelectedRowUUIDs() {
        Set<T> selectedRows = selectionModel.getSelectedSet();

        // Compatibility with existing API
        if ( selectedRows.size() == 0 ) {
            return null;
        }

        // Create the array of UUIDs
        String[] uuids = new String[selectedRows.size()];
        int rowCount = 0;
        for ( T row : selectedRows ) {
            uuids[rowCount++] = row.getUuid();
        }
        return uuids;
    }

    /**
     * Open selected item(s)
     * 
     * @param e
     */
    @UiHandler("openSelectedToSingleTabButton")
    public void openSelectedToSingleTab(ClickEvent e) {
        Set<T> selectedSet = selectionModel.getSelectedSet();
        // TODO directly push the selected QueryPageRows
        List<MultiViewRow> multiViewRowList = new ArrayList<MultiViewRow>( selectedSet.size() );
        for ( T selected : selectedSet ) {
            MultiViewRow row = new MultiViewRow();
            row.uuid = selected.getUuid();
            row.format = selected.getFormat();
            row.name = selected.getName();
            multiViewRowList.add( row );
        }
        editEvent.open( multiViewRowList.toArray( new MultiViewRow[multiViewRowList.size()] ) );
    }

    /**
     * Refresh table programmatically
     */
    public void refresh() {
        selectionModel.clear();
        cellTable.setVisibleRangeAndClearData( cellTable.getVisibleRange(),
                                               true );
    }

    /**
     * Override to add additional columns to the table
     * 
     * @param columnPicker
     * @param sortableHeaderGroup
     */
    protected abstract void addAncillaryColumns(ColumnPicker<T> columnPicker,
                                                SortableHeaderGroup<T> sortableHeaderGroup);

    /**
     * Set up table and common columns
     */
    protected void doCellTable() {

        ProvidesKey<T> providesKey = new ProvidesKey<T>() {
            public Object getKey(T row) {
                return row.getUuid();
            }
        };

        cellTable = new CellTable<T>( providesKey );
        selectionModel = new MultiSelectionModel<T>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<T> columnPicker = new ColumnPicker<T>( cellTable );
        SortableHeaderGroup<T> sortableHeaderGroup = new SortableHeaderGroup<T>( cellTable );

        final TextColumn<T> uuidNumberColumn = new TextColumn<T>() {
            public String getValue(T row) {
                return row.getUuid();
            }
        };
        columnPicker.addColumn( uuidNumberColumn,
                                new SortableHeader<T, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.uuid(),
                                                                          uuidNumberColumn ),
                                false );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column
        Column<T, String> openColumn = new Column<T, String>( new ButtonCell() ) {
            public String getValue(T row) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<T, String>() {
            public void update(int index,
                               T row,
                               String value) {
                editEvent.open( row.getUuid() );
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( constants.Open() ),
                                true );

        // cellTable.setPageSize( pageSize );
        cellTable.setWidth( "100%" );

        pager = new SimplePager() {

            // We want pageSize to remain constant
            @Override
            public int getPageSize() {
                return pageSize;
            }

            // Page forward by an exact size rather than the number of visible
            // rows as is in the norm in the underlying implementation
            @Override
            public void nextPage() {
                if ( getDisplay() != null ) {
                    Range range = getDisplay().getVisibleRange();
                    setPageStart( range.getStart()
                                  + getPageSize() );
                }
            }

            // Page back by an exact size rather than the number of visible
            // rows as is in the norm in the underlying implementation
            @Override
            public void previousPage() {
                if ( getDisplay() != null ) {
                    Range range = getDisplay().getVisibleRange();
                    setPageStart( range.getStart()
                                  - getPageSize() );
                }
            }

            // Override so the last page is shown with a number of rows less
            // than the pageSize rather than always showing the pageSize number
            // of rows and possibly repeating rows on the last and penultimate
            // page
            @Override
            public void setPageStart(int index) {
                if ( getDisplay() != null ) {
                    Range range = getDisplay().getVisibleRange();
                    int displayPageSize = getPageSize();
                    if ( isRangeLimited()
                         && getDisplay().isRowCountExact() ) {
                        displayPageSize = Math.min( getPageSize(),
                                                    getDisplay().getRowCount()
                                                            - index );
                    }
                    index = Math.max( 0,
                                      index );
                    if ( index != range.getStart() ) {
                        getDisplay().setVisibleRange( index,
                                                      displayPageSize );
                    }
                }
            }

            // Override to display "0 of 0" when there are no records (otherwise
            // you get "1-1 of 0") and "1 of 1" when there is only one record
            // (otherwise you get "1-1 of 1"). Not internationalised (but
            // neither is SimplePager)
            protected String createText() {
                NumberFormat formatter = NumberFormat.getFormat( "#,###" );
                HasRows display = getDisplay();
                Range range = display.getVisibleRange();
                int pageStart = range.getStart() + 1;
                int pageSize = range.getLength();
                int dataSize = display.getRowCount();
                int endIndex = Math.min( dataSize,
                                         pageStart
                                                 + pageSize
                                                 - 1 );
                endIndex = Math.max( pageStart,
                                     endIndex );
                boolean exact = display.isRowCountExact();
                if ( dataSize == 0 ) {
                    return "0 of 0";
                } else if ( pageStart == endIndex ) {
                    return formatter.format( pageStart )
                           + " of "
                           + formatter.format( dataSize );
                }
                return formatter.format( pageStart )
                       + "-"
                       + formatter.format( endIndex )
                       + (exact ? " of " : " of over ")
                       + formatter.format( dataSize );
            }

        };
        pager.setDisplay( cellTable );
        pager.setPageSize( pageSize );

        columnPickerButton = columnPicker.createToggleButton();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        for ( Command unloadListener : unloadListenerSet ) {
            unloadListener.execute();
        }
    }

    /**
     * Link a data provider to the table
     * 
     * @param dataProvider
     */
    protected void setDataProvider(AsyncDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
        this.dataProvider.addDataDisplay( cellTable );
    }

    @UiHandler("feedImage")
    void openFeed(ClickEvent e) {
        if ( !feedImage.isVisible()
             || feedURL == null
             || "".equals( feedURL ) ) {
            return;
        }
        Window.open( feedURL,
                     "_blank",
                     null );
    }

    /**
     * Open selected item(s)
     * 
     * @param e
     */
    @UiHandler("openSelectedButton")
    void openSelected(ClickEvent e) {
        Set<T> selectedSet = selectionModel.getSelectedSet();
        for ( T selected : selectedSet ) {
            // TODO directly push the selected QueryPageRow
            editEvent.open( selected.getUuid() );
        }
    }

    /**
     * Refresh table in response to ClickEvent
     * 
     * @param e
     */
    @UiHandler("refreshButton")
    void refresh(ClickEvent e) {
        refresh();
    }

    /**
     * Returns an area of the widget in which additional buttons can be added
     * 
     * @return
     */
    public HorizontalPanel getToolbar() {
        return this.toolbar;
    }
}
