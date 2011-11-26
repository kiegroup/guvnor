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

package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.ConfigurationsLoaderMock;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeView.Presenter;
import org.drools.guvnor.client.rpc.CategoryServiceAsyncMock;
import org.drools.guvnor.client.rpc.RepositoryServiceAsyncMock;
import org.junit.Before;

import java.util.*;

import static org.mockito.Mockito.*;

public class BrowseTreeTestBase {

    protected BrowseTreeView view;
    protected Presenter presenter;
    private RepositoryServiceAsyncMockImpl repositoryServiceAsyncMock;
    protected CategoryServiceAsyncMockImpl categoryServiceAsyncMock;
    protected IsTreeItem rootCategoryTreeItem;
    protected IsTreeItem find;
    private IsTreeItem rootStatesTreeItem;
    protected IsTreeItem incomingInboxTreeItem;
    protected IsTreeItem inboxRecentEdited;
    protected IsTreeItem inboxRecentViewed;
    protected IsTreeItem rootTreeItem;
    protected PlaceController placeController;
    protected ClientFactory clientFactory;
    protected NavigationViewFactory navigationViewFactory;
    protected EventBus eventBus;

    @Before
    public void setUp() throws Exception {
        setUpServices();
        setUpView();
        setUpPresenter();
    }

    protected void setUpPresenter() {
        eventBus = mock( EventBus.class );
        navigationViewFactory = mock( NavigationViewFactory.class );
        clientFactory = mock( ClientFactory.class );
        when(
                clientFactory.getRepositoryService()
        ).thenReturn(
                repositoryServiceAsyncMock
        );
        when(
                clientFactory.getCategoryService()
        ).thenReturn(
                categoryServiceAsyncMock
        );
        placeController = mock( PlaceController.class );
        when(
                clientFactory.getPlaceController()
        ).thenReturn(
                placeController
        );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        when(
                navigationViewFactory.getBrowseTreeView()
        ).thenReturn(
                view
        );

        presenter = new BrowseTree( clientFactory, eventBus);
    }

    private void setUpServices() {
        repositoryServiceAsyncMock = new RepositoryServiceAsyncMockImpl();
        categoryServiceAsyncMock = new CategoryServiceAsyncMockImpl();
    }

    private void setUpView() {
        view = mock( BrowseTreeView.class );
        find = mock( IsTreeItem.class );
        rootTreeItem = mock( IsTreeItem.class );
        rootCategoryTreeItem = mock( IsTreeItem.class );
        rootStatesTreeItem = mock( IsTreeItem.class );
        incomingInboxTreeItem = mock( IsTreeItem.class );
        inboxRecentEdited = mock( IsTreeItem.class );
        inboxRecentViewed = mock( IsTreeItem.class );
        when( view.addFind() ).thenReturn( find );
        when( view.addRootTreeItem() ).thenReturn( rootTreeItem );
        when( view.addRootCategoryTreeItem() ).thenReturn( rootCategoryTreeItem );
        when( view.addRootStateTreeItem() ).thenReturn( rootStatesTreeItem );
        when( view.addInboxIncomingTreeItem() ).thenReturn( incomingInboxTreeItem );
        when( view.addInboxRecentEditedTreeItem() ).thenReturn( inboxRecentEdited );
        when( view.addInboxRecentViewedTreeItem() ).thenReturn( inboxRecentViewed );
    }

    protected void setUpStates(String... states) {
        repositoryServiceAsyncMock.setStates( states );
    }

    protected void setUpChildren(IsTreeItem parent, IsTreeItem... children) {
        ArrayList rootChildList = new ArrayList();
        rootChildList.addAll( Arrays.asList( children ) );
        when( view.getChildren( parent ) ).thenReturn( rootChildList );
    }

    protected void setUpCapabilities(Capability... list) {
        List<Capability> capabilities = new ArrayList<Capability>();
        capabilities.addAll( Arrays.asList( list ) );
        ConfigurationsLoaderMock.loadUserCapabilities( capabilities );
    }

    protected void verifyAddedTreeItemsToCategory(IsTreeItem parent, String... categories) {
        for (String category : categories) {
            verify( view ).addTreeItem( parent, category );
        }
    }

    class RepositoryServiceAsyncMockImpl extends RepositoryServiceAsyncMock {
        private String[] states;

        public void setStates(String... states) {
            this.states = states;
        }

        public void listStates(AsyncCallback<String[]> cb) {
            cb.onSuccess( states );
        }
    }

    class CategoryServiceAsyncMockImpl extends CategoryServiceAsyncMock {
        private Map<String, String[]> categories = new HashMap<String, String[]>();

        public void addCategorySelection(String path, String... categories) {
            this.categories.put( path, categories );
        }

        public void loadChildCategories(String path, AsyncCallback<String[]> cb) {
            String[] subCategories = categories.get( path );
            cb.onSuccess( subCategories );
        }
    }

}
