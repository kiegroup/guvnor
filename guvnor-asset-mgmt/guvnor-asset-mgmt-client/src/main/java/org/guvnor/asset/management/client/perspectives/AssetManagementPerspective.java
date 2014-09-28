/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.guvnor.asset.management.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * A Perspective
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Asset Management", isDefault = false)
public class AssetManagementPerspective {


    @Inject
    private PlaceManager placeManager;

    private Menus menus;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(PanelType.ROOT_TAB);
        perspective.setName("Asset Management");

        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("Repository Configuration")));
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("Promote Changes")));
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("Build Management")));
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("Release Management")));

        perspective.setTransient(true);
        return perspective;
    }

    @PostConstruct
    public void init() {
        buildMenuBar();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu("Screens")
                .menus()
                .menu("Repository Configuration")
                .respondsWith(
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo("Repository Configuration");
                            }
                        })
                .endMenu()
                .menu("Promote Changes")
                .respondsWith(
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo("Promote Changes");
                            }
                        })
                .endMenu()
                .menu("Build Management")
                .respondsWith(
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo("Build Management");
                            }
                        })
                .endMenu()
                .menu("Release Management")
                .respondsWith(
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo("Release Management");
                            }
                        })
                .endMenu()
                .endMenus()
                .endMenu()
                .build();

    }
}
