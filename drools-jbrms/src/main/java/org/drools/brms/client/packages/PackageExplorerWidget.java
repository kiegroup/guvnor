package org.drools.brms.client.packages;

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

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.DirtyableFlexTable;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.ruleeditor.NewAssetWizard;
import org.drools.brms.client.rulelist.AssetItemListViewer;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Contains the explorer to view (and lazy load) the packages in a repository.
 * This uses the explorer type motif, with a tree on the left, and a list or
 * "panel" on the right.
 *
 * @author Michael Neale
 */
public class PackageExplorerWidget extends DirtyableComposite {


    private final Tree               exTree;
    private final DirtyableFlexTable layout;
    private final TreeListener       treeListener;
    private AssetItemListViewer      listView;
    private EditItemEvent            editEvent;
    private String                   uuid;
    private String                   currentlySelectedPackage;

    /**
     * This is for the generic and re-useable package explorer.
     */
    public PackageExplorerWidget(EditItemEvent edit) {
        this( edit,
              null,
              null );
    }

    /**
     * This will open an explorer locked to one specific package.
     *
     * @param edit
     *            The edit event (action) when the user wants to open an item.
     * @param uuid
     *            The package to lock this to.
     */
    public PackageExplorerWidget(EditItemEvent edit,
                                 String uuid,
                                 String snapshotName) {

        this.editEvent = edit;
        this.uuid = uuid;

        exTree = new Tree();
        layout = new DirtyableFlexTable();

        treeListener = new TreeListener() {

            public void onTreeItemSelected(TreeItem selected) {
                PackageTreeItem event = (PackageTreeItem) selected.getUserObject();

                Command selectEvent = event.command;
                LoadingPopup.showMessage( "Please wait..." );
                DeferredCommand.add( selectEvent );
            }

            public void onTreeItemStateChanged(TreeItem arg0) {
                // ignore
            }

        };

        exTree.addTreeListener( treeListener );
        VerticalPanel left = new VerticalPanel();

        if ( snapshotName == null ) {
            // only care about new buttons if its not read only
            FlexTable buttons = new FlexTable();
            buttons.getCellFormatter().setStyleName( 0,
                                                     0,
                                                     "new-asset-Icons" );
            buttons.getCellFormatter().setAlignment( 0,
                                                     0,
                                                     HasHorizontalAlignment.ALIGN_CENTER,
                                                     HasVerticalAlignment.ALIGN_MIDDLE );

            buttons.setWidget( 0,
                               0,
                               getNewWizardButtons() );
            left.add( buttons );
            buttons.setWidth( "100%" );
        }

        //		ScrollPanel scroll = new ScrollPanel();
        //		scroll.add(exTree);
        //		scroll.setHeight("100%");
        //		left.add(scroll);
        left.add( exTree );

        layout.setWidget( 0,
                          0,
                          left );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setVerticalAlignment( 0,
                                        0,
                                        HasVerticalAlignment.ALIGN_TOP );
        layout.getFlexCellFormatter().setRowSpan( 0,
                                                  1,
                                                  2 );
        layout.getFlexCellFormatter().setAlignment( 0,
                                                    1,
                                                    HasHorizontalAlignment.ALIGN_CENTER,
                                                    HasVerticalAlignment.ALIGN_TOP );

        refreshTreeView();

        TreeItem item = exTree.getItem( 0 );
        if ( item != null ) exTree.setSelectedItem( item );

        layout.setWidget( 0,
                          1,
                          new HTML( "<i>Please choose a package to edit, explore, or create a new package.</i>" ) );
        layout.getFlexCellFormatter().setWidth( 0,
                                                0,
                                                "25%" );
        layout.getFlexCellFormatter().setAlignment( 0,
                                                    1,
                                                    HasHorizontalAlignment.ALIGN_LEFT,
                                                    HasVerticalAlignment.ALIGN_TOP );
        listView = new AssetItemListViewer( this.editEvent, AssetItemListViewer.RULE_LIST_TABLE_ID );
        initWidget( layout );
    }

