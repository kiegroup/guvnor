/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.explorer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.navigation.CloseAllPlacesEvent;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.ActivityMapper;
import org.drools.guvnor.client.util.TabbedPanel;

public class MultiActivityManager implements
        PlaceChangeEvent.Handler,
        ClosePlaceEvent.Handler,
        CloseAllPlacesEvent.Handler {

    private final ActivityMapper activityMapper;
    private TabbedPanel tabbedPanel;
    private final EventBus eventBus;
    private final Map<Place, Pair> activeActivities = new HashMap<Place, Pair>();

    public MultiActivityManager(ClientFactory clientFactory, EventBus eventBus) {
        this.activityMapper = clientFactory.getActivityMapper();
        this.eventBus = eventBus;

        eventBus.addHandler(
                PlaceChangeEvent.TYPE,
                this);
        eventBus.addHandler(
                ClosePlaceEvent.TYPE,
                this);
        eventBus.addHandler(
                CloseAllPlacesEvent.TYPE,
                this);
    }

    public void setTabbedPanel(TabbedPanel tabbedPanel) {
        if (this.tabbedPanel == null) {
            this.tabbedPanel = tabbedPanel;
        } else {
            throw new IllegalStateException(TabbedPanel.class.getName() + " can only be set once.");
        }
    }

    public void onPlaceChange(PlaceChangeEvent event) {

        if (tabbedPanel == null) {
            throw new IllegalStateException(TabbedPanel.class.getName() + " is not set for " + MultiActivityManager.class.getName());
        } else {
            if (isActivityAlreadyActive(event.getNewPlace())) {
                showExistingActivity(event.getNewPlace());
            } else if (ifPlaceExists(event)) {
                startNewActivity(event.getNewPlace());
            }
        }
    }

    private void showExistingActivity(Place token) {
        tabbedPanel.show(token);
    }

    private boolean isActivityAlreadyActive(Place token) {
        return activeActivities.keySet().contains(token);
    }

    private void startNewActivity(final Place newPlace) {
        Activity activity = activityMapper.getActivity(newPlace);

        final ResettableEventBus resettableEventBus = new ResettableEventBus(eventBus);

        activeActivities.put(newPlace, new Pair(activity, resettableEventBus));

        activity.start(
                new AcceptItem() {
                    public void add(String tabTitle, IsWidget widget) {
                        tabbedPanel.addTab(
                                tabTitle,
                                widget,
                                newPlace);
                    }
                },
                resettableEventBus);
    }

    private boolean ifPlaceExists(PlaceChangeEvent event) {
        return !event.getNewPlace().equals(Place.NOWHERE);
    }

    public void onClosePlace(ClosePlaceEvent closePlaceEvent) {
        Pair pair = activeActivities.get(closePlaceEvent.getPlace());
        if (pair != null && pair.getActivity().mayStop()) {
            pair.getActivity().onStop();
            pair.getResettableEventBus().removeHandlers();
            activeActivities.remove(closePlaceEvent.getPlace());
            tabbedPanel.close(closePlaceEvent.getPlace());
        }
    }

    public void onCloseAllPlaces(CloseAllPlacesEvent event) {
        Set<Place> places = new HashSet<Place>();
        places.addAll(activeActivities.keySet());

        for (Place place : places) {
            eventBus.fireEvent(new ClosePlaceEvent(place));
        }
    }

    private class Pair {

        private Activity activity;
        private ResettableEventBus resettableEventBus;

        public Pair(Activity activity, ResettableEventBus resettableEventBus) {
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
