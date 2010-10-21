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
package org.drools.guvnor.client.util;

import java.util.Arrays;

import org.drools.guvnor.client.admin.ArchivedAssetManager;
import org.drools.guvnor.client.admin.BackupManager;
import org.drools.guvnor.client.admin.CategoryManager;
import org.drools.guvnor.client.admin.LogViewer;
import org.drools.guvnor.client.admin.PermissionViewer;
import org.drools.guvnor.client.admin.RepoConfigManager;
import org.drools.guvnor.client.admin.RuleVerifierManager;
import org.drools.guvnor.client.admin.StateManager;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.explorer.ExplorerViewCenterPanel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.PackageEditor;
import org.drools.guvnor.client.packages.SnapshotView;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.qa.AnalysisView;
import org.drools.guvnor.client.qa.ScenarioPackageView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.ruleeditor.MultiViewEditor;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.rulelist.AssetItemGridDataLoader;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.rulelist.QueryWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author rikkola
 *
 */
public class TabOpener {

    private Constants                     constants             = ((Constants) GWT.create( Constants.class ));

    private static final String           REPOCONFIG            = "repoconfig";
    private static final String           RULE_VERIFIER_MANAGER = "ruleVerifierManager";
    private static final String           SECURITY_PERMISSIONS  = "securityPermissions";
    private static final String           ERROR_LOG             = "errorLog";
    private static final String           BAKMAN                = "bakman";
    private static final String           STATEMAN              = "stateman";
    private static final String           ARCHMAN               = "archman";
    private static final String           CATMAN                = "catman";

    private final ExplorerViewCenterPanel explorerViewCenterPanel;

    private static TabOpener              instance;

    private TabOpener(ExplorerViewCenterPanel explorerViewCenterPanel) {
        this.explorerViewCenterPanel = explorerViewCenterPanel;
    }

    public static void initIstance(ExplorerViewCenterPanel centertabbedPanel) {
        instance = new TabOpener( centertabbedPanel );
    }

    public static TabOpener getInstance() {
        return instance;
    }

    /**
     * Open an asset if it is not already open.
     */
    public void openAsset(final String uuid) {
        if ( uuid.contains( "<" ) ) {
            return;
        }
        History.newItem( "asset=" + uuid ); // NON-NLS

        if ( !explorerViewCenterPanel.showIfOpen( uuid ) ) {

            final boolean[] loading = {true};

            Timer t = new Timer() {
                public void run() {
                    if ( loading[0] ) {
                        LoadingPopup.showMessage( constants.LoadingAsset() );
                    }
                }
            };
            t.schedule( 200 );

            RepositoryServiceFactory.getService().loadRuleAsset( uuid,
                                                                 new GenericCallback<RuleAsset>() {
                                                                     public void onSuccess(final RuleAsset a) {
                                                                         SuggestionCompletionCache.getInstance().doAction( a.metaData.packageName,
                                                                                                                           new Command() {
                                                                                                                               public void execute() {
                                                                                                                                   loading[0] = false;
                                                                                                                                   EditItemEvent edit = new EditItemEvent() {
                                                                                                                                       public void open(String key) {
                                                                                                                                           openAsset( key );
                                                                                                                                       }

                                                                                                                                       public void open(MultiViewRow[] rows) {
                                                                                                                                           for ( MultiViewRow row : rows ) {
                                                                                                                                               openAsset( row.uuid );
                                                                                                                                           }
                                                                                                                                       }
                                                                                                                                   };
                                                                                                                                   RuleViewer rv = new RuleViewer( a,
                                                                                                                                                                   edit );
                                                                                                                                   explorerViewCenterPanel.addTab( a.metaData.name,
                                                                                                                                                                   rv,
                                                                                                                                                                   uuid );
                                                                                                                                   rv.setCloseCommand( new Command() {
                                                                                                                                       public void execute() {
                                                                                                                                           explorerViewCenterPanel.close( uuid );
                                                                                                                                       }
                                                                                                                                   } );

                                                                                                                                   // When model is saved update the package view if it is opened.
                                                                                                                                   if ( a.metaData.format.equals( AssetFormats.MODEL ) ) {
                                                                                                                                       Command command = new Command() {
                                                                                                                                           public void execute() {
                                                                                                                                               PackageEditor packageEditor = explorerViewCenterPanel.getOpenedPackageEditors().get( a.metaData.packageName );
                                                                                                                                               if ( packageEditor != null ) {
                                                                                                                                                   packageEditor.reload();
                                                                                                                                               }
                                                                                                                                           }
                                                                                                                                       };
                                                                                                                                       rv.setCheckedInCommand( command );
                                                                                                                                       rv.setArchiveCommand( command );
                                                                                                                                   }

                                                                                                                                   LoadingPopup.close();
                                                                                                                               }
                                                                                                                           } );
                                                                     }
                                                                 } );
        }
    }

