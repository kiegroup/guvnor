package org.jboss.bpm.console.client.navigation.tasks;

import com.google.gwt.place.shared.PlaceController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class TasksTreeTest {

    private TasksTree tasksTree;
    private TasksTreeView.Presenter presenter;
    private PlaceController placeController;
    private TasksTreeView view;

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(TasksTreeView.class);

        placeController = Mockito.mock(PlaceController.class);
        tasksTree = new TasksTree(view, placeController);
        presenter = tasksTree;
    }

    @Test
    public void testPresenterIsSet() throws Exception {
        Mockito.verify(view).setPresenter(presenter);
    }

    @Test
    public void testReturnsViewsWidgetWithAsWidget() throws Exception {
        tasksTree.asWidget();
        Mockito.verify(view).asWidget();
    }

    @Test
    public void testGoToPersonalTasks() throws Exception {
        presenter.onPersonalTasksSelected();
        Mockito.verify(placeController).goTo(Matchers.any(PersonalTasksPlace.class));
    }

    @Test
    public void testGoToGroupTasks() throws Exception {
        presenter.onGroupTasksSelected();
        Mockito.verify(placeController).goTo(Matchers.any(GroupTasksPlace.class));
    }

}
