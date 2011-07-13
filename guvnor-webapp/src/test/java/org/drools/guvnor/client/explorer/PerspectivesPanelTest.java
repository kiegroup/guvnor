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

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class PerspectivesPanelTest {

    private PerspectivesPanel perspectivesPanel;
    private PerspectivesPanelView view;
    private PlaceController placeController;
    private PerspectivesPanelView.Presenter presenter;

    @Before
    public void setUp() throws Exception {
        view = mock( PerspectivesPanelView.class );
        placeController = mock( PlaceController.class );
        perspectivesPanel = new PerspectivesPanel( view, placeController );
        presenter = getPresenter();
    }

    private PerspectivesPanelView.Presenter getPresenter() {
        return perspectivesPanel;
    }

    @Test
    public void testPresenter() throws Exception {
        verify( view ).setPresenter( presenter );
    }

    @Test
    public void testSetWidget() throws Exception {
        TabContentWidget tabContentWidget = mock( TabContentWidget.class );

        when( tabContentWidget.getTabTitle() ).thenReturn( "Mock title" );
        when( tabContentWidget.getID() ).thenReturn( "MOCK_ID" );

        perspectivesPanel.setWidget( tabContentWidget );

        verify( view ).setWidget( "Mock title", tabContentWidget, "MOCK_ID" );
    }

    @Test
    public void testCanHandleNullWidgets() throws Exception {
        /*
          GWT ActivityManager sets the widget to null before updating it.
          Guvnor perspectives view does not support this.
          This is why we need to catch null sets for setWidget() before they make it to the view.
        */

        IsWidget isWidget = null;

        perspectivesPanel.setWidget( isWidget );
        verify( view, never() ).setWidget( Matchers.<String>any(), Matchers.<IsWidget>any(), Matchers.<String>any() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWidgetMustBeTabContentWidget() throws Exception {
        IsWidget isWidget = mock( IsWidget.class );
        perspectivesPanel.setWidget( isWidget );
    }

    @Test
    public void testChangePerspective() throws Exception {
        AuthorPerspective authorPerspective = new AuthorPerspective();
        IFramePerspective runtimePerspective = new IFramePerspective();

        perspectivesPanel.addPerspective( authorPerspective );
        perspectivesPanel.addPerspective( runtimePerspective );

        goToAndVerify( authorPerspective );

        goToAndVerify( runtimePerspective );
    }

    private void goToAndVerify( Perspective perspective ) throws UnknownPerspective {
        presenter.onPerspectiveChange( perspective.getName() );

        fail();
//        verify( placeController ).goTo( perspective );
    }


}
