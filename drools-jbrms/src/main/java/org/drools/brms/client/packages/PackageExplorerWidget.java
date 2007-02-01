package org.drools.brms.client.packages;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Contains the explorer to view (and lazy load) the packages in a repository.
 * 
 * @author Michael Neale
 */
public class PackageExplorerWidget extends Composite {

    private final Tree exTree;
    private final FlexTable layout;
    private final TreeListener treeListener;
    
    
    public PackageExplorerWidget() {
        
        exTree = new Tree();
        layout = new FlexTable();
        
        treeListener = new TreeListener() {

            public void onTreeItemSelected(TreeItem selected) {
                Command selectEvent = (Command) selected.getUserObject();
                selectEvent.execute();
            }

            public void onTreeItemStateChanged(TreeItem arg0) {
                //ignore                
            }
            
        };
        
        exTree.addTreeListener( treeListener );
        
        refreshTreeView( );
        
        Image newPackage = new Image("images/new_package.gif");
        newPackage.setTitle( "Create a new package" );
        newPackage.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showNewPackage(w);                
            }            
        });
        
        layout.setWidget( 1, 0, newPackage );  
        
        layout.getCellFormatter().setStyleName( 1, 0, "new-asset-Icons" );
        layout.getCellFormatter().setAlignment( 1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        
        initWidget( layout );
    }




    private void refreshTreeView() {
        
        layout.setWidget( 0, 0, new Label("Please wait...") );
        
        RepositoryServiceFactory.getService().listRulePackages( new GenericCallback() {

            public void onSuccess(Object data) {
                String[] packages = (String[]) data;
                
                exTree.clear();
                for ( int i = 0; i < packages.length; i++ ) {
                    addPackage( packages[i] );
                }
                
                layout.setWidget( 0, 0, exTree );
                FlexCellFormatter formatter = layout.getFlexCellFormatter();
                formatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
                layout.getFlexCellFormatter().setRowSpan( 0, 1, 2 );
                layout.getFlexCellFormatter().setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP );
                
            }
            
        });
        

    }

    
    /**
     * Pops up a new package wizard, and creates a new package should
     * sir decide to create said package. Nice package sir.
     */
    private void showNewPackage(Widget w) {
        final FormStylePopup pop = new FormStylePopup("images/new_wiz.gif", "Create a new package");
        final TextBox nameBox = new TextBox();
        nameBox.setTitle( "The name of the package. Avoid spaces, use underscore instead." );
        
        pop.addAttribute( "Package name", nameBox );
        final TextArea descBox = new TextArea();
        pop.addAttribute( "Description", descBox );
        
        Button create = new Button("Create package");
        create.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                createPackageAction(nameBox.getText(), descBox.getText());  
                pop.hide();
            }

        
        });
        
        
        pop.addAttribute( "", create );
        
        pop.setStyleName( "ks-popups-Popup" );
        
        pop.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() - 100 );
        pop.show();
    }

    private void createPackageAction(final String name, final String descr) {
        LoadingPopup.showMessage( "Creating package - please wait..." );
        RepositoryServiceFactory.getService().createPackage( name, descr, new GenericCallback() {
            public void onSuccess(Object data) {
                LoadingPopup.close();
                refreshTreeView();
            }
        });
    }        
    



    /**
     * Add a package to the tree.
     * @param name
     */
    private void addPackage(final String name) {
        
        TreeItem pkg = makeItem(name, "images/package.gif");
        
        pkg.setUserObject( new Command() {
            public void execute() {
                loadPackageConfig(name);
            }
        });
        
        pkg.addItem( makeItem("Business rules", "images/rule_asset.gif") );
        pkg.addItem( makeItem("Technical rules", "images/technical_rule_assets.gif") );
        pkg.addItem( makeItem("Functions", "images/function_assets.gif") );
        pkg.addItem( makeItem("Model", "images/model_asset.gif") );

        
        exTree.addItem( pkg );

    }

    /**
     * Load up the package config data and display it.
     */
    private void loadPackageConfig(String name) {
        RepositoryServiceFactory.getService().loadPackage( name, new GenericCallback() {

            public void onSuccess(Object data) {
                PackageConfigData conf = (PackageConfigData) data;
                PackageEditor ed = new PackageEditor(conf);
                ed.setWidth( "100%" );
                ed.setHeight( "100%" );
                layout.setWidget( 0, 1, ed );
                layout.setWidget( 0, 1, ed );                
            }            
        });
        
    }




    private TreeItem makeItem(String name, String icon) {
        TreeItem item = new TreeItem();
        item.setHTML( "<img src=\""+ icon + "\">" + name + "</a>" );
        return item;
    }
    
}