    public void openAssetsToMultiView(MultiViewRow[] rows) {

        String blockingAssetName = null;
        final String[] uuids = new String[rows.length];
        final String[] names = new String[rows.length];

        for ( int i = 0; i < rows.length; i++ ) {
            // Check if any of these assets are already opened.
            if ( explorerViewCenterPanel.showIfOpen( rows[i].uuid ) ) {
                blockingAssetName = rows[i].name;
                break;
            }
            uuids[i] = rows[i].uuid;
            names[i] = rows[i].name;
        }

        if ( blockingAssetName != null ) {
            FormStylePopup popup = new FormStylePopup( "images/information.gif", //NON-NLS
                                                       Format.format( constants.Asset0IsAlreadyOpenPleaseCloseItBeforeOpeningMultiview(),
                                                                      blockingAssetName ) );
            popup.show();
            return;
        }

        MultiViewEditor multiview = new MultiViewEditor( rows,
                                                         new EditItemEvent() {
                                                             public void open(String key) {
                                                                 openAsset( key );
                                                             }

                                                             public void open(MultiViewRow[] rows) {
                                                                 for ( MultiViewRow row : rows ) {
                                                                     openAsset( row.uuid );
                                                                 }
                                                             }
                                                         } );

        multiview.setCloseCommand( new Command() {
            public void execute() {
                explorerViewCenterPanel.close( Arrays.toString( uuids ) );
            }
        } );

        explorerViewCenterPanel.addTab( Arrays.toString( names ),
                                        multiview,
                                        uuids );

    }

    /**
     * Open a package editor if it is not already open.
     */
    public void openPackageEditor(final String uuid,
                                  final Command refPackageList) {

        if ( !explorerViewCenterPanel.showIfOpen( uuid ) ) {
            LoadingPopup.showMessage( constants.LoadingPackageInformation() );
            RepositoryServiceFactory.getService().loadPackageConfig( uuid,
                                                                     new GenericCallback<PackageConfigData>() {
                                                                         public void onSuccess(PackageConfigData conf) {
                                                                             PackageEditor ed = new PackageEditor( conf,
                                                                                                                   new Command() {
                                                                                                                       public void execute() {
                                                                                                                           explorerViewCenterPanel.close( uuid );
                                                                                                                       }
                                                                                                                   },
                                                                                                                   refPackageList,
                                                                                                                   new EditItemEvent() {
                                                                                                                       public void open(String uuid) {
                                                                                                                           openAsset( uuid );
                                                                                                                       }

                                                                                                                       public void open(MultiViewRow[] rows) {
                                                                                                                           for ( MultiViewRow row : rows ) {
                                                                                                                               openAsset( row.uuid );
                                                                                                                           }
                                                                                                                       }
                                                                                                                   } );
                                                                             explorerViewCenterPanel.addTab( conf.name,
                                                                                                             ed,
                                                                                                             conf.uuid );
                                                                             LoadingPopup.close();
                                                                         }
                                                                     } );
        }
    }

    public void openFind() {
        if ( !explorerViewCenterPanel.showIfOpen( "FIND" ) ) { //NON-NLS
            explorerViewCenterPanel.addTab( constants.Find(),
                                            new QueryWidget( new EditItemEvent() {
                                                public void open(String uuid) {
                                                    openAsset( uuid );
                                                }

                                                public void open(MultiViewRow[] rows) {
                                                    for ( MultiViewRow row : rows ) {
                                                        openAsset( row.uuid );
                                                    }
                                                }
                                            } ),
                                            "FIND" ); //NON-NLS

        }
    }

