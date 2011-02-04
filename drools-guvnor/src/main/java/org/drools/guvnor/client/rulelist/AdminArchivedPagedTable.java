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

package org.drools.guvnor.client.rulelist;

import java.util.Date;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.resources.RuleFormatImageResource;
import org.drools.guvnor.client.rpc.AdminArchivedPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.ruleeditor.EditorLauncher;
import org.drools.guvnor.client.table.ColumnPicker;
import org.drools.guvnor.client.table.SortableHeader;
import org.drools.guvnor.client.table.SortableHeaderGroup;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Widget with a table of Archived Assets.
 * 
 * @author manstis
 */
public class AdminArchivedPagedTable extends AbstractPagedTable<AdminArchivedPageRow> {

    private static final int PAGE_SIZE = 10;

    /**
     * Construct a table to display Archived AssetItems
     * 
     * @param packageUuid
     * @param formatInList
     * @param formatIsRegistered
     * @param editEvent
     */
    public AdminArchivedPagedTable(OpenItemCommand editEvent) {
        super( PAGE_SIZE,
               editEvent );
        setDataProvider( new AsyncDataProvider<AdminArchivedPageRow>() {
            protected void onRangeChanged(HasData<AdminArchivedPageRow> display) {
                PageRequest request = new PageRequest( pager.getPageStart(),
                                                                 pageSize );
                repositoryService.loadArchivedAssets( request,
                                                      new GenericCallback<PageResponse<AdminArchivedPageRow>>() {
                                                          public void onSuccess(PageResponse<AdminArchivedPageRow> response) {
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
    protected void addAncillaryColumns(ColumnPicker<AdminArchivedPageRow> columnPicker,
                                       SortableHeaderGroup<AdminArchivedPageRow> sortableHeaderGroup) {

        Column<AdminArchivedPageRow, RuleFormatImageResource> formatColumn = new Column<AdminArchivedPageRow, RuleFormatImageResource>( new RuleFormatImageResourceCell() ) {

            public RuleFormatImageResource getValue(AdminArchivedPageRow row) {
                return EditorLauncher.getAssetFormatIcon( row.getFormat() );
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<AdminArchivedPageRow, RuleFormatImageResource>(
                                                                                                   sortableHeaderGroup,
                                                                                                   constants.Format(),
                                                                                                   formatColumn ),
                                true );

        TextColumn<AdminArchivedPageRow> nameColumn = new TextColumn<AdminArchivedPageRow>() {
            public String getValue(AdminArchivedPageRow row) {
                String name = row.getName();
                return name;
            }
        };
        columnPicker.addColumn( nameColumn,
                                new SortableHeader<AdminArchivedPageRow, String>(
                                                                                  sortableHeaderGroup,
                                                                                  constants.Name(),
                                                                                  nameColumn ),
                                true );

        TextColumn<AdminArchivedPageRow> packageNameColumn = new TextColumn<AdminArchivedPageRow>() {
            public String getValue(AdminArchivedPageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SortableHeader<AdminArchivedPageRow, String>(
                                                                                  sortableHeaderGroup,
                                                                                  constants.PackageName(),
                                                                                  packageNameColumn ),
                                false );

        TextColumn<AdminArchivedPageRow> lastContributorColumn = new TextColumn<AdminArchivedPageRow>() {
            public String getValue(AdminArchivedPageRow row) {
                return row.getLastContributor();
            }
        };
        columnPicker.addColumn( lastContributorColumn,
                                new SortableHeader<AdminArchivedPageRow, String>(
                                                                                  sortableHeaderGroup,
                                                                                  constants.LastContributor(),
                                                                                  lastContributorColumn ),
                                true );

        Column<AdminArchivedPageRow, Date> lastModifiedColumn = new Column<AdminArchivedPageRow, Date>( new
                                                                                                        DateCell(
                                                                                                                  DateTimeFormat.getFormat(
                                                                                                                          DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(AdminArchivedPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<AdminArchivedPageRow, Date>(
                                                                                sortableHeaderGroup,
                                                                                constants.LastModified(),
                                                                                lastModifiedColumn ),
                                true );

    }

}
