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

public class BrowseTreeBuilder extends NavigationItemBuilder {

    private AuthorNavigationViewFactory navigationViewFactory;
    private BrowseTree browseTree;

    @Override
    public boolean hasPermissionToBuild() {
        return true;
    }

    @Override
    public IsWidget getHeader() {
        return navigationViewFactory.getBrowseHeaderView();
    }

    @Override
    public IsWidget getContent() {
        return getBrowseTree().getView();
    }

    private BrowseTree getBrowseTree() {
        if (browseTree == null) {
            createNewBrowseTree();
        }
        return browseTree;
    }

    public void createNewBrowseTree() {
        BrowseTreeView view = navigationViewFactory.getBrowseTreeView();
        browseTree = new BrowseTree(
                view,
                navigationViewFactory.getRepositoryService(),
                navigationViewFactory.getCategoryService()
        );
    }

    @Override
    public void setViewFactory(AuthorNavigationViewFactory navigationViewFactory) {
        this.navigationViewFactory = navigationViewFactory;
    }
}