    public void openSnapshot(final SnapshotInfo snap) {
        if ( !explorerViewCenterPanel.showIfOpen( snap.name + snap.uuid ) ) {
            LoadingPopup.showMessage( constants.LoadingSnapshot() );
            RepositoryServiceFactory.getService().loadPackageConfig( snap.uuid,
                                                                     new GenericCallback<PackageConfigData>() {
                                                                         public void onSuccess(PackageConfigData conf) {
                                                                             explorerViewCenterPanel.addTab( Format.format( constants.SnapshotLabel(),
                                                                                                                            snap.name ),
                                                                                                             new SnapshotView( snap,
                                                                                                                               conf,
                                                                                                                               new Command() {
                                                                                                                                   public void execute() {
                                                                                                                                       explorerViewCenterPanel.close( snap.name + snap.uuid );
                                                                                                                                   }
                                                                                                                               } ),
                                                                                                             snap.name + snap.uuid );
                                                                             LoadingPopup.close();
                                                                         }
                                                                     } );

        }
    }

    public void openAdministrationSelection(int id) {

        switch ( id ) {
            case 0 :
                if ( !explorerViewCenterPanel.showIfOpen( CATMAN ) ) {
                    explorerViewCenterPanel.addTab( constants.CategoryManager(),
                                                    new CategoryManager(),
                                                    CATMAN );
                }
                break;
            case 1 :
                if ( !explorerViewCenterPanel.showIfOpen( ARCHMAN ) ) {
                    explorerViewCenterPanel.addTab( constants.ArchivedManager(),
                                                    new ArchivedAssetManager(),
                                                    ARCHMAN );
                }
                break;

            case 2 :
                if ( !explorerViewCenterPanel.showIfOpen( STATEMAN ) ) {
                    explorerViewCenterPanel.addTab( constants.StateManager(),
                                                    new StateManager(),
                                                    STATEMAN );
                }
                break;
            case 3 :
                if ( !explorerViewCenterPanel.showIfOpen( BAKMAN ) ) {
                    explorerViewCenterPanel.addTab( constants.ImportExport(),
                                                    new BackupManager(),
                                                    BAKMAN );
                }
                break;

            case 4 :
                if ( !explorerViewCenterPanel.showIfOpen( ERROR_LOG ) ) {
                    explorerViewCenterPanel.addTab( constants.EventLog(),
                                                    new LogViewer(),
                                                    ERROR_LOG );
                }
                break;
            case 5 :
                if ( !explorerViewCenterPanel.showIfOpen( SECURITY_PERMISSIONS ) ) {
                    explorerViewCenterPanel.addTab( constants.UserPermissionMappings(),
                                                    new PermissionViewer(),
                                                    SECURITY_PERMISSIONS );
                }
                break;
            case 6 :
                Frame aboutInfoFrame = new Frame( "../AboutInfo.html" ); //NON-NLS

                FormStylePopup aboutPop = new FormStylePopup();
                aboutPop.setWidth( 600 + "px" );
                aboutPop.setTitle( constants.About() );
                String hhurl = GWT.getModuleBaseURL() + "webdav";
                aboutPop.addAttribute( constants.WebDAVURL() + ":",
                                       new SmallLabel( "<b>" + hhurl + "</b>" ) );
                aboutPop.addAttribute( constants.Version() + ":",
                                       aboutInfoFrame );
                aboutPop.show();
                break;

            case 7 :
                if ( !explorerViewCenterPanel.showIfOpen( RULE_VERIFIER_MANAGER ) ) {
                    explorerViewCenterPanel.addTab( constants.RulesVerificationManager(),
                                                    new RuleVerifierManager(),
                                                    RULE_VERIFIER_MANAGER );
                }
                break;
            case 8 :
                if ( !explorerViewCenterPanel.showIfOpen( REPOCONFIG ) ) //NON-NLS
                explorerViewCenterPanel.addTab( constants.RepositoryConfig(),
                                                new RepoConfigManager(),
                                                REPOCONFIG );
                break;
        }

    }

    /**
     * Show the inbox of the given name.
     */
    public void openInbox(String title,
                          final String widgetID) {
        if ( !explorerViewCenterPanel.showIfOpen( widgetID ) ) {
            AssetItemGrid g = new AssetItemGrid( createEditEvent(),
                                                 widgetID,
                                                 new AssetItemGridDataLoader() {
                                                     public void loadData(int startRow,
                                                                          int numberOfRows,
                                                                          GenericCallback<TableDataResult> cb) {
                                                         RepositoryServiceFactory.getService().loadInbox( widgetID,
                                                                                                          cb );
                                                     }
                                                 } );
            explorerViewCenterPanel.addTab( title,
                                            g,
                                            widgetID );
        }
    }

