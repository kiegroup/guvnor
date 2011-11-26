package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.place.shared.PlaceController;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TasksNavigationItemBuilderTest {

    private TasksNavigationItemBuilder builder;
    private NavigationViewFactory navigationViewFactory;
    private TasksHeaderView tasksHeaderView;

    @Before
    public void setUp() throws Exception {
        navigationViewFactory = mock(NavigationViewFactory.class);
        tasksHeaderView = mock(TasksHeaderView.class);
        when(
                navigationViewFactory.getTasksHeaderView()
        ).thenReturn(
                tasksHeaderView
        );
        TasksTreeView tasksTreeView = mock(TasksTreeView.class);
        when(
                navigationViewFactory.getTasksTreeView()
        ).thenReturn(
                tasksTreeView
        );
        PlaceController placeController = mock(PlaceController.class);
        builder = new TasksNavigationItemBuilder(navigationViewFactory, placeController);
    }

    @Test
    public void testAlwaysBuilds() throws Exception {
        assertTrue(builder.hasPermissionToBuild());
    }

    @Test
    public void testHeader() throws Exception {
        assertEquals(tasksHeaderView, builder.getHeader());
    }

    @Test
    public void testContent() throws Exception {
        assertTrue(builder.getContent() instanceof TasksTree);
    }
}
