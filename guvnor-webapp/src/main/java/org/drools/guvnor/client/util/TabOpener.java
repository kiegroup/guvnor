/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.util;

import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.admin.ArchivedAssetManager;
import org.drools.guvnor.client.admin.BackupManager;
import org.drools.guvnor.client.admin.CategoryManager;
import org.drools.guvnor.client.admin.LogViewer;
import org.drools.guvnor.client.admin.PermissionViewer;
import org.drools.guvnor.client.admin.RepoConfigManager;
import org.drools.guvnor.client.admin.RuleVerifierManager;
import org.drools.guvnor.client.admin.StateManager;
import org.drools.guvnor.client.admin.WorkspaceManager;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.ExplorerViewCenterPanel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.PackageEditor;
import org.drools.guvnor.client.packages.SnapshotView;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.qa.AnalysisView;
import org.drools.guvnor.client.qa.ScenarioPackageView;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.ServerPushNotification;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.ruleeditor.MultiViewEditor;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.rulelist.QueryWidget;
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;
import org.drools.guvnor.client.widgets.tables.CategoryPagedTable;
import org.drools.guvnor.client.widgets.tables.InboxIncomingPagedTable;
import org.drools.guvnor.client.widgets.tables.InboxPagedTable;
import org.drools.guvnor.client.widgets.tables.StatePagedTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TabOpener {

    private Constants                     constants             = GWT.create( Constants.class );
    private static Images                 images                = GWT.create( Images.class );

    private static final String           REPOCONFIG            = "repoconfig";
    private static final String           RULE_VERIFIER_MANAGER = "ruleVerifierManager";
    private static final String           SECURITY_PERMISSIONS  = "securityPermissions";
    private static final String           ERROR_LOG             = "errorLog";
    private static final String           BAKMAN                = "bakman";
    private static final String           STATEMAN              = "stateman";
    private static final String           ARCHMAN               = "archman";
    private static final String           CATMAN                = "catman";
    private static final String           WORKSPACES            = "workspaces";

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
        History.newItem( "asset="
                         + uuid ); // NON-NLS

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

            loadRuleAsset( uuid,
                           loading );
        }
    }

    private void loadRuleAsset(final String uuid,
                               final boolean[] loading) {
        RepositoryServiceFactory.getAssetService().loadRuleAsset( uuid,
                                                             createGenericCallback( uuid,
                                                                                    loading ) );
    }

    private GenericCallback<RuleAsset> createGenericCallback(final String uuid,
                                                             final boolean[] loading) {
        return new GenericCallback<RuleAsset>() {
            public void onSuccess(final RuleAsset ruleAsset) {
                SuggestionCompletionCache.getInstance().doAction( ruleAsset.metaData.packageName,
                                                                  createCommandForSuggestCompletionCache( uuid,
                                                                                                          loading,
                                                                                                          ruleAsset ) );
            }

            private Command createCommandForSuggestCompletionCache(final String uuid,
                                                                   final boolean[] loading,
                                                                   final RuleAsset ruleAsset) {
                return new Command() {
                    public void execute() {
                        loading[0] = false;
                        RuleViewer ruleViewer = new RuleViewer( ruleAsset,
                                                                createEditEvent() );
                        explorerViewCenterPanel.addTab( ruleAsset.metaData.name,
                                                        ruleViewer,
                                                        uuid );
                        ruleViewer.setCloseCommand( createCloseCommandForRuleViewer( uuid ) );

                        // When model is saved update the package view if it is
                        // opened.
                        if ( ruleAsset.metaData.format.equals( AssetFormats.MODEL ) ) {
                            Command command = createCheckInAndArchiveCommandForRuleViewer( ruleAsset );
                            ruleViewer.setCheckedInCommand( command );
                            ruleViewer.setArchiveCommand( command );
                        }

                        LoadingPopup.close();
                    }

                    private Command createCheckInAndArchiveCommandForRuleViewer(final RuleAsset ruleAsset) {
                        Command command = new Command() {
                            public void execute() {
                                PackageEditor packageEditor = explorerViewCenterPanel.getOpenedPackageEditors().get( ruleAsset.metaData.packageName );
                                if ( packageEditor != null ) {
                                    packageEditor.reload();
                                }
                            }
                        };
                        return command;
                    }

                    private Command createCloseCommandForRuleViewer(final String uuid) {
                        return new Command() {
                            public void execute() {
                                explorerViewCenterPanel.close( uuid );
                            }
                        };
                    }

                };
            }
        };
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
            FormStylePopup popup = new FormStylePopup( images.information(),
                                                       Format.format( constants.Asset0IsAlreadyOpenPleaseCloseItBeforeOpeningMultiview(),
                                                                      blockingAssetName ) );
            popup.show();
            return;
        }

        MultiViewEditor multiview = new MultiViewEditor( rows,
                                                         createEditEvent() );

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
                                                                                                                   refPackageList );
                                                                             explorerViewCenterPanel.addTab( conf.name,
                                                                                                             ed,
                                                                                                             conf.uuid );
                                                                             LoadingPopup.close();
                                                                         }
                                                                     } );
        }
    }

    public void openFind() {
        if ( !explorerViewCenterPanel.showIfOpen( "FIND" ) ) { // NON-NLS
            explorerViewCenterPanel.addTab( constants.Find(),
                                            new QueryWidget( new OpenItemCommand() {
                                                
                                                public void open(String uuid) {
                                                    openAsset( uuid );
                                                }

                                                public void open(MultiViewRow[] rows) {
                                                    openAssetsToMultiView( rows );
                                                }
                                            } ),
                                            "FIND" ); // NON-NLS

        }
    }

    public void openSnapshot(final SnapshotInfo snap) {
        if ( !explorerViewCenterPanel.showIfOpen( snap.name
                                                  + snap.uuid ) ) {
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
                                                                                                                                       explorerViewCenterPanel.close( snap.name
                                                                                                                                                                      + snap.uuid );
                                                                                                                                   }
                                                                                                                               } ),
                                                                                                             snap.name
                                                                                                                     + snap.uuid );
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
                Frame aboutInfoFrame = new Frame( "../AboutInfo.html" ); // NON-NLS

                FormStylePopup aboutPop = new FormStylePopup();
                aboutPop.setWidth( 600 + "px" );
                aboutPop.setTitle( constants.About() );
                String hhurl = GWT.getModuleBaseURL()
                               + "webdav";
                aboutPop.addAttribute( constants.WebDAVURL()
                                               + ":",
                                       new SmallLabel( "<b>"
                                                       + hhurl
                                                       + "</b>" ) );
                aboutPop.addAttribute( constants.Version()
                                               + ":",
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
                if ( !explorerViewCenterPanel.showIfOpen( REPOCONFIG ) ) explorerViewCenterPanel.addTab( constants.RepositoryConfig(),
                                                                                                         new RepoConfigManager(),
                                                                                                         REPOCONFIG );
                break;
            case 9 :
                if ( !explorerViewCenterPanel.showIfOpen( WORKSPACES ) ) explorerViewCenterPanel.addTab( constants.Workspaces(),
                                                                                                         new WorkspaceManager(),
                                                                                                         WORKSPACES );
                break;
        }

    }

    /**
     * Show the inbox of the given name.
     */
    public void openInbox(String title,
                          final String inboxName) {
        if ( !explorerViewCenterPanel.showIfOpen( inboxName ) ) {
            InboxPagedTable table;
            if ( inboxName.equals( ExplorerNodeConfig.INCOMING_ID ) ) {
                table = new InboxIncomingPagedTable( inboxName,
                                                     createEditEvent() );

            } else {
                table = new InboxPagedTable( inboxName,
                                             createEditEvent() );
            }
            explorerViewCenterPanel.addTab( title,
                                                table,
                                                inboxName );
        }
    }

    /**
     * open a state or category !
     */
    public void openState(String title,
                          String widgetID) {
        if ( !explorerViewCenterPanel.showIfOpen( widgetID ) ) {
            final String stateName = widgetID.substring( widgetID.indexOf( "-" ) + 1 );
            final StatePagedTable table = new StatePagedTable( stateName,
                                                               createEditEvent() );

            final ServerPushNotification push = new ServerPushNotification() {
                public void messageReceived(PushResponse response) {
                    if ( response.messageType.equals( "statusChange" )
                         && (response.message).equals( stateName ) ) {
                        table.refresh();
                    }
                }
            };
            PushClient.instance().subscribe( push );
            table.addUnloadListener( new Command() {
                public void execute() {
                    PushClient.instance().unsubscribe( push );
                }
            } );

            explorerViewCenterPanel.addTab( constants.Status()
                                                    + title,
                                                    table,
                                            widgetID );
        }
    }

    /**
     * open a category
     */
    public void openCategory(String title,
                             String widgetID) {
        if ( !explorerViewCenterPanel.showIfOpen( widgetID ) ) {
            final String categoryPath = widgetID.substring( widgetID.indexOf( "-" ) + 1 );
            final CategoryPagedTable table = new CategoryPagedTable( categoryPath,
                                                                     createEditEvent(),
                                                                     GWT.getModuleBaseURL()
                                                                             + "feed/category?name="
                                                                             + categoryPath
                                                                             + "&viewUrl="
                                                                             + Util.getSelfURL() );
            final ServerPushNotification push = new ServerPushNotification() {
                public void messageReceived(PushResponse response) {
                    if ( response.messageType.equals( "categoryChange" )
                         && response.message.equals( categoryPath ) ) {
                        table.refresh();
                    }
                }
            };
            PushClient.instance().subscribe( push );
            table.addUnloadListener( new Command() {
                public void execute() {
                    PushClient.instance().unsubscribe( push );
                }
            } );

            explorerViewCenterPanel.addTab( (constants.CategoryColon())
                                                    + title,
                                            table,
                                            widgetID );
        }
    }

    private OpenItemCommand createEditEvent() {
        return new OpenItemCommand() {
            public void open(String uuid) {
                openAsset( uuid );
            }

            public void open(MultiViewRow[] rows) {
                openAssetsToMultiView( rows );
            }
        };
    }

    public void openPackageViewAssets(final String packageUuid,
                                      final String packageName,
                                      String key,
                                      final List<String> formatInList,
                                      Boolean formatIsRegistered,
                                      final String itemName) {
        if ( !explorerViewCenterPanel.showIfOpen( key ) ) {

            String feedUrl = GWT.getModuleBaseURL()
                             + "feed/package?name="
                             + packageName
                             + "&viewUrl="
                             + Util.getSelfURL()
                             + "&status=*";
            final AssetPagedTable table = new AssetPagedTable( packageUuid,
                                                               formatInList,
                                                               formatIsRegistered,
                                                               createEditEvent(),
                                                               feedUrl );
            explorerViewCenterPanel.addTab( itemName
                                                    + " ["
                                                    + packageName
                                                    + "]",
                                            table,
                                            key );

            final ServerPushNotification sub = new ServerPushNotification() {
                public void messageReceived(PushResponse response) {
                    if ( response.messageType.equals( "packageChange" )
                         && response.message.equals( packageName ) ) {
                        table.refresh();
                    }
                }
            };
            PushClient.instance().subscribe( sub );
            table.addUnloadListener( new Command() {
                public void execute() {
                    PushClient.instance().unsubscribe( sub );
                }
            } );
        }
    }

    public void openTestScenario(String packageUuid,
                                 String packageName) {

        if ( !explorerViewCenterPanel.showIfOpen( "scenarios"
                                                  + packageUuid ) ) {
            String m = Format.format( constants.ScenariosForPackage(),
                                      packageName );
            explorerViewCenterPanel.addTab( m,
                                            new ScenarioPackageView( packageUuid,
                                                                     packageName,
                                                                     createEditEvent(),
                                                                     explorerViewCenterPanel ),
                                            "scenarios"
                                                    + packageUuid );
        }
    }

    public void openVerifierView(String packageUuid,
                                 String packageName) {
        if ( !explorerViewCenterPanel.showIfOpen( "analysis"
                                                  + packageUuid ) ) { // NON-NLS
            String m = Format.format( constants.AnalysisForPackage(),
                                      packageName );
            explorerViewCenterPanel.addTab( m,
                                            new AnalysisView( packageUuid,
                                                              packageName,
                                                              createEditEvent() ),
                                            "analysis"
                                                    + packageUuid );
        }
    }

    public void openSnapshotAssetList(final String name,
                                      final String uuid,
                                      final String[] assetTypes,
                                      String key) {
        if ( !explorerViewCenterPanel.showIfOpen( key ) ) {
            AssetPagedTable table = new AssetPagedTable( uuid,
                                                         Arrays.asList( assetTypes ),
                                                         null,
                                                         createEditEvent() );

            VerticalPanel vp = new VerticalPanel();
            vp.add( new HTML( "<i><small>"
                              + constants.SnapshotListingFor()
                              + name
                              + "</small></i>" ) );
            vp.add( table );
            explorerViewCenterPanel.addTab( constants.SnapshotItems(),
                                            vp,
                                            key );
        }
    }

}
