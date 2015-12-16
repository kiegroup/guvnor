/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.asset.management.client.editors.repository.structure.configure.ConfigureScreenPopupViewImpl;
import org.guvnor.asset.management.client.editors.repository.structure.promote.PromoteScreenPopupViewImpl;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupViewImpl;
import org.guvnor.asset.management.client.perspectives.AssetManagementPerspective;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Perspective to show Assets
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "AssetsPerspective")
public class AssetsPerspective extends AssetManagementPerspective {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ProjectMenu projectMenu;

    @Inject
    private SyncBeanManager iocManager;

    private Command releaseCommand = null;

    private Command promoteCommand = null;

    private Command configureCommand = null;

    @PostConstruct
    public void init(){
        this.releaseCommand = new Command() {
            @Override
            public void execute() {
                final ReleaseScreenPopupViewImpl releaseScreenPopupView = iocManager.lookupBean( ReleaseScreenPopupViewImpl.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                releaseScreenPopupView.addHiddenHandler( new ModalHiddenHandler() {
                    @Override
                    public void onHidden( ModalHiddenEvent evt ) {
                        iocManager.destroyBean( releaseScreenPopupView );
                    }
                });
                releaseScreenPopupView.show();
            }
        };

        this.promoteCommand = new Command() {
            @Override
            public void execute() {
                final PromoteScreenPopupViewImpl promoteScreenPopupView = iocManager.lookupBean( PromoteScreenPopupViewImpl.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                promoteScreenPopupView.addHiddenHandler( new ModalHiddenHandler() {
                    @Override
                    public void onHidden( ModalHiddenEvent evt ) {
                        iocManager.destroyBean( promoteScreenPopupView );
                    }
                });
                promoteScreenPopupView.show();
            }
        };

        this.configureCommand = new Command() {
            @Override
            public void execute() {
                final ConfigureScreenPopupViewImpl configureScreenPopupView = iocManager.lookupBean( ConfigureScreenPopupViewImpl.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                configureScreenPopupView.addHiddenHandler( new ModalHiddenHandler() {
                    @Override
                    public void onHidden( ModalHiddenEvent evt ) {
                        iocManager.destroyBean( configureScreenPopupView );
                    }
                });
                configureScreenPopupView.show();
            }
        };
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition perspective = super.getPerspective();

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "ApproveOperation Form" ) ) );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "DisplayError Form" ) ) );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "SelectAssetsToPromote Form" ) ) );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "Review Form" ) ) );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RequiresRework Form" ) ) );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "repositoryStructureScreen" ) ) );


        return perspective;
    }

    @WorkbenchMenu
    @Override
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu( "Release" )
                    .respondsWith( releaseCommand )
                .endMenu()
                .newTopLevelMenu( "Configure" )
                    .respondsWith( configureCommand )
                .endMenu()
                .newTopLevelMenu( "Promote" )
                    .respondsWith( promoteCommand )
                .endMenu()
                .build();
    }
}
