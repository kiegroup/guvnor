package org.drools.guvnor.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.*;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;

import java.util.Collection;

public class AppController {

    private final ClientFactory clientFactory;

    private final PerspectivesPanel perspectivesPanel;

    public AppController( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;

        perspectivesPanel = createPerspectivesPanel();
        loadPerspectives();
        setUpActivityMapper();
        setUpHistoryMapper();
    }

    private void setUpHistoryMapper() {
        PlaceHistoryHandler historyHandler = clientFactory.getPlaceHistoryHandler();
        historyHandler.register(
                clientFactory.getPlaceController(),
                clientFactory.getEventBus(),
                new FindPlace() );

        historyHandler.handleCurrentHistory();
    }

    private PerspectivesPanel createPerspectivesPanel() {
        return new PerspectivesPanel(
                clientFactory.getPerspectivesPanelView(),
                clientFactory.getPlaceController() );
    }

    private void setUpActivityMapper() {
        ActivityManager activityManager = clientFactory.getActivityManager();
        activityManager.setDisplay( perspectivesPanel );
    }

    public IsWidget getMainPanel() {
        return perspectivesPanel.getView();
    }

    private void loadPerspectives() {
        ConfigurationServiceAsync configurationServiceAsync = clientFactory.getConfigurationService();

        PerspectiveLoader perspectiveLoader = new PerspectiveLoader( configurationServiceAsync );
        perspectiveLoader.loadPerspectives( new LoadPerspectives() {
            public void loadPerspectives( Collection<Perspective> perspectives ) {
                for (Perspective perspective : perspectives) {
                    perspectivesPanel.addPerspective( perspective );
                }
            }
        } );
    }

    public void setUserName( String userName ) {
        perspectivesPanel.setUserName( userName );
    }
}
