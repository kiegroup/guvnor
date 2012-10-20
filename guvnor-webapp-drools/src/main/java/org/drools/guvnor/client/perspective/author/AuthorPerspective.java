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

package org.drools.guvnor.client.perspective.author;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.admin.AdminTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.deployment.DeploymentTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.qa.QATreeBuilder;
import org.drools.guvnor.client.perspective.Perspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

@ApplicationScoped
public class AuthorPerspective implements Perspective {

    public final static String AUTHOR_PERSPECTIVE = "AuthorPerspective";

    @Inject
    PlaceManager placeManager;

    @Inject
    Identity identity;

    public AuthorPerspective() {
    }

    @Override
    public Collection<NavigationItemBuilder> getBuilders(ClientFactory clientFactory, EventBus eventBus) {

        Collection<NavigationItemBuilder> navigationItemBuilders = new ArrayList<NavigationItemBuilder>();

        navigationItemBuilders.add(new BrowseTreeBuilder(clientFactory, identity));

        navigationItemBuilders.add(new ModulesTreeBuilder(clientFactory, eventBus, identity, AUTHOR_PERSPECTIVE));

        navigationItemBuilders.add(new QATreeBuilder(placeManager, identity));

        navigationItemBuilders.add(new DeploymentTreeBuilder(placeManager, identity));

        navigationItemBuilders.add(new AdminTreeBuilder(placeManager, identity));

        return navigationItemBuilders;
    }
}
