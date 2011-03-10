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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IFramePerspectiveActivityTest {

    private AcceptsOneWidget panel;
    private EventBus eventBus;
    private IFramePerspectiveView view;
    private ClientFactory clientFactory;

    @Before
    public void setUp() throws Exception {
        panel = mock(AcceptsOneWidget.class);
        eventBus = mock(EventBus.class);
        clientFactory = mock(ClientFactory.class);
        view = mock(IFramePerspectiveView.class);
        when(clientFactory.getIFramePerspectiveView()).thenReturn(view);

    }

    @Test
    public void testStart() throws Exception {
        IFramePerspectivePlace place = new IFramePerspectivePlace();
        place.setName("Drools Manual");
        place.setUrl("http://drools.org/manual");
        IFramePerspectiveActivity iFramePerspectiveActivity = new IFramePerspectiveActivity(clientFactory, place);
        iFramePerspectiveActivity.start(panel, eventBus);

        verify(view).setName("Drools Manual");
        verify(view).setUrl("http://drools.org/manual");
        verify(panel).setWidget(any(IFramePerspectiveView.class));
    }
}
