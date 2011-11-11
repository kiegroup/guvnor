/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.widgets.categorynav;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is a rule/resource navigator that uses the server side categories to
 * navigate the repository. Uses the the
 * {@link com.google.gwt.user.client.ui.Tree} widget.
 */
public class CategoryExplorerWidget extends Composite
    implements
    SelectionHandler<TreeItem>,
    OpenHandler<TreeItem> {

    private static Images         images          = (Images) GWT.create( Images.class );
    private static Constants      constants       = ((Constants) GWT.create( Constants.class ));

    private Tree                  navTreeWidget   = new Tree();
    private VerticalPanel         panel           = new VerticalPanel();
    private CategoryServiceAsync  categoryService = RepositoryServiceFactory.getCategoryService();
    private CategorySelectHandler categorySelectHandler;
    private String                selectedPath;
    private Panel                 emptyCategories;

    public void setTreeSize(String width) {
        navTreeWidget.setWidth( width );
    }

    /**
     * Create a new cat explorer.
     * 
     * @param handler
     */
    public CategoryExplorerWidget(CategorySelectHandler handler) {
        panel.add( navTreeWidget );

        this.categorySelectHandler = handler;
        loadInitialTree();

        initWidget( panel );
        navTreeWidget.addSelectionHandler( this );
        navTreeWidget.addOpenHandler( this );
        this.setStyleName( "category-explorer-Tree" );
    }

    /**
     * This refreshes the view.
     */
    public void refresh() {
        navTreeWidget.removeItems();
        selectedPath = null;
        loadInitialTree();
    }

    public void showEmptyTree() {

        if ( this.emptyCategories == null ) {
            AbsolutePanel p = new AbsolutePanel();
            p.add( new HTML( constants.NoCategoriesCreatedYetTip() ) );
            Button b = new Button( constants.Refresh() );
            b.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    refresh();
                }
            } );
            p.add( b );
            p.setStyleName( "small-Text" ); //NON-NLS
            this.emptyCategories = p;
            this.panel.add( this.emptyCategories );
        }
        this.navTreeWidget.setVisible( false );
        emptyCategories.setVisible( true );

    }

    /** This will refresh the tree and restore it back to the original state */
    private void loadInitialTree() {
        navTreeWidget.addItem( constants.PleaseWait() );
        Scheduler scheduler = Scheduler.get();
        scheduler.scheduleDeferred( new Command() {
            public void execute() {
                categoryService.loadChildCategories( "/",
                                                     new GenericCallback<String[]>() {

                                                         public void onSuccess(String[] categories) {
                                                             selectedPath = null;
                                                             navTreeWidget.removeItems();

                                                             TreeItem root = new TreeItem();
                                                             root.setHTML( AbstractImagePrototype.create(images.desc()).getHTML() );
                                                             navTreeWidget.addItem( root );

                                                             if ( categories.length == 0 ) {
                                                                 showEmptyTree();
                                                             } else {
                                                                 hideEmptyTree();
                                                             }
                                                             for ( int i = 0; i < categories.length; i++ ) {
                                                                 TreeItem it = new TreeItem();
                                                                 it.setHTML( AbstractImagePrototype.create(images.categorySmall()).getHTML() + h( categories[i] ) );
                                                                 it.setUserObject( categories[i] );
                                                                 it.addItem( new PendingItem() );
                                                                 root.addItem( it );
                                                             }

                                                             root.setState( true );
                                                         }

                                                     } );
            }
        } );

    }

    private String h(String cat) {
        return cat.replace( "<",
                            "&lt;" ).replace( ">",
                                              "&gt;" );
    }

    private void hideEmptyTree() {
        if ( this.emptyCategories != null ) {
            this.emptyCategories.setVisible( false );
        }
        this.navTreeWidget.setVisible( true );
    }

    public void onSelection(SelectionEvent<TreeItem> event) {
        this.selectedPath = getPath( event.getSelectedItem() );
        this.categorySelectHandler.selected( selectedPath );
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        TreeItem item = event.getTarget();

        if ( hasBeenLoaded( item ) ) {
            return;
        }

        final TreeItem root = item;

        //walk back up to build a tree
        this.selectedPath = getPath( item );

        categoryService.loadChildCategories( selectedPath,
                                             new GenericCallback<String[]>() {

                                                 public void onSuccess(String[] list) {
                                                     TreeItem child = root.getChild( 0 );
                                                     if ( child instanceof PendingItem ) {
                                                         // root.removeItem( child );
                                                         child.setVisible( false );
                                                     }
                                                     for ( int i = 0; i < list.length; i++ ) {
                                                         TreeItem it = new TreeItem();
                                                         it.setHTML( AbstractImagePrototype.create(images.categorySmall()).getHTML() + h( list[i] ) );
                                                         it.setUserObject( list[i] );
                                                         it.addItem( new PendingItem() );

                                                         root.addItem( it );
                                                     }
                                                 }

                                             } );

    }

    private boolean hasBeenLoaded(TreeItem item) {
        if ( item.getChildCount() == 1 && item.getChild( 0 ) instanceof PendingItem ) {
            return false;
        }
        return true;
    }

    private String getPath(TreeItem item) {
        String categoryPath = (String) item.getUserObject();
        if ( categoryPath == null ) return null;
        TreeItem parent = item.getParentItem();
        while ( parent.getUserObject() != null ) {
            categoryPath = ((String) parent.getUserObject()) + "/" + categoryPath;
            parent = parent.getParentItem();
        }
        return categoryPath;
    }

    private static class PendingItem extends TreeItem {
        public PendingItem() {
            super( constants.PleaseWait() );

        }
    }

    public String getSelectedPath() {
        return this.selectedPath;
    }

    public boolean isSelected() {
        return this.selectedPath != null;
    }

}