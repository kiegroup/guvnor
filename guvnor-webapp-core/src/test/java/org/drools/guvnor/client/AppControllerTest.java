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

package org.drools.guvnor.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.FindPlace;
import org.drools.guvnor.client.explorer.MultiActivityManager;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.perspective.PerspectivesPanelView;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.util.TabbedPanel;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AppControllerTest {

    private PlaceHistoryHandler placeHistoryHandler;
    private PlaceController placeController;
    private EventBus eventBus;
    private MultiActivityManager activityManager;

    @Before
    public void setUp() throws Exception {
        final ClientFactory clientFactory = mock( ClientFactory.class );
        NavigationViewFactory navigationViewFactory = mock(NavigationViewFactory.class);
        PerspectivesPanelView perspectivesPanelView = mock( PerspectivesPanelView.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
        		navigationViewFactory
        );
        when(
                clientFactory.getNavigationViewFactory().getPerspectivesPanelView()
        ).thenReturn(
                perspectivesPanelView
        );
        
        PerspectiveFactory perspectiveFactory = mock(PerspectiveFactory.class);
        when(
                clientFactory.getPerspectiveFactory()
        ).thenReturn(
                perspectiveFactory
        );
        
        when(
                perspectiveFactory.getRegisteredPerspectiveTypes()
        ).thenReturn(
                new String[]{"author", "runtime"}
                
        );
        
        placeController = mock( PlaceController.class );
        when(
                clientFactory.getPlaceController()
        ).thenReturn(
                placeController
        );

        ConfigurationServiceAsync configurationService = mock( ConfigurationServiceAsync.class );
        when(
                clientFactory.getConfigurationService()
        ).thenReturn(
                configurationService
        );

        activityManager = mock( MultiActivityManager.class );
        when(
                clientFactory.getActivityManager()
        ).thenReturn(
                activityManager
        );

        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        when(
                clientFactory.getPlaceHistoryHandler()
        ).thenReturn(
                placeHistoryHandler
        );

        eventBus = mock( EventBus.class );

        new AppControllerImpl( clientFactory, eventBus );
    }

    @Test
    public void testFindPlaceIsSetUp() throws Exception {
        verify(
                placeHistoryHandler
        ).register(
                eq( placeController ),
                eq( eventBus ),
                any( FindPlace.class )
        );
    }

    @Test
    public void testActivityManagerIsSetUp() throws Exception {
        verify( activityManager ).setTabbedPanel( any( TabbedPanel.class ) );
    }

}
