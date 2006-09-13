/*
 * Copyright 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.brms.client.categorynav;

import org.drools.brms.client.ErrorPopup;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This is a rule/resource navigator that uses the server side categories to 
 * navigate the repository.
 * Uses the the {@link com.google.gwt.user.client.ui.Tree} widget.
 */
public class RulesNavigatorTree
    extends Composite implements TreeListener
    {

     
    
    private Tree                   navTreeWidget = new Tree();
    private FlexTable              table         = new FlexTable();
    private RepositoryServiceAsync service       = RepositoryServiceFactory.getService();
    private CategorySelectHandler  categorySelectHandler;
    private String selectedPath;

    public void setTreeSize(String width) {
        navTreeWidget.setWidth( width );
    }

    public RulesNavigatorTree(CategorySelectHandler handler) {
        table.setWidget( 0, 0, navTreeWidget );
        
        FlexCellFormatter formatter = table.getFlexCellFormatter();
        formatter.setColSpan( 0, 0, 2 );
        
        Image refresh = new Image("images/refresh.gif");
        refresh.setTitle( "Refresh categories" );        
        refresh.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                navTreeWidget.removeItems();
                loadInitialTree();
            }            
        });
                
        Image newCat = new Image("images/new.gif");
        newCat.setTitle( "Create a new category" );
        newCat.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                CategoryEditor newCat = new CategoryEditor(selectedPath);
                newCat.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() - 10  );
                newCat.show();
            }            
        });
        
        
        table.setWidget( 1, 0, newCat);
        table.setWidget( 1, 1, refresh );
        formatter.setAlignment( 1, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE );
        formatter.setAlignment( 1, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE );
        
        this.categorySelectHandler = handler;
        loadInitialTree();
        
        initWidget( table );
        navTreeWidget.addTreeListener( this );
    }

    /** This will refresh the tree and restore it back to the original state */
    private void loadInitialTree() {
        navTreeWidget.addItem( "Please wait..." );
        service.loadChildCategories( "/",
                                     new AsyncCallback() {

                                         public void onFailure(Throwable caught) {                                             
                                             ErrorPopup.showMessage( "A server error occurred loading categories." );
                                             navTreeWidget.removeItems();
                                             navTreeWidget.addItem( "Unable to load categories." );
                                         }

                                         public void onSuccess(Object result) {
                                             selectedPath = null;
                                             navTreeWidget.removeItems();
                                             String[] categories = (String[]) result;
                                             for ( int i = 0; i < categories.length; i++ ) {
                                                 navTreeWidget.addItem( categories[i] ).addItem( new PendingItem() );
                                             }

                                         }

                                     } );

        
    }
    
    

    public void onShow() {
        //move along... these are not the droids you're looking for...
    }

    public void onTreeItemSelected(TreeItem item) {
        this.selectedPath = getPath( item );        
        this.categorySelectHandler.selected( selectedPath );
    }
    

    public void onTreeItemStateChanged(TreeItem item) {

        if ( hasBeenLoaded( item ) ) {
            return;
        }

        final TreeItem root = item;

        //walk back up to build a tree
        this.selectedPath = getPath( item );

        item.setUserObject( new Boolean( true ) );

        service.loadChildCategories( selectedPath,
                                     new AsyncCallback() {

                                         public void onFailure(Throwable caught) {
                                             ErrorPopup.showMessage( "Unable to load categories for [" + selectedPath + "]");            
                                         }

                                         public void onSuccess(Object result) {
                                             TreeItem child = root.getChild( 0 );
                                             if ( child instanceof PendingItem ) {
                                                 root.removeItem( child );
                                             }
                                             String[] list = (String[]) result;
                                             for ( int i = 0; i < list.length; i++ ) {
                                                 root.addItem( list[i] ).addItem( new PendingItem() );
                                             }
                                         }

                                     } );

    }

    private boolean hasBeenLoaded(TreeItem item) {
        if ( item.getUserObject() == null ) return false;
        return (((Boolean) item.getUserObject()).booleanValue());
    }

    private String getPath(TreeItem item) {
        String categoryPath = item.getText();
        TreeItem parent = item.getParentItem();
        while ( parent != null ) {
            categoryPath = parent.getText() + "/" + categoryPath;
            parent = parent.getParentItem();
        }
        return categoryPath;
    }

    private static class PendingItem extends TreeItem {
        public PendingItem() {
            super( "Please wait..." );
        }
    }

}
