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

import java.util.Date;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.resources.ComparableImage;
import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.widgets.tables.sorting.AbstractSortableHeaderGroup;
import org.drools.guvnor.client.widgets.tables.sorting.SimpleSortableHeader;
import org.drools.guvnor.client.widgets.tables.sorting.SimpleSortableHeaderGroup;

/**
 * Widget with a table of Archived Assets.
 */
public class AdminArchivedPagedTable extends AbstractAssetPagedTable<AdminArchivedPageRow> {

    // UI
    interface AdminArchivedPagedTableBinder
            extends
            UiBinder<Widget, AdminArchivedPagedTable> {

    }

    @UiField()
    protected Button restoreSelectedAssetButton;

    @UiField()
    protected Button deleteSelectedAssetButton;

    private static AdminArchivedPagedTableBinder uiBinder = GWT.create( AdminArchivedPagedTableBinder.class );

    // Commands for UI
    private Command restoreSelectedAssetCommand;
    private Command deleteSelectedAssetCommand;

    // Other stuff
    private static final int PAGE_SIZE = 10;
    private final ClientFactory clientFactory;

    public AdminArchivedPagedTable( Command restoreSelectedAssetCommand,
                                    Command deleteSelectedAssetCommand,
                                    ClientFactory clientFactory ) {
        super( PAGE_SIZE,
               clientFactory );
        this.restoreSelectedAssetCommand = restoreSelectedAssetCommand;
        this.deleteSelectedAssetCommand = deleteSelectedAssetCommand;
        this.clientFactory = clientFactory;
        setDataProvider( new AsyncDataProvider<AdminArchivedPageRow>() {
            protected void onRangeChanged( HasData<AdminArchivedPageRow> display ) {
                PageRequest request = new PageRequest( pager.getPageStart(),
                                                       pageSize );
                assetService.loadArchivedAssets( request,
                                                 new GenericCallback<PageResponse<AdminArchivedPageRow>>() {
                                                     public void onSuccess( PageResponse<AdminArchivedPageRow> response ) {
                                                         updateRowCount( response.getTotalRowSize(),
                                                                         response.isTotalRowSizeExact() );
                                                         updateRowData( response.getStartRowIndex(),
                                                                        response.getPageRowList() );
                                                     }
                                                 } );
            }
        } );

    }

    /**
     * Set up table and common columns. Additional columns can be appended
     * between the "checkbox" and "open" columns by overriding
     * <code>addAncillaryColumns()</code>
     */
    @Override
    protected void doCellTable() {

        ProvidesKey<AdminArchivedPageRow> providesKey = new ProvidesKey<AdminArchivedPageRow>() {
            public Object getKey( AdminArchivedPageRow row ) {
                return row.getUuid();
            }
        };

        cellTable = new CellTable<AdminArchivedPageRow>( providesKey );
        selectionModel = new MultiSelectionModel<AdminArchivedPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<AdminArchivedPageRow> columnPicker = new ColumnPicker<AdminArchivedPageRow>( cellTable );
        SimpleSortableHeaderGroup<AdminArchivedPageRow> sortableHeaderGroup = new SimpleSortableHeaderGroup<AdminArchivedPageRow>( cellTable );

        final TextColumn<AdminArchivedPageRow> uuidNumberColumn = new TextColumn<AdminArchivedPageRow>() {
            public String getValue( AdminArchivedPageRow row ) {
                return row.getUuid();
            }
        };
        columnPicker.addColumn( uuidNumberColumn,
                                new SimpleSortableHeader<AdminArchivedPageRow, String>( sortableHeaderGroup,
                                                                                        constants.uuid(),
                                                                                        uuidNumberColumn ),
                                false );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column
        Column<AdminArchivedPageRow, String> openColumn = new Column<AdminArchivedPageRow, String>( new ButtonCell() ) {
            public String getValue( AdminArchivedPageRow row ) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<AdminArchivedPageRow, String>() {
            public void update( int index,
                                AdminArchivedPageRow row,
                                String value ) {
                clientFactory.getPlaceController().goTo( new AssetEditorPlace( row.getUuid() ) );
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( constants.Open() ),
                                true );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    @Override
    protected void addAncillaryColumns( ColumnPicker<AdminArchivedPageRow> columnPicker,
                                        AbstractSortableHeaderGroup<AdminArchivedPageRow> sortableHeaderGroup ) {

        Column<AdminArchivedPageRow, ComparableImage> formatColumn = new Column<AdminArchivedPageRow, ComparableImage>( new ComparableImageCell() ) {

            public ComparableImage getValue( AdminArchivedPageRow row ) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new ComparableImage( row.getFormat(), factory.getAssetEditorIcon( row.getFormat() ) );
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SimpleSortableHeader<AdminArchivedPageRow, ComparableImage>( sortableHeaderGroup,
                                                                                                 constants.Format(),
                                                                                                 formatColumn ),
                                true );

        TextColumn<AdminArchivedPageRow> nameColumn = new TextColumn<AdminArchivedPageRow>() {
            public String getValue( AdminArchivedPageRow row ) {
                String name = row.getName();
                return name;
            }
        };
        columnPicker.addColumn( nameColumn,
                                new SimpleSortableHeader<AdminArchivedPageRow, String>( sortableHeaderGroup,
                                                                                        constants.Name(),
                                                                                        nameColumn ),
                                true );

        TextColumn<AdminArchivedPageRow> packageNameColumn = new TextColumn<AdminArchivedPageRow>() {
            public String getValue( AdminArchivedPageRow row ) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SimpleSortableHeader<AdminArchivedPageRow, String>( sortableHeaderGroup,
                                                                                        constants.PackageName(),
                                                                                        packageNameColumn ),
                                false );

        TextColumn<AdminArchivedPageRow> lastContributorColumn = new TextColumn<AdminArchivedPageRow>() {
            public String getValue( AdminArchivedPageRow row ) {
                return row.getLastContributor();
            }
        };
        columnPicker.addColumn( lastContributorColumn,
                                new SimpleSortableHeader<AdminArchivedPageRow, String>( sortableHeaderGroup,
                                                                                        constants.LastContributor(),
                                                                                        lastContributorColumn ),
                                true );

        Column<AdminArchivedPageRow, Date> lastModifiedColumn = new Column<AdminArchivedPageRow, Date>( new DateCell( DateTimeFormat.getFormat(
                DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( AdminArchivedPageRow row ) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SimpleSortableHeader<AdminArchivedPageRow, Date>( sortableHeaderGroup,
                                                                                      constants.LastModified(),
                                                                                      lastModifiedColumn ),
                                true );

    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("restoreSelectedAssetButton")
    void restoreSelectedAsset( ClickEvent e ) {
        restoreSelectedAssetCommand.execute();
    }

    @UiHandler("deleteSelectedAssetButton")
    void deleteSelectedAsset( ClickEvent e ) {
        deleteSelectedAssetCommand.execute();
    }

}
