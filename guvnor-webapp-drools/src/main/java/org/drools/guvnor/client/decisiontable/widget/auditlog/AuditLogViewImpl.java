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

import java.util.Map;

import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.AuditLogCellListResources;
import org.drools.guvnor.client.widgets.tables.GuvnorSimplePager;
import org.drools.ide.common.client.modeldriven.auditlog.AuditLog;
import org.drools.ide.common.client.modeldriven.auditlog.AuditLogEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
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

/**
 * The AuditLog View implementation
 */
public class AuditLogViewImpl extends Popup
    implements
    AuditLogView {

    private static final String           DATE_TIME_FORMAT = ApplicationPreferences.getDroolsDateTimeFormat();

    private static final DateTimeFormat   format           = DateTimeFormat.getFormat( DATE_TIME_FORMAT );

    protected int                         MIN_WIDTH        = 500;
    protected int                         MIN_HEIGHT       = 200;

    private final AuditLog                auditLog;

    private final Widget                  popupContent;

    private final AuditLogEntryCellHelper renderer         = new AuditLogEntryCellHelper( format );

    @UiField
    ScrollPanel                           spEvents;

    private DisclosurePanel               dpEventTypes;
    private CellList<AuditLogEntry>       events;
    private final VerticalPanel           lstEventTypes    = new VerticalPanel();

    interface AuditLogViewImplBinder
        extends
        UiBinder<Widget, AuditLogViewImpl> {
    }

    private static AuditLogViewImplBinder uiBinder = GWT.create( AuditLogViewImplBinder.class );

    public AuditLogViewImpl(final AuditLog auditLog) {
        setTitle( Constants.INSTANCE.DecisionTableAuditLog() );
        this.auditLog = auditLog;

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
        for ( Map.Entry<Class< ? extends AuditLogEntry>, Boolean> e : auditLog.getAuditLogFilter().getAcceptedTypes().entrySet() ) {
            lstEventTypes.add( makeEventTypeCheckBox( e.getKey(),
                                                      e.getValue() ) );
        }

        events = new CellList<AuditLogEntry>( new AuditLogEntryCell( renderer,
                                                                     format ),
                                                                     AuditLogCellListResources.INSTANCE );
        events.setEmptyListWidget( new Label( Constants.INSTANCE.DecisionTableAuditLogNoEntries() ) );
        events.setKeyboardPagingPolicy( KeyboardPagingPolicy.CHANGE_PAGE );
        events.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.DISABLED );
        events.setPageSize( 5 );

        GuvnorSimplePager gsp = new GuvnorSimplePager();
        gsp.setPageSize( 5 );
        gsp.setDisplay( events );

        ListDataProvider<AuditLogEntry> dlp = new ListDataProvider<AuditLogEntry>();
        dlp.addDataDisplay( events );
        dlp.setList( auditLog );

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

    private Widget makeEventTypeCheckBox(final Class< ? extends AuditLogEntry> eventType,
                                         final Boolean isEnabled) {
        final CheckBox chkEventType = new CheckBox( renderer.getEventTypeDisplayText( eventType ) );
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

}
