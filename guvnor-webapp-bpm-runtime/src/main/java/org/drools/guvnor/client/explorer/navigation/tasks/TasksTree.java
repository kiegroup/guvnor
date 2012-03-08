package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

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
        goTo(new PersonalTasksPlace());
    }

    public void onGroupTasksSelected() {
        goTo(new GroupTasksPlace());
    }

    private void goTo(Place place) {
        placeController.goTo(place);
    }
}
