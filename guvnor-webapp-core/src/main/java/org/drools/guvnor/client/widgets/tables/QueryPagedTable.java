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
import java.util.List;

import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.resources.RuleFormatImageResource;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Widget with a table of repository query results.
 */
public class QueryPagedTable extends AbstractAssetPagedTable<QueryPageRow> {

    private static final int PAGE_SIZE = 10;    
    private final ClientFactory clientFactory;
    
    public QueryPagedTable(final List<MetaDataQuery> metadata,
                           final Date createdAfter,
                           final Date createdBefore,
                           final Date lastModifiedAfter,
                           final Date lastModifiedBefore,
                           final Boolean searchArchived,
                           ClientFactory clientFactory) {
        super( PAGE_SIZE,
                clientFactory);
        this.clientFactory = clientFactory;

        setDataProvider( new AsyncDataProvider<QueryPageRow>() {
            protected void onRangeChanged(HasData<QueryPageRow> display) {
                QueryMetadataPageRequest request = new QueryMetadataPageRequest();
                request.setMetadata( metadata );
                request.setCreatedAfter( createdAfter );
                request.setCreatedBefore( createdBefore );
                request.setLastModifiedAfter( lastModifiedAfter );
                request.setLastModifiedBefore( lastModifiedBefore );
                request.setSearchArchived( searchArchived );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                repositoryService.queryMetaData( request,
                                                  new GenericCallback<PageResponse<QueryPageRow>>() {
                                                      public void onSuccess(PageResponse<QueryPageRow> response) {
                                                          updateRowCount( response.getTotalRowSize(),
                                                                          response.isTotalRowSizeExact() );
                                                          updateRowData( response.getStartRowIndex(),
                                                                         response.getPageRowList() );
                                                      }
                                                  } );
            }
        } );
    }

    public QueryPagedTable(final String searchText,
                           final Boolean searchArchived,
                           ClientFactory clientFactory) {
        super( PAGE_SIZE,
                clientFactory);
        this.clientFactory = clientFactory;

        setDataProvider( new AsyncDataProvider<QueryPageRow>() {
            protected void onRangeChanged(HasData<QueryPageRow> display) {
                QueryPageRequest request = new QueryPageRequest();
                request.setSearchText( searchText );
                request.setSearchArchived( searchArchived );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                assetService.quickFindAsset( request,
                                                  new GenericCallback<PageResponse<QueryPageRow>>() {
                                                      public void onSuccess(PageResponse<QueryPageRow> response) {
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
     * Constructor
     * 
     * @param searchText
     * @param searchArchived
     * @param isCaseSensitive
     */
    public QueryPagedTable(final String searchText,
                           final Boolean searchArchived,
                           final Boolean isCaseSensitive,
                           ClientFactory clientFactory) {
        super( PAGE_SIZE,
                clientFactory);
        this.clientFactory = clientFactory;

        setDataProvider( new AsyncDataProvider<QueryPageRow>() {
            protected void onRangeChanged(HasData<QueryPageRow> display) {
                QueryPageRequest request = new QueryPageRequest();
                request.setSearchText( searchText );
                request.setSearchArchived( searchArchived );
                request.setIsCaseSensitive( isCaseSensitive );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                assetService.quickFindAsset( request,
                                                  new GenericCallback<PageResponse<QueryPageRow>>() {
                                                      public void onSuccess(PageResponse<QueryPageRow> response) {
                                                          updateRowCount( response.getTotalRowSize(),
                                                                          response.isTotalRowSizeExact() );
                                                          updateRowData( response.getStartRowIndex(),
                                                                         response.getPageRowList() );
                                                      }
                                                  } );
            }
        } );
    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<QueryPageRow> columnPicker,
                                       SortableHeaderGroup<QueryPageRow> sortableHeaderGroup) {

        Column<QueryPageRow, RuleFormatImageResource> formatColumn = new Column<QueryPageRow, RuleFormatImageResource>( new RuleFormatImageResourceCell() ) {

            public RuleFormatImageResource getValue(QueryPageRow row) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new RuleFormatImageResource(row.getFormat(), factory.getAssetEditorIcon(row.getFormat()));
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<QueryPageRow, RuleFormatImageResource>(
                                                                                           sortableHeaderGroup,
                                                                                           constants.Format(),
                                                                                           formatColumn ),
                                true );

        TitledTextColumn<QueryPageRow> titleColumn = new TitledTextColumn<QueryPageRow>() {
            public TitledText getValue(QueryPageRow row) {
                TitledText tt = new TitledText( row.getName(),
                                                row.getAbbreviatedDescription() );
                return tt;
            }
        };
        columnPicker.addColumn( titleColumn,
                                new SortableHeader<QueryPageRow, TitledText>(
                                                                              sortableHeaderGroup,
                                                                              constants.Name(),
                                                                              titleColumn ),
                                true );

        TextColumn<QueryPageRow> packageNameColumn = new TextColumn<QueryPageRow>() {
            public String getValue(QueryPageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SortableHeader<QueryPageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.PackageName(),
                                                                          packageNameColumn ),
                                false );

        Column<QueryPageRow, Date> createdDateColumn = new Column<QueryPageRow, Date>( new
                                                                                       DateCell(
                                                                                                 DateTimeFormat.getFormat(
                                                                                                         DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(QueryPageRow row) {
                return row.getCreatedDate();
            }
        };
        columnPicker.addColumn( createdDateColumn,
                                new SortableHeader<QueryPageRow, Date>(
                                                                        sortableHeaderGroup,
                                                                        constants.CreatedDate(),
                                                                        createdDateColumn ),
                                false );

        Column<QueryPageRow, Date> lastModifiedColumn = new Column<QueryPageRow, Date>( new
                                                                                        DateCell(
                                                                                                  DateTimeFormat.getFormat(
                                                                                                          DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(QueryPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<QueryPageRow, Date>(
                                                                        sortableHeaderGroup,
                                                                        constants.LastModified(),
                                                                        lastModifiedColumn ),
                                true );

    }

}
