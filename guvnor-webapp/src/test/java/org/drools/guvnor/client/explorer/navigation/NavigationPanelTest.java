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

import java.util.ArrayList;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.perspectives.ChangePerspectiveEvent;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.perspectives.Perspective;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class NavigationPanelTest {

    private NavigationPanelView view;
    private NavigationPanel presenter;
    private EventBus eventBus;
    private ClientFactory clientFactory;

    @Before
    public void setUp() throws Exception {
        view = mock(NavigationPanelView.class);
        clientFactory = mock(ClientFactory.class);
        NavigationViewFactory navigationViewFactory = mock(NavigationViewFactory.class);
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        when(
                navigationViewFactory.getNavigationPanelView()
        ).thenReturn(
                view
        );
        eventBus = mock(EventBus.class);
        presenter = new NavigationPanel(clientFactory, eventBus);
    }

    @Test
    public void testHandlerIsSet() throws Exception {
        verify(eventBus).addHandler(ChangePerspectiveEvent.TYPE, presenter);
    }

    @Test
    public void testPerspectiveChange() throws Exception {

        Perspective perspective = mock(Perspective.class);
        ArrayList<NavigationItemBuilder> navigationItemBuilders = new ArrayList<NavigationItemBuilder>();

        final IsWidget header = mock(IsWidget.class);
        final IsWidget content = mock(IsWidget.class);
        final IsWidget headerThatIsNeverShown = mock(IsWidget.class);
        final IsWidget contentThatIsNeverShown = mock(IsWidget.class);

        navigationItemBuilders.add(createNavigationItemBuilder(true, header, content));
        navigationItemBuilders.add(createNavigationItemBuilder(false, headerThatIsNeverShown, contentThatIsNeverShown));
        when(
                perspective.getBuilders(clientFactory, eventBus)
        ).thenReturn(
                navigationItemBuilders
        );

        presenter.onChangePerspective(new ChangePerspectiveEvent(perspective));

        verify(view).clear();
        verify(view).add(header, content);
        verify(view, never()).add(headerThatIsNeverShown, contentThatIsNeverShown);
    }

    private NavigationItemBuilder createNavigationItemBuilder(final boolean permissionToBuild, final IsWidget header, final IsWidget content) {
        return new NavigationItemBuilder() {
            @Override public boolean hasPermissionToBuild() {
                return permissionToBuild;
            }

            @Override public IsWidget getHeader() {
                return header;
            }

            @Override public IsWidget getContent() {
                return content;
            }
        };
    }

}
