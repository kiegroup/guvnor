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

package org.jboss.bpm.console.client.navigation.modules;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.jboss.bpm.console.client.navigation.NavigationViewFactory;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

public class GlobalAreaTreeItemTest {

    private GlobalAreaTreeItemView view;
    private GlobalAreaTreeItem presenter;
    private NavigationViewFactory navigationViewFactory;
    private ModuleServiceAsync moduleService;

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(GlobalAreaTreeItemView.class);
        ClientFactory clientFactory = Mockito.mock(ClientFactory.class);
        ModulesTreeItemView modulesTreeItemView = Mockito.mock(ModulesTreeItemView.class);
        navigationViewFactory = Mockito.mock(NavigationViewFactory.class);
        Mockito.when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        Mockito.when(
                navigationViewFactory.getGlobalAreaTreeItemView()
        ).thenReturn(
                view
        );
        
        moduleService = Mockito.mock(ModuleServiceAsync.class);
        Mockito.when(
                clientFactory.getModuleService()
        ).thenReturn(
                moduleService
        );

        presenter = new GlobalAreaTreeItem(clientFactory);
    }

    @Test
    public void testSetUp() throws Exception {
        Mockito.verify(navigationViewFactory).getGlobalAreaTreeItemView();
        Mockito.verify(view).setPresenter(presenter);
        Mockito.verify(moduleService).loadGlobalModule(Matchers.<AsyncCallback<Module>>any());
    }
}
