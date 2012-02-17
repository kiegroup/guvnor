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

package org.drools.guvnor.client.explorer;

import java.util.Map;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.navigation.modules.Folder;
import org.drools.guvnor.client.explorer.navigation.modules.PackageView;
import org.drools.guvnor.client.explorer.navigation.modules.PackageHierarchicalView;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.util.Util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/*
 * This class contains static node config for BRMS' explorer widgets
 */
public class ExplorerNodeConfig {

    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    // Browse
     public static final String CATEGORY_ID = "category";                                 // NON-NLS
    public static final String RECENT_EDITED_ID = "recentEdited";
    public static final String RECENT_VIEWED_ID = "recentViewed";
    public static final String INCOMING_ID = "incoming";

    // QA
    public static final String TEST_SCENARIOS_ID = "testScenarios";
    public static final String TEST_SCENARIOS_ROOT_ID = "roottestScenarios";
    public static final String ANALYSIS_ID = "analysis";
    public static final String ANALYSIS_ROOT_ID = "rootanalysis";

    // Table configurations
    public static final String RULE_LIST_TABLE_ID = "rulelist";
    public static final String PACKAGEVIEW_LIST_TABLE_ID = "packageviewlist";

    // Package snapshot
    public static final String PACKAGE_SNAPSHOTS = "packageSnapshots";

    public static void setupDeploymentTree( Tree tree, Map<TreeItem, String> itemWidgets ) {
        TreeItem root = tree.addItem( Util.getHeader( images.chartOrganisation(), constants.PackageSnapshots() ) );
        root.setState( true );
        itemWidgets.put( root, PACKAGE_SNAPSHOTS );
        deploymentListPackages( root );
    }

    private static void deploymentListPackages( final TreeItem root ) {
        RepositoryServiceFactory.getPackageService().listPackages( new GenericCallback<PackageConfigData[]>() {
            public void onSuccess( PackageConfigData[] values ) {
                PackageView ph = new PackageHierarchicalView();

                for (PackageConfigData val : values) {
                    ph.addPackage( val );
                }
                for (Folder hf : ph.getRootFolder().getChildren()) {
                    buildDeploymentTree( root, hf );
                }
            }
        } );
    }

    private static void buildDeploymentTree( TreeItem root, Folder fldr ) {
        if ( fldr.getPackageConfigData() != null ) {
            TreeItem pkg = new TreeItem( Util.getHeader( images.snapshotSmall(), fldr.getPackageConfigData().name ) );
            pkg.setUserObject( fldr.getPackageConfigData() );
            pkg.addItem( new TreeItem( constants.PleaseWaitDotDotDot() ) );
            root.addItem( pkg );
        } else {
            TreeItem tn = new TreeItem( Util.getHeader( images.emptyPackage(), fldr.getFolderName() ) );
            root.addItem( tn );
            for (Folder c : fldr.getChildren()) {
                buildDeploymentTree( tn, c );
            }
        }
    }

    private static void doCategoryNode( final TreeItem treeItem, final String path, final Map<TreeItem, String> itemWidgets ) {
        infanticide( treeItem );
        RepositoryServiceFactory.getCategoryService().loadChildCategories( path, createGenericCallbackForLoadChildCategories( treeItem, path, itemWidgets ) );
    }

    private static GenericCallback<String[]> createGenericCallbackForLoadChildCategories( final TreeItem treeItem, final String path, final Map<TreeItem, String> itemWidgets ) {
        return new GenericCallback<String[]>() {
            public void onSuccess( String[] value ) {
                if ( value.length == 0 ) {
                    infanticide( treeItem );
                } else {
                    createChildNodes( treeItem, path, itemWidgets, value );
                }
            }

            private void createChildNodes( final TreeItem treeItem, final String path, final Map<TreeItem, String> itemWidgets, String[] value ) {
                for (int i = 0; i < value.length; i++) {

                    final String current = value[i];
                    final TreeItem childNode = new TreeItem( Util.getHeader( images.categorySmall(), current ) );

                    //ID for category tabs. 
                    String widgetId = CATEGORY_ID + "-" + ((path.equals( "/" )) ? current : path + "/" + current);
                    itemWidgets.put( childNode, widgetId );
                    treeItem.addItem( childNode );

                    childNode.addItem( new TreeItem( Util.getHeader( images.categorySmall(), constants.PleaseWaitDotDotDot() ) ) );
                    childNode.getTree().addOpenHandler( createOpenHandlerForTree( itemWidgets, childNode ) );
                }
            }

            private OpenHandler<TreeItem> createOpenHandlerForTree( final Map<TreeItem, String> itemWidgets, final TreeItem childNode ) {
                return new OpenHandler<TreeItem>() {
                    boolean expanding = false;

                    public void onOpen( OpenEvent<TreeItem> event ) {
                        if ( !expanding && event.getTarget() == childNode ) {
                            expanding = true;
                            String widgetID = itemWidgets.get( event.getTarget() );
                            String path = widgetID.substring( widgetID.indexOf( "-" ) + 1 );
                            infanticide( childNode );
                            doCategoryNode( childNode, path, itemWidgets );
                            expanding = false;
                        }
                    }
                };
            }

        };
    }

    private static void infanticide( final TreeItem treeNode ) {
        treeNode.removeItems();
    }

    public static Tree getQAStructure( final Map<TreeItem, String> itemWidgets ) {
        Tree tree = new Tree();
        tree.setAnimationEnabled( true );

        final TreeItem scenarios = new TreeItem( Util.getHeader( images.testManager(), constants.TestScenariosInPackages() ) );
        scenarios.addItem( new TreeItem( constants.PleaseWaitDotDotDot() ) );
        tree.addItem( scenarios );
        itemWidgets.put( scenarios, TEST_SCENARIOS_ROOT_ID );

        final TreeItem analysis = new TreeItem( Util.getHeader( images.analyze(), constants.Analysis() ) );
        analysis.addItem( new TreeItem( constants.PleaseWaitDotDotDot() ) );
        itemWidgets.put( analysis, ANALYSIS_ROOT_ID );

        if ( ApplicationPreferences.showVerifier() ) {
            tree.addItem( analysis );
        }

        return tree;
    }
}
