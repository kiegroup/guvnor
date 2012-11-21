/*
 * Copyright 2012 JBoss Inc
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

package org.kie.projecteditor.client.forms;

import org.junit.Before;
import org.junit.Test;
import org.kie.projecteditor.shared.model.KSessionModel;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class KSessionsPanelTest {

    private KSessionsPanelView view;
    private KSessionsPanel kSessionsPanel;

    @Before
    public void setUp() throws Exception {
        view = mock(KSessionsPanelView.class);
        kSessionsPanel = new KSessionsPanel(view);
    }

    @Test
    public void testShowEmptyList() throws Exception {
        kSessionsPanel.setSessions(new ArrayList<KSessionModel>());

        verify(view).setPresenter(kSessionsPanel);
        verify(view).clearList();
        verify(view, never()).addKSessionModel(any(KSessionModel.class));
    }
}
