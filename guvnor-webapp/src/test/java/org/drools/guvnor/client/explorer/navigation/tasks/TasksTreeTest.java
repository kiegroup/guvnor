package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.place.shared.PlaceController;
import org.junit.Before;
import org.junit.Test;

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
        verify(placeController).goTo(any(PersonalTasksPlace.class));
    }

    @Test
    public void testGoToGroupTasks() throws Exception {
        presenter.onGroupTasksSelected();
        verify(placeController).goTo(any(GroupTasksPlace.class));
    }

}
