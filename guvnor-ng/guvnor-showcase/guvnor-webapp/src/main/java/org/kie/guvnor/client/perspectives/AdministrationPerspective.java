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
package org.kie.guvnor.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcesMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItemSubMenu;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * A Perspective for Administrators
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "org.kie.guvnor.client.perspectives.administration")
public class AdministrationPerspective {

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private PlaceManager placeManager;

    private PerspectiveDefinition perspective;
    private MenuBar menuBar;

    @PostConstruct
    public void init() {
        buildPerspective();
        buildMenuBar();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    @WorkbenchMenu
    public MenuBar getMenuBar() {
        return this.menuBar;
    }

    private void buildPerspective() {
        this.perspective = new PerspectiveDefinitionImpl();
        this.perspective.setName( "Administration" );

        this.perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RepositoriesEditor" ) ) );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        this.perspective.getRoot().insertChild( Position.WEST, west );
    }

    private void buildMenuBar() {
        this.menuBar = new DefaultMenuBar();
        final MenuBar subMenu = new DefaultMenuBar();
        subMenu.addItem( new DefaultMenuItemCommand( "Files",
                                                     new Command() {
                                                         @Override
                                                         public void execute() {
                                                             placeManager.goTo( "FileExplorer" );
                                                         }
                                                     } ) );
        final MenuItemSubMenu subMenuItem = new DefaultMenuItemSubMenu( "Explore",
                                                                        subMenu );
        this.menuBar.addItem( subMenuItem );
        this.menuBar.addItem( newResourcesMenu );
    }

}
