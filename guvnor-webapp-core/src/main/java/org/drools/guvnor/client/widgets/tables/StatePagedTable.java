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
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.StatePageRequest;
import org.drools.guvnor.client.rpc.StatePageRow;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Widget with a table of "By State" query results.
 */
public class StatePagedTable extends AbstractAssetPagedTable<StatePageRow> {

    private static final int PAGE_SIZE = 10;
    private final ClientFactory clientFactory;
    
    public StatePagedTable(
            final String stateName,
            ClientFactory clientFactory ) {
        super( PAGE_SIZE,
                clientFactory );
        this.clientFactory = clientFactory;
        
        setDataProvider(new AsyncDataProvider<StatePageRow>() {
            protected void onRangeChanged(HasData<StatePageRow> display) {
                StatePageRequest request = new StatePageRequest();
                request.setStateName( stateName );
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                repositoryService.loadRuleListForState( request,
                                                        new GenericCallback<PageResponse<StatePageRow>>() {
                                                            public void onSuccess(PageResponse<StatePageRow> response) {
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
    protected void addAncillaryColumns(ColumnPicker<StatePageRow> columnPicker,
                                       SortableHeaderGroup<StatePageRow> sortableHeaderGroup) {

        Column<StatePageRow, RuleFormatImageResource> formatColumn = new Column<StatePageRow, RuleFormatImageResource>( new RuleFormatImageResourceCell() ) {

            public RuleFormatImageResource getValue(StatePageRow row) {
                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new RuleFormatImageResource(row.getFormat(), factory.getAssetEditorIcon(row.getFormat()));
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<StatePageRow, RuleFormatImageResource>(
                                                                                           sortableHeaderGroup,
                                                                                           constants.Format(),
                                                                                           formatColumn ),
                                true );

        TitledTextColumn<StatePageRow> titleColumn = new TitledTextColumn<StatePageRow>() {
            public TitledText getValue(StatePageRow row) {
                TitledText tt = new TitledText( row.getName(),
                                                row.getAbbreviatedDescription() );
                return tt;
            }
        };
        columnPicker.addColumn( titleColumn,
                                new SortableHeader<StatePageRow, TitledText>(
                                                                              sortableHeaderGroup,
                                                                              constants.Name(),
                                                                              titleColumn ),
                                true );

        TextColumn<StatePageRow> packageNameColumn = new TextColumn<StatePageRow>() {
            public String getValue(StatePageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SortableHeader<StatePageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.PackageName(),
                                                                          packageNameColumn ),
                                true );

        TextColumn<StatePageRow> statusNameColumn = new TextColumn<StatePageRow>() {
            public String getValue(StatePageRow row) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn( statusNameColumn,
                                new SortableHeader<StatePageRow, String>(
                                                                          sortableHeaderGroup,
                                                                          constants.Status(),
                                                                          statusNameColumn ),
                                true );

        Column<StatePageRow, Date> lastModifiedColumn = new Column<StatePageRow, Date>( new
                                                                                        DateCell(
                                                                                                  DateTimeFormat.getFormat(
                                                                                                          DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(StatePageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<StatePageRow, Date>(
                                                                        sortableHeaderGroup,
                                                                        constants.LastModified(),
                                                                        lastModifiedColumn ),
                                true );
    }

}
