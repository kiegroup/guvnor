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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.ConfigurationsLoaderMock;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.explorer.navigation.BrowseTreeView.Presenter;
import org.drools.guvnor.client.rpc.CategoryServiceAsyncMock;
import org.drools.guvnor.client.rpc.RepositoryServiceAsyncMock;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.*;

public class BrowseTreeTest {

    private BrowseTreeView view;
    private Presenter presenter;
    private TabManager tabManager;
    private RepositoryServiceAsyncMockImpl repositoryServiceAsyncMock;
    private CategoryServiceAsyncMockImpl categoryServiceAsyncMock;
    private IsTreeItem rootCategoryTreeItem;
    private IsTreeItem find;
    private IsTreeItem rootStatesTreeItem;
    private IsTreeItem incomingInboxTreeItem;
    private IsTreeItem inboxRecentEdited;
    private IsTreeItem inboxRecentViewed;
    private IsTreeItem rootTreeItem;

    @Before
    public void setUp() throws Exception {
        setUpServices();
        setUpView();
        setUpCapabilities();
        setUpPresenter();
        setUpTabManager();
    }

    private void setUpPresenter() {
        presenter = new BrowseTree(view, repositoryServiceAsyncMock, categoryServiceAsyncMock);
    }

    private void setUpTabManager() {
        tabManager = mock(TabManager.class);
        TabContainer.init(tabManager);
    }

    private void setUpServices() {
        repositoryServiceAsyncMock = new RepositoryServiceAsyncMockImpl();
        categoryServiceAsyncMock = new CategoryServiceAsyncMockImpl();
    }

    private void setUpView() {
        view = mock(BrowseTreeView.class);
        find = mock(IsTreeItem.class);
        rootTreeItem = mock(IsTreeItem.class);
        rootCategoryTreeItem = mock(IsTreeItem.class);
        rootStatesTreeItem = mock(IsTreeItem.class);
        incomingInboxTreeItem = mock(IsTreeItem.class);
        inboxRecentEdited = mock(IsTreeItem.class);
        inboxRecentViewed = mock(IsTreeItem.class);
        when(view.addFind()).thenReturn(find);
        when(view.addRootTreeItem()).thenReturn(rootTreeItem);
        when(view.addRootCategoryTreeItem()).thenReturn(rootCategoryTreeItem);
        when(view.addRootStateTreeItem()).thenReturn(rootStatesTreeItem);
        when(view.addInboxIncomingTreeItem()).thenReturn(incomingInboxTreeItem);
        when(view.addInboxRecentEditedTreeItem()).thenReturn(inboxRecentEdited);
        when(view.addInboxRecentViewedTreeItem()).thenReturn(inboxRecentViewed);
    }

    private void setUpStates(String... states) {
        repositoryServiceAsyncMock.setStates(states);
    }

    @Test
    public void testPresenterSet() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testFindSelection() throws Exception {
        presenter.onTreeItemSelection(find, "find");

        verify(tabManager).openFind();
    }

    @Test
    public void testInboxIncomingSelection() throws Exception {
        presenter.onTreeItemSelection(incomingInboxTreeItem, "title1");

        verify(tabManager).openInboxIncomingPagedTable(ExplorerNodeConfig.INCOMING_ID);
    }

    @Test
    public void testInboxRecentEditedSelection() throws Exception {
        presenter.onTreeItemSelection(inboxRecentEdited, "title2");

        verify(tabManager).openInboxPagedTable(ExplorerNodeConfig.RECENT_EDITED_ID);
    }

    @Test
    public void testInboxRecentViewedSelection() throws Exception {
        presenter.onTreeItemSelection(inboxRecentViewed, "title3");

        verify(tabManager).openInboxPagedTable(ExplorerNodeConfig.RECENT_VIEWED_ID);
    }

    @Test
    public void testOpenStateSelection() throws Exception {
        setUpStates("title1");
        categoryServiceAsyncMock.addCategorySelection("/");
        setUpCapabilities(Capability.SHOW_KNOWLEDGE_BASES_VIEW);

        IsTreeItem state = mock(IsTreeItem.class);
        when(view.addStateItem("title1")).thenReturn(state);

        setUpPresenter();

        presenter.onTreeItemOpen(rootTreeItem);
        presenter.onTreeItemSelection(state, "title1");

        verify(tabManager).openStatePagedTable("title1");
    }

    @Test
    public void testLoadRoot() throws Exception {
        verify(view).addRootTreeItem();
        verify(view).addFind();
        verify(view, never()).addRootStateTreeItem();
        verify(view).addRootCategoryTreeItem();
    }

