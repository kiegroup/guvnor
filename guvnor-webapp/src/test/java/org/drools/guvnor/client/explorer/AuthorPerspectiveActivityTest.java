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
import org.drools.guvnor.client.explorer.AuthorPerspectiveView.Presenter;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AuthorPerspectiveActivityTest {

    private AuthorPerspectiveView view;
    private AuthorPerspectiveActivity authorPerspective;
    private Presenter presenter;
    private AcceptsOneWidget rootPanel;

    @Before
    public void setUp() throws Exception {
        rootPanel = mock(AcceptsOneWidget.class);
        view = mock(AuthorPerspectiveView.class);

        ClientFactory clientFactory = mock(ClientFactory.class);
        when(clientFactory.getAuthorPerspectiveView()).thenReturn(view);

        authorPerspective = new AuthorPerspectiveActivity(clientFactory);
        presenter = getPresenter();

        authorPerspective.start(rootPanel, mock(EventBus.class));
    }

    public Presenter getPresenter() {
        return authorPerspective;
    }

    @Test
    public void testPresenterAndViewAreSet() throws Exception {
        verify(rootPanel).setWidget(view);
        verify(view).setPresenter(presenter);
    }
}
