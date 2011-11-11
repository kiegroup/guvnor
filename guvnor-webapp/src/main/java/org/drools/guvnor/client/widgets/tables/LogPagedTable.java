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

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.navigation.admin.widget.EventLogPresenter.EventLogView;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.LogPageRow;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget with a table of Log entries.
 */
public class LogPagedTable extends AbstractPagedTable<LogPageRow>
    implements
    EventLogView {

    // UI
    interface LogPagedTableBinder
        extends
        UiBinder<Widget, LogPagedTable> {
    }

    @UiField()
    protected Button                   cleanButton;

    @UiField()
    protected Button                   refreshButton;

    private static LogPagedTableBinder uiBinder        = GWT.create( LogPagedTableBinder.class );

    private static Images              images          = (Images) GWT.create( Images.class );
    private static final String        HTML_ERROR_ICON = makeImage( images.error() );
    private static final String        HTML_INFO_ICON  = makeImage( images.information() );

    private static String makeImage(ImageResource resource) {
        AbstractImagePrototype prototype = AbstractImagePrototype.create( resource );
        return prototype.getHTML();
    }

    // Other stuff
    private static final int PAGE_SIZE = 10;

    /**
     * Constructor
     * 
     * @param cleanCommand
     */
    public LogPagedTable() {
        super( PAGE_SIZE );
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

    public HasClickHandlers getClearEventLogButton() {
        return this.cleanButton;
    }

    public HasClickHandlers getRefreshEventLogButton() {
        return this.refreshButton;
    }

    public void showClearingLogMessage() {
        LoadingPopup.showMessage( constants.CleaningLogMessages() );
    }

    public void hideClearingLogMessage() {
        LoadingPopup.close();
    }

    public int getStartRowIndex() {
        return this.pager.getPageStart();
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

}
