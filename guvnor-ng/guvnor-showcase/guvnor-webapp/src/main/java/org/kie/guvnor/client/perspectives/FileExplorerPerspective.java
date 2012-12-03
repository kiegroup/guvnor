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
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "FileExplorerPerspective", isDefault = true)
public class FileExplorerPerspective {

    @Inject
    private PlaceManager placeManager;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName("File Explorer");

        p.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("RepositoriesEditor")));

        final PanelDefinition west = new PanelDefinitionImpl();
        west.setWidth(300);
        west.setMinWidth(200);
        west.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("FileExplorer")));

        p.getRoot().insertChild(Position.WEST, west);


        return p;
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();
        menuBar.addItem(new DefaultMenuItemSubMenu("New", subMenuBar));

        subMenuBar.addItem(new DefaultMenuItemCommand("Folder", new Command() {
            @Override
            public void execute() {
                placeManager.goTo("newFolderPopup");
            }
        }));

        subMenuBar.addItem(new DefaultMenuItemCommand("pom.xml", new Command() {
            @Override
            public void execute() {
                // TODO Create pom.xml
                // TODO Go to pom.xml editor
//                placeManager.goTo("newProjectPopup");
            }
        }));

        return menuBar;
    }
}
