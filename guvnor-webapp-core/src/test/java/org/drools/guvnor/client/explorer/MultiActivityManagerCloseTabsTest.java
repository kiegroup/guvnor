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
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.place.shared.Place;
import com.google.web.bindery.event.shared.Event;
import org.drools.guvnor.client.explorer.navigation.CloseAllPlacesEvent;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.util.Activity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MultiActivityManagerCloseTabsTest extends MultiActivityManagerTestBase {

    private Place place;
    private HandlerRegistration handlerRegistration;

    @Override @Before
    public void setUp() throws Exception {
        super.setUp();
        place = mock(Place.class);
        multiActivityManager.setTabbedPanel(tabbedPanel);
        handlerRegistration = mock(HandlerRegistration.class);
    }

    protected EventBus setUpEventBus() {
        return new EventBusMock();
    }

//    @Test
//    public void testItIsSafeToCloseATabThatDoesNotExist() throws Exception {
//        multiActivityManager.onClosePlace(new ClosePlaceEvent(new AssetEditorPlace("I-do-Not-Exist")));
//        // Does nothing
//    }

    @Test
    public void testClosingATabIsBlockedByTheActivity() throws Exception {
        Activity activity = goTo(place);
        setUpMayStop(activity, false);

        multiActivityManager.onClosePlace(new ClosePlaceEvent(place));
        verify(activity, never()).onStop();
    }

    @Test
    public void testCloseTabCallsOnStopAndRemovesWrappersHandlers() throws Exception {
        ArgumentCaptor<ResettableEventBus> resettableEventBusArgumentCaptor = ArgumentCaptor.forClass(ResettableEventBus.class);

        ClosePlaceEvent.Handler handler = mock(ClosePlaceEvent.Handler.class);

        Activity activity = goTo(place);

        setUpMayStop(activity, true);

        verify(activity).start(any(AcceptItem.class), resettableEventBusArgumentCaptor.capture());

        resettableEventBusArgumentCaptor.getValue().addHandler(ClosePlaceEvent.TYPE, handler);

        multiActivityManager.onClosePlace(new ClosePlaceEvent(place));
        verify(activity).onStop();
        verify(handlerRegistration).removeHandler();
    }

    @Test
    public void testCloseAllTabs() throws Exception {
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        Place place3 = mock(Place.class);
        Activity activity1 = setUpMayStop(goTo(place1), true);
        Activity activity2 = setUpMayStop(goTo(place2), true);
        Activity activity3 = setUpMayStop(goTo(place3), true);

        multiActivityManager.onCloseAllPlaces(new CloseAllPlacesEvent());

        verify(activity1).onStop();
        verify(activity2).onStop();
        verify(activity3).onStop();
    }

    private Activity setUpMayStop(Activity activity, boolean value) {
        when(
                activity.mayStop()
        ).thenReturn(
                value
        );
        return activity;
    }

    class EventBusMock extends EventBus {

        @Override
        public <H> com.google.web.bindery.event.shared.HandlerRegistration addHandler(Event.Type<H> type, H handler) {
            return handlerRegistration;
        }

        @Override
        public <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
            return handlerRegistration;
        }

        @Override
        public <H extends EventHandler> HandlerRegistration addHandlerToSource(GwtEvent.Type<H> type, Object source, H handler) {
            return null;
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            if (event instanceof ClosePlaceEvent) {
                multiActivityManager.onClosePlace((ClosePlaceEvent) event);
            }
        }

        @Override
        public void fireEventFromSource(GwtEvent<?> event, Object source) {
        }
    }

}
