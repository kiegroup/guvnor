package org.jboss.bpm.console.client.navigation.settings;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.processes.ProcessNavigationViewFactory;

public class SettingsNavigationItemBuilder extends NavigationItemBuilder {

    private ProcessNavigationViewFactory navigationViewFactory;
    private PlaceController placeController;

    public SettingsNavigationItemBuilder(ProcessNavigationViewFactory navigationViewFactory, PlaceController placeController) {
        this.navigationViewFactory = navigationViewFactory;
        this.placeController = placeController;
    }

    @Override
    public boolean hasPermissionToBuild() {
        return true;
    }

    @Override
    public IsWidget getHeader() {
        return navigationViewFactory.getSettingsHeaderView();
    }

    @Override
    public IsWidget getContent() {
        return new SettingsTree(navigationViewFactory.getSettingsTreeView(), placeController);
    }
}