    @Test
    public void testShowRulesMenuHasCapability() throws Exception {

        setUpCapabilities(Capability.SHOW_CREATE_NEW_ASSET);

        BrowseTreeView treeView = mock(BrowseTreeView.class);
        presenter = new BrowseTree(treeView, repositoryServiceAsyncMock, categoryServiceAsyncMock);

        verify(treeView).showMenu();
    }

    @Test
    public void testShowRulesMenuNoCapability() throws Exception {
        verify(view, never()).showMenu();
    }

    @Test
    public void testLoadRootWithCapabilitiesForStateItems() throws Exception {

        setUpCapabilities(Capability.SHOW_KNOWLEDGE_BASES_VIEW);

        BrowseTreeView treeView = mock(BrowseTreeView.class);
        presenter = new BrowseTree(treeView, repositoryServiceAsyncMock, categoryServiceAsyncMock);

        verify(treeView).addRootTreeItem();
        verify(treeView).addFind();
        verify(treeView).addRootStateTreeItem();
        verify(treeView).addRootCategoryTreeItem();
    }

    @Test
    public void testCategorySelection() throws Exception {
        categoryServiceAsyncMock.addCategorySelection("/", "categoryName1", "categoryName2", "categoryName3");

        IsTreeItem category1 = mock(IsTreeItem.class);
        when(view.addTreeItem(rootCategoryTreeItem, "categoryName1")).thenReturn(category1);

        presenter.onTreeItemOpen(rootTreeItem);
        presenter.onTreeItemOpen(rootCategoryTreeItem);
        presenter.onTreeItemSelection(category1, "categoryName1");

        verify(tabManager).openCategory("categoryName1", "/categoryName1");
    }

    @Test
    public void testLoadFirstLevelCategories() throws Exception {
        categoryServiceAsyncMock.addCategorySelection("/", "category1", "category2", "category3");

        presenter.onTreeItemOpen(rootTreeItem);
        presenter.onTreeItemOpen(rootCategoryTreeItem);

        verify(view).removeCategories(rootCategoryTreeItem);
        verifyAddedTreeItemsToCategory(rootCategoryTreeItem, "category1", "category2", "category3");
    }

    @Test
    public void testLoadThirdLevelCategory() throws Exception {
        categoryServiceAsyncMock.addCategorySelection("/", "one", "two", "three");
        categoryServiceAsyncMock.addCategorySelection("/one", "t1", "t2");
        categoryServiceAsyncMock.addCategorySelection("/one/t2", "a", "b", "c", "d", "e", "f");

        IsTreeItem oneTreeItem = mock(IsTreeItem.class);
        when(view.addTreeItem(rootCategoryTreeItem, "one")).thenReturn(oneTreeItem);
        IsTreeItem t2TreeItem = mock(IsTreeItem.class);
        when(view.addTreeItem(oneTreeItem, "t2")).thenReturn(t2TreeItem);

        setUpChildren(rootCategoryTreeItem, oneTreeItem);
        setUpChildren(oneTreeItem, t2TreeItem);
        setUpChildren(t2TreeItem);

        presenter.onTreeItemOpen(rootTreeItem);
        presenter.onTreeItemOpen(rootCategoryTreeItem);
        presenter.onTreeItemOpen(oneTreeItem);
        presenter.onTreeItemOpen(t2TreeItem);

        verify(view).removeCategories(t2TreeItem);
        verifyAddedTreeItemsToCategory(t2TreeItem, "a", "b", "c", "d", "e", "f");
    }

    private void setUpChildren(IsTreeItem parent, IsTreeItem... children) {
        ArrayList rootChildList = new ArrayList();
        rootChildList.addAll(Arrays.asList(children));
        when(view.getChildren(parent)).thenReturn(rootChildList);
    }

    private void setUpCapabilities(Capability... list) {
        List<Capability> capabilities = new ArrayList<Capability>();
        capabilities.addAll(Arrays.asList(list));
        ConfigurationsLoaderMock.loadUserCapabilities(capabilities);
    }

    private void verifyAddedTreeItemsToCategory(IsTreeItem parent, String... categories) {
        for (String category : categories) {
            verify(view).addTreeItem(parent, category);
        }
    }

    class RepositoryServiceAsyncMockImpl extends RepositoryServiceAsyncMock {
        private String[] states;

        public void setStates(String... states) {
            this.states = states;
        }

        public void listStates(AsyncCallback<String[]> cb) {
            cb.onSuccess(states);
        }
    }

    class CategoryServiceAsyncMockImpl extends CategoryServiceAsyncMock {
        private Map<String, String[]> categories = new HashMap<String, String[]>();

        public void addCategorySelection(String path, String... categories) {
            this.categories.put(path, categories);
        }

        public void loadChildCategories(String path, AsyncCallback<String[]> cb) {
            String[] subCategories = categories.get(path);
            cb.onSuccess(subCategories);
        }
    }

}
