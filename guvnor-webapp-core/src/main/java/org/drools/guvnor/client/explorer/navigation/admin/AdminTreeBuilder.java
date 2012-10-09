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

package org.drools.guvnor.client.explorer.navigation.admin;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.common.StackItemHeader;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class AdminTreeBuilder extends NavigationItemBuilder {

    private final AdministrationTree administrationTree;

    @Inject
    public AdminTreeBuilder(PlaceManager placeManager) {
        administrationTree = new AdministrationTree(placeManager);
    }

    @Override
    public boolean hasPermissionToBuild() {
        return UserCapabilities.INSTANCE.hasCapability(Capability.SHOW_ADMIN);
    }

    @Override
    public IsWidget getHeader() {
        StackItemHeaderViewImpl view = new StackItemHeaderViewImpl();
        StackItemHeader header = new StackItemHeader(view);
        header.setName(administrationTree.getName());
        header.setImageResource(administrationTree.getImage());
        return view;
    }

    @Override
    public IsWidget getContent() {
        return administrationTree.createContent();
    }

}