    /**
     * open a state or category !
     */
    public void openState(String title,
                          String widgetID) {
        if ( !explorerViewCenterPanel.showIfOpen( widgetID ) ) {
            final String stateName = widgetID.substring( widgetID.indexOf( "-" ) + 1 );
            final AssetItemGrid grid = new AssetItemGrid( createEditEvent(),
                                                          AssetItemGrid.RULE_LIST_TABLE_ID,
                                                          new AssetItemGridDataLoader() {
                                                              public void loadData(int skip,
                                                                                   int numberOfRows,
                                                                                   GenericCallback<TableDataResult> cb) {
                                                                  RepositoryServiceFactory.getService().loadRuleListForState( stateName,
                                                                                                                              skip,
                                                                                                                              numberOfRows,
                                                                                                                              AssetItemGrid.RULE_LIST_TABLE_ID,
                                                                                                                              cb );

                                                              }
                                                          },
                                                          null );
            final ServerPushNotification push = new ServerPushNotification() {
                public void messageReceived(PushResponse response) {
                    if ( response.messageType.equals( "statusChange" ) && (response.message).equals( stateName ) ) {
                        grid.refreshGrid();
                    }
                }
            };
            PushClient.instance().subscribe( push );
            grid.addUnloadListener( new Command() {
                public void execute() {
                    PushClient.instance().unsubscribe( push );
                }
            } );

            explorerViewCenterPanel.addTab( constants.Status() + title,
                                            grid,
                                            widgetID );
        }
    }

    /**
     * open a category 
     */
    public void openCategory(String title,
                             String widgetID) {
        if ( !explorerViewCenterPanel.showIfOpen( widgetID ) ) {
            final String categoryName = widgetID.substring( widgetID.indexOf( "-" ) + 1 );
            final AssetItemGrid grid = new AssetItemGrid( createEditEvent(),
                                                          AssetItemGrid.RULE_LIST_TABLE_ID,
                                                          new AssetItemGridDataLoader() {
                                                              public void loadData(int skip,
                                                                                   int numberOfRows,
                                                                                   GenericCallback<TableDataResult> cb) {
                                                                  RepositoryServiceFactory.getService().loadRuleListForCategories( categoryName,
                                                                                                                                   skip,
                                                                                                                                   numberOfRows,
                                                                                                                                   AssetItemGrid.RULE_LIST_TABLE_ID,
                                                                                                                                   cb );
                                                              }
                                                          },
                                                          GWT.getModuleBaseURL() + "feed/category?name=" + categoryName + "&viewUrl=" + Util.getSelfURL() );
            final ServerPushNotification push = new ServerPushNotification() {
                public void messageReceived(PushResponse response) {
                    if ( response.messageType.equals( "categoryChange" ) && response.message.equals( categoryName ) ) {
                        grid.refreshGrid();
                    }
                }
            };
            PushClient.instance().subscribe( push );
            grid.addUnloadListener( new Command() {
                public void execute() {
                    PushClient.instance().unsubscribe( push );
                }
            } );

            explorerViewCenterPanel.addTab( (constants.CategoryColon()) + title,
                                            grid,
                                            widgetID );
        }
    }

    private EditItemEvent createEditEvent() {
        return new EditItemEvent() {
            public void open(String uuid) {
                openAsset( uuid );
            }

            public void open(MultiViewRow[] rows) {
                for ( MultiViewRow row : rows ) {
                    openAsset( row.uuid );
                }
            }
        };
    }

