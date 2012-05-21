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

package org.jboss.bpm.console.client.navigation;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.Event;
import org.drools.guvnor.client.asseteditor.RefreshAssetEditorEvent;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.perspective.ChangePerspectiveEvent;
import org.drools.guvnor.client.perspective.Perspective;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class NavigationPanelTest {

    private NavigationPanelView view;
    private NavigationPanel presenter;
    private EventBus eventBus;
    private ClientFactory clientFactory;
    private HandlerRegistration handlerRegistration;

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(NavigationPanelView.class);
        clientFactory = Mockito.mock(ClientFactory.class);
        NavigationViewFactory navigationViewFactory = Mockito.mock(NavigationViewFactory.class);
        Mockito.when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        Mockito.when(
                navigationViewFactory.getNavigationPanelView()
        ).thenReturn(
                view
        );
        handlerRegistration = Mockito.mock(HandlerRegistration.class);
        eventBus = Mockito.spy(new EventBusMock());
        presenter = new NavigationPanel(clientFactory, eventBus);
    }

    @Test
    public void testHandlerIsSet() throws Exception {
        Mockito.verify(eventBus).addHandler(ChangePerspectiveEvent.TYPE, presenter);
    }

    @Test
    public void testPerspectiveChange() throws Exception {

        Perspective perspective = Mockito.mock(Perspective.class);
        ArrayList<NavigationItemBuilder> navigationItemBuilders = new ArrayList<NavigationItemBuilder>();

        final IsWidget header = Mockito.mock(IsWidget.class);
        final IsWidget content = Mockito.mock(IsWidget.class);
        final IsWidget headerThatIsNeverShown = Mockito.mock(IsWidget.class);
        final IsWidget contentThatIsNeverShown = Mockito.mock(IsWidget.class);

        navigationItemBuilders.add(createNavigationItemBuilder(true, header, content));
        navigationItemBuilders.add(createNavigationItemBuilder(false, headerThatIsNeverShown, contentThatIsNeverShown));
        Mockito.when(
                perspective.getBuilders(Matchers.eq(clientFactory), Matchers.any(EventBus.class))
        ).thenReturn(
                navigationItemBuilders
        );

        presenter.onChangePerspective(new ChangePerspectiveEvent(perspective));

        Mockito.verify(view).clear();
        Mockito.verify(view).add(header, content);
        Mockito.verify(view, Mockito.never()).add(headerThatIsNeverShown, contentThatIsNeverShown);
    }

    @Test
    public void testEventBusClearedOnPerspectiveChange() throws Exception {
        Perspective perspective = Mockito.mock(Perspective.class);

        ArgumentCaptor<ResettableEventBus> resettableEventBusArgumentCaptor = ArgumentCaptor.forClass(ResettableEventBus.class);
        Mockito.when(
                perspective.getBuilders(Matchers.eq(clientFactory), resettableEventBusArgumentCaptor.capture())
        ).thenReturn(
                Collections.<NavigationItemBuilder>emptyList()
        );

        presenter.onChangePerspective(new ChangePerspectiveEvent(perspective));

        resettableEventBusArgumentCaptor.getValue().addHandler(RefreshAssetEditorEvent.TYPE, new RefreshAssetEditorEvent.Handler() {
            public void onRefreshAsset(RefreshAssetEditorEvent refreshAssetEditorEvent) {
                //Nothing here, just setting one up se we can see if this gets cleared.
            }
        });

        presenter.onChangePerspective(new ChangePerspectiveEvent(perspective));

        Mockito.verify(handlerRegistration).removeHandler();
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

    class EventBusMock extends EventBus {

        @Override
        public <H> com.google.web.bindery.event.shared.HandlerRegistration addHandler(Event.Type<H> type, H handler) {
            return handlerRegistration;
        }

        @Override
        public <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
            return handlerRegistration;
        }

        @Override public <H extends EventHandler> HandlerRegistration addHandlerToSource(GwtEvent.Type<H> type, Object source, H handler) {
            return null;
        }

        @Override public void fireEvent(GwtEvent<?> event) {
        }

        @Override public void fireEventFromSource(GwtEvent<?> event, Object source) {
        }
    }
}
