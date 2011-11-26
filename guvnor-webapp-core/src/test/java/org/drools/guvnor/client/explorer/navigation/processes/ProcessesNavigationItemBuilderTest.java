/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation.processes;

import com.google.gwt.place.shared.PlaceController;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProcessesNavigationItemBuilderTest {

    private ProcessesNavigationItemBuilder builder;
    private ProcessesHeaderView processesHeaderView;

    @Before
    public void setUp() throws Exception {
        processesHeaderView = mock(ProcessesHeaderView.class);
        NavigationViewFactory navigationViewFactory = mock(NavigationViewFactory.class);
        when(
                navigationViewFactory.getProcessesHeaderView()
        ).thenReturn(
                processesHeaderView
        );
        ProcessesTreeView processesTreeView = mock(ProcessesTreeView.class);
        when(
                navigationViewFactory.getProcessesTreeView()
        ).thenReturn(
                processesTreeView
        );
        PlaceController placeController = mock(PlaceController.class);
        builder = new ProcessesNavigationItemBuilder(navigationViewFactory, placeController);
    }

    @Test
    public void testAlwaysBuilds() throws Exception {
        assertTrue(builder.hasPermissionToBuild());
    }

    @Test
    public void testHeader() throws Exception {
        assertEquals(processesHeaderView, builder.getHeader());
    }

    @Test
    public void testContent() throws Exception {
        assertTrue(builder.getContent() instanceof ProcessesTree);
    }
}
