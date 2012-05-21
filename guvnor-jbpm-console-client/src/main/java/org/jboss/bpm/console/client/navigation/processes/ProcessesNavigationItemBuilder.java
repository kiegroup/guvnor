package org.jboss.bpm.console.client.navigation.processes;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.bpm.console.client.navigation.NavigationItemBuilder;

public class ProcessesNavigationItemBuilder extends NavigationItemBuilder {

    private ProcessNavigationViewFactory navigationViewFactory;
    private PlaceController placeController;

    public ProcessesNavigationItemBuilder(ProcessNavigationViewFactory navigationViewFactory, PlaceController placeController) {
        this.navigationViewFactory = navigationViewFactory;
        this.placeController = placeController;
    }

    @Override
    public boolean hasPermissionToBuild() {
        return true;
    }

    @Override
    public IsWidget getHeader() {
        return navigationViewFactory.getProcessesHeaderView();
    }

    @Override
    public IsWidget getContent() {
        return new ProcessesTree(navigationViewFactory.getProcessesTreeView(), placeController);
    }
}
