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

import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

public class NavigationViewFactoryImpl implements NavigationViewFactory {

    NavigationPanelView navigationPanelView;

    public NavigationPanelView getNavigationPanelView() {
        if (navigationPanelView == null) {
            navigationPanelView = new NavigationPanelViewImpl();
        }
        return navigationPanelView;
    }

    public BrowseHeaderView getBrowseHeaderView() {
        return new BrowseHeaderViewImpl();
    }

    public BrowseTreeView getBrowseTreeView() {
        return new BrowseTreeViewImpl();
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

    public KnowledgeBasesTreeView getKnowledgeBasesTreeView() {
        return null;  //TODO: Generated code -Rikkola-
    }

    public RepositoryServiceAsync getRepositoryService() {
        return RepositoryServiceFactory.getService();
    }

    public CategoryServiceAsync getCategoryService() {
        return RepositoryServiceFactory.getCategoryService();
    }
}
