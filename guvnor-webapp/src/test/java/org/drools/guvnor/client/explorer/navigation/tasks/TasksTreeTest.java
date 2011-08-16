package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.place.shared.PlaceController;
import org.drools.guvnor.client.explorer.places.RuntimePlace;
import org.junit.Before;
import org.junit.Test;

import static org.drools.guvnor.client.explorer.navigation.RuntimeTestMethods.*;
import static org.mockito.Mockito.*;

public class TasksTreeTest {

    private TasksTree tasksTree;
    private TasksTreeView.Presenter presenter;
    private PlaceController placeController;
    private TasksTreeView view;

    @Before
    public void setUp() throws Exception {
        view = mock(TasksTreeView.class);

        placeController = mock(PlaceController.class);
        tasksTree = new TasksTree(view, placeController);
        presenter = tasksTree;
    }

    @Test
    public void testPresenterIsSet() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testReturnsViewsWidgetWithAsWidget() throws Exception {
        tasksTree.asWidget();
        verify(view).asWidget();
    }

    @Test
    public void testGoToPersonalTasks() throws Exception {
        presenter.onPersonalTasksSelected();
        assertGoesTo(placeController, RuntimePlace.Location.PERSONAL_TASKS);
    }

    @Test
    public void testGoToGroupTasks() throws Exception {
        presenter.onGroupTasksSelected();
        assertGoesTo(placeController, RuntimePlace.Location.GROUP_TASKS);
    }

}
