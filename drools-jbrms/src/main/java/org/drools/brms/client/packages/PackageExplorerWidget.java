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
    private AssetItemListViewer listView;
    private EditItemEvent editEvent;
    private String uuid;
    
    /**
     * This is for the generic and re-useable package explorer.
     */    
    public PackageExplorerWidget(EditItemEvent edit) {
        this(edit, null, false);
    }
    
    /**
     * This will open an explorer locked to one specific package.
     * @param edit The edit event (action) when the user wants to open an item.
     * @param uuid The package to lock this to.
     */
    public PackageExplorerWidget(EditItemEvent edit, String uuid, boolean readonly) {
        
        this.editEvent = edit;
        this.uuid = uuid;
        exTree = new Tree();
        layout = new FlexTable();
        
        treeListener = new TreeListener() {

            public void onTreeItemSelected(TreeItem selected) {
                PackageTreeItem event = (PackageTreeItem) selected.getUserObject();
                
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

        if (!readonly) {
            //only care about new buttons if its not read only
            FlexTable buttons = new FlexTable();
            buttons.getCellFormatter().setStyleName( 0, 0, "new-asset-Icons" );
            buttons.getCellFormatter().setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
    
            buttons.setWidget( 0, 0, getNewWizardButtons() );
            left.add( buttons );
            buttons.setWidth( "100%" );
        }
        left.add( exTree );
        
        layout.setWidget( 0, 0, left );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
        layout.getFlexCellFormatter().setRowSpan( 0, 1, 2 );
        layout.getFlexCellFormatter().setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP );

        refreshTreeView( );
        
        TreeItem item  = exTree.getItem( 0 );
        if (item != null) exTree.setSelectedItem( item );

        layout.setWidget( 0, 1, new HTML("<i>Please choose a package to edit, explore, or create a new package.</i>") );
        layout.getFlexCellFormatter().setWidth( 0, 0, "25%" );
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
        newWizards.add( uploadModel );
        newWizards.add( newRule );
        newWizards.add( newFunction );
        newWizards.add( newDSL );
        return newWizards;

    }

    public void refreshTreeView() {
        
        if (this.uuid == null) {
            LoadingPopup.showMessage( "Loading list of packages ..." );
            RepositoryServiceFactory.getService().listPackages( new GenericCallback() {
                public void onSuccess(Object data) {
                    PackageConfigData[] packages = (PackageConfigData[]) data;
                    exTree.clear();
                    for ( int i = 0; i < packages.length; i++ ) {
                        if (i == 0) {
                            addPackage( packages[i], true );
                        } else {
                            addPackage( packages[i], false );
                        }
                    }
                    LoadingPopup.close();
                }
            });
        } else {
            LoadingPopup.showMessage( "Loading package ..." );
            RepositoryServiceFactory.getService().loadPackageConfig( uuid, new GenericCallback() {
                public void onSuccess(Object data) {
                    PackageConfigData pack = (PackageConfigData) data;
                    exTree.clear();
                    addPackage(pack, true);

                    LoadingPopup.close();
                }
            });            
        }
        

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
    private void addPackage(final PackageConfigData conf, boolean preSelect) {
        
        TreeItem pkg = makeItem(conf.name, "images/package.gif", new PackageTreeItem(new Command() {
            public void execute() {
                loadPackageConfig(conf.uuid);
            }
        }));
        
        pkg.addItem( makeItem("Business rules", "images/rule_asset.gif", showListEvent(conf.uuid, AssetFormats.BUSINESS_RULE_FORMATS)) );
        pkg.addItem( makeItem("Technical rules", "images/technical_rule_assets.gif", showListEvent(conf.uuid, AssetFormats.TECHNICAL_RULE_FORMATS)) );
        pkg.addItem( makeItem("Functions", "images/function_assets.gif", showListEvent(conf.uuid, new String[] {AssetFormats.FUNCTION})) );
        pkg.addItem( makeItem("DSL", "images/dsl.gif", showListEvent(conf.uuid, new String[] {AssetFormats.DSL})) );
        pkg.addItem( makeItem("Model", "images/model_asset.gif", showListEvent(conf.uuid, new String[] {AssetFormats.MODEL}) ) );
        
        exTree.addItem( pkg );
        if (preSelect) {
            exTree.setSelectedItem( pkg, true );
        }
    }

    /**
     * This will create a "show list" event to be attached to the tree.
     */
    private PackageTreeItem showListEvent(final String uuid, final String[] format) {
        
        final GenericCallback cb = new GenericCallback() {
            public void onSuccess(Object data) {
                final TableDataResult table = (TableDataResult) data;
                listView.loadTableData( table );      
                listView.setWidth( "100%" );
                layout.setWidget( 0, 1, listView );
                layout.getFlexCellFormatter().setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
                LoadingPopup.close();
            }
        };
        
        return new PackageTreeItem(new Command() {
            public void execute() {
                RepositoryServiceFactory.getService().listAssets( uuid, format, 
                                                                          -1, -1, cb);                
            }            
        });
    }




    /**
     * Load up the package config data and display it.
     */
    private void loadPackageConfig(String uuid) {

        RepositoryServiceFactory.getService().loadPackageConfig( uuid, new GenericCallback() {

            public void onSuccess(Object data) {
                PackageConfigData conf = (PackageConfigData) data;
                PackageEditor ed = new PackageEditor(conf);
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
           
        public PackageTreeItem(Command com) {
            this.command = com;
            
        }
                
    }
    
}
