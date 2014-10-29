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
package org.guvnor.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import org.guvnor.client.resources.i18n.AppConstants;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.structure.client.editors.repository.clone.CloneRepositoryForm;
import org.guvnor.structure.client.editors.repository.create.CreateRepositoryForm;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.client.menu.CustomSplashHelp;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

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
                final CloneRepositoryForm cloneRepositoryWizard = iocManager.lookupBean( CloneRepositoryForm.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                cloneRepositoryWizard.addCloseHandler( new CloseHandler<PopupPanel>() {

                    @Override
                    public void onClose( CloseEvent<PopupPanel> event ) {
                        iocManager.destroyBean( cloneRepositoryWizard );
                    }

                } );
                cloneRepositoryWizard.show();
            }

        };

        this.newRepoCommand = new Command() {
            @Override
            public void execute() {
                final CreateRepositoryForm newRepositoryWizard = iocManager.lookupBean( CreateRepositoryForm.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                newRepositoryWizard.addCloseHandler( new CloseHandler<CreateRepositoryForm>() {
                    @Override
                    public void onClose( CloseEvent<CreateRepositoryForm> event ) {
                        iocManager.destroyBean( newRepositoryWizard );
                    }
                } );
                newRepositoryWizard.show();
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
                .newTopLevelMenu( "Authoring" ).respondsWith(new Command() {
                    @Override
                    public void execute() {
                        if ( defaultPerspective != null ) {
                            placeManager.goTo(new DefaultPlaceRequest("AuthoringPerspective"));
                        } else {
                            Window.alert( " perspective not found." );
                        }
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Artifacts" ).respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest("org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective"));
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Wires" )
                .menus()
                .menu("Scratch Pad")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                            placeManager.goTo(new DefaultPlaceRequest("WiresScratchPadPerspective"));
                    }
                } )
                .endMenu()
                .menu("Trees")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest("WiresTreesPerspective"));
                    }
                } )
                .endMenu()
                .menu("Bayesian networks")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest("WiresBayesianPerspective"));
                    }
                } )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "Workbench" )
                .menus()
                .menu("Apps")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest("AppsPerspective"));
                    }
                } )
                .endMenu()
                .menu("Perspectives")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                            placeManager.goTo(new DefaultPlaceRequest("PerspectiveEditorPerspective"));
                    }
                } )
                .endMenu()
                .menu("Plugins")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest("PlugInAuthoringPerspective"));
                    }
                } )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.Logout() )
                .respondsWith( new LogoutCommand() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.Find() )
                .position( MenuPosition.RIGHT )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "FindForm" );
                    }
                } )
                .endMenu()
                .newTopLevelCustomMenu( iocManager.lookupBean( CustomSplashHelp.class ).getInstance() )
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
            authService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                    final String location = GWT.getModuleBaseURL().replaceFirst("/" + GWT.getModuleName() + "/",  "/logout.jsp");
                    redirect( location );
                }
            } ).logout();
        }
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}