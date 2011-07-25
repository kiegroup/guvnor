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

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.common.StackItemHeader;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.explorer.MultiAssetView;
import org.drools.guvnor.client.explorer.MultiAssetViewImpl;
import org.drools.guvnor.client.explorer.navigation.admin.AdminTreeView;
import org.drools.guvnor.client.explorer.navigation.browse.*;
import org.drools.guvnor.client.explorer.navigation.deployment.DeploymentTreeView;
import org.drools.guvnor.client.explorer.navigation.modules.*;
import org.drools.guvnor.client.explorer.navigation.qa.QATreeView;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

public class NavigationViewFactoryImpl implements NavigationViewFactory {


    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private NavigationPanelView navigationPanelView;
    private KnowledgeModulesTreeViewImpl knowledgeModulesTreeView;
    private BrowseTreeViewImpl browseTreeView;
    private KnowledgeModulesTreeItemViewImpl knowledgeModulesTreeItemView;
    private ModulesNewAssetMenuViewImpl modulesNewAssetMenuView;

    public NavigationPanelView getNavigationPanelView() {
        if ( navigationPanelView == null ) {
            navigationPanelView = new NavigationPanelViewImpl();
        }
        return navigationPanelView;
    }

    public BrowseHeaderView getBrowseHeaderView() {
        return new BrowseHeaderViewImpl();
    }

    public BrowseTreeView getBrowseTreeView() {
        if ( browseTreeView == null ) {
            browseTreeView = new BrowseTreeViewImpl();
        }
        return browseTreeView;
    }

    public AdminTreeView getAdminTreeView() {
        return null;  //TODO: Generated code -Rikkola-
    }

    public DeploymentTreeView getDeploymentTreeView() {
        return null;  //TODO: Generated code -Rikkola-
    }

    public QATreeView getQATreeView() {
        return null;  //TODO: Generated code -Rikkola-
    }

    public KnowledgeModulesTreeView getKnowledgeModulesTreeView() {
        if ( knowledgeModulesTreeView == null ) {
            knowledgeModulesTreeView = new KnowledgeModulesTreeViewImpl();
        }
        return knowledgeModulesTreeView;
    }

    public RepositoryServiceAsync getRepositoryService() {
        return RepositoryServiceFactory.getService();
    }

    public CategoryServiceAsync getCategoryService() {
        return RepositoryServiceFactory.getCategoryService();
    }

    public IsWidget getKnowledgeModulesHeaderView() {
        StackItemHeaderViewImpl view = new StackItemHeaderViewImpl();
        StackItemHeader header = new StackItemHeader( view );
        header.setName( constants.KnowledgeBases() );
        header.setImageResource( images.packages() );
        return view;
    }

    public KnowledgeModulesTreeItemView getKnowledgeModulesTreeItemView() {
        if ( knowledgeModulesTreeItemView == null ) {
            knowledgeModulesTreeItemView = new KnowledgeModulesTreeItemViewImpl();
        }
        return knowledgeModulesTreeItemView;
    }

    public ModulesNewAssetMenuView getModulesNewAssetMenuView() {
        if ( modulesNewAssetMenuView == null ) {
            modulesNewAssetMenuView = new ModulesNewAssetMenuViewImpl();
        }
        return modulesNewAssetMenuView;
    }

    public GlobalAreaTreeItemView getGlobalAreaTreeItemView() {
        return new GlobalAreaTreeItemViewImpl();
    }

    public ModuleTreeItemView getModuleTreeItemView() {
        return new ModuleTreeItemViewImpl();
    }

    public RulesNewMenuView getRulesNewMenuView() {
        return new RulesNewMenuViewImpl();
    }

    public MultiAssetView getMultiAssetView() {
        return new MultiAssetViewImpl();
    }
}
