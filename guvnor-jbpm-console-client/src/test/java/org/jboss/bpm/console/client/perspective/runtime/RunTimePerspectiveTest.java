package org.jboss.bpm.console.client.perspective.runtime;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.processes.ProcessesNavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.reporting.ReportingNavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.settings.SettingsNavigationItemBuilder;
import org.jboss.bpm.console.client.navigation.tasks.TasksNavigationItemBuilder;
import org.jboss.bpm.console.client.perspective.RunTimePerspective;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collection;

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
