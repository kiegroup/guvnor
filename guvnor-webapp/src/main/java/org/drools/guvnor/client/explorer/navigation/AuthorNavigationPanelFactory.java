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

public class AuthorNavigationPanelFactory extends NavigationPanelFactory {

    private final NavigationViewFactory navigationViewFactory;
    private NavigationPanel navigationPanel;

    public AuthorNavigationPanelFactory(NavigationViewFactory navigationViewFactory) {
        this.navigationViewFactory = navigationViewFactory;
    }

    public NavigationPanel createNavigationPanel() {
        navigationPanel = new NavigationPanel(navigationViewFactory.getNavigationPanelView());

        add(new BrowseTreeBuilder());

        add(new KnowledgeBasesTreeBuilder());

        add(new QATreeBuilder());

        add(new DeploymentTreeBuilder());

        add(new AdminTreeBuilder());

        return navigationPanel;
    }


    private void add(NavigationItemBuilder navigationItemBuilder) {
        if (navigationItemBuilder.hasPermissionToBuild()) {
            navigationItemBuilder.setViewFactory(navigationViewFactory);
            navigationPanel.add(navigationItemBuilder.getHeader(), navigationItemBuilder.getContent());
        }
    }
}
