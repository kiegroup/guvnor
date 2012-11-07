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

package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.uberfire.security.Identity;

public class ModulesTreeBuilder extends NavigationItemBuilder {

    private final ClientFactory clientFactory;
    private ModulesTree modulesTree;
    private final EventBus eventBus;
    private final String perspectiveType;
    private final Identity identity;

    public ModulesTreeBuilder(ClientFactory clientFactory,
                              EventBus eventBus,
                              Identity identity,
                              String perspectiveType) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.identity = identity;
        this.perspectiveType = perspectiveType;
    }

    @Override
    public boolean hasPermissionToBuild() {
        return UserCapabilities.canSeeModulesTree(identity);
    }

    @Override
    public IsWidget getHeader() {
        return clientFactory.getNavigationViewFactory().getModulesHeaderView(this.perspectiveType);
    }

    @Override
    public IsWidget getContent() {
        if (modulesTree == null) {
            createModuleTree();
        }
        return clientFactory.getNavigationViewFactory().getModulesTreeView();
    }

    private void createModuleTree() {
        modulesTree = new ModulesTree(clientFactory, eventBus, identity, perspectiveType);
    }
}
