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

package org.drools.guvnor.client.explorer.navigation.admin;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilderOld;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class AdministrationTree extends NavigationItemBuilderOld {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);
    private final ClientFactory clientFactory;

    public AdministrationTree(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        mainTree.addSelectionHandler(this);
    }

    public MenuBar createMenu() {
        return null;
    }

    public Tree createTree() {
        return new AdminTree(itemWidgets);
    }

    public String getName() {
        return constants.Administration();
    }

    public ImageResource getImage() {
        return images.rules();
    }

    public IsWidget createContent() {
        return this;
    }

    public void refreshTree() {
        //TODO: Generated code -Rikkola-
    }

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();
        String widgetID = itemWidgets.get(item);

        int id = Integer.parseInt(widgetID);
        clientFactory.getPlaceController().goTo( new ManagerPlace(id) );
    }
}
