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

package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;

public class BrowseTreeBuilder extends NavigationItemBuilder {

    private BrowseTree browseTree;
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public BrowseTreeBuilder( ClientFactory clientFactory, EventBus eventBus ) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    @Override
    public boolean hasPermissionToBuild() {
        return true;
    }

    @Override
    public IsWidget getHeader() {
        return clientFactory.getNavigationViewFactory().getBrowseHeaderView();
    }

    @Override
    public IsWidget getContent() {
        return getBrowseTree().getView();
    }

    private BrowseTree getBrowseTree() {
        if ( browseTree == null ) {
            createNewBrowseTree();
        }
        return browseTree;
    }

    public void createNewBrowseTree() {
        browseTree = new BrowseTree(
                clientFactory, eventBus
        );
    }
}
