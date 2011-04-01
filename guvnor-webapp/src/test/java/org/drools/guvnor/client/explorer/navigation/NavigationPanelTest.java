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

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NavigationPanelTest {

    @Test
    public void testTestAdd() throws Exception {
        NavigationPanelView view = mock(NavigationPanelView.class);
        NavigationPanel navigationPanel = new NavigationPanel(view);

        IsWidget header = mock(IsWidget.class);
        IsWidget content = mock(IsWidget.class);

        navigationPanel.add(header, content);

        verify(view).add(header, content);
    }

    // TODO: No nulls allowed! -Rikkola-

    // Test list

    // Test if permissions ok and visible

}
