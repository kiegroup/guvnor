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

package org.drools.guvnor.client.widgets.tables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.AbstractAssetPageRow;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.rulelist.OpenItemCommand;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Widget that shows rows of paged data where columns "uuid", "name" and
 * "format" are common. A "checkbox" and "open" button column are added by
 * default. Additional columns can be inserted inbetween these columns by
 * overriding <code>addAncillaryColumns()</code>. A "RSS Feed" button can also
 * be included if required.
 * 
 * Based upon work by Geoffrey de Smet.
 */
public abstract class AbstractAssetPagedTable<T extends AbstractAssetPageRow> extends AbstractPagedTable<T> {

    // UI
    @SuppressWarnings("rawtypes")
    interface AssetPagedTableBinder
        extends
        UiBinder<Widget, AbstractAssetPagedTable> {
    }

    protected static final Constants     constants         = GWT.create( Constants.class );

    private static AssetPagedTableBinder uiBinder          = GWT.create( AssetPagedTableBinder.class );

    @UiField()
    protected Image                      feedImage;

    protected Set<Command>               unloadListenerSet = new HashSet<Command>();
    protected MultiSelectionModel<T>     selectionModel;
    protected final OpenItemCommand      openSelectedCommand;

    protected String                     feedURL;

    /**
     * Constructor
     * 
     * @param pageSize
     * @param editEvent
     */
    public AbstractAssetPagedTable(int pageSize,
                                   OpenItemCommand editEvent) {
        this( pageSize,
              editEvent,
              null );
    }

    /**
     * Constructor
     * 
     * @param pageSize
     * @param openSelectedCommand
     * @param feedURL
     */
    public AbstractAssetPagedTable(int pageSize,
                                   OpenItemCommand openSelectedCommand,
                                   String feedURL) {
        super( pageSize );
        this.openSelectedCommand = openSelectedCommand;
        this.feedURL = feedURL;
        if ( this.feedURL == null
                || "".equals( feedURL ) ) {
            this.feedImage.setVisible( false );
        }

    }

    /**
     * Register an UnloadListener used to remove "RSS Feed Listeners" when the
     * table is unloaded
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
     * Open selected item(s) to a single tab
     * 
     * @param e
     */
    @UiHandler("openSelectedToSingleTabButton")
    public void openSelectedToSingleTab(ClickEvent e) {
        Set<T> selectedSet = selectionModel.getSelectedSet();
        List<MultiViewRow> multiViewRowList = new ArrayList<MultiViewRow>( selectedSet.size() );
        for ( T selected : selectedSet ) {
            MultiViewRow row = new MultiViewRow();
            row.uuid = selected.getUuid();
            row.format = selected.getFormat();
            row.name = selected.getName();
            multiViewRowList.add( row );
        }
        openSelectedCommand.open( multiViewRowList.toArray( new MultiViewRow[multiViewRowList.size()] ) );
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
     * Set up table and common columns. Additional columns can be appended
     * between the "checkbox" and "open" columns by overriding
     * <code>addAncillaryColumns()</code>
     */
    @Override
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
                openSelectedCommand.open( row.getUuid() );
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( constants.Open() ),
                                true );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();

    }

    /**
     * Disconnect all listening consumers
     */
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

    /**
     * Construct a widget representing the table
     */
    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
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
     * Open selected item(s) to separate tabs
     * 
     * @param e
     */
    @UiHandler("openSelectedButton")
    void openSelected(ClickEvent e) {
        Set<T> selectedSet = selectionModel.getSelectedSet();
        for ( T selected : selectedSet ) {
            openSelectedCommand.open( selected.getUuid() );
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
}
