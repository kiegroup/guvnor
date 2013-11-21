/*
 * Copyright 2010 JBoss Inc
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
import java.util.List;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.resources.ComparableImage;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.SortableColumnMetaData;
import org.drools.guvnor.client.rpc.SortableColumnsMetaData;
import org.drools.guvnor.client.rpc.SortableFieldNames;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;
import org.drools.guvnor.client.widgets.tables.sorting.AbstractSortableHeader;
import org.drools.guvnor.client.widgets.tables.sorting.AbstractSortableHeaderGroup;
import org.drools.guvnor.client.widgets.tables.sorting.SortableHeader;
import org.drools.guvnor.client.widgets.tables.sorting.SortableHeaderGroup;

/**
 * Widget with a table of Assets.
 */
public class AssetPagedTable extends AbstractAssetPagedTable<AssetPageRow> {

    private static final int PAGE_SIZE = 10;
    private ClientFactory clientFactory;

    private SortableHeaderGroup<AssetPageRow> sortableHeaderGroup;

    public AssetPagedTable( String packageUuid,
                            List<String> formatInList,
                            Boolean formatIsRegistered,
                            ClientFactory clientFactory ) {
        this( packageUuid,
              formatInList,
              formatIsRegistered,
              null,
              clientFactory );
    }

    public AssetPagedTable( final String packageUuid,
                            final List<String> formatInList,
                            final Boolean formatIsRegistered,
                            String feedURL,
                            ClientFactory clientFactory ) {
        super( PAGE_SIZE,
               feedURL,
               clientFactory );
        this.clientFactory = clientFactory;

        setDataProvider( new AsyncDataProvider<AssetPageRow>() {
            protected void onRangeChanged( HasData<AssetPageRow> display ) {

                final List<AbstractSortableHeader<AssetPageRow>> headers = sortableHeaderGroup.getSortOrderList();
                final SortableColumnsMetaData sortMetaData = new SortableColumnsMetaData();
                for ( AbstractSortableHeader<AssetPageRow> header : headers ) {
                    final SortableHeader h = ( (SortableHeader) header );
                    sortMetaData.getSortListOrder().add( new SortableColumnMetaData( h.getFieldName().getFieldName(),
                                                                                     h.getSortDirection() ) );
                }

                AssetPageRequest request = new AssetPageRequest( packageUuid,
                                                                 formatInList,
                                                                 formatIsRegistered,
                                                                 pager.getPageStart(),
                                                                 pageSize,
                                                                 sortMetaData );
                assetService.findAssetPage( request,
                                            new GenericCallback<PageResponse<AssetPageRow>>() {
                                                public void onSuccess( PageResponse<AssetPageRow> response ) {
                                                    updateRowCount( response.getTotalRowSize(),
                                                                    response.isTotalRowSizeExact() );
                                                    updateRowData( response.getStartRowIndex(),
                                                                   response.getPageRowList() );
                                                }
                                            } );
            }
        } );

        // Add a ColumnSortEvent.AsyncHandler to connect sorting to the AsyncDataPRrovider.
        ColumnSortEvent.AsyncHandler columnSortHandler = new ColumnSortEvent.AsyncHandler( cellTable );
        cellTable.addColumnSortHandler( columnSortHandler );
    }

