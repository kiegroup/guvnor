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

package org.jboss.bpm.console.client.perspective.runtime;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.perspective.PerspectivesPanel;
import org.drools.guvnor.client.perspective.PerspectivesPanelView;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;
import org.jboss.bpm.console.client.perspective.ChangePerspectiveEvent;
import org.jboss.bpm.console.client.perspective.RunTimePerspective;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class PerspectivesPanelTest {

    private PerspectivesPanel perspectivesPanel;
    private PerspectivesPanelView view;
    private PerspectivesPanelView.Presenter presenter;
    private EventBusMock eventBus;

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(PerspectivesPanelView.class);
        NavigationViewFactory navigationViewFactory = Mockito.mock(NavigationViewFactory.class);
        BpmConsoleClientFactory clientFactory = Mockito.mock(BpmConsoleClientFactory.class);
        eventBus = Mockito.spy(new EventBusMock());
        Mockito.when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        Mockito.when(
                navigationViewFactory.getPerspectivesPanelView()
        ).thenReturn(
                view
        );

        PerspectiveFactory perspectiveFactory = Mockito.mock(PerspectiveFactory.class);
        Mockito.when(
                clientFactory.getPerspectiveFactory()
        ).thenReturn(
                perspectiveFactory
        );

        Mockito.when(
                perspectiveFactory.getPerspective("runtime")
        ).thenReturn(
                new RunTimePerspective()
        );

        Mockito.when(
                perspectiveFactory.getRegisteredPerspectiveTypes()
        ).thenReturn(
                new String[]{"author", "runtime"}

        );

        ModuleServiceAsync packageService = Mockito.mock(ModuleServiceAsync.class);
        Mockito.when(
                clientFactory.getModuleService()
        ).thenReturn(
                packageService
        );

        perspectivesPanel = new PerspectivesPanel(clientFactory, eventBus);
        presenter = getPresenter();
    }

    private PerspectivesPanelView.Presenter getPresenter() {
        return perspectivesPanel;
    }

    @Test
    public void testPresenter() throws Exception {
        Mockito.verify(view).setPresenter(presenter);
    }

    @Test
    public void testPerspectiveListIsLoaded() throws Exception {
        Mockito.verify(view).addPerspective("author", "author");
        Mockito.verify(view).addPerspective("runtime", "runtime");
    }

    @Test
    public void testChangePerspectiveToRunTime() throws Exception {

        presenter.onChangePerspective("runtime");

        Assert.assertTrue(eventBus.getLatestEvent() instanceof ChangePerspectiveEvent);
        Assert.assertTrue(((ChangePerspectiveEvent) eventBus.getLatestEvent()).getPerspective() instanceof RunTimePerspective);
    }

    class EventBusMock extends EventBus {

        private List<GwtEvent<?>> events = new ArrayList<GwtEvent<?>>();

        @Override
        public <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
            return null;
        }

        @Override
        public <H extends EventHandler> HandlerRegistration addHandlerToSource(GwtEvent.Type<H> type, Object source, H handler) {
            return null;
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            events.add(event);
        }

        @Override
        public void fireEventFromSource(GwtEvent<?> event, Object source) {
        }

        public GwtEvent<?> getLatestEvent() {
            return events.get(events.size() - 1);
        }
    }
}
