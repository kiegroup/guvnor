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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.Util;

import java.util.ArrayList;
import java.util.Collection;

import static org.drools.guvnor.client.resources.GuvnorImages.INSTANCE;

public class BrowseTreeViewImpl extends Composite implements BrowseTreeView {

    private static Constants constants = GWT.create( Constants.class );
    private TreeItem root;
    private TreeItem states;
    private TreeItem inbox;
    private ClientFactory clientFactory;

    interface BrowseTreeViewImplBinder
            extends
            UiBinder<Widget, BrowseTreeViewImpl> {
    }

    private static BrowseTreeViewImplBinder uiBinder = GWT.create( BrowseTreeViewImplBinder.class );

    private Presenter presenter;

    @UiField
    DockLayoutPanel layout;

    @UiField
    Tree tree;

    public BrowseTreeViewImpl(final ClientFactory clientFactory) {
        initWidget( uiBinder.createAndBindUi( this ) );

        this.clientFactory = clientFactory;

        addSelectionHandler();
        addOpenHandler();
        inbox = tree.addItem( Util.getHeader(INSTANCE.InboxNoAlt(), constants.Inbox() ) );
    }

    private void addSelectionHandler() {
        tree.addSelectionHandler( new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> treeItemSelectionEvent) {
                TreeItem selectedItem = treeItemSelectionEvent.getSelectedItem();
                presenter.onTreeItemSelection( selectedItem, selectedItem.getText() );
            }
        } );
    }

    private void addOpenHandler() {
        tree.addOpenHandler( new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> treeItemOpenEvent) {
                presenter.onTreeItemOpen( treeItemOpenEvent.getTarget() );
            }
        } );
    }

    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    public IsTreeItem addRootTreeItem() {
        root = tree.addItem( Util.getHeader( INSTANCE.RuleAssetNoAlt(), constants.AssetsTreeView() ) );
        return root;
    }

    public IsTreeItem addInboxIncomingTreeItem() {
        return inbox.addItem( Util.getHeader( INSTANCE.CategorySmallNoAlt(), getInboxIncomingName() ) );
    }

    public IsTreeItem addInboxRecentEditedTreeItem() {
        return inbox.addItem( Util.getHeader( INSTANCE.CategorySmallNoAlt(), getInboxRecentEditedName() ) );
    }

    public IsTreeItem addInboxRecentViewedTreeItem() {
        return inbox.addItem( Util.getHeader( INSTANCE.CategorySmallNoAlt(), getInboxRecentViewedName() ) );
    }
    
    public String getInboxIncomingName() {
        return constants.IncomingChanges();
    }

    public String getInboxRecentEditedName() {
        return constants.RecentlyEdited();
    }

    public String getInboxRecentViewedName() {
        return constants.RecentlyOpened();
    }

    public Collection<IsTreeItem> getChildren(final IsTreeItem openedItem) {
        Collection<IsTreeItem> children = new ArrayList<IsTreeItem>();

        TreeItem parent = openedItem.asTreeItem();
        for (int i = 0; i < parent.getChildCount(); i++) {
            children.add( parent.getChild( i ) );
        }

        return children;
    }
    
    public IsTreeItem addFind() {
        return root.addItem( Util.getHeader( INSTANCE.FindNoAlt(), constants.Find() ) );
    }

    public IsTreeItem addRootStateTreeItem() {
        states = root.addItem( Util.getHeader( INSTANCE.StatusSmallNoAlt(), constants.ByStatus() ) );
        return states;
    }

    public IsTreeItem addRootCategoryTreeItem() {
        return root.addItem( Util.getHeader( INSTANCE.ChartOrganisationNoAlt(), constants.ByCategory() ) );
    }

    public IsTreeItem addTreeItem(final IsTreeItem parent, final String name) {
        return parent.asTreeItem().addItem( name );
    }

    public void removeStates() {
        states.removeItems();
    }

    public IsTreeItem addStateItem(final String state) {
        return states.addItem( state );
    }

    public void removeCategories(final IsTreeItem treeItem) {
        treeItem.asTreeItem().removeItems();
    }
    
}
