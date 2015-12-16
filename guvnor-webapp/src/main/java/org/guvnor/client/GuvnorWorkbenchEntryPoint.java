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
package org.guvnor.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizard;
import org.guvnor.client.resources.i18n.AppConstants;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.structure.client.editors.repository.clone.CloneRepositoryPresenter;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.VoidCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * GWT's Entry-point for Drools Workbench
 */
@EntryPoint
public class GuvnorWorkbenchEntryPoint {

    @Inject
    private Caller<AppConfigService> appConfigService;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    private CloneRepositoryPresenter cloneRepositoryPresenter;

    private Command newRepoCommand = null;
    private Command cloneRepoCommand = null;

    @AfterInitialization
    public void startApp() {
        buildCommands();
        setupMenu();
        hideLoadingPopup();
    }

    private void buildCommands() {
        this.cloneRepoCommand = new Command() {

            @Override
            public void execute() {
                cloneRepositoryPresenter.showForm();
            }

        };

        this.newRepoCommand = new Command() {
            @Override
            public void execute() {
                final CreateRepositoryWizard newRepositoryWizard = iocManager.lookupBean( CreateRepositoryWizard.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                newRepositoryWizard.onCloseCallback( new Callback<Void>() {
                    @Override
                    public void callback( Void result ) {
                        iocManager.destroyBean( newRepositoryWizard );
                    }
                } );
                newRepositoryWizard.start();
            }
        };
    }

    private void setupMenu() {
        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();

        final Menus menus = MenuFactory
                .newTopLevelMenu( AppConstants.INSTANCE.Home() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        if ( defaultPerspective != null ) {
                            placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                        } else {
                            Window.alert( "Default perspective not found." );
                        }
                    }
                } )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.MenuOrganizationalUnits() )
                .menus()
                .menu( AppConstants.INSTANCE.MenuManageOrganizationalUnits() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );
                    }
                } )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "Repositories" )
                .menus()
                .menu( "Repositories List" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "RepositoriesEditor" );
                    }
                } )
                .endMenu()
                .menu( "Clone repository" )
                .respondsWith( cloneRepoCommand )
                .endMenu()
                .menu( "New repository" )
                .respondsWith( newRepoCommand )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "Authoring" ).respondsWith( new Command() {
                    @Override
                    public void execute() {
                        if ( defaultPerspective != null ) {
                            placeManager.goTo( new DefaultPlaceRequest( "AuthoringPerspective" ) );
                        } else {
                            Window.alert( " perspective not found." );
                        }
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Artifacts" ).respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" ) );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Wires" )
                .menus()
                .menu( "Scratch Pad" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "WiresScratchPadPerspective" ) );
                    }
                } )
                .endMenu()
                .menu( "Trees" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "WiresTreesPerspective" ) );
                    }
                } )
                .endMenu()
                .menu( "Bayesian networks" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "WiresBayesianPerspective" ) );
                    }
                } )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "Workbench" )
                .menus()
                .menu( "Apps" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "AppsPerspective" ) );
                    }
                } )
                .endMenu()
                .menu( "Plugins" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "PlugInAuthoringPerspective" ) );
                    }
                } )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "Messages" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "MessagesPerspective" ) );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Inbox" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "InboxPerspective" ) );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Assets" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "AssetsPerspective" ) );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Projects" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "ProjectsPerspective" ) );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.Logout() )
                .respondsWith( new LogoutCommand() )
                .position( MenuPosition.RIGHT )
                .endMenu()
                .build();

        menubar.addMenus( menus );
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop:
        while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    private List<AbstractWorkbenchPerspectiveActivity> getPerspectiveActivities() {

        //Get Perspective Providers
        final Set<AbstractWorkbenchPerspectiveActivity> activities = activityManager.getActivities( AbstractWorkbenchPerspectiveActivity.class );

        //Sort Perspective Providers so they're always in the same sequence!
        List<AbstractWorkbenchPerspectiveActivity> sortedActivities = new ArrayList<AbstractWorkbenchPerspectiveActivity>( activities );
        Collections.sort( sortedActivities,
                          new Comparator<AbstractWorkbenchPerspectiveActivity>() {

                              @Override
                              public int compare( AbstractWorkbenchPerspectiveActivity o1,
                                                  AbstractWorkbenchPerspectiveActivity o2 ) {
                                  return o1.getDefaultPerspectiveLayout().getName().compareTo( o2.getDefaultPerspectiveLayout().getName() );
                              }

                          } );

        return sortedActivities;
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    private class LogoutCommand implements Command {

        @Override
        public void execute() {
            authService.call( new VoidCallback() ).logout();
        }
    }

}