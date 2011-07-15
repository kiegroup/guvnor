/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.explorer.navigation.modules.GlobalAreaTreeItem;
import org.drools.guvnor.client.explorer.navigation.modules.GlobalAreaTreeItemView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PackageServiceAsync;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

public class GlobalAreaTreeItemTest {

    private GlobalAreaTreeItemView view;
    private GlobalAreaTreeItem presenter;
    private NavigationViewFactory navigationViewFactory;
    private PackageServiceAsync packageService;

    @Before
    public void setUp() throws Exception {
        view = mock( GlobalAreaTreeItemView.class );
        ClientFactory clientFactory = mock( ClientFactory.class );
        navigationViewFactory = mock( NavigationViewFactory.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        when(
                navigationViewFactory.getGlobalAreaTreeItemView()
        ).thenReturn(
                view
        );

        packageService = mock( PackageServiceAsync.class );
        when(
                clientFactory.getPackageService()
        ).thenReturn(
                packageService
        );

        presenter = new GlobalAreaTreeItem( clientFactory );
    }

    @Test
    public void testSetUp() throws Exception {
        verify( navigationViewFactory ).getGlobalAreaTreeItemView();
        verify( view ).setPresenter( presenter );
        verify( packageService ).loadGlobalPackage( Matchers.<AsyncCallback<PackageConfigData>>any() );
    }
}
