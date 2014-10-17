/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;

/**
 * A Perspective to show M2_REPO related screen
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "org.guvnor.DefaultPerspective", isDefault = true)
public class DemoPerspective {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    private PerspectiveDefinition perspective;
    private Menus menus;

    @PostConstruct
    private void init() {
        buildPerspective();
        buildMenuBar();
    }

    private void buildPerspective() {
        this.perspective = new PerspectiveDefinitionImpl(StaticWorkbenchPanelPresenter.class.getName() );
        this.perspective.setName("Keycloak perspective");

        this.perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("settingsScreen")));

        final PanelDefinition west = new PanelDefinitionImpl(SimpleWorkbenchPanelPresenter.class.getName());
        west.setWidth(300);
        west.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("defaultLHSMenu")));

        this.perspective.getRoot().insertChild(CompassPosition.WEST, west);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    @OnStartup
    public void onStartup() {

    }

    private void buildMenuBar() {
        this.menus = MenuFactory.newTopLevelMenu("Add Realm")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {

                    }
                })
                .endMenu()
                .build();
    }

}
