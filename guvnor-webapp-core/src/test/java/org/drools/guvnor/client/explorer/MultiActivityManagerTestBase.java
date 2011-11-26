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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.ActivityMapper;
import org.drools.guvnor.client.util.TabbedPanel;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public abstract class MultiActivityManagerTestBase {

    private ActivityMapper activityMapper;
    private PlaceHistoryMapper placeHistoryMapper;
    protected EventBus eventBus;
    protected MultiActivityManager multiActivityManager;
    protected TabbedPanel tabbedPanel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(MultiActivityManagerTestBase.class);
        activityMapper = mock(ActivityMapper.class);
        placeHistoryMapper = mock(PlaceHistoryMapper.class);
        eventBus = setUpEventBus();
        tabbedPanel = mock(TabbedPanel.class);

        ClientFactory clientFactory = mock(ClientFactory.class);

        when(
                clientFactory.getActivityMapper()
        ).thenReturn(
                activityMapper
        );
        when(
                clientFactory.getPlaceHistoryMapper()
        ).thenReturn(
                placeHistoryMapper
        );

        multiActivityManager = new MultiActivityManager(clientFactory, eventBus);
    }

    protected EventBus setUpEventBus() {
        return mock(EventBus.class);
    }

    protected Activity goTo(Place place) {
        Activity activity = setUpActivityForAPlace(place);
        PlaceChangeEvent placeChangeEvent = setUpPlaceChangeEvent(place);

        multiActivityManager.onPlaceChange(placeChangeEvent);

        return activity;
    }

    private Activity setUpActivityForAPlace(Place newPlace) {
        Activity activity = mock(Activity.class);
        when(
                activityMapper.getActivity(newPlace)
        ).thenReturn(
                activity
        );
        return activity;
    }

    protected PlaceChangeEvent setUpPlaceChangeEvent(Place newPlace) {
        PlaceChangeEvent placeChangeEvent = mock(PlaceChangeEvent.class);
        when(
                placeChangeEvent.getNewPlace()
        ).thenReturn(
                newPlace
        );
        return placeChangeEvent;
    }
}
