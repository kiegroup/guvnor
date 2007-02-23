package org.drools.brms.client.packages;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.ruleeditor.NewAssetWizard;
import org.drools.brms.client.rulelist.AssetItemListViewer;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
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
    private String currentPackage;
    private AssetItemListViewer listView;
    private EditItemEvent editEvent;
    
    
    public PackageExplorerWidget(EditItemEvent edit) {
        
        this.editEvent = edit;
        exTree = new Tree();
        layout = new FlexTable();
        
        treeListener = new TreeListener() {

            public void onTreeItemSelected(TreeItem selected) {
                PackageTreeItem event = (PackageTreeItem) selected.getUserObject();
                currentPackage = event.name;
                Command selectEvent = event.command;
                LoadingPopup.showMessage( "Please wait..." );
                DeferredCommand.add( selectEvent );                
            }

            public void onTreeItemStateChanged(TreeItem arg0) {
                //ignore                
            }
            
        };
        
        exTree.addTreeListener( treeListener );
        
        VerticalPanel left = new VerticalPanel();
        left.add( exTree );
        
        FlexTable buttons = new FlexTable();
        buttons.getCellFormatter().setStyleName( 0, 0, "new-asset-Icons" );
        buttons.getCellFormatter().setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );

        buttons.setWidget( 0, 0, getNewWizardButtons() );
        left.add( buttons );
        buttons.setWidth( "100%" );
        
        layout.setWidget( 0, 0, left );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
        layout.getFlexCellFormatter().setRowSpan( 0, 1, 2 );
        layout.getFlexCellFormatter().setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP );

        
        
        refreshTreeView( );
        
        TreeItem item  = exTree.getItem( 0 );
        if (item != null) exTree.setSelectedItem( item );

        layout.setWidget( 0, 1, new HTML("<i>Please choose a package to edit, explore, or create a new package.</i>") );
        
        layout.getFlexCellFormatter().setWidth( 0, 0, "20%" );
        
        layout.getFlexCellFormatter().setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        
        listView = new AssetItemListViewer(this.editEvent);
        
        initWidget( layout );
    }




    /** Return all the new wizard buttons. */
    private HorizontalPanel getNewWizardButtons() {
        HorizontalPanel newWizards = new HorizontalPanel();
        
        Image newPackage = new Image("images/new_package.gif");
        newPackage.setTitle( "Create a new package" );
        newPackage.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showNewPackage(w);                
            }            
        });
        
        Image uploadModel = new Image("images/model_asset.gif");
        uploadModel.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                int left = 70;
                int top = 100;
                  
                NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
                    public void open(String key) {                  
                        editEvent.open( key );                      
                    }
                }, false, AssetFormats.MODEL, "Create a new model archive");
                pop.setPopupPosition( left, top );
                
                pop.show();  
            }            
        });
        uploadModel.setTitle( "This creates a new model archive - models contain classes/types that rules use." );
        
        newWizards.add( uploadModel );
        
        Image newRule = new Image("images/new_rule.gif");
        newRule.setTitle( "Create new rule" );

        newRule.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
              int left = 70;
              int top = 100;
                
              NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
                  public void open(String key) {                  
                      editEvent.open( key );                      
                  }
              }, true, null, "Create a new rule asset");
              pop.setPopupPosition( left, top );
              
              pop.show();
            }
            
        });
        
        final Image newFunction = new Image("images/function_assets.gif");
        newFunction.setTitle( "Create a new function" );
        newFunction.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                int left = 70;
                int top = 100;
                  
                NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
                    public void open(String key) {                  
                        editEvent.open( key );                      
                    }
                }, false, AssetFormats.FUNCTION, "Create a new function");
                pop.setPopupPosition( left, top );
                
                pop.show();                
            }
        } );
        
        final Image newDSL = new Image("images/dsl.gif");
        newDSL.setTitle( "Create a new DSL (language configuration)" );
        newDSL.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                int left = 70;
                int top = 100;
                  
                NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
                    public void open(String key) {                  
                        editEvent.open( key );                      
                    }
                }, false, AssetFormats.DSL, "Create a new language configuration");
                pop.setPopupPosition( left, top );
                
                pop.show();                
            }
        } );        
        
        newWizards.add( newPackage );
        newWizards.add( newRule );
        newWizards.add( newFunction );
        newWizards.add( newDSL );
        return newWizards;

    }







    private void refreshTreeView() {
        
        LoadingPopup.showMessage( "Loading list of packages ..." );
        
        RepositoryServiceFactory.getService().listRulePackages( new GenericCallback() {

            public void onSuccess(Object data) {
                String[] packages = (String[]) data;
                
                exTree.clear();
                for ( int i = 0; i < packages.length; i++ ) {
                    addPackage( packages[i] );
                }
                
                LoadingPopup.close();
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
        
        TreeItem pkg = makeItem(name, "images/package.gif", new PackageTreeItem(name,new Command() {
            public void execute() {
                loadPackageConfig(name);
            }
        }));
        
        pkg.addItem( makeItem("Business rules", "images/rule_asset.gif", showListEvent(name, AssetFormats.BUSINESS_RULE_FORMATS)) );
        pkg.addItem( makeItem("Technical rules", "images/technical_rule_assets.gif", showListEvent(name, AssetFormats.TECHNICAL_RULE_FORMATS)) );
        pkg.addItem( makeItem("Functions", "images/function_assets.gif", showListEvent(name, new String[] {AssetFormats.FUNCTION})) );
        pkg.addItem( makeItem("DSL", "images/dsl.gif", showListEvent(name, new String[] {AssetFormats.DSL})) );
        pkg.addItem( makeItem("Model", "images/model_asset.gif", showListEvent(name, new String[] {AssetFormats.MODEL}) ) );
        
        exTree.addItem( pkg );
    }








    /**
     * This will create a "show list" event to be attached to the tree.
     */
    private PackageTreeItem showListEvent(final String name, final String[] format) {
        
        final GenericCallback cb = new GenericCallback() {
            public void onSuccess(Object data) {
                final TableDataResult table = (TableDataResult) data;
                listView.loadTableData( table );      
                listView.setWidth( "100%" );
                //listView.setHeight( "100%" );
                layout.setWidget( 0, 1, listView );
                layout.getFlexCellFormatter().setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
                LoadingPopup.close();
            }
        };
        
        return new PackageTreeItem(name, new Command() {
            public void execute() {
                RepositoryServiceFactory.getService().listAssetsByFormat( currentPackage, format, 
                                                                          -1, -1, cb);                
            }            
        });
    }




    /**
     * Load up the package config data and display it.
     */
    private void loadPackageConfig(String name) {
        this.currentPackage = name;
        RepositoryServiceFactory.getService().loadPackage( name, new GenericCallback() {

            public void onSuccess(Object data) {
                PackageConfigData conf = (PackageConfigData) data;
                PackageEditor ed = new PackageEditor(conf);
//                ed.setWidth( "100%" );
//                ed.setHeight( "100%" );
                layout.setWidget( 0, 1, ed );              
            }            
        });
        
    }




    private TreeItem makeItem(String name, String icon, Object command) {
        TreeItem item = new TreeItem();
        item.setHTML( "<img src=\""+ icon + "\">" + name + "</a>" );
        item.setUserObject( command );
        return item;
    }
    
    static class PackageTreeItem {
        Command command;
        String name;   
        public PackageTreeItem(String name, Command com) {
            this.command = com;
            this.name = name;
        }
                
    }
    
}
