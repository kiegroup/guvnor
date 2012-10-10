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
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilderOld;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.util.Util;
import org.uberfire.client.mvp.PlaceManager;

import java.util.Iterator;
import java.util.Map;

public class QATree extends NavigationItemBuilderOld
        implements
        OpenHandler<TreeItem> {

    private final PlaceManager placeManager;

    public QATree(PlaceManager placeManager) {
        this.placeManager = placeManager;

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
        return Constants.INSTANCE.QA1();
    }

    public ImageResource getImage() {
        return DroolsGuvnorImageResources.INSTANCE.analyze();
    }

    public IsWidget createContent() {
        return this;
    }

    public void refreshTree() {
        //TODO: Generated code -Rikkola-
    }

    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();

        if ( item.getUserObject() instanceof Module ) {
            Module pc = (Module) item.getUserObject();
            String id = itemWidgets.get( item );


            if ( ExplorerNodeConfig.TEST_SCENARIOS_ID.equals( id ) ) {

                placeManager.goTo( new TestScenarioListPlace( pc.getUuid() ) );

            } else if ( ExplorerNodeConfig.ANALYSIS_ID.equals( id ) ) {
                placeManager.goTo( new VerifierPlace( pc.getUuid() ) );
            }
        }
    }

    public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem node = event.getTarget();
        if ( ExplorerNodeConfig.TEST_SCENARIOS_ROOT_ID.equals( itemWidgets.get( node ) ) ) {
            ModuleServiceAsync moduleService = GWT.create(ModuleService.class);
            moduleService.listModules( new GenericCallback<Module[]>() {
                public void onSuccess(Module[] conf) {
                    node.removeItems();
                    removeTestScenarioIDs( itemWidgets );

                    for (int i = 0; i < conf.length; i++) {
                        final Module c = conf[i];
                        TreeItem pkg = new TreeItem( Util.getHeader( DroolsGuvnorImageResources.INSTANCE.packages(),
                                c.getName() ) );

                        node.addItem( pkg );
                        pkg.setUserObject( c );
                        itemWidgets.put( pkg,
                                ExplorerNodeConfig.TEST_SCENARIOS_ID );
                    }
                }
            } );
        } else if ( ExplorerNodeConfig.ANALYSIS_ROOT_ID.equals( itemWidgets.get( node ) ) ) {
            ModuleServiceAsync moduleService = GWT.create(ModuleService.class);
            moduleService.listModules( new GenericCallback<Module[]>() {
                public void onSuccess(Module[] conf) {
                    node.removeItems();
                    removeAnalysisIDs( itemWidgets );
                    for (int i = 0; i < conf.length; i++) {
                        final Module c = conf[i];
                        TreeItem pkg = new TreeItem( Util.getHeader( DroolsGuvnorImageResources.INSTANCE.packages(),
                                c.getName() ) );

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
            String id = entry.getValue();
            if ( ExplorerNodeConfig.TEST_SCENARIOS_ID.equals( id ) ) {
                it.remove();
            }
        }
    }

    private void removeAnalysisIDs(Map<TreeItem, String> itemWidgets) {
        for (Iterator<Map.Entry<TreeItem, String>> it = itemWidgets.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<TreeItem, String> entry = it.next();
            String id = entry.getValue();
            if ( ExplorerNodeConfig.ANALYSIS_ID.equals( id ) ) {
                it.remove();
            }
        }
    }
}
