/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.navigation.CloseAllPlacesEvent;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.moduleeditor.ModuleEditorWrapper;
import org.drools.guvnor.client.util.ScrollTabLayoutPanel;
import org.drools.guvnor.client.util.TabbedPanel;

/**
 * This is the tab panel manager.
 */
public class ExplorerViewCenterPanel extends Composite
    implements
    TabbedPanel {

    private final ScrollTabLayoutPanel       tabLayoutPanel;

    private PanelMap                         openedTabs          = new PanelMap();

    private Map<String, ModuleEditorWrapper> openedModuleEditors = new HashMap<String, ModuleEditorWrapper>();

    private ClientFactory                    clientFactory;
    private final EventBus                   eventBus;

    public ExplorerViewCenterPanel(final ClientFactory clientFactory,
                                   final EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        tabLayoutPanel = new ScrollTabLayoutPanel();

        addBeforeSelectionHandler();

        initWidget( createLayout() );
    }

    private DockLayoutPanel createLayout() {
        DockLayoutPanel layoutPanel = new DockLayoutPanel( Style.Unit.EM );

        layoutPanel.addSouth( createBottomPanel(),
                              2 );
        layoutPanel.add( tabLayoutPanel );
        return layoutPanel;
    }

    private HorizontalPanel createBottomPanel() {
        HorizontalPanel bottomPanel = new HorizontalPanel();
        bottomPanel.setWidth( "100%" );
        bottomPanel.setStyleName( "bottom-panel" );
        bottomPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
        bottomPanel.add( createCloseAllButton() );
        return bottomPanel;
    }

    private Button createCloseAllButton() {
        ConstantsCore constants = GWT.create( ConstantsCore.class );
        Button button = new Button( constants.CloseAllItems() );
        button.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                eventBus.fireEvent( new CloseAllPlacesEvent() );
            }
        } );
        return button;
    }

    private void addBeforeSelectionHandler() {
        tabLayoutPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> integerBeforeSelectionEvent) {
                if ( !tabLayoutPanel.isCanSelectTabToggle() ) {
                    integerBeforeSelectionEvent.cancel();
                    clientFactory.getDeprecatedPlaceController().goTo( openedTabs.getKey( integerBeforeSelectionEvent.getItem() ) );
                }
            }
        } );
    }

    public boolean contains(Place key) {
        return openedTabs.contains( key );
    }

    public void show(Place key) {
        if ( openedTabs.contains( key ) ) {
            LoadingPopup.close();
            tabLayoutPanel.selectTab( openedTabs.get( key ) );
        }
    }

    /**
     * Add a new tab. Should only do this if have checked showIfOpen to avoid
     * dupes being opened.
     * 
     * @param tabname
     *            The displayed tab name.
     * @param widget
     *            The contents.
     * @param place
     *            A place which is unique.
     */
    public void addTab(final String tabname,
                       IsWidget widget,
                       final Place place) {

        ScrollPanel localTP = new ScrollPanel();
        localTP.add( widget );
        tabLayoutPanel.add( localTP,
                            newClosableLabel(
                                              tabname,
                                              place
                            ) );
        tabLayoutPanel.selectTab( localTP );

        if ( widget instanceof ModuleEditorWrapper ) {
            this.getOpenedModuleEditors().put( tabname,
                                               (ModuleEditorWrapper) widget );
        }

        openedTabs.put( place,
                        localTP );
    }

    private Widget newClosableLabel(final String title,
                                    final Place place) {
        ClosableLabel closableLabel = new ClosableLabel( title );

        closableLabel.addCloseHandler( new CloseHandler<ClosableLabel>() {
            public void onClose(CloseEvent<ClosableLabel> event) {
                eventBus.fireEvent( new ClosePlaceEvent( place ) );
            }

        } );

        return closableLabel;
    }

    public void close(Place key) {

        int widgetIndex = openedTabs.getIndex( key );

        Place nextPlace = getPlace( widgetIndex );

        tabLayoutPanel.remove( openedTabs.get( key ) );
        openedTabs.remove( key );

        if ( nextPlace != null ) {
            goTo( nextPlace );
        } else {
            goTo( Place.NOWHERE );
        }
    }

    private Place getPlace(int widgetIndex) {
        if ( isOnlyOneTabLeft() ) {
            return Place.NOWHERE;
        } else if ( isSelectedTabIndex( widgetIndex ) ) {
            return getNeighbour( widgetIndex );
        } else {
            return null;
        }
    }

    private void goTo(Place place) {
        clientFactory.getDeprecatedPlaceController().goTo( place );
    }

    private Place getNeighbour(int widgetIndex) {
        if ( isLeftMost( widgetIndex ) ) {
            return getNextPlace();
        } else {
            return getPreviousPlace();
        }
    }

    private boolean isLeftMost(int widgetIndex) {
        return widgetIndex == 0;
    }

    private boolean isSelectedTabIndex(int widgetIndex) {
        return tabLayoutPanel.getSelectedIndex() == widgetIndex;
    }

    private Place getPreviousPlace() {
        if ( tabLayoutPanel.getSelectedIndex() > 0 ) {
            return openedTabs.getKey( tabLayoutPanel.getSelectedIndex() - 1 );
        }
        return null;
    }

    private Place getNextPlace() {
        return openedTabs.getKey( tabLayoutPanel.getSelectedIndex() + 1 );
    }

    private boolean isOnlyOneTabLeft() {
        return tabLayoutPanel.getWidgetCount() == 1;
    }

    public Map<String, ModuleEditorWrapper> getOpenedModuleEditors() {
        return openedModuleEditors;
    }

    private class PanelMap {

        private final Map<Place, Panel> keysToPanel = new HashMap<Place, Panel>();
        private final List<Place>       keys        = new ArrayList<Place>();

        Panel get(Place key) {
            return keysToPanel.get( key );
        }

        Place getKey(int index) {
            return keys.get( index );
        }

        void remove(Place key) {
            keys.remove( key );
            keysToPanel.remove( key );
        }

        public boolean contains(Place key) {
            return keysToPanel.containsKey( key );
        }

        public void put(Place key,
                        Panel panel) {
            keys.add( key );
            keysToPanel.put( key,
                             panel );
        }

        public int getIndex(Place key) {
            return keys.indexOf( key );
        }
    }
}