    /** Return all the new wizard buttons. */
    private HorizontalPanel getNewWizardButtons() {
        HorizontalPanel newWizards = new HorizontalPanel();

        Image newPackage = new Image( "images/new_package.gif" );
        newPackage.setTitle( "Create a new package" );
        newPackage.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showNewPackage( w );
            }
        } );

        Image uploadModel = new ImageButton( "images/model_asset.gif" );
        uploadModel.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                launchWizard( AssetFormats.MODEL,
                              "Create a new model archive" );
            }
        } );
        uploadModel.setTitle( "This creates a new model archive - models contain classes/types that rules use." );

        Image newRule = new ImageButton( "images/new_rule.gif" );
        newRule.setTitle( "Create new rule" );

        newRule.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                int left = 70;
                int top = 100;

                NewAssetWizard pop = new NewAssetWizard( new EditItemEvent() {
                                                             public void open(String key) {
                                                                 editEvent.open( key );
                                                             }
                                                         },
                                                         true,
                                                         null,
                                                         "Create a new rule asset",
                                                         currentlySelectedPackage );

                pop.show();
            }

        } );

        final Image newFunction = new ImageButton( "images/function_assets.gif" );
        newFunction.setTitle( "Create a new function" );
        newFunction.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                launchWizard( AssetFormats.FUNCTION,
                              "Create a new function" );
            }
        } );

        final Image newDSL = new ImageButton( "images/dsl.gif" );
        newDSL.setTitle( "Create a new DSL (language configuration)" );
        newDSL.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                launchWizard( AssetFormats.DSL,
                              "Create a new language configuration" );
            }
        } );

        final Image newRuleflow = new ImageButton( "images/ruleflow_small.gif" );
        newRuleflow.setTitle( "Create (upload) a new ruleflow." );
        newRuleflow.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                launchWizard( AssetFormats.RULE_FLOW_RF,
                              "Create a new ruleflow" );
            }
        } );

        final Image newEnum = new ImageButton("images/new_enumeration.gif");
        newEnum.setTitle( "Create a new data enumeration (drop down list)" );
        newEnum.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
                launchWizard( AssetFormats.ENUMERATION,
                              "Create a new enumeration (drop down mapping).");
            }

        });

        final Image newScenario = new ImageButton("images/test_manager.gif");
        newScenario.setTitle("Create a new scenario for testing and verification.");
        newScenario.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				launchWizard(AssetFormats.TEST_SCENARIO, "Create a new scenario for testing and verification.");
			}
		});

        newWizards.add( newPackage );
        newWizards.add( uploadModel );
        newWizards.add( newRule );
        newWizards.add( newFunction );
        newWizards.add( newDSL );
        newWizards.add( newRuleflow );
        newWizards.add( newEnum );
        newWizards.add( newScenario );
        return newWizards;

    }

    public void refreshTreeView() {

        if ( this.uuid == null ) {
            LoadingPopup.showMessage( "Loading list of packages ..." );
            RepositoryServiceFactory.getService().listPackages( new GenericCallback() {
                public void onSuccess(Object data) {
                    PackageConfigData[] packages = (PackageConfigData[]) data;
                    exTree.clear();
                    for ( int i = 0; i < packages.length; i++ ) {
                        if ( i == 0 ) {
                            renderPackageNodeOnTree( packages[i],
                                        true );
                        } else {
                            renderPackageNodeOnTree( packages[i],
                                        false );
                        }
                    }
                    LoadingPopup.close();
                }
            } );
        } else {
            LoadingPopup.showMessage( "Loading package ..." );
            RepositoryServiceFactory.getService().loadPackageConfig( uuid,
                                                                     new GenericCallback() {
                                                                         public void onSuccess(Object data) {
                                                                             PackageConfigData pack = (PackageConfigData) data;
                                                                             exTree.clear();
                                                                             renderPackageNodeOnTree( pack,
                                                                                         true );

                                                                             LoadingPopup.close();
                                                                         }
                                                                     } );
        }

    }

    /**
     * Pops up a new package wizard, and creates a new package should sir decide
     * to create said package. Nice package sir.
     */
    private void showNewPackage(Widget w) {
        NewPackageWizard pop = new NewPackageWizard( new Command() {
            public void execute() {
                refreshTreeView();
            }
        } );
        pop.show();
    }

    /**
     * Add a package to the tree.
     *
     * @param name
     */
    private void renderPackageNodeOnTree(final PackageConfigData conf,
                            boolean preSelect) {

        TreeItem pkg = makeItem( conf.name,
                                 "images/package.gif",
                                 new PackageTreeItem( new Command() {

                                     public void execute() {

                                         if ( isDirty() ) {
                                             if ( Window.confirm( "Discard Changes ? " ) ) {
                                                 resetDirty();
                                                 loadPackageConfig( conf.uuid );
                                             }
                                         } else {

                                             loadPackageConfig( conf.uuid );
                                         }
                                     }
                                 } ) );

        pkg.addItem( makeItem( "Business rule assets",
                               "images/rule_asset.gif",
                               showListEvent( conf.uuid,
                                              AssetFormats.BUSINESS_RULE_FORMATS ) ) );
        pkg.addItem( makeItem( "Technical rule assets",
                               "images/technical_rule_assets.gif",
                               showListEvent( conf.uuid,
                                              new String[]{AssetFormats.DRL} ) ) );
        pkg.addItem( makeItem( "Functions",
                               "images/function_assets.gif",
                               showListEvent( conf.uuid,
                                              new String[]{AssetFormats.FUNCTION} ) ) );
        pkg.addItem( makeItem( "DSL configurations",
                               "images/dsl.gif",
                               showListEvent( conf.uuid,
                                              new String[]{AssetFormats.DSL} ) ) );
        pkg.addItem( makeItem( "Model",
                               "images/model_asset.gif",
                               showListEvent( conf.uuid,
                                              new String[]{AssetFormats.MODEL} ) ) );

        pkg.addItem( makeItem( "Rule Flows",
                "images/ruleflow_small.gif",
                showListEvent( conf.uuid,
                               new String[]{AssetFormats.RULE_FLOW_RF} ) ) );

        pkg.addItem( makeItem( "Enumerations",
                "images/enumeration.gif",
                showListEvent( conf.uuid,
                               new String[]{AssetFormats.ENUMERATION} ) ) );


        pkg.addItem(makeItem( "Test Scenarios",
                               "images/test_manager.gif",
                               showListEvent( conf.uuid,
                                              new String[]{AssetFormats.TEST_SCENARIO} ) ) );

        exTree.addItem( pkg );
        if ( preSelect ) {
            exTree.setSelectedItem( pkg,
                                    true );
        }
    }

    /**
     * This will create a "show list" event to be attached to the tree.
     */
    private PackageTreeItem showListEvent(final String uuid,
                                          final String[] format) {

        final GenericCallback cb = new GenericCallback() {
            public void onSuccess(Object data) {
                final TableDataResult table = (TableDataResult) data;
                listView.loadTableData( table );
                listView.setWidth( "100%" );
                layout.setWidget( 0,
                                  1,
                                  listView );
                layout.getFlexCellFormatter().setAlignment( 0,
                                                            1,
                                                            HasHorizontalAlignment.ALIGN_LEFT,
                                                            HasVerticalAlignment.ALIGN_TOP );
                LoadingPopup.close();
            }
        };

        return new PackageTreeItem( new Command() {
            public void execute() {
                LoadingPopup.showMessage( "Loading list, please wait..." );
                RepositoryServiceFactory.getService().listAssets( uuid,
                                                                  format,
                                                                  -1,
                                                                  -1,
                                                                  cb );
            }
        } );
    }

    /**
     * Load up the package config data and display it.
     */
    private void loadPackageConfig(String uuid) {

        LoadingPopup.showMessage( "Loading package information ..." );

        RepositoryServiceFactory.getService().loadPackageConfig( uuid,
                                                                 new GenericCallback() {

                                                                     public void onSuccess(Object data) {
                                                                         final PackageConfigData conf = (PackageConfigData) data;

                                                                         StackPanel sp = new StackPanel();
                                                                         currentlySelectedPackage = conf.name;

                                                                         FormStyleLayout infoLayout = new FormStyleLayout( "images/package_large.png",
                                                                                                                           conf.name );
                                                                         infoLayout.setStyleName( "package-Editor" );
                                                                         infoLayout.setWidth( "100%" );
                                                                         infoLayout.addAttribute( "Description:",
                                                                                                  new Label( conf.description ) );
                                                                         infoLayout.addAttribute( "Date created:",
                                                                                                  new Label( conf.dateCreated.toLocaleString() ) );

                                                                         if ( conf.isSnapshot ) {
                                                                             infoLayout.addAttribute( "Snapshot created on:",
                                                                                                      new Label( conf.lastModified.toLocaleString() ) );
                                                                             infoLayout.addAttribute( "Snapshot comment:",
                                                                                                      new Label( conf.checkinComment ) );
                                                                             final String uri = PackageBuilderWidget.getDownloadLink( conf );
                                                                             // Button download = new Button("Download package");
                                                                             // download.addClickListener( new ClickListener() {
                                                                             // public void onClick(Widget arg0) {
                                                                             // Window.open( uri, "downloading...",
                                                                             // "resizable=no,scrollbars=yes,status=no" );
                                                                             // }
                                                                             // });

                                                                             HTML html = new HTML( "<a href='" + uri + "' target='_blank'>Download binary package</a>" );
                                                                             infoLayout.addAttribute( "Download package:",
                                                                                                      html );
                                                                             infoLayout.addAttribute( "Package URI:",
                                                                                                      new Label( uri ) );
                                                                             Button viewSource = new Button( "View package source" );
                                                                             viewSource.addClickListener( new ClickListener() {
                                                                                 public void onClick(Widget w) {
                                                                                     PackageBuilderWidget.doBuildSource( conf.uuid,
                                                                                                                         conf.name );
                                                                                 }
                                                                             } );
                                                                             infoLayout.addAttribute( "Show package source:",
                                                                                                      viewSource );
                                                                         }

                                                                         if ( !conf.isSnapshot ) {
                                                                             infoLayout.addRow( new HTML( "<i>Choose one of the options below</i>" ) );
                                                                         }

                                                                         Command makeDirtyCommand = new Command() {
                                                                             public void execute() {
                                                                                 makeDirty();
                                                                             }

                                                                         };

                                                                         Command cleanDirtyCommand = new Command() {
                                                                             public void execute() {
                                                                                 resetDirty();
                                                                             }

                                                                         };

                                                                         sp.add( infoLayout,
                                                                                 "<img src='images/information.gif'/>Info",
                                                                                 true );
                                                                         if ( !conf.isSnapshot ) {
                                                                             sp.add( new PackageEditor( conf,
                                                                                                        makeDirtyCommand,
                                                                                                        cleanDirtyCommand,
                                                                                                        refreshCommand ),
                                                                                     "<img src='images/package.gif'/>Edit Package configuration",
                                                                                     true );
                                                                             sp.add( new PackageBuilderWidget( conf,
                                                                                                               editEvent ),
                                                                                     "<img src='images/package_build.gif'/>Build, validate and deploy",
                                                                                     true );
                                                                         } else {
                                                                             sp.add( new PackageEditor( conf,
                                                                                                        makeDirtyCommand,
                                                                                                        cleanDirtyCommand,
                                                                                                        refreshCommand ),
                                                                                     "<img src='images/package.gif'/>View Package configuration",
                                                                                     true );

                                                                         }
                                                                         sp.setWidth( "100%" );
                                                                         layout.setWidget( 0,
                                                                                           1,
                                                                                           sp );
                                                                         LoadingPopup.close();
                                                                     }
                                                                 } );

    }

    Command refreshCommand = new Command() {
                               public void execute() {
                                   refreshTreeView();
                               }
                           };

    private TreeItem makeItem(String name,
                              String icon,
                              Object command) {
        TreeItem item = new TreeItem();
        item.setHTML( "<img src=\"" + icon + "\">" + name + "</a>" );
        item.setUserObject( command );
        return item;
    }

    private void launchWizard(String format,
                              String title) {
        int left = 70;
        int top = 100;

        NewAssetWizard pop = new NewAssetWizard( new EditItemEvent() {
                                                     public void open(String key) {
                                                         editEvent.open( key );
                                                     }
                                                 },
                                                 false,
                                                 format,
                                                 title,
                                                 currentlySelectedPackage );

        pop.show();
    }

    static class PackageTreeItem {
        Command command;

        public PackageTreeItem(Command com) {
            this.command = com;

        }

    }
}