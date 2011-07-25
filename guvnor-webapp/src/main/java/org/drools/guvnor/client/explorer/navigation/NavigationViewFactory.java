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

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.MultiAssetView;
import org.drools.guvnor.client.explorer.navigation.admin.AdminTreeView;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseHeaderView;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeView;
import org.drools.guvnor.client.explorer.navigation.browse.RulesNewMenuView;
import org.drools.guvnor.client.explorer.navigation.deployment.DeploymentTreeView;
import org.drools.guvnor.client.explorer.navigation.modules.*;
import org.drools.guvnor.client.explorer.navigation.qa.QATreeView;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;

public interface NavigationViewFactory {

    NavigationPanelView getNavigationPanelView();

    BrowseHeaderView getBrowseHeaderView();

    BrowseTreeView getBrowseTreeView();

    AdminTreeView getAdminTreeView();

    DeploymentTreeView getDeploymentTreeView();

    QATreeView getQATreeView();

    KnowledgeModulesTreeView getKnowledgeModulesTreeView();

    RepositoryServiceAsync getRepositoryService();

    CategoryServiceAsync getCategoryService();

    IsWidget getKnowledgeModulesHeaderView();

    KnowledgeModulesTreeItemView getKnowledgeModulesTreeItemView();

    ModulesNewAssetMenuView getModulesNewAssetMenuView();

    GlobalAreaTreeItemView getGlobalAreaTreeItemView();

    ModuleTreeItemView getModuleTreeItemView();

    RulesNewMenuView getRulesNewMenuView();

    MultiAssetView getMultiAssetView();
}
