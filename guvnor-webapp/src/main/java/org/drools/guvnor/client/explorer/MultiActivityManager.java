/*
 * Copyright 2011 JBoss Inc
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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.packages.CloseTabEvent;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.ActivityMapper;
import org.drools.guvnor.client.util.TabbedPanel;

import java.util.HashMap;
import java.util.Map;

public class MultiActivityManager implements
        PlaceChangeEvent.Handler,
        CloseTabEvent.Handler {

    private final ActivityMapper activityMapper;
    private TabbedPanel tabbedPanel;
    private final EventBus eventBus;
    private PlaceHistoryMapper placeHistoryMapper;
    private final Map<String, Pair> activeActivities = new HashMap<String, Pair>();

    public MultiActivityManager( ActivityMapper activityMapper,
                                 PlaceHistoryMapper placeHistoryMapper,
                                 EventBus eventBus ) {
        this.activityMapper = activityMapper;
        this.placeHistoryMapper = placeHistoryMapper;
        this.eventBus = eventBus;

        eventBus.addHandler(
                PlaceChangeEvent.TYPE, this );
    }

    public void setTabbedPanel( TabbedPanel tabbedPanel ) {
        this.tabbedPanel = tabbedPanel;
    }

    public void onPlaceChange( PlaceChangeEvent event ) {

        final String token = placeHistoryMapper.getToken( event.getNewPlace() );

        if ( tabbedPanel.contains( token ) ) {

            tabbedPanel.show( token );

        } else if ( ifPlaceExists( event ) ) {
            Activity activity = activityMapper.getActivity( event.getNewPlace() );


            final ResettableEventBus resettableEventBus = new ResettableEventBus( eventBus );

            activeActivities.put( token, new Pair( activity, resettableEventBus ) );

            activity.start(
                    new AcceptTabItem() {
                        public void addTab( String tabTitle, IsWidget widget ) {
                            tabbedPanel.addTab( tabTitle, widget, token );
                        }
                    },
                    resettableEventBus );
        }
    }

    private boolean ifPlaceExists( PlaceChangeEvent event ) {
        return !event.getNewPlace().equals( Place.NOWHERE );
    }

    public void onCloseTab( CloseTabEvent closeTabEvent ) {
        Pair pair = activeActivities.get( closeTabEvent.getKey() );
        if ( pair != null ) {
            if ( pair.getActivity().mayStop() ) {
                pair.getActivity().onStop();
                pair.getResettableEventBus().removeHandlers();
                activeActivities.remove( closeTabEvent.getKey() );
                tabbedPanel.close( closeTabEvent.getKey() );
            }
        }
    }

    private class Pair {

        private Activity activity;
        private ResettableEventBus resettableEventBus;

        public Pair( Activity activity, ResettableEventBus resettableEventBus ) {
            this.activity = activity;
            this.resettableEventBus = resettableEventBus;
        }

        public Activity getActivity() {
            return activity;
        }

        public ResettableEventBus getResettableEventBus() {
            return resettableEventBus;
        }
    }
}
