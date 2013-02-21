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
package org.kie.guvnor.m2repo.client.perspectives;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.guvnor.commons.ui.client.handlers.NewResourceHandler;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.client.workbench.widgets.toolbar.IconType;
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBarItem;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import static org.uberfire.client.workbench.widgets.menu.MenuFactory.*;

/**
 * A Perspective to show Guvnor's M2_REPO related screens
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "GuvnorM2RepoPerspective", isDefault = false)
public class GuvnorM2RepoPerspective {

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected WorkbenchContext context;

    @Inject
    private IOCBeanManager iocBeanManager;

    @Inject
    private NewResourcePresenter newResourcePresenter;

    private PerspectiveDefinition perspective;
    private Menus                 menus;
    private ToolBar               toolBar;

    @PostConstruct
    public void init() {
        buildPerspective();
        buildMenuBar();
        buildToolBar();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return this.toolBar;
    }

    private void buildPerspective() {
/*        final PlaceRequest place1 = new DefaultPlaceRequest( "FileExplorer" );
        place1.addParameter( "instance",
                             "1" );*/
/*        final PlaceRequest place2 = new DefaultPlaceRequest( "M2RepoEditor" );
        place2.addParameter( "instance",
                             "2" );*/

        this.perspective = new PerspectiveDefinitionImpl();
        this.perspective.setName( "Guvnor M2 Repository Explorer" );

        this.perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "M2RepoEditor" ) ) );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        this.perspective.getRoot().insertChild( Position.WEST, west );
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu( "Explore" )
                .menus()
                .menu( "Guvnor M2 Repository Explorer" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "M2RepoEditor" );
                    }
                } )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "New" )
                .withItems( getNewMenuItems() )
                .endMenu().build();
    }

    private List<MenuItem> getNewMenuItems() {
        final Collection<IOCBeanDef<NewResourceHandler>> handlerBeans = iocBeanManager.lookupBeans( NewResourceHandler.class );
        final List<MenuItem> newItems = new ArrayList<MenuItem>( handlerBeans.size() );
        if ( handlerBeans.size() > 0 ) {
            for ( IOCBeanDef<NewResourceHandler> handlerBean : handlerBeans ) {
                final NewResourceHandler handler = handlerBean.getInstance();
                final String description = handler.getDescription();
                newItems.add( newSimpleItem( description ).respondsWith( new Command() {
                    @Override
                    public void execute() {
                        // TODO Need to get the currently selected path.
                        // This will entail adding a new ApplicationContext class to UberFire
                        // that observes PathChangeEvents raised by the FileExplorer (and others)
                        // that sets the currently selected Path.
                        handler.create( context.getActivePath(), null );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
            }
        }

        return newItems;
    }

    private void buildToolBar() {
        this.toolBar = new DefaultToolBar( "guvnor.new.item" );
        final String tooltip = "Constants.INSTANCE.newItem()";
        final Command command = new Command() {
            @Override
            public void execute() {
                newResourcePresenter.show();
            }
        };
        toolBar.addItem( new DefaultToolBarItem( IconType.FILE,
                                                 tooltip,
                                                 command ) );

    }

}
