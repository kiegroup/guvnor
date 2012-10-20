/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.decisiontable.widget.auditlog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.widgets.tables.GuvnorSimplePager;
import org.drools.guvnor.shared.security.AppRoles;
import org.drools.ide.common.client.modeldriven.auditlog.AuditLog;
import org.drools.ide.common.client.modeldriven.auditlog.AuditLogEntry;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.uberfire.security.Identity;

/**
 * The AuditLog View implementation
 */
public class AuditLogViewImpl extends Popup
    implements
    AuditLogView {

    protected int                     MIN_WIDTH     = 500;
    protected int                     MIN_HEIGHT    = 200;

    private final AuditLog            auditLog;

    private final Widget              popupContent;

    @UiField
    ScrollPanel                       spEvents;

    private DisclosurePanel           dpEventTypes;
    private CellTable<AuditLogEntry>  events;
    private final VerticalPanel       lstEventTypes = new VerticalPanel();

    //The current user's security context (admins can see all records)
    private final Identity identity;

    interface AuditLogViewImplBinder
        extends
        UiBinder<Widget, AuditLogViewImpl> {
    }

    private static AuditLogViewImplBinder uiBinder = GWT.create( AuditLogViewImplBinder.class );

    public AuditLogViewImpl(final AuditLog auditLog,
                            final Identity identity) {
        setTitle( Constants.INSTANCE.DecisionTableAuditLog() );
        this.auditLog = auditLog;
        this.identity = identity;

        setHeight( getPopupHeight() + "px" );
        setWidth( getPopupWidth() + "px" );

        this.popupContent = uiBinder.createAndBindUi( this );
    }

    /**
     * Width of pop-up, 50% of the client width or MIN_WIDTH
     * 
     * @return
     */
    private int getPopupWidth() {
        int w = (int) (Window.getClientWidth() * 0.50);
        if ( w < MIN_WIDTH ) {
            w = MIN_WIDTH;
        }
        return w;
    }

    /**
     * Height of pop-up, 50% of the client height or MIN_HEIGHT
     * 
     * @return
     */
    protected int getPopupHeight() {
        int h = (int) (Window.getClientHeight() * 0.50);
        if ( h < MIN_HEIGHT ) {
            h = MIN_HEIGHT;
        }
        return h;
    }

    @Override
    public Widget getContent() {
        for ( Map.Entry<String, Boolean> e : auditLog.getAuditLogFilter().getAcceptedTypes().entrySet() ) {
            lstEventTypes.add( makeEventTypeCheckBox( e.getKey(),
                                                      e.getValue() ) );
        }

        events = new CellTable<AuditLogEntry>();

        final ListDataProvider<AuditLogEntry> dlp = new ListDataProvider<AuditLogEntry>( filterDeletedEntries( auditLog ) );
        dlp.addDataDisplay( events );

        AuditLogEntrySummaryColumn summaryColumn = new AuditLogEntrySummaryColumn();
        AuditLogEntryCommentColumn commentColumn = new AuditLogEntryCommentColumn();

        events.addColumn( summaryColumn );
        events.addColumn( commentColumn );

        events.setColumnWidth( summaryColumn,
                               50.0,
                               Unit.PCT );
        events.setColumnWidth( commentColumn,
                               50.0,
                               Unit.PCT );

        //If the current user is not an Administrator include the delete comment column
        if ( !identity.hasRole(AppRoles.ADMIN) ) {

            AuditLogEntryDeleteCommentColumn deleteCommentColumn = new AuditLogEntryDeleteCommentColumn();
            deleteCommentColumn.setFieldUpdater( new FieldUpdater<AuditLogEntry, ImageResource>() {

                public void update(int index,
                                   AuditLogEntry row,
                                   ImageResource value) {
                    row.setDeleted( true );
                    dlp.setList( filterDeletedEntries( auditLog ) );
                    dlp.refresh();
                }

            } );
            events.addColumn( deleteCommentColumn );
            events.setColumnWidth( commentColumn,
                                   45.0,
                                   Unit.PCT );
            events.setColumnWidth( deleteCommentColumn,
                                   5.0,
                                   Unit.PCT );
        }

        events.setEmptyTableWidget( new Label( Constants.INSTANCE.DecisionTableAuditLogNoEntries() ) );
        events.setKeyboardPagingPolicy( KeyboardPagingPolicy.CHANGE_PAGE );
        events.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.DISABLED );
        events.setPageSize( 5 );

        GuvnorSimplePager gsp = new GuvnorSimplePager();
        gsp.setPageSize( 5 );
        gsp.setDisplay( events );

        VerticalPanel vp = new VerticalPanel();
        vp.add( gsp );
        vp.add( events );

        spEvents.setAlwaysShowScrollBars( false );
        spEvents.add( vp );

        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                fixWidgetSizes();
            }

        } );

        return this.popupContent;
    }

    private Widget makeEventTypeCheckBox(final String eventType,
                                         final Boolean isEnabled) {
        final CheckBox chkEventType = new CheckBox( AuditLogEntryCellHelper.getEventTypeDisplayText( eventType ) );
        chkEventType.setValue( Boolean.TRUE.equals( isEnabled ) );
        chkEventType.addValueChangeHandler( new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                auditLog.getAuditLogFilter().getAcceptedTypes().put( eventType,
                                                                     event.getValue() );
            }

        } );

        return chkEventType;
    }

    private void fixWidgetSizes() {
        final int lstEventsHeight = getClientHeight() - dpEventTypes.getOffsetHeight();
        events.setWidth( spEvents.getElement().getClientWidth() + "px" );
        spEvents.setHeight( lstEventsHeight + "px" );
    }

    @UiFactory
    DisclosurePanel makeEventTypeDisclosurePanel() {
        //For some inexplicable reason it is impossible to I18N the DisclosurePanel title with uiBinder
        dpEventTypes = new DisclosurePanel( Constants.INSTANCE.DecisionTableAuditLogEvents() );
        dpEventTypes.add( lstEventTypes );

        dpEventTypes.addOpenHandler( new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                fixWidgetSizes();
            }

        } );

        dpEventTypes.addCloseHandler( new CloseHandler<DisclosurePanel>() {

            @Override
            public void onClose(CloseEvent<DisclosurePanel> event) {
                fixWidgetSizes();
            }

        } );

        return dpEventTypes;
    }

    private List<AuditLogEntry> filterDeletedEntries(final List<AuditLogEntry> entries) {
        if ( identity.hasRole(AppRoles.ADMIN) ) {
            return entries;
        }
        final List<AuditLogEntry> filteredEntries = new ArrayList<AuditLogEntry>();
        final Iterator<AuditLogEntry> i = entries.iterator();
        while ( i.hasNext() ) {
            final AuditLogEntry entry = i.next();
            if ( !entry.isDeleted() ) {
                filteredEntries.add( entry );
            }
        }
        return filteredEntries;
    }

}
