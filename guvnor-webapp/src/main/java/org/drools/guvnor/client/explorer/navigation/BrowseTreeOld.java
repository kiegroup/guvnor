/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.explorer.navigation;

import java.util.Iterator;
import java.util.Map;

import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;

public class BrowseTreeOld extends NavigationItemBuilderOld
        implements
        OpenHandler<TreeItem> {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = (Images) GWT.create(Images.class);

    public BrowseTreeOld() {
        mainTree.setAnimationEnabled(true);
        ExplorerNodeConfig.setupBrowseTree(mainTree,
                itemWidgets);
        mainTree.addSelectionHandler(this);
        mainTree.addOpenHandler(this);
    }

    public MenuBar createMenu() {
        if (UserCapabilities.INSTANCE.hasCapability(Capability.SHOW_CREATE_NEW_ASSET)) {
            return RulesNewMenu.getMenu();
        } else {
            return null;
        }
    }

    public Tree createTree() {
        return new Tree();
    }

    public String getName() {
        return constants.Browse();
    }

    public ImageResource getImage() {
        return images.ruleAsset();
    }

    public IsWidget createContent() {
        return this;
    }

    public void refreshTree() {
        mainTree.clear();
        itemWidgets.clear();
        ExplorerNodeConfig.setupBrowseTree(mainTree,
                itemWidgets);
    }

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();
        String widgetID = itemWidgets.get(item);

        TabManager tabManager = TabContainer.getInstance();

        if (widgetID != null) {
            if (widgetID.equals(ExplorerNodeConfig.FIND_ID)) {
                tabManager.openFind();
            } else {
                String itemText = item.getText();

                if (showInbox(widgetID)) {
//                    tabManager.openInbox(itemText, widgetID);
                } else if (widgetID.startsWith(ExplorerNodeConfig.STATES_ID)) {
//                    tabManager.openState(itemText, widgetID);
                } else if (widgetID.startsWith(ExplorerNodeConfig.CATEGORY_ID)) {
//                    tabManager.openCategory(itemText);
                }
            }
        }
    }

    private boolean showInbox(String widgetID) {
        return widgetID.equals(ExplorerNodeConfig.INCOMING_ID)
                || widgetID.equals(ExplorerNodeConfig.RECENT_EDITED_ID)
                || widgetID.equals(ExplorerNodeConfig.RECENT_VIEWED_ID);
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        open(event.getTarget());
    }

    private void open(IsTreeItem node) {
        if (ExplorerNodeConfig.STATES_ROOT_ID.equals(itemWidgets.get(node))) {
            removeStateIDs(itemWidgets);
            node.asTreeItem().removeItems();
            ExplorerNodeConfig.setupStatesStructure(node.asTreeItem(),
                    itemWidgets);
        } else if (ExplorerNodeConfig.CATEGORY_ROOT_ID.equals(itemWidgets.get(node))) {
            removeCategoryIDs(itemWidgets);
            node.asTreeItem().removeItems();
            ExplorerNodeConfig.setupCategoriesStructure(node.asTreeItem(),
                    itemWidgets);
        }
    }

    private void removeStateIDs(Map<TreeItem, String> itemWidgets) {
        for (Iterator<Map.Entry<TreeItem, String>> it = itemWidgets.entrySet().iterator(); it.hasNext();) {
            Map.Entry<TreeItem, String> entry = it.next();
            TreeItem item = entry.getKey();
            String id = entry.getValue();
            if (id.startsWith(ExplorerNodeConfig.STATES_ID + "-")) {
                it.remove();
            }
        }
    }

    private void removeCategoryIDs(Map<TreeItem, String> itemWidgets) {
        for (Iterator<Map.Entry<TreeItem, String>> it = itemWidgets.entrySet().iterator(); it.hasNext();) {
            Map.Entry<TreeItem, String> entry = it.next();
            TreeItem item = entry.getKey();
            String id = entry.getValue();
            if (id.startsWith(ExplorerNodeConfig.CATEGORY_ID + "-")) {
                it.remove();
            }
        }
    }

}
