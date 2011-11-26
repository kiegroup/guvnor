package org.drools.guvnor.client.explorer.navigation.settings;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;

public class SettingsNavigationItemBuilder extends NavigationItemBuilder {

    private NavigationViewFactory navigationViewFactory;
    private PlaceController placeController;

    public SettingsNavigationItemBuilder(NavigationViewFactory navigationViewFactory, PlaceController placeController) {
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