    public void openPackageViewAssets(final String packageUuid,
                                      final String packageName,
                                      String key,
                                      final String[] formats,
                                      final String itemName) {
        if ( !explorerViewCenterPanel.showIfOpen( key ) ) {

            final AssetItemGrid grid = new AssetItemGrid( new EditItemEvent() {
                                                              public void open(String uuid) {
                                                                  openAsset( uuid );
                                                              }

                                                              public void open(MultiViewRow[] rows) {
                                                                  openAssetsToMultiView( rows );
                                                              }
                                                          },
                                                          AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID,
                                                          new AssetItemGridDataLoader() {
                                                              public void loadData(int startRow,
                                                                                   int numberOfRows,
                                                                                   GenericCallback<TableDataResult> cb) {
                                                                  RepositoryServiceFactory.getService().listAssets( packageUuid,
                                                                                                                    formats,
                                                                                                                    startRow,
                                                                                                                    numberOfRows,
                                                                                                                    AssetItemGrid.PACKAGEVIEW_LIST_TABLE_ID,
                                                                                                                    cb );
                                                              }
                                                          },
                                                          GWT.getModuleBaseURL() + "feed/package?name=" + packageName + "&viewUrl=" + Util.getSelfURL() + "&status=*" );
            explorerViewCenterPanel.addTab( itemName + " [" + packageName + "]",
                                            grid,
                                            key );

            final ServerPushNotification sub = new ServerPushNotification() {
                public void messageReceived(PushResponse response) {
                    if ( response.messageType.equals( "packageChange" ) && response.message.equals( packageName ) ) {
                        grid.refreshGrid();
                    }
                }
            };
            PushClient.instance().subscribe( sub );
            grid.addUnloadListener( new Command() {
                public void execute() {
                    PushClient.instance().unsubscribe( sub );
                }
            } );
        }
    }

    public void openTestScenario(String packageUuid,
                                 String packageName) {

        if ( !explorerViewCenterPanel.showIfOpen( "scenarios" + packageUuid ) ) {
            final EditItemEvent edit = new EditItemEvent() {
                public void open(String key) {
                    openAsset( key );
                }

                public void open(MultiViewRow[] rows) {
                    for ( MultiViewRow row : rows ) {
                        openAsset( row.uuid );
                    }
                }
            };

            String m = Format.format( constants.ScenariosForPackage(),
                                      packageName );
            explorerViewCenterPanel.addTab( m,
                                            new ScenarioPackageView( packageUuid,
                                                                     packageName,
                                                                     edit,
                                                                     explorerViewCenterPanel ),
                                            "scenarios" + packageUuid );
        }
    }

    public void openVerifierView(String packageUuid,
                                 String packageName) {
        if ( !explorerViewCenterPanel.showIfOpen( "analysis" + packageUuid ) ) { //NON-NLS
            final EditItemEvent edit = new EditItemEvent() {
                public void open(String key) {
                    openAsset( key );
                }

                public void open(MultiViewRow[] rows) {
                    for ( MultiViewRow row : rows ) {
                        openAsset( row.uuid );
                    }
                }
            };

            String m = Format.format( constants.AnalysisForPackage(),
                                      packageName );
            explorerViewCenterPanel.addTab( m,
                                            new AnalysisView( packageUuid,
                                                              packageName,
                                                              edit ),
                                            "analysis" + packageUuid );
        }
    }

    public void openSnapshotAssetList(final String name,
                                      final String uuid,
                                      final String[] assetTypes,
                                      String key) {
        if ( !explorerViewCenterPanel.showIfOpen( key ) ) {
            AssetItemGrid grid = new AssetItemGrid( new EditItemEvent() {
                                                        public void open(String key) {
                                                            openAsset( key );
                                                        }

                                                        public void open(MultiViewRow[] rows) {
                                                            for ( MultiViewRow row : rows ) {
                                                                openAsset( row.uuid );
                                                            }
                                                        }
                                                    },
                                                    AssetItemGrid.RULE_LIST_TABLE_ID,
                                                    new AssetItemGridDataLoader() {
                                                        public void loadData(int startRow,
                                                                             int numberOfRows,
                                                                             GenericCallback<TableDataResult> cb) {
                                                            RepositoryServiceFactory.getService().listAssets( uuid,
                                                                                                              assetTypes,
                                                                                                              startRow,
                                                                                                              numberOfRows,
                                                                                                              AssetItemGrid.RULE_LIST_TABLE_ID,
                                                                                                              cb );
                                                        }
                                                    } );

            VerticalPanel vp = new VerticalPanel();
            vp.add( new HTML( "<i><small>" + constants.SnapshotListingFor() + name + "</small></i>" ) );
            vp.add( grid );
            explorerViewCenterPanel.addTab( constants.SnapshotItems(),
                                            vp,
                                            key );
        }
    }

}
