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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class NavigationPanelFactoryTest {

    private NavigationPanelView view;
    private NavigationViewFactory navigationViewFactory;

    @Before
    public void setUp() throws Exception {
        view = mock( NavigationPanelView.class );
        navigationViewFactory = mock( NavigationViewFactory.class );

        when( navigationViewFactory.getNavigationPanelView() ).thenReturn( view );
    }

    @Test
    public void testNoNavigationItems() throws Exception {
        NavigationPanelFactory navigationPanelFactory = new NavigationPanelFactory( navigationViewFactory );

        NavigationPanel navigationPanel = navigationPanelFactory.createNavigationPanel( Collections.<NavigationItemBuilder>emptySet() );

        assertNotNull( navigationPanel );
        verify( view, never() ).add( Matchers.<IsWidget>any(), Matchers.<IsWidget>any() );
    }

    @Test
    public void testAddNavigationItems() throws Exception {
        NavigationPanelFactory navigationPanelFactory = new NavigationPanelFactory( navigationViewFactory );

        HashSet<NavigationItemBuilder> navigationItemBuilders = new HashSet<NavigationItemBuilder>();
        navigationItemBuilders.add( createNavigationItemBuilder( true ) );
        navigationItemBuilders.add( createNavigationItemBuilder( true ) );
        navigationItemBuilders.add( createNavigationItemBuilder( false ) );
        NavigationPanel navigationPanel = navigationPanelFactory.createNavigationPanel( navigationItemBuilders );

        assertNotNull( navigationPanel );
        verify( view, times( 2 ) ).add( Matchers.<IsWidget>any(), Matchers.<IsWidget>any() );
    }

    private NavigationItemBuilder createNavigationItemBuilder( boolean buildPermission ) {
        NavigationItemBuilder navigationItemBuilder = mock( NavigationItemBuilder.class );
        when( navigationItemBuilder.hasPermissionToBuild() ).thenReturn( buildPermission );
        return navigationItemBuilder;
    }
}
