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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.packages.CloseTabEvent;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.ActivityMapper;
import org.drools.guvnor.client.util.TabbedPanel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class MultiActivityManagerTest {

    private ActivityMapper activityMapper;
    private PlaceHistoryMapper placeHistoryMapper;
    private EventBus eventBus;
    private MultiActivityManager multiActivityManager;
    private TabbedPanel tabbedPanel;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks( MultiActivityManagerTest.class );
        activityMapper = mock( ActivityMapper.class );
        placeHistoryMapper = mock( PlaceHistoryMapper.class );
        eventBus = mock( EventBus.class );
        tabbedPanel = mock( TabbedPanel.class );

        ClientFactory clientFactory = mock( ClientFactory.class );

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
        when(
                clientFactory.getEventBus()
        ).thenReturn(
                eventBus
        );

        multiActivityManager = new MultiActivityManager( clientFactory );
    }

    @Test
    public void testPlaceChangeEventHandlerIsSet() throws Exception {
        verify( eventBus ).addHandler(
                PlaceChangeEvent.TYPE,
                multiActivityManager );
    }

    @Test
    public void testCloseTabEventHandlerIsSet() throws Exception {
        verify( eventBus ).addHandler(
                CloseTabEvent.TYPE,
                multiActivityManager );
    }

    @Test(expected = IllegalStateException.class)
    public void testTabbedPanelCanOnlyBeSetOnce() throws Exception {
        multiActivityManager.setTabbedPanel( tabbedPanel );
        multiActivityManager.setTabbedPanel( tabbedPanel );
    }

    @Test(expected = IllegalStateException.class)
    public void testTabbedPanelIsNotSet() throws Exception {
        PlaceChangeEvent placeChangeEvent = mock( PlaceChangeEvent.class );
        multiActivityManager.onPlaceChange( placeChangeEvent );
    }

    @Test
    public void testItIsSafeToCloseATabThatDoesNotExist() throws Exception {
        multiActivityManager.onCloseTab( new CloseTabEvent( "I-do-Not-Exist" ) );
        // Does nothing
    }

    @Test
    public void testChangePlace() throws Exception {
        Place oldPlace = mock( Place.class );
        Place newPlace = mock( Place.class );

        multiActivityManager.setTabbedPanel( tabbedPanel );

        verifyGoToNewPlace( oldPlace, "oldMockTabTitle", "oldMockToken" );
        verifyGoToNewPlace( newPlace, "newMockTabTitle", "newMockToken" );
    }

    @Test
    public void testPlaceGetsShownIfItAlreadyActive() throws Exception {
        Place oldPlace = mock( Place.class );
        Place newPlace = mock( Place.class );

        multiActivityManager.setTabbedPanel( tabbedPanel );

        verifyGoToNewPlace( oldPlace, "oldMockTabTitle", "oldMockTabToken" );
        verifyGoToExistingPlace( newPlace, "newMockTabToken" );
    }

    @Test
    public void testGoingNoWhereGetsYouNoWhere() throws Exception {
        multiActivityManager.setTabbedPanel( tabbedPanel );
        PlaceChangeEvent placeChangeEvent = setUpPlaceChangeEvent( Place.NOWHERE );
        multiActivityManager.onPlaceChange( placeChangeEvent );
        verify( tabbedPanel, never() ).show( anyString() );
        verify( tabbedPanel, never() ).addTab( anyString(), Matchers.<IsWidget>any(), anyString() );
    }

    @Test
    public void testCloseTabCallsOnStopAndRemovesWrappersHandlers() throws Exception {
        Place place = mock( Place.class );
        multiActivityManager.setTabbedPanel( tabbedPanel );
        ArgumentCaptor<ResettableEventBus> resettableEventBusArgumentCaptor = ArgumentCaptor.forClass( ResettableEventBus.class );

        HandlerRegistration handlerRegistration = mock( HandlerRegistration.class );
        CloseTabEvent.Handler handler = mock( CloseTabEvent.Handler.class );
        when( eventBus.addHandler( CloseTabEvent.TYPE, handler ) ).thenReturn( handlerRegistration );

        Activity activity = goTo( place, "mockTabToken" );

        when( activity.mayStop() ).thenReturn( true );

        verify( activity ).start( any( AcceptTabItem.class ), resettableEventBusArgumentCaptor.capture() );
        resettableEventBusArgumentCaptor.getValue().addHandler( CloseTabEvent.TYPE, handler );

        multiActivityManager.onCloseTab( new CloseTabEvent( "mockTabToken" ) );
        verify( activity ).onStop();
        verify( handlerRegistration ).removeHandler();
    }

    @Test
    public void testClosingATabIsBlockedByTheActivity() throws Exception {
        Place place = mock( Place.class );
        multiActivityManager.setTabbedPanel( tabbedPanel );

        Activity activity = goTo( place, "mockTabToken" );
        when( activity.mayStop() ).thenReturn( false );

        multiActivityManager.onCloseTab( new CloseTabEvent( "mockTabToken" ) );
        verify( activity, never() ).onStop();
    }

    private void verifyGoToExistingPlace(Place newPlace, String tabToken) {
        goTo( newPlace, tabToken );
        tabbedPanel.show( tabToken );
        verify( tabbedPanel, never() ).addTab( anyString(), Matchers.<IsWidget>any(), eq( tabToken ) );
    }

    private void verifyGoToNewPlace(Place place, String tabTitle, String tabToken) {
        ArgumentCaptor<AcceptTabItem> acceptTabItemArgumentCaptor = ArgumentCaptor.forClass( AcceptTabItem.class );
        IsWidget tabContentWidget = mock( IsWidget.class );

        Activity activity = goTo( place, tabToken );

        verify( activity ).start( acceptTabItemArgumentCaptor.capture(), any( ResettableEventBus.class ) );

        acceptTabItemArgumentCaptor.getValue().addTab( tabTitle, tabContentWidget );
        verify( tabbedPanel ).addTab( tabTitle, tabContentWidget, tabToken );
    }

    private Activity goTo(Place place, String tabToken) {
        Activity activity = setUpActivityForAPlace( place );
        setUpTokenForAPlace( place, tabToken );
        PlaceChangeEvent placeChangeEvent = setUpPlaceChangeEvent( place );

        multiActivityManager.onPlaceChange( placeChangeEvent );

        return activity;
    }

    private void setUpTokenForAPlace(Place newPlace, String token) {
        when(
                placeHistoryMapper.getToken( newPlace )
        ).thenReturn(
                token
        );
    }

    private Activity setUpActivityForAPlace(Place newPlace) {
        Activity activity = mock( Activity.class );
        when(
                activityMapper.getActivity( newPlace )
        ).thenReturn(
                activity
        );
        return activity;
    }

    private PlaceChangeEvent setUpPlaceChangeEvent(Place newPlace) {
        PlaceChangeEvent placeChangeEvent = mock( PlaceChangeEvent.class );
        when(
                placeChangeEvent.getNewPlace()
        ).thenReturn(
                newPlace
        );
        return placeChangeEvent;
    }

}
