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

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;

public class KnowledgeModulesTreeBuilder extends NavigationItemBuilder {

    private final ClientFactory clientFactory;
    private KnowledgeModulesTree knowledgeModulesTree;

    public KnowledgeModulesTreeBuilder( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;
    }

    @Override
    public boolean hasPermissionToBuild() {
        return UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_KNOWLEDGE_BASES_VIEW );
    }

    @Override
    public IsWidget getHeader() {
        return clientFactory.getNavigationViewFactory().getKnowledgeModulesHeaderView();
    }

    @Override
    public IsWidget getContent() {
        if ( knowledgeModulesTree == null ) {
            createKnowledgeModuleTree();
        }
        return clientFactory.getNavigationViewFactory().getKnowledgeModulesTreeView();
    }

    private void createKnowledgeModuleTree() {
        knowledgeModulesTree = new KnowledgeModulesTree( clientFactory );
    }
}
