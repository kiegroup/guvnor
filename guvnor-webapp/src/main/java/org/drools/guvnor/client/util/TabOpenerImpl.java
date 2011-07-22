/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerViewCenterPanel;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SnapshotView;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.client.ruleeditor.MultiViewEditor;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.widgets.tables.*;

import java.util.Arrays;
import java.util.List;

public class TabOpenerImpl
        implements
        TabManager {

    private Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private final ExplorerViewCenterPanel explorerViewCenterPanel;
    private final ClientFactory clientFactory;

    protected TabOpenerImpl(ClientFactory clientFactory,
                            ExplorerViewCenterPanel explorerViewCenterPanel) {
        this.clientFactory = clientFactory;
        this.explorerViewCenterPanel = explorerViewCenterPanel;
    }

    public void openAssetsToMultiView(MultiViewRow[] rows) {

        String blockingAssetName = null;
        final String[] uuids = new String[rows.length];
        final String[] names = new String[rows.length];

        for (int i = 0; i < rows.length; i++) {
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
                    constants.Asset0IsAlreadyOpenPleaseCloseItBeforeOpeningMultiview( blockingAssetName ) );
            popup.show();
            return;
        }

        MultiViewEditor multiview = new MultiViewEditor( rows,
                clientFactory );

        multiview.setCloseCommand( new Command() {
            public void execute() {
                explorerViewCenterPanel.close( Arrays.toString( uuids ) );
            }
        } );

        explorerViewCenterPanel.addTab( Arrays.toString( names ),
                multiview,
                uuids.toString() );

    }

    public void openSnapshot(
            final SnapshotInfo snap) {
        if ( !explorerViewCenterPanel.showIfOpen( snap.name
                + snap.uuid ) ) {
            LoadingPopup.showMessage( constants.LoadingSnapshot() );
            RepositoryServiceFactory.getPackageService().loadPackageConfig( snap.uuid,
                    new GenericCallback<PackageConfigData>() {
                        public void onSuccess(PackageConfigData conf) {
                            explorerViewCenterPanel.addTab( constants.SnapshotLabel( snap.name ),
                                    new SnapshotView(
                                            clientFactory,
                                            snap,
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


    /**
     * open a category
     */
    public void openCategory(String categoryName,
                             final String categoryPath) {
        final CategoryPagedTable table = new CategoryPagedTable( categoryPath,
                GWT.getModuleBaseURL()
                        + "feed/category?name="
                        + categoryPath
                        + "&viewUrl="
                        + Util.getSelfURL(),
                clientFactory );
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
                + categoryName,
                table,
                categoryPath );
    }

    private OpenItemCommand createEditEvent() {
        return new OpenItemCommand() {
            public void open(String uuid) {
//                openAsset( uuid );
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
            final AssetPagedTable table = new AssetPagedTable(
                    packageUuid,
                    formatInList,
                    formatIsRegistered,
                    feedUrl,
                    clientFactory );
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


    public void openSnapshotAssetList(final String name,
                                      final String uuid,
                                      final String[] assetTypes,
                                      String key) {
        if ( !explorerViewCenterPanel.showIfOpen( key ) ) {
            AssetPagedTable table = new AssetPagedTable(
                    uuid,
                    Arrays.asList( assetTypes ),
                    null,
                    clientFactory );

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

    public boolean showIfOpen(String id) {
        return explorerViewCenterPanel.showIfOpen( id );
    }

    private void addTab(String title, IsWidget widget, String id) {
        explorerViewCenterPanel.addTab( title, widget, id );
    }

    public void openInboxIncomingPagedTable(String title) {
        if ( !showIfOpen( title ) ) {
            addTab( title,
                    new InboxIncomingPagedTable(
                            title,
                            clientFactory ),
                    title );
        }
    }

    public void openInboxPagedTable(String title) {
        if ( !showIfOpen( title ) ) {
            addTab( title,
                    new InboxPagedTable(
                            title,
                            clientFactory ),
                    title );
        }
    }

    public void openStatePagedTable(final String stateName) {
        final StatePagedTable table = new StatePagedTable(
                stateName,
                clientFactory );

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

        addTab( constants.Status()
                + stateName,
                table,
                stateName );
    }
}
