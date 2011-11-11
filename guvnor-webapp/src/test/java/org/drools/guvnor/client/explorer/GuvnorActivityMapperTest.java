/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewActivity;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesActivity;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesActivity;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksActivity;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksActivity;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GuvnorActivityMapperTest {

    private GuvnorActivityMapper guvnorActivityMapper;

    @Before
    public void setUp() throws Exception {
        ClientFactory clientFactory = mock(ClientFactory.class);
        guvnorActivityMapper = new GuvnorActivityMapper(clientFactory);
    }

    @Test
    public void testPersonalTasksActivity() throws Exception {
        assertTrue(guvnorActivityMapper.getActivity(new PersonalTasksPlace()) instanceof PersonalTasksActivity);
    }

    @Test
    public void testGroupTasks() throws Exception {
        assertTrue(guvnorActivityMapper.getActivity(new GroupTasksPlace()) instanceof GroupTasksActivity);
    }

    @Test
    public void testReportingTemplates() throws Exception {
        assertTrue(guvnorActivityMapper.getActivity(new ReportTemplatesPlace()) instanceof ReportTemplatesActivity);
    }

    @Test
    public void testPreferences() throws Exception {
        assertTrue(guvnorActivityMapper.getActivity(new PreferencesPlace()) instanceof PreferencesActivity);
    }

    @Test
    public void testProcessOverview() throws Exception {
        assertTrue(guvnorActivityMapper.getActivity(new ProcessOverviewPlace()) instanceof ProcessOverviewActivity);
    }
}
