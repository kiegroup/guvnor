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

import java.util.Iterator;
import java.util.Map;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.util.TabOpener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class BrowseTree extends AbstractTree
    implements
    OpenHandler<TreeItem> {
    private static Constants constants = GWT.create( Constants.class );
    private static Images    images    = (Images) GWT.create( Images.class );

    /** Table set up for the inboxes */
    static {
        TableConfig conf = new TableConfig();
        conf.headers = new String[2];
        conf.headers[0] = constants.Name();
        conf.headers[1] = constants.Date();
        conf.headerTypes = new String[2];
        conf.headerTypes[0] = "class java.lang.String";
        conf.headerTypes[1] = "class java.util.Calendar";
        conf.rowsPerPage = 500;
        AssetItemGrid.registerTableConfig( conf,
                                           ExplorerNodeConfig.RECENT_EDITED_ID );
        AssetItemGrid.registerTableConfig( conf,
                                           ExplorerNodeConfig.RECENT_VIEWED_ID );

        conf = new TableConfig();
        conf.headers = new String[3];
        conf.headers[0] = constants.Name();
        conf.headers[1] = constants.Date();
        conf.headers[2] = constants.From();
        conf.headerTypes = new String[3];
        conf.headerTypes[0] = "class java.lang.String";
        conf.headerTypes[1] = "class java.util.Calendar";
        conf.headerTypes[2] = "class java.lang.String";
        conf.rowsPerPage = 500;

        AssetItemGrid.registerTableConfig( conf,
                                           ExplorerNodeConfig.INCOMING_ID );
    }

    public BrowseTree() {
        this.name = constants.Browse();
        this.image = images.ruleAsset();

        mainTree.setAnimationEnabled( true );
        ExplorerNodeConfig.setupBrowseTree( mainTree,
                                            itemWidgets );
        mainTree.addSelectionHandler( this );
        mainTree.addOpenHandler( (OpenHandler<TreeItem>) this );
    }

    @Override
    protected Tree createTree() {
        return new Tree();
    }

    public void refreshTree() {
        mainTree.clear();
        itemWidgets.clear();
        ExplorerNodeConfig.setupBrowseTree( mainTree,
                                            itemWidgets );
    }

    // Show the associated widget in the deck panel
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();
        String widgetID = itemWidgets.get( item );

        TabOpener opener = TabOpener.getInstance();

        if ( widgetID != null ) {
            if ( widgetID.equals( ExplorerNodeConfig.FIND_ID ) ) {
                opener.openFind();
            } else if ( widgetID.equals( ExplorerNodeConfig.INCOMING_ID ) || widgetID.equals( ExplorerNodeConfig.RECENT_EDITED_ID ) || widgetID.equals( ExplorerNodeConfig.RECENT_VIEWED_ID ) ) {
                opener.openInbox( item.getText(),
                                  widgetID );
            } else if ( widgetID.startsWith( ExplorerNodeConfig.STATES_ID ) ) {
                opener.openState( item.getText(),
                                  widgetID );
            } else if ( widgetID.startsWith( ExplorerNodeConfig.CATEGORY_ID ) ) {
                opener.openCategory( item.getText(),
                                     widgetID );
            }
        }
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem node = event.getTarget();
        if ( ExplorerNodeConfig.STATES_ROOT_ID.equals( itemWidgets.get( node ) ) ) {
            removeStateIDs( itemWidgets );
            node.removeItems();
            ExplorerNodeConfig.setupStatesStructure( node,
                                                     itemWidgets );
        } else if ( ExplorerNodeConfig.CATEGORY_ROOT_ID.equals( itemWidgets.get( node ) ) ) {
            removeCategoryIDs( itemWidgets );
            node.removeItems();
            ExplorerNodeConfig.setupCategoriesStructure( node,
                                                         itemWidgets );
        }
    }

    private void removeStateIDs(Map<TreeItem, String> itemWidgets) {
        Iterator<TreeItem> it = itemWidgets.keySet().iterator();
        while ( it.hasNext() ) {
            TreeItem item = (TreeItem) it.next();
            String id = itemWidgets.get( item );
            if ( id.startsWith( ExplorerNodeConfig.STATES_ID + "-" ) ) {
                it.remove();
            }
        }
    }

    private void removeCategoryIDs(Map<TreeItem, String> itemWidgets) {
        Iterator<TreeItem> it = itemWidgets.keySet().iterator();
        while ( it.hasNext() ) {
            TreeItem item = (TreeItem) it.next();
            String id = itemWidgets.get( item );
            if ( id.startsWith( ExplorerNodeConfig.CATEGORY_ID + "-" ) ) {
                it.remove();
            }
        }
    }

}
