package org.drools.guvnor.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.FindPlace;
import org.drools.guvnor.client.explorer.PerspectivesPanel;
import org.drools.guvnor.client.explorer.PerspectivesPanelView;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AppControllerTest {

    private ClientFactory clientFactory;
    private PlaceHistoryHandler placeHistoryHandler;
    private PlaceController placeController;
    private EventBus eventBus;
    private ActivityManager activityManager;

    @Before
    public void setUp() throws Exception {
        clientFactory = mock( ClientFactory.class );
        PerspectivesPanelView perspectivesPanelView = mock( PerspectivesPanelView.class );
        when(
                clientFactory.getPerspectivesPanelView()
        ).thenReturn(
                perspectivesPanelView
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

        activityManager = mock( ActivityManager.class );
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
        when(
                clientFactory.getEventBus()
        ).thenReturn(
                eventBus
        );

        new AppController( clientFactory );
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
        verify( activityManager ).setDisplay( any( PerspectivesPanel.class ) );
    }

}
