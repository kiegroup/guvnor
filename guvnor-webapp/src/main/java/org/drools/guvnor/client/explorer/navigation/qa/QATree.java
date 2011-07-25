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

package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilderOld;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.util.Util;

import java.util.Iterator;
import java.util.Map;

public class QATree extends NavigationItemBuilderOld
        implements
        OpenHandler<TreeItem> {
    private static Constants constants = GWT.create( Constants.class );
    private static Images images = (Images) GWT.create( Images.class );
    private final ClientFactory clientFactory;

    public QATree(ClientFactory clientFactory) {

        this.clientFactory = clientFactory;

        //Add Selection listener
        mainTree.addSelectionHandler( this );
        mainTree.addOpenHandler( this );
    }

    public MenuBar createMenu() {
        return null;
    }

    public Tree createTree() {
        return ExplorerNodeConfig.getQAStructure( itemWidgets );
    }

    public String getName() {
        return constants.QA1();
    }

    public ImageResource getImage() {
        return images.analyze();
    }

    public IsWidget createContent() {
        return this;
    }

    public void refreshTree() {
        //TODO: Generated code -Rikkola-
    }

    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();

        if ( item.getUserObject() instanceof PackageConfigData ) {
            PackageConfigData pc = (PackageConfigData) item.getUserObject();
            String id = itemWidgets.get( item );


            if ( ExplorerNodeConfig.TEST_SCENARIOS_ID.equals( id ) ) {

                clientFactory.getPlaceController().goTo( new TestScenarioListPlace( pc.uuid ) );

            } else if ( ExplorerNodeConfig.ANALYSIS_ID.equals( id ) ) {
                clientFactory.getPlaceController().goTo( new VerifierPlace( pc.uuid ) );
            }
        }
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem node = event.getTarget();
        if ( ExplorerNodeConfig.TEST_SCENARIOS_ROOT_ID.equals( itemWidgets.get( node ) ) ) {
            RepositoryServiceFactory.getPackageService().listPackages( new GenericCallback<PackageConfigData[]>() {
                public void onSuccess(PackageConfigData[] conf) {
                    node.removeItems();
                    removeTestScenarioIDs( itemWidgets );

                    for (int i = 0; i < conf.length; i++) {
                        final PackageConfigData c = conf[i];
                        TreeItem pkg = new TreeItem( Util.getHeader( images.packages(),
                                c.name ) );

                        node.addItem( pkg );
                        pkg.setUserObject( c );
                        itemWidgets.put( pkg,
                                ExplorerNodeConfig.TEST_SCENARIOS_ID );
                    }
                }
            } );
        } else if ( ExplorerNodeConfig.ANALYSIS_ROOT_ID.equals( itemWidgets.get( node ) ) ) {
            RepositoryServiceFactory.getPackageService().listPackages( new GenericCallback<PackageConfigData[]>() {
                public void onSuccess(PackageConfigData[] conf) {
                    node.removeItems();
                    removeAnalysisIDs( itemWidgets );
                    for (int i = 0; i < conf.length; i++) {
                        final PackageConfigData c = conf[i];
                        TreeItem pkg = new TreeItem( Util.getHeader( images.packages(),
                                c.name ) );

                        node.addItem( pkg );
                        pkg.setUserObject( c );
                        itemWidgets.put( pkg,
                                ExplorerNodeConfig.ANALYSIS_ID );
                    }
                }
            } );
        }
    }

    private void removeTestScenarioIDs(Map<TreeItem, String> itemWidgets) {
        for (Iterator<Map.Entry<TreeItem, String>> it = itemWidgets.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<TreeItem, String> entry = it.next();
            TreeItem item = entry.getKey();
            String id = entry.getValue();
            if ( ExplorerNodeConfig.TEST_SCENARIOS_ID.equals( id ) ) {
                it.remove();
            }
        }
    }

    private void removeAnalysisIDs(Map<TreeItem, String> itemWidgets) {
        for (Iterator<Map.Entry<TreeItem, String>> it = itemWidgets.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<TreeItem, String> entry = it.next();
            TreeItem item = entry.getKey();
            String id = entry.getValue();
            if ( ExplorerNodeConfig.ANALYSIS_ID.equals( id ) ) {
                it.remove();
            }
        }
    }
}
