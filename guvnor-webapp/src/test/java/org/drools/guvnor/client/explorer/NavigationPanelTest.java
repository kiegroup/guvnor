/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.explorer.NavigationPanelView.Presenter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class NavigationPanelTest {

    private NavigationPanelView view;
    private Presenter presenter;

    @Before
    public void setUp() throws Exception {
        view = mock(NavigationPanelView.class);

        presenter = new NavigationPanel(view);
    }

    @Test
    public void testMock() throws Exception {
        assertTrue(true);
    }

// Test list

    // Test if permissions ok and visible

}
