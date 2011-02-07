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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Widget with a table of Log entries.
 * 
 * @author manstis
 */
public class LogPagedTable extends AbstractPagedTable<LogPageRow> {

    // UI
    interface LogPagedTableBinder
        extends
        UiBinder<Widget, LogPagedTable> {
    }

    @UiField()
    protected Button cleanButton;

    private static LogPagedTableBinder uiBinder        = GWT.create( LogPagedTableBinder.class );

    private static Images              images          = (Images) GWT.create( Images.class );
    private static final String        HTML_ERROR_ICON = makeImage( images.error() );
    private static final String        HTML_INFO_ICON  = makeImage( images.information() );

    private static String makeImage(ImageResource resource) {
        AbstractImagePrototype prototype = AbstractImagePrototype.create( resource );
        return prototype.getHTML();
    }
    
    //Commands for UI
    private Command  cleanCommand;

    //Other stuff
    private static final int           PAGE_SIZE       = 10;

    public LogPagedTable(Command cleanCommand) {
        super( PAGE_SIZE );
        this.cleanCommand = cleanCommand;
        setDataProvider( new AsyncDataProvider<LogPageRow>() {
            protected void onRangeChanged(HasData<LogPageRow> display) {
                PageRequest request = new PageRequest();
                request.setStartRowIndex( pager.getPageStart() );
                request.setPageSize( pageSize );
                repositoryService.showLog( request,
                                                             new GenericCallback<PageResponse<LogPageRow>>() {
                                                                 public void onSuccess(PageResponse<LogPageRow> response) {
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
    protected void addAncillaryColumns(ColumnPicker<LogPageRow> columnPicker,
                                       SortableHeaderGroup<LogPageRow> sortableHeaderGroup) {

        // Things got messy with nested, nested anonymous classes
        AbstractCell<Long> severityCell = new AbstractCell<Long>() {

            @Override
            public void render(Context context,
                               Long value,
                               SafeHtmlBuilder sb) {
                if ( value.intValue() == 0 ) {
                    sb.appendHtmlConstant( HTML_ERROR_ICON );
                } else if ( value.intValue() == 1 ) {
                    sb.appendHtmlConstant( HTML_INFO_ICON );
                }
            }

        };
        Column<LogPageRow, Long> severityColumn = new Column<LogPageRow, Long>( severityCell ) {
            public Long getValue(LogPageRow row) {
                return Long.valueOf( row.getSeverity() );
            }
        };
        columnPicker.addColumn( severityColumn,
                                new SortableHeader<LogPageRow, Long>(
                                                                      sortableHeaderGroup,
                                                                      constants.Severity(),
                                                                      severityColumn ),
                                true );

        Column<LogPageRow, String> messageColumn = new Column<LogPageRow, String>( new TextCell() ) {
            public String getValue(LogPageRow row) {
                return row.getMessage();
            }
        };
        columnPicker.addColumn( messageColumn,
                                new SortableHeader<LogPageRow, String>(
                                                                        sortableHeaderGroup,
                                                                        constants.Message(),
                                                                        messageColumn ),
                                true );

        Column<LogPageRow, Date> timestampColumn = new Column<LogPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(LogPageRow row) {
                return row.getTimestamp();
            }
        };
        columnPicker.addColumn( timestampColumn,
                                new SortableHeader<LogPageRow, Date>( sortableHeaderGroup,
                                                                      constants.Timestamp(),
                                                                      timestampColumn ),
                                true );

    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("cleanButton")
    void clean(ClickEvent e) {
        cleanCommand.execute();
    }

}
