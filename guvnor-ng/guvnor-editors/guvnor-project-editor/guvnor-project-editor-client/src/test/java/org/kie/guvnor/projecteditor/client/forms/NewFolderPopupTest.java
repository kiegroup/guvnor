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

package org.kie.guvnor.projecteditor.client.forms;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.ClosePlaceEvent;

import javax.enterprise.event.Event;
import java.lang.annotation.Annotation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NewFolderPopupTest {

    private NewFolderPopup popup;
    private PlaceManager placeManager;
    private NewFolderPopupView view;
    private NewFolderPopupView.Presenter presenter;
    private NewFolderPopupTest.MockClosePlaceEvent closeEvent;

    @Before
    public void setUp() throws Exception {
        placeManager = mock(PlaceManager.class);
        view = mock(NewFolderPopupView.class);
        closeEvent = mock(MockClosePlaceEvent.class);
        popup = new NewFolderPopup(
                placeManager,
                new MockFileServiceCaller(),
                view,
                closeEvent);

        presenter = popup;
    }

    @Test
    public void testPresenter() throws Exception {
        verify(view).setPresenter(presenter);
    }

    class MockClosePlaceEvent
            implements Event<ClosePlaceEvent> {

        @Override
        public void fire(ClosePlaceEvent closePlaceEvent) {
            //TODO -Rikkola-
        }

        @Override
        public Event<ClosePlaceEvent> select(Annotation... annotations) {
            return null;  //TODO -Rikkola-
        }

        @Override
        public <U extends ClosePlaceEvent> Event<U> select(Class<U> uClass, Annotation... annotations) {
            return null;  //TODO -Rikkola-
        }
    }
}
