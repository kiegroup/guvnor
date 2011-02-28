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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.resources.RuleFormatImageResource;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.ruleeditor.EditorLauncher;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Widget with a table of Assets.
 */
public class AssetPagedTable extends AbstractAssetPagedTable<AssetPageRow> {

    private static final int PAGE_SIZE = 10;

    /**
     * Constructor
     * 
     * @param packageUuid
     * @param formatInList
     * @param formatIsRegistered
     * @param editEvent
     */
    public AssetPagedTable(String packageUuid,
                           List<String> formatInList,
                           Boolean formatIsRegistered,
                           OpenItemCommand editEvent) {
        this( packageUuid,
              formatInList,
              formatIsRegistered,
              editEvent,
              null );
    }

    /**
     * Constructor
     * 
     * @param packageUuid
     * @param formatInList
     * @param formatIsRegistered
     * @param editEvent
     * @param feedURL
     */
    public AssetPagedTable(final String packageUuid,
                           final List<String> formatInList,
                           final Boolean formatIsRegistered,
                           final OpenItemCommand editEvent,
                           String feedURL) {
        super( PAGE_SIZE,
               editEvent,
               feedURL );
        setDataProvider( new AsyncDataProvider<AssetPageRow>() {
            protected void onRangeChanged(HasData<AssetPageRow> display) {
                AssetPageRequest request = new AssetPageRequest( packageUuid,
                                                                 formatInList,
                                                                 formatIsRegistered,
                                                                 pager.getPageStart(),
                                                                 pageSize );
                assetService.findAssetPage( request,
                                                 new GenericCallback<PageResponse<AssetPageRow>>() {
                                                     public void onSuccess(PageResponse<AssetPageRow> response) {
                                                         updateRowCount( response.getTotalRowSize(),
                                                                         true );
                                                         updateRowData( response.getStartRowIndex(),
                                                                        response.getPageRowList() );
                                                     }
                                                 } );
            }
        } );

    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<AssetPageRow> columnPicker,
                                       SortableHeaderGroup<AssetPageRow> sortableHeaderGroup) {

        Column<AssetPageRow, RuleFormatImageResource> formatColumn = new Column<AssetPageRow, RuleFormatImageResource>( new RuleFormatImageResourceCell() ) {

            public RuleFormatImageResource getValue(AssetPageRow row) {
                return EditorLauncher.getAssetFormatIcon( row.getFormat() );
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<AssetPageRow, RuleFormatImageResource>(
                                                                                           sortableHeaderGroup,
                                                                                           constants.Format(),
                                                                                           formatColumn ),
                                true );

        TitledTextColumn<AssetPageRow> titleColumn = new TitledTextColumn<AssetPageRow>() {
            public TitledText getValue(AssetPageRow row) {
                TitledText tt = new TitledText( row.getName(),
                                                row.getAbbreviatedDescription() );
                return tt;
            }
        };
        columnPicker.addColumn( titleColumn,
                                new SortableHeader<AssetPageRow, TitledText>(
                                                                              sortableHeaderGroup,
                                                                              constants.Name(),
                                                                              titleColumn ),
                                true );

        TextColumn<AssetPageRow> packageNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SortableHeader<AssetPageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.PackageName(),
                                                                          packageNameColumn ),
                                false );

        TextColumn<AssetPageRow> stateNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn( stateNameColumn,
                                new SortableHeader<AssetPageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.Status(),
                                                                          stateNameColumn ),
                                true );

        TextColumn<AssetPageRow> creatorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCreator();
            }
        };
        columnPicker.addColumn( creatorColumn,
                                new SortableHeader<AssetPageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.Creator(),
                                                                          creatorColumn ),
                                false );

        Column<AssetPageRow, Date> createdDateColumn = new Column<AssetPageRow, Date>( new
                                                                                       DateCell(
                                                                                                 DateTimeFormat.getFormat(
                                                                                                         DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(AssetPageRow row) {
                return row.getCreatedDate();
            }
        };
        columnPicker.addColumn( createdDateColumn,
                                new SortableHeader<AssetPageRow, Date>(
                                                                        sortableHeaderGroup,
                                                                        constants.CreatedDate(),
                                                                        createdDateColumn ),
                                false );

        TextColumn<AssetPageRow> lastContributorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getLastContributor();
            }
        };
        columnPicker.addColumn( lastContributorColumn,
                                new SortableHeader<AssetPageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.LastContributor(),
                                                                          lastContributorColumn ),
                                false );

        Column<AssetPageRow, Date> lastModifiedColumn = new Column<AssetPageRow, Date>( new
                                                                                        DateCell(
                                                                                                  DateTimeFormat.getFormat(
                                                                                                          DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(AssetPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<AssetPageRow, Date>(
                                                                        sortableHeaderGroup,
                                                                        constants.LastModified(),
                                                                        lastModifiedColumn ),
                                true );

        TextColumn<AssetPageRow> categorySummaryColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCategorySummary();
            }
        };
        columnPicker.addColumn( categorySummaryColumn,
                                new SortableHeader<AssetPageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.Categories(),
                                                                          categorySummaryColumn ),
                                false );

        TextColumn<AssetPageRow> externalSourceColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getExternalSource();
            }
        };
        columnPicker.addColumn( externalSourceColumn,
                                new SortableHeader<AssetPageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.ExternalSource(),
                                                                          externalSourceColumn ),
                                false );

    }

}
