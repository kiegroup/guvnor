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

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.TabOpener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class AdministrationTree extends AbstractTree {

    private static Constants constants = GWT.create( Constants.class );
    private static Images    images    = (Images) GWT.create( Images.class );

    public AdministrationTree() {
        this.name = constants.Administration();
        this.image = images.rules();

        //Add Selection listener
        mainTree.addSelectionHandler( this );
    }

    @Override
    protected Tree createTree() {
        return ExplorerNodeConfig.getAdminStructure( itemWidgets );
    }

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TabOpener tabOpener = TabOpener.getInstance();
        TreeItem item = event.getSelectedItem();
        String widgetID = itemWidgets.get( item );

        int id = Integer.parseInt( widgetID );
        tabOpener.openAdministrationSelection( id );
    }
}
