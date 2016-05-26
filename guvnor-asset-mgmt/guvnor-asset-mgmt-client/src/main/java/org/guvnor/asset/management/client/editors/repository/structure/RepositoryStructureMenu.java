/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.repository.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.asset.management.client.editors.repository.structure.configure.ConfigureScreenPopupViewImpl;
import org.guvnor.asset.management.client.editors.repository.structure.promote.PromoteScreenPopupViewImpl;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupViewImpl;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.shared.security.impl.KieWorkbenchACLImpl;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;

import static org.guvnor.asset.management.security.AssetsMgmtFeatures.*;

@Dependent
public class RepositoryStructureMenu
        implements org.uberfire.workbench.model.menu.Menus {

    /**
     * WM, Impl class was injected here due to an errai IOC issue. I we inject just KieWorkbenchACL then
     * we have errors at ProjectScreenPresenter when the webapp is being built. So it was decided to just us the Impl
     * class here.
     */
    private final KieWorkbenchACLImpl kieACL;
    private final ProjectContext projectContext;

    private final ReleaseScreenPopupViewImpl   releaseScreenPopupView;
    private final ConfigureScreenPopupViewImpl configureScreenPopupView;
    private final PromoteScreenPopupViewImpl   promoteScreenPopupView;


    public enum MenuItems {
        CONFIGURE_MENU_ITEM,
        PROMOTE_MENU_ITEM,
        RELEASE_MENU_ITEM;
    }

    private final List<MenuItem> items = new ArrayList<MenuItem>();

    private MenuItem configure;
    private MenuItem release;
    private MenuItem promote;

    private boolean promoteIsGranted = false;
    private boolean configureIsGranted = false;
    private boolean releaseIsGranted = false;

    private Caller<AssetManagementService> assetManagementServices;


    @Inject
    public RepositoryStructureMenu( final KieWorkbenchACLImpl kieACL,
                                    final ProjectContext projectContext,
                                    final Caller<AssetManagementService> assetManagementServices,
                                    final ReleaseScreenPopupViewImpl releaseScreenPopupView,
                                    final ConfigureScreenPopupViewImpl configureScreenPopupView,
                                    final PromoteScreenPopupViewImpl promoteScreenPopupView ) {
        this.kieACL = kieACL;
        this.projectContext = projectContext;
        this.assetManagementServices = assetManagementServices;
        this.releaseScreenPopupView = releaseScreenPopupView;
        this.configureScreenPopupView = configureScreenPopupView;
        this.promoteScreenPopupView = promoteScreenPopupView;
    }

    public void init( final HasModel<RepositoryStructureModel> hasModel ) {
        // TODO: ask for the model
        configure = MenuFactory
                .newTopLevelMenu( Constants.INSTANCE.Configure() )
                .withRoles( kieACL.getGrantedRoles( CONFIGURE_REPOSITORY ) )
                .respondsWith( getConfigureCommand( hasModel ) )
                .endMenu()
                .build().getItems().get( 0 );

        promote = MenuFactory
                .newTopLevelMenu( Constants.INSTANCE.Promote() )
                .withRoles( kieACL.getGrantedRoles( PROMOTE_ASSETS ) )
                .respondsWith( getPromoteCommand() )
                .endMenu()
                .build().getItems().get( 0 );

        release = MenuFactory
                .newTopLevelMenu( Constants.INSTANCE.Release() )
                .withRoles( kieACL.getGrantedRoles( RELEASE_PROJECT ) )
                .respondsWith( getReleaseCommand( hasModel ) )
                .endMenu()
                .build().getItems().get( 0 );

        items.add( configure );
        items.add( promote );
        items.add( release );


        MenuItem item;
        item = getItem( MenuItems.CONFIGURE_MENU_ITEM );
        configureIsGranted = item != null && item.isEnabled();

        item = getItem( MenuItems.PROMOTE_MENU_ITEM );
        promoteIsGranted = item != null && item.isEnabled();

        item = getItem( MenuItems.RELEASE_MENU_ITEM );
        releaseIsGranted = item != null && item.isEnabled();

    }

    private Command getConfigureCommand( final HasModel<RepositoryStructureModel> hasModel ) {
        return new Command() {
            @Override
            public void execute() {
                final RepositoryStructureModel model = hasModel.getModel();

                if ( model != null && (model.isSingleProject() || model.isMultiModule()) ) {
                    configureScreenPopupView.configure( projectContext.getActiveRepository().getAlias(),
                                                        projectContext.getActiveBranch(),
                                                        model.getActivePom().getGav().getVersion(),
                                                        new com.google.gwt.user.client.Command() {
                                                            @Override
                                                            public void execute() {
                                                                configureRepository();
                                                                configureScreenPopupView.hide();
                                                            }
                                                        } );
                    configureScreenPopupView.show();
                }
            }
        };
    }

    private Command getPromoteCommand() {
        return new Command() {
            @Override
            public void execute() {
                promoteScreenPopupView.configure( projectContext.getActiveRepository().getAlias(),
                                                  projectContext.getActiveBranch(),
                                                  projectContext.getActiveRepository().getBranches(),
                                                  new com.google.gwt.user.client.Command() {
                                                      @Override
                                                      public void execute() {
                                                          promoteChanges();
                                                          promoteScreenPopupView.hide();
                                                      }
                                                  } );
                promoteScreenPopupView.show();
            }
        };
    }

    private Command getReleaseCommand( final HasModel<RepositoryStructureModel> hasModel ) {
        return new Command() {
            @Override
            public void execute() {
                final RepositoryStructureModel model = hasModel.getModel();
                if ( model != null && (model.isSingleProject() || model.isMultiModule()) ) {
                    releaseScreenPopupView.configure( projectContext.getActiveRepository().getAlias(),
                                                      projectContext.getActiveBranch(),
                                                      trimSnapshotFromVersion( model.getActivePom().getGav().getVersion() ),
                                                      model.getActivePom().getGav().getVersion(),
                                                      new com.google.gwt.user.client.Command() {
                                                          @Override
                                                          public void execute() {
                                                              releaseProject();
                                                              releaseScreenPopupView.hide();
                                                          }
                                                      } );
                    releaseScreenPopupView.show();
                }
            }
        };
    }

    private String trimSnapshotFromVersion( final String version ) {
        if ( version != null && version.endsWith( "-SNAPSHOT" ) ) {
            return version.replace( "-SNAPSHOT", "" );
        } else {
            return version;
        }
    }


    private void configureRepository() {
        assetManagementServices.call( new RemoteCallback<Long>() {
                                          @Override
                                          public void callback( Long taskId ) {
                                              //view.displayNotification( "Repository Configuration Started!" );
                                          }
                                      },
                                      new ErrorCallback<Message>() {
                                          @Override
                                          public boolean error( Message message,
                                                                Throwable throwable ) {
                                              ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      } ).configureRepository( projectContext.getActiveRepository().getAlias(),
                                                               projectContext.getActiveBranch(),
                                                               configureScreenPopupView.getDevBranch(),
                                                               configureScreenPopupView.getReleaseBranch(),
                                                               configureScreenPopupView.getVersion() );
    }

    private void promoteChanges() {
        assetManagementServices.call( new RemoteCallback<Long>() {
                                          @Override
                                          public void callback( Long taskId ) {
                                              // view.displayNotification( "Promote Changes Process Started!" );
                                          }
                                      },
                                      new ErrorCallback<Message>() {
                                          @Override
                                          public boolean error( Message message,
                                                                Throwable throwable ) {

                                              ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      } ).promoteChanges( projectContext.getActiveRepository().getAlias(),
                                                          projectContext.getActiveBranch(),
                                                          promoteScreenPopupView.getTargetBranch() );

    }

    private void releaseProject() {
        assetManagementServices.call( new RemoteCallback<Long>() {
                                          @Override
                                          public void callback( Long taskId ) {
//                                              view.displayNotification( "Release project process started" );
                                          }
                                      },
                                      new ErrorCallback<Message>() {
                                          @Override
                                          public boolean error( Message message,
                                                                Throwable throwable ) {
                                              ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      } ).releaseProject( projectContext.getActiveRepository().getAlias(),
                                                          projectContext.getActiveBranch(),
                                                          releaseScreenPopupView.getUsername(),
                                                          releaseScreenPopupView.getPassword(),
                                                          getServerURL(),
                                                          releaseScreenPopupView.getDeployToRuntime(),
                                                          releaseScreenPopupView.getVersion() );
    }

    private String getServerURL() {
        final String serverURL = releaseScreenPopupView.getServerURL();

        if ( serverURL != null && !serverURL.isEmpty() && serverURL.endsWith( "/" ) ) {
            return serverURL.substring( 0,
                                        serverURL.length() - 1 );
        } else {
            return serverURL;
        }
    }

    private MenuItem getItem( final MenuItems itemKey ) {
        return this.getItemsMap().get( itemKey );
    }

    @Override
    public List<MenuItem> getItems() {
        return items;
    }

    @Override
    public Map<Object, MenuItem> getItemsMap() {

        return new HashMap<Object, MenuItem>() {
            {
                put( MenuItems.CONFIGURE_MENU_ITEM,
                     configure );
                put( MenuItems.PROMOTE_MENU_ITEM,
                     promote );
                put( MenuItems.RELEASE_MENU_ITEM,
                     release );
            }
        };
    }

    @Override
    public void accept( MenuVisitor visitor ) {
        if ( visitor.visitEnter( this ) ) {
            for ( final MenuItem item : items ) {
                item.accept( visitor );
            }
            visitor.visitLeave( this );
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void enableAssetsManagementMenu( final boolean enable ) {
        enableConfigure( configureIsGranted && enable );
        enablePromote( promoteIsGranted && enable );
        enableRelease( releaseIsGranted && enable );
    }

    private void enableConfigure( final boolean enable ) {
        configure.setEnabled( enable );
    }

    private void enablePromote( final boolean enable ) {
        promote.setEnabled( enable );
    }

    private void enableRelease( final boolean enable ) {
        release.setEnabled( enable );
    }
}
