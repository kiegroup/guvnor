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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.resources.ComparableImage;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;
import org.drools.guvnor.client.widgets.tables.sorting.AbstractSortableHeaderGroup;
import org.drools.guvnor.client.widgets.tables.sorting.SimpleSortableHeader;
import org.drools.guvnor.client.widgets.tables.sorting.SimpleSortableHeaderGroup;

/**
 * Widget with a table of "By Category" query results.
 */
public class CategoryPagedTable extends AbstractAssetPagedTable<CategoryPageRow> {

    private static final int PAGE_SIZE = 10;
    private final ClientFactory clientFactory;

    /**
     * Constructor
     * @param categoryName
     * @param feedURL
     */
    public CategoryPagedTable( final String categoryName,
                               final String feedURL,
                               ClientFactory clientFactory ) {
        super( PAGE_SIZE,
               feedURL,
               clientFactory );
        this.clientFactory = clientFactory;

        setDataProvider( new AsyncDataProvider<CategoryPageRow>() {
            protected void onRangeChanged( HasData<CategoryPageRow> display ) {
                CategoryPageRequest request = new CategoryPageRequest();
                request.setCategoryPath( categoryName );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                categoryService.loadRuleListForCategories( request,
                                                           new GenericCallback<PageResponse<CategoryPageRow>>() {
                                                               public void onSuccess( PageResponse<CategoryPageRow> response ) {
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

        ProvidesKey<CategoryPageRow> providesKey = new ProvidesKey<CategoryPageRow>() {
            public Object getKey( CategoryPageRow row ) {
                return row.getUuid();
            }
        };

        cellTable = new CellTable<CategoryPageRow>( providesKey );
        selectionModel = new MultiSelectionModel<CategoryPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<CategoryPageRow> columnPicker = new ColumnPicker<CategoryPageRow>( cellTable );
        SimpleSortableHeaderGroup<CategoryPageRow> sortableHeaderGroup = new SimpleSortableHeaderGroup<CategoryPageRow>( cellTable );

        final TextColumn<CategoryPageRow> uuidNumberColumn = new TextColumn<CategoryPageRow>() {
            public String getValue( CategoryPageRow row ) {
                return row.getUuid();
            }
        };
        columnPicker.addColumn( uuidNumberColumn,
                                new SimpleSortableHeader<CategoryPageRow, String>( sortableHeaderGroup,
                                                                                   constants.uuid(),
                                                                                   uuidNumberColumn ),
                                false );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column
        Column<CategoryPageRow, String> openColumn = new Column<CategoryPageRow, String>( new ButtonCell() ) {
            public String getValue( CategoryPageRow row ) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<CategoryPageRow, String>() {
            public void update( int index,
                                CategoryPageRow row,
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
    protected void addAncillaryColumns( ColumnPicker<CategoryPageRow> columnPicker,
                                        AbstractSortableHeaderGroup<CategoryPageRow> sortableHeaderGroup ) {

        Column<CategoryPageRow, ComparableImage> formatColumn = new Column<CategoryPageRow, ComparableImage>( new ComparableImageCell() ) {

            public ComparableImage getValue( CategoryPageRow row ) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new ComparableImage( row.getFormat(), factory.getAssetEditorIcon( row.getFormat() ) );
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SimpleSortableHeader<CategoryPageRow, ComparableImage>( sortableHeaderGroup,
                                                                                            constants.Format(),
                                                                                            formatColumn ),
                                true );

        TitledTextColumn<CategoryPageRow> titleColumn = new TitledTextColumn<CategoryPageRow>() {
            public TitledText getValue( CategoryPageRow row ) {
                TitledText tt = new TitledText( row.getName(),
                                                row.getAbbreviatedDescription() );
                return tt;
            }
        };
        columnPicker.addColumn( titleColumn,
                                new SimpleSortableHeader<CategoryPageRow, TitledText>( sortableHeaderGroup,
                                                                                       constants.Name(),
                                                                                       titleColumn ),
                                true );

        TextColumn<CategoryPageRow> packageNameColumn = new TextColumn<CategoryPageRow>() {
            public String getValue( CategoryPageRow row ) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SimpleSortableHeader<CategoryPageRow, String>( sortableHeaderGroup,
                                                                                   constants.PackageName(),
                                                                                   packageNameColumn ),
                                true );

        TextColumn<CategoryPageRow> statusNameColumn = new TextColumn<CategoryPageRow>() {
            public String getValue( CategoryPageRow row ) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn( statusNameColumn,
                                new SimpleSortableHeader<CategoryPageRow, String>( sortableHeaderGroup,
                                                                                   constants.Status(),
                                                                                   statusNameColumn ),
                                true );

        Column<CategoryPageRow, Date> lastModifiedColumn = new Column<CategoryPageRow, Date>( new DateCell( DateTimeFormat.getFormat(
                DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( CategoryPageRow row ) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SimpleSortableHeader<CategoryPageRow, Date>( sortableHeaderGroup,
                                                                                 constants.LastModified(),
                                                                                 lastModifiedColumn ),
                                true );

    }

    @Override
    protected void onUnload() {
        super.onUnload();
        for ( Command unloadListener : unloadListenerSet ) {
            unloadListener.execute();
        }
    }

}
