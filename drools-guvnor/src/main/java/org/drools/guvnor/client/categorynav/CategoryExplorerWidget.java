package org.drools.guvnor.client.categorynav;
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



import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a rule/resource navigator that uses the server side categories to
 * navigate the repository.
 * Uses the the {@link com.google.gwt.user.client.ui.Tree} widget.
 */
public class CategoryExplorerWidget extends Composite
    implements
    TreeListener {

    private Tree                   navTreeWidget = new Tree();
    private VerticalPanel          panel         = new VerticalPanel();
    private RepositoryServiceAsync service       = RepositoryServiceFactory.getService();
    private CategorySelectHandler  categorySelectHandler;
    private String                 selectedPath;
    private Panel                  emptyCategories;


    public void setTreeSize(String width) {
        navTreeWidget.setWidth( width );
    }



    /**
     * Create a new cat explorer.
     * @param handler
     */
    public CategoryExplorerWidget(CategorySelectHandler handler) {
        panel.add(navTreeWidget);

        this.categorySelectHandler = handler;
        loadInitialTree();

        initWidget( panel );
        navTreeWidget.addTreeListener( this );
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

        if (this.emptyCategories == null) {
                AbsolutePanel p = new AbsolutePanel();
                 p.add( new HTML("No categories created yet. Add some categories from the administration screen.") );
                 Button b = new Button("Refresh");
                 b.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        refresh();
                    }
                 });
                 p.add( b );
                 p.setStyleName( "small-Text" );
                 this.emptyCategories = p;
                 this.panel.add( this.emptyCategories );
        }
        emptyCategories.setVisible( true );

    }

    /** This will refresh the tree and restore it back to the original state */
    private void loadInitialTree() {
        navTreeWidget.addItem( "Please wait..." );
        DeferredCommand.addCommand(new Command() {
			public void execute() {
		        service.loadChildCategories( "/",
		                                     new GenericCallback() {


		                                         public void onSuccess(Object result) {
		                                             selectedPath = null;
		                                             navTreeWidget.removeItems();

		                                             TreeItem root = new TreeItem();
		                                             root.setHTML("<img src=\"images/desc.gif\"/>");
		                                             navTreeWidget.addItem(root);

		                                             String[] categories = (String[]) result;

		                                             if (categories.length == 0) {
		                                                 showEmptyTree();
		                                             } else {
		                                                 hideEmptyTree();
		                                             }
		                                             for ( int i = 0; i < categories.length; i++ ) {
		                                                 TreeItem it = new TreeItem();
		                                                 it.setHTML( "<img src=\"images/category_small.gif\"/>" + h(categories[i]) );
		                                                 it.setUserObject( categories[i] );
		                                                 it.addItem( new PendingItem() );
		                                                 root.addItem( it );
		                                             }

		                                             root.setState(true);
		                                         }



		                                     } );
			}}
        );

    }

    private String h(String cat) {
        return cat.replace("<", "&lt;").replace(">", "&gt;");
    }


    private void hideEmptyTree() {
        if (this.emptyCategories != null) {
            this.emptyCategories.setVisible( false );
        }

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

        //item.setUserObject( new Boolean( true ) );

        service.loadChildCategories( selectedPath,
                                     new GenericCallback() {

                                         public void onSuccess(Object result) {
                                             TreeItem child = root.getChild( 0 );
                                             if ( child instanceof PendingItem ) {
                                                 // root.removeItem( child );
                                                 child.setVisible( false );
                                             }
                                             String[] list = (String[]) result;
                                             for ( int i = 0; i < list.length; i++ ) {
                                                 TreeItem it = new TreeItem();
                                                 it.setHTML( "<img src=\"images/category_small.gif\"/>" + list[i] );
                                                 it.setUserObject( list[i] );
                                                 it.addItem( new PendingItem() );

                                                 root.addItem( it );
                                             }
                                         }

                                     } );

    }

    private boolean hasBeenLoaded(TreeItem item) {
        if (item.getChildCount() == 1 && item.getChild( 0 ) instanceof PendingItem) {
            return false;
        }
        return true;
    }

    private String getPath(TreeItem item) {
        String categoryPath = (String) item.getUserObject();
        if (categoryPath == null) return null;
        TreeItem parent = item.getParentItem();
        while ( parent.getUserObject() != null ) {
            categoryPath = ((String)parent.getUserObject()) + "/" + categoryPath;
            System.out.println("categoryPath: " + categoryPath);
            parent = parent.getParentItem();
        }
        return categoryPath;
    }

    private static class PendingItem extends TreeItem {
        public PendingItem() {
            super( "Please wait..." );

        }
    }

    public String getSelectedPath() {
        return this.selectedPath;
    }


	public boolean isSelected() {
		return this.selectedPath != null;
	}

}