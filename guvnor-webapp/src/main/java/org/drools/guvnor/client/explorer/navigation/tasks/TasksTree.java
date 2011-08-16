package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.places.RuntimePlace;

public class TasksTree implements TasksTreeView.Presenter, IsWidget {

    private PlaceController placeController;
    private TasksTreeView view;

    public TasksTree(TasksTreeView view, PlaceController placeController) {
        this.view = view;
        this.view.setPresenter(this);
        this.placeController = placeController;
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    public void onPersonalTasksSelected() {
        goTo(RuntimePlace.Location.PERSONAL_TASKS);
    }

    public void onGroupTasksSelected() {
        goTo(RuntimePlace.Location.GROUP_TASKS);
    }

    private void goTo(RuntimePlace.Location location) {
        placeController.goTo(new RuntimePlace(location));
    }
}
