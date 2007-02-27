package org.drools.brms.client.categorynav;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;

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

    public void setTreeSize(String width) {
        navTreeWidget.setWidth( width );
    }



    /**
     * Create a new cat explorer.
     * @param handler
     */
    public CategoryExplorerWidget(CategorySelectHandler handler) {
        panel.add( navTreeWidget );


        this.categorySelectHandler = handler;
        loadInitialTree();

        initWidget( panel );
        navTreeWidget.addTreeListener( this );
    }

    /**
     * This refreshes the view.
     */
    public void refresh() {
        navTreeWidget.removeItems();
        loadInitialTree();
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
                                             boolean empty = false;
                                             if (categories.length == 0) {
                                                 empty = true;
                                                 navTreeWidget.addItem( "No categories created yet. Add some categories from the administration screen." );
                                             }
                                             for ( int i = 0; i < categories.length; i++ ) {
                                                 TreeItem it = new TreeItem();
                                                 it.setHTML( "<img src=\"images/category_small.gif\"/>" + categories[i] );
                                                 it.setUserObject( categories[i] );
                                                 it.addItem( new PendingItem() );
                                                 navTreeWidget.addItem( it );
                                             }
                                             if (!empty) navTreeWidget.setSelectedItem( navTreeWidget.getItem( 0 ) );

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

        //item.setUserObject( new Boolean( true ) );

        service.loadChildCategories( selectedPath,
                                     new AsyncCallback() {

                                         public void onFailure(Throwable caught) {
                                             ErrorPopup.showMessage( "Unable to load categories for [" + selectedPath + "]" );
                                         }

                                         public void onSuccess(Object result) {
                                             TreeItem child = root.getChild( 0 );
                                             if ( child instanceof PendingItem ) {
                                                 root.removeItem( child );
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
        TreeItem parent = item.getParentItem();
        while ( parent != null ) {
            categoryPath = ((String)parent.getUserObject()) + "/" + categoryPath;
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
