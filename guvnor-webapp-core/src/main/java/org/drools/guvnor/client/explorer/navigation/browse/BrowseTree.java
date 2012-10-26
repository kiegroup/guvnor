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

import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeView.Presenter;
import org.drools.guvnor.shared.security.AppRoles;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import java.util.*;

public class BrowseTree implements Presenter {

    private final BrowseTreeView view;
    private final ClientFactory clientFactory;
    private final Map<IsTreeItem, String> categories = new HashMap<IsTreeItem, String>();
    private final List<IsTreeItem> states = new ArrayList<IsTreeItem>();
    private IsTreeItem incomingInboxTreeItem;
    private IsTreeItem findRootTreeItem;
    private IsTreeItem inboxRecentlyEditedTreeItem;
    private IsTreeItem inboxRecentlyViewedTreeItem;
    private IsTreeItem root;
    private IsTreeItem categoriesRootItem;
    private final Identity identity;

    public BrowseTree(ClientFactory clientFactory,
                      Identity identity) {
        this.view = clientFactory.getNavigationViewFactory().getBrowseTreeView();
        this.clientFactory = clientFactory;
        this.identity = identity;
        this.view.setPresenter(this);

        root = this.view.addRootTreeItem();
        addInbox();
        findRootTreeItem = this.view.addFind();
        if (canShowStates()) {
            IsTreeItem statesRootTreeItem = this.view.addRootStateTreeItem();
        }
        addRootCategory();
    }

    private void addInbox() {
        incomingInboxTreeItem = this.view.addInboxIncomingTreeItem();
        inboxRecentlyEditedTreeItem = this.view.addInboxRecentEditedTreeItem();
        inboxRecentlyViewedTreeItem = this.view.addInboxRecentViewedTreeItem();
    }

    private boolean canShowStates() {
        return UserCapabilities.canSeeStatuses(identity) ;
    }

    private void addRootCategory() {
        categoriesRootItem = this.view.addRootCategoryTreeItem();
        categories.put(categoriesRootItem, "/");
    }

    private void addCategoryItem(String categoryName, IsTreeItem treeItem) {
        IsTreeItem subItem = view.addTreeItem(treeItem, categoryName);
        String path = getItemPath(categoryName, categories.get(treeItem));
        categories.put(subItem, path);
    }

    private String getItemPath(String categoryName, String parentItemPath) {
        String path;
        if (isParentRoot(parentItemPath)) {
            path = parentItemPath + categoryName;
        } else {
            path = parentItemPath + "/" + categoryName;
        }
        return path;
    }

    private boolean isParentRoot(String parentItemPath) {
        return parentItemPath.equals("/");
    }

    private void addSubStatesToTreeItem() {
        view.removeStates();
        clientFactory.getRepositoryService().listStates(new GenericCallback<String[]>() {
            public void onSuccess(String[] result) {
                for (String name : result) {
                    IsTreeItem item = view.addStateItem(name);
                    states.add(item);
                }
            }
        });
    }

    public BrowseTreeView getView() {
        return view;
    }

    public void onTreeItemSelection(IsTreeItem selectedItem, String title) {
        if (states.contains(selectedItem)) {
            clientFactory.getPlaceManager().goTo(new DefaultPlaceRequest("stateScreen").addParameter("state", title));
        } else if (categories.containsKey(selectedItem)) {
            clientFactory.getPlaceManager().goTo(new DefaultPlaceRequest("categoryScreen").addParameter("category", categories.get(selectedItem)));
        } else if (selectedItem.equals(incomingInboxTreeItem)) {
            goTo(new InboxPlace(ExplorerNodeConfig.INCOMING_ID));
        } else if (selectedItem.equals(inboxRecentlyEditedTreeItem)) {
            goTo(new InboxPlace(ExplorerNodeConfig.RECENT_EDITED_ID));
        } else if (selectedItem.equals(inboxRecentlyViewedTreeItem)) {
            goTo(new InboxPlace(ExplorerNodeConfig.RECENT_VIEWED_ID));
        } else if (selectedItem.equals(findRootTreeItem)) {
            clientFactory.getPlaceManager().goTo("search");
        }
    }

    private void goTo(PlaceRequest newPlace) {
        clientFactory.getPlaceManager().goTo(newPlace);
    }

    public void onTreeItemOpen(IsTreeItem openedItem) {
        if (root.equals(openedItem)) {
            if (canShowStates()) {
                addSubStatesToTreeItem();
            }
            view.removeCategories(categoriesRootItem);
            loadCategories(categoriesRootItem);
        } else if (categories.containsKey(openedItem)) {
            Collection<IsTreeItem> children = view.getChildren(openedItem);
            for (IsTreeItem child : children) {
                view.removeCategories(child);
                loadCategories(child);
            }
        }
    }

    private void loadCategories(final IsTreeItem treeItem) {
        String path = categories.get(treeItem);
        clientFactory.getCategoryService().loadChildCategories(path,
                new GenericCallback<String[]>() {
                    public void onSuccess(String[] result) {
                        for (String categoryName : result) {
                            addCategoryItem(categoryName, treeItem);
                        }
                    }
                });
    }

}
