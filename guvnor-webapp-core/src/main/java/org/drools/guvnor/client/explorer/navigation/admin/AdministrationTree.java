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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilderOld;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.ImagesCore;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

public class AdministrationTree extends NavigationItemBuilderOld {

    private final PlaceManager placeManager;

    public AdministrationTree(PlaceManager placeManager,
                              Identity identity) {
        super(identity);
        this.placeManager = placeManager;
        mainTree.addSelectionHandler(this);
    }

    public MenuBar createMenu() {
        return null;
    }

    public Tree createTree() {
        return new AdminTree(itemWidgets);
    }

    public String getName() {
        return ConstantsCore.INSTANCE.Administration();
    }

    public ImageResource getImage() {
        return ImagesCore.INSTANCE.rules();
    }

    public IsWidget createContent() {
        return this;
    }

    public void refreshTree() {
        //TODO: Generated code
    }

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();

        placeManager.goTo(itemWidgets.get(item));
    }
}