    /**
     * Set up table and common columns. Additional columns can be appended
     * between the "checkbox" and "open" columns by overriding
     * <code>addAncillaryColumns()</code>
     */
    @Override
    protected void doCellTable() {

        ProvidesKey<AssetPageRow> providesKey = new ProvidesKey<AssetPageRow>() {
            public Object getKey( AssetPageRow row ) {
                return row.getUuid();
            }
        };

        cellTable = new CellTable<AssetPageRow>( providesKey );
        selectionModel = new MultiSelectionModel<AssetPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<AssetPageRow> columnPicker = new ColumnPicker<AssetPageRow>( cellTable );
        sortableHeaderGroup = new SortableHeaderGroup<AssetPageRow>( cellTable );

        final TextColumn<AssetPageRow> uuidNumberColumn = new TextColumn<AssetPageRow>() {
            public String getValue( AssetPageRow row ) {
                return row.getUuid();
            }
        };
        columnPicker.addColumn( uuidNumberColumn,
                                new TextHeader( constants.uuid() ),
                                false );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        // Add "Open" button column
        Column<AssetPageRow, String> openColumn = new Column<AssetPageRow, String>( new ButtonCell() ) {
            public String getValue( AssetPageRow row ) {
                return constants.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<AssetPageRow, String>() {
            public void update( int index,
                                AssetPageRow row,
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
    protected void addAncillaryColumns( ColumnPicker<AssetPageRow> columnPicker,
                                        AbstractSortableHeaderGroup<AssetPageRow> sortableHeaderGroup ) {

        Column<AssetPageRow, ComparableImage> formatColumn = new Column<AssetPageRow, ComparableImage>( new ComparableImageCell() ) {

            public ComparableImage getValue( AssetPageRow row ) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new ComparableImage( row.getFormat(),
                                            factory.getAssetEditorIcon( row.getFormat() ) );
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<AssetPageRow>( sortableHeaderGroup,
                                                                  SortableFieldNames.FORMAT_PROPERTY_NAME,
                                                                  constants.Format() ),
                                true );

        TitledTextColumn<AssetPageRow> titleColumn = new TitledTextColumn<AssetPageRow>() {
            public TitledText getValue( AssetPageRow row ) {
                TitledText tt = new TitledText( row.getName(),
                                                row.getAbbreviatedDescription() );
                return tt;
            }
        };
        columnPicker.addColumn( titleColumn,
                                new SortableHeader<AssetPageRow>( sortableHeaderGroup,
                                                                  SortableFieldNames.TITLE_PROPERTY_NAME,
                                                                  constants.Name() ),
                                true );

        TextColumn<AssetPageRow> packageNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue( AssetPageRow row ) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new TextHeader( constants.PackageName() ),
                                false );

        TextColumn<AssetPageRow> stateNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue( AssetPageRow row ) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn( stateNameColumn,
                                new SortableHeader<AssetPageRow>( sortableHeaderGroup,
                                                                  SortableFieldNames.STATE_PROPERTY_NAME,
                                                                  constants.Status() ),
                                true );

        TextColumn<AssetPageRow> creatorColumn = new TextColumn<AssetPageRow>() {
            public String getValue( AssetPageRow row ) {
                return row.getCreator();
            }
        };
        columnPicker.addColumn( creatorColumn,
                                new SortableHeader<AssetPageRow>( sortableHeaderGroup,
                                                                  SortableFieldNames.CREATOR_PROPERTY_NAME,
                                                                  constants.Creator() ),
                                false );

        Column<AssetPageRow, Date> createdDateColumn = new Column<AssetPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( AssetPageRow row ) {
                return row.getCreatedDate();
            }
        };
        columnPicker.addColumn( createdDateColumn,
                                new TextHeader( constants.CreatedDate() ),
                                false );

        TextColumn<AssetPageRow> lastContributorColumn = new TextColumn<AssetPageRow>() {
            public String getValue( AssetPageRow row ) {
                return row.getLastContributor();
            }
        };
        columnPicker.addColumn( lastContributorColumn,
                                new SortableHeader<AssetPageRow>( sortableHeaderGroup,
                                                                  SortableFieldNames.LAST_CONTRIBUTOR_PROPERTY_NAME,
                                                                  constants.LastContributor() ),
                                false );

        Column<AssetPageRow, Date> lastModifiedColumn = new Column<AssetPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( AssetPageRow row ) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<AssetPageRow>( sortableHeaderGroup,
                                                                  SortableFieldNames.LAST_MODIFIED_PROPERTY_NAME,
                                                                  constants.LastModified() ),
                                true );

        TextColumn<AssetPageRow> categorySummaryColumn = new TextColumn<AssetPageRow>() {
            public String getValue( AssetPageRow row ) {
                return row.getCategorySummary();
            }
        };
        columnPicker.addColumn( categorySummaryColumn,
                                new SortableHeader<AssetPageRow>( sortableHeaderGroup,
                                                                  SortableFieldNames.CATEGORY_PROPERTY_NAME,
                                                                  constants.Categories() ),
                                false );

        TextColumn<AssetPageRow> externalSourceColumn = new TextColumn<AssetPageRow>() {
            public String getValue( AssetPageRow row ) {
                return row.getExternalSource();
            }
        };
        columnPicker.addColumn( externalSourceColumn,
                                new TextHeader( constants.ExternalSource() ),
                                false );

        Column<AssetPageRow, Boolean> isDisabledColumn = new Column<AssetPageRow, Boolean>( new RuleEnabledStateCell() ) {
            public Boolean getValue( AssetPageRow row ) {
                return row.isDisabled();
            }
        };
        columnPicker.addColumn( isDisabledColumn,
                                new TextHeader( constants.AssetTableIsDisabled() ),
                                false );

    }

}
