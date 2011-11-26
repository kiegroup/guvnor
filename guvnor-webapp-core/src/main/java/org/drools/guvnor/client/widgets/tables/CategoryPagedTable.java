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

import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.resources.RuleFormatImageResource;
import org.drools.guvnor.client.rpc.CategoryPageRequest;
import org.drools.guvnor.client.rpc.CategoryPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Widget with a table of "By Category" query results.
 */
public class CategoryPagedTable extends AbstractAssetPagedTable<CategoryPageRow> {

    private static final int PAGE_SIZE = 10;
    private final ClientFactory clientFactory;
    /**
     * Constructor
     * 
     * @param categoryName
     * @param feedURL
     */
    public CategoryPagedTable(final String categoryName,
                              final String feedURL,
                              ClientFactory clientFactory) {
        super( PAGE_SIZE,
               feedURL,
                clientFactory);
        this.clientFactory = clientFactory;
        
        setDataProvider( new AsyncDataProvider<CategoryPageRow>() {
            protected void onRangeChanged(HasData<CategoryPageRow> display) {
                CategoryPageRequest request = new CategoryPageRequest();
                request.setCategoryPath( categoryName );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                categoryService.loadRuleListForCategories( request,
                                                             new GenericCallback<PageResponse<CategoryPageRow>>() {
                                                                 public void onSuccess(PageResponse<CategoryPageRow> response) {
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
    protected void addAncillaryColumns(ColumnPicker<CategoryPageRow> columnPicker,
                                       SortableHeaderGroup<CategoryPageRow> sortableHeaderGroup) {

        Column<CategoryPageRow, RuleFormatImageResource> formatColumn = new Column<CategoryPageRow, RuleFormatImageResource>( new RuleFormatImageResourceCell() ) {

            public RuleFormatImageResource getValue(CategoryPageRow row) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new RuleFormatImageResource(row.getFormat(), factory.getAssetEditorIcon(row.getFormat()));
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<CategoryPageRow, RuleFormatImageResource>(
                                                                                              sortableHeaderGroup,
                                                                                              constants.Format(),
                                                                                              formatColumn ),
                                true );

        TitledTextColumn<CategoryPageRow> titleColumn = new TitledTextColumn<CategoryPageRow>() {
            public TitledText getValue(CategoryPageRow row) {
                TitledText tt = new TitledText( row.getName(),
                                                row.getAbbreviatedDescription() );
                return tt;
            }
        };
        columnPicker.addColumn( titleColumn,
                                new SortableHeader<CategoryPageRow, TitledText>(
                                                                                 sortableHeaderGroup,
                                                                                 constants.Name(),
                                                                                 titleColumn ),
                                true );

        TextColumn<CategoryPageRow> packageNameColumn = new TextColumn<CategoryPageRow>() {
            public String getValue(CategoryPageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SortableHeader<CategoryPageRow, String>(
                                                                             sortableHeaderGroup,
                                                                             constants.PackageName(),
                                                                             packageNameColumn ),
                                true );

        TextColumn<CategoryPageRow> statusNameColumn = new TextColumn<CategoryPageRow>() {
            public String getValue(CategoryPageRow row) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn( statusNameColumn,
                                new SortableHeader<CategoryPageRow, String>(
                                                                             sortableHeaderGroup,
                                                                             constants.Status(),
                                                                             statusNameColumn ),
                                true );

        Column<CategoryPageRow, Date> lastModifiedColumn = new Column<CategoryPageRow, Date>( new
                                                                                              DateCell(
                                                                                                        DateTimeFormat.getFormat(
                                                                                                                DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(CategoryPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<CategoryPageRow, Date>(
                                                                           sortableHeaderGroup,
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
