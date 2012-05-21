package org.jboss.bpm.console.client.perspective.runtime;

import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessesNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsNavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksNavigationItemBuilder;
import org.jboss.bpm.console.client.perspective.RunTimePerspective;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RunTimePerspectiveTest {

    @Test
    public void testAllBuildersExist() throws Exception {
        EventBus eventBus = Mockito.mock(EventBus.class);
        ClientFactory clientFactory = Mockito.mock(ClientFactory.class);
        Collection<NavigationItemBuilder> builders = new RunTimePerspective().getBuilders(clientFactory, eventBus);

        Assert.assertEquals(4, builders.size());
        Assert.assertTrue(builders.toArray()[0] instanceof TasksNavigationItemBuilder);
        Assert.assertTrue(builders.toArray()[1] instanceof ProcessesNavigationItemBuilder);
        Assert.assertTrue(builders.toArray()[2] instanceof ReportingNavigationItemBuilder);
        Assert.assertTrue(builders.toArray()[3] instanceof SettingsNavigationItemBuilder);
    }
}
