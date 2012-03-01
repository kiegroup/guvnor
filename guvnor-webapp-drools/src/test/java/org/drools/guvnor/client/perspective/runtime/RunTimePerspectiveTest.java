package org.drools.guvnor.client.perspective.runtime;

import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessesNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksNavigationItemBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RunTimePerspectiveTest {

    @Test
    public void testAllBuildersExist() throws Exception {
        ClientFactory clientFactory = mock(ClientFactory.class);
        EventBus eventBus = mock(EventBus.class);
        Collection<NavigationItemBuilder> builders = new RunTimeWorkspace().getBuilders(clientFactory, eventBus);

        assertEquals(4, builders.size());
        assertTrue(builders.toArray()[0] instanceof TasksNavigationItemBuilder);
        assertTrue(builders.toArray()[1] instanceof ProcessesNavigationItemBuilder);
        assertTrue(builders.toArray()[2] instanceof ReportingNavigationItemBuilder);
        assertTrue(builders.toArray()[3] instanceof SettingsNavigationItemBuilder);
    }
}
