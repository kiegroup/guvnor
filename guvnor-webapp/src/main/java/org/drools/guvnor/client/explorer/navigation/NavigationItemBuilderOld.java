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

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;
import org.drools.guvnor.client.rulelist.OpenItemCommand;

import java.util.HashMap;
import java.util.Map;

public abstract class NavigationItemBuilderOld extends Composite
        implements
        SelectionHandler<TreeItem>, NavigationItem {

    protected final Tree mainTree;

    protected Map<TreeItem, String> itemWidgets = new HashMap<TreeItem, String>();

    public NavigationItemBuilderOld() {

        mainTree = createTree();
        mainTree.setStyleName( "guvnor-Tree" );

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel( Style.Unit.EM );

        MenuBar menu = createMenu();
        if ( menu != null ) {
            dockLayoutPanel.addNorth( menu, 2 );
        }

        dockLayoutPanel.add( new ScrollPanel( mainTree ) );

        /*

        TODO: wrap with DockLayoutPanel if has a menu, if not, just wrap with a ScrollPanel -Rikkola-
        TODO: check the permissions to show menus in the AbstractTree implementations -Rikkola-
        TODO: check the permissions to show abstract tree implementations in NavigationPanel, but keep the logic for this in the AT impl -Rikkola-
        TODO: This should be an interface -Rikkola-
        TODO: Deprecate TabOpener and create places -Rikkola-


         */


        initWidget( dockLayoutPanel );
    }

    public abstract Tree createTree();

}
