/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModuleRow;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.RepositoryStructureDataView;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.security.impl.KieWorkbenchACLImpl;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

import static org.guvnor.asset.management.security.AssetsMgmtFeatures.*;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.*;

@Dependent
@WorkbenchScreen(identifier = "repositoryStructureScreen")
public class RepositoryStructurePresenter
        implements RepositoryStructureView.Presenter,
                   RepositoryStructureDataView.Presenter,
                   ProjectModulesView.Presenter {

    private RepositoryStructureView view;

    private Caller<POMService> pomService;

    private Caller<RepositoryStructureService> repositoryStructureService;

    private Caller<AssetManagementService> assetManagementServices;

    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private Event<ProjectContextChangeEvent> contextChangeEvent;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    /**
     * WM, Impl class was injected here due to an errai IOC issue. I we inject just KieWorkbenchACL then
     * we have errors at ProjectScreenPresenter when the webapp is being built. So it was decided to just us the Impl
     * class here.
     */
    private KieWorkbenchACLImpl kieACL;

    private PlaceManager placeManager;

    private ProjectContext workbenchContext;

    private ProjectWizard wizard;

    private ErrorPopupPresenter errorPopup;

    private RepositoryStructureModel model;

    private Project project;

    private Project lastAddedModule;

    private Project lastDeletedModule;

    private Repository repository;

    private String branch;

    private ObservablePath pathToRepositoryStructure;

    private PlaceRequest placeRequest;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private ListDataProvider<ProjectModuleRow> dataProvider = new ListDataProvider<ProjectModuleRow>();

    private Menus menus;

    private MenuItem configure;

    private MenuItem release;

    private MenuItem promote;

    private boolean promoteIsGranted = false;

    private boolean configureIsGranted = false;

    private boolean releaseIsGranted = false;

    public enum MenuItems {
        CONFIGURE_MENU_ITEM,
        PROMOTE_MENU_ITEM,
        RELEASE_MENU_ITEM
    }

    public RepositoryStructurePresenter() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public RepositoryStructurePresenter( final RepositoryStructureView view,
                                         final Caller<POMService> pomService,
                                         final Caller<RepositoryStructureService> repositoryStructureService,
                                         final Caller<AssetManagementService> assetManagementService,
                                         final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent,
                                         final Event<ProjectContextChangeEvent> contextChangeEvent,
                                         final ErrorPopupPresenter errorPopup,
                                         final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                         final KieWorkbenchACLImpl kieACL,
                                         final PlaceManager placeManager,
                                         final ProjectContext workbenchContext,
                                         final ProjectWizard wizard ) {
        this.view = view;
        this.pomService = pomService;
        this.repositoryStructureService = repositoryStructureService;
        this.assetManagementServices = assetManagementService;
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
        this.contextChangeEvent = contextChangeEvent;
        this.errorPopup = errorPopup;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.kieACL = kieACL;
        this.placeManager = placeManager;
        this.workbenchContext = workbenchContext;
        this.wizard = wizard;

        view.setPresenter( this );
        view.getDataView().setPresenter( this );
        view.getModulesView().setPresenter( this );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        makeMenuBar();
        processContextChange( workbenchContext.getActiveRepository(),
                              workbenchContext.getActiveBranch(),
                              workbenchContext.getActiveProject() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.RepositoryStructure();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view.asWidget();
    }

    @OnClose
    public void onClose() {
        concurrentUpdateSessionInfo = null;
        if ( pathToRepositoryStructure != null ) {
            pathToRepositoryStructure.dispose();
        }
    }

    @OnFocus
    public void onFocus() {
        //workaround.
        dataProvider.flush();
        dataProvider.refresh();
    }

    private void onContextChange( @Observes final ProjectContextChangeEvent event ) {
        processContextChange( event.getRepository(),
                              event.getBranch(),
                              event.getProject() );
    }

    private void processContextChange( final Repository repository,
                                       final String branch,
                                       final Project project ) {
        boolean repoOrBranchChanged = false;

        if ( repository == null ) {
            clearView();
            view.setModulesViewVisible( false );
            enableActions( false );

        } else if ( ( repoOrBranchChanged = repositoryOrBranchChanged( repository, branch ) ) || ( project != null && !project.equals( this.project ) ) ) {
            this.repository = repository;
            this.branch = branch;
            this.project = project;

            if ( repoOrBranchChanged || ( ( lastAddedModule == null || !lastAddedModule.equals( project ) ) && lastDeletedModule == null ) ) {
                init();
            }
            lastAddedModule = null;
            lastDeletedModule = null;
        }
    }

    private void init() {
        view.showBusyIndicator( Constants.INSTANCE.Loading() );
        clearView();
        repositoryStructureService.call( getLoadModelSuccessCallback(),
                                         new HasBusyIndicatorDefaultErrorCallback( view ) ).load( repository );
    }

    private RemoteCallback<RepositoryStructureModel> getLoadModelSuccessCallback() {
        return new RemoteCallback<RepositoryStructureModel>() {
            @Override
            public void callback( final RepositoryStructureModel model ) {
                view.hideBusyIndicator();
                RepositoryStructurePresenter.this.model = model;
                boolean initialized = false;
                dataProvider.getList().clear();
                if ( pathToRepositoryStructure != null ) {
                    destroyObservablePath( pathToRepositoryStructure );
                }
                concurrentUpdateSessionInfo = null;

                if ( model == null ) {
                    RepositoryStructurePresenter.this.model = new RepositoryStructureModel();
                    view.getDataView().setMode( RepositoryStructureDataView.ViewMode.CREATE_STRUCTURE );
                    view.getModulesView().setMode( ProjectModulesView.ViewMode.MODULES_VIEW );
                    view.setModel( RepositoryStructurePresenter.this.model );
                    view.setModulesViewVisible( false );

                    enableAssetsManagementMenu( false );
                    pathToRepositoryStructure = null;

                } else if ( model.isMultiModule() ) {
                    view.getDataView().setMode( RepositoryStructureDataView.ViewMode.EDIT_MULTI_MODULE_PROJECT );
                    view.getModulesView().setMode( ProjectModulesView.ViewMode.MODULES_VIEW );
                    view.setModel( model );
                    view.setModulesViewVisible( true );
                    initialized = true;

                    pathToRepositoryStructure = createObservablePath( model.getPathToPOM() );

                    updateModulesList( model.getModules() );
                    enableAssetsManagementMenu( true );

                } else if ( model.isSingleProject() ) {
                    view.getDataView().setMode( RepositoryStructureDataView.ViewMode.EDIT_SINGLE_MODULE_PROJECT );
                    view.getModulesView().setMode( ProjectModulesView.ViewMode.PROJECTS_VIEW );
                    view.setModel( model );
                    view.setModulesViewVisible( false );
                    initialized = true;

                    pathToRepositoryStructure = createObservablePath( model.getOrphanProjects().get( 0 ).getPomXMLPath() );
                    enableAssetsManagementMenu( true );

                } else {
                    view.getDataView().setMode( RepositoryStructureDataView.ViewMode.EDIT_UNMANAGED_REPOSITORY );
                    view.getModulesView().setMode( ProjectModulesView.ViewMode.PROJECTS_VIEW );
                    view.setModulesViewVisible( true );
                    initialized = true;

                    enableAssetsManagementMenu( false );
                    view.setModel( model );
                    updateProjectsList( model.getOrphanProjects() );
                }

                addStructureChangeListeners();
                updateEditorTitle( initialized );
            }
        };
    }

    //Package-protected to override for Unit Tests
    ObservablePath createObservablePath( final Path path ) {
        return IOC.getBeanManager().lookupBean( ObservablePath.class ).getInstance().wrap( path );
    }

    //Package-protected to override for Unit Tests
    void destroyObservablePath( final ObservablePath path ) {
        path.dispose();
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        init();
    }

    private void initRepositoryStructure() {
        //TODO add parameters validation
        if ( model != null ) {
            if ( model.isMultiModule() ) {
                doRepositoryInitialization( DeploymentMode.VALIDATED );

            } else if ( model.isSingleProject() ) {
                wizard.initialise( new POM() );
                wizard.start( new Callback<Project>() {
                                  @Override
                                  public void callback( Project result ) {
                                      lastAddedModule = result;
                                      if ( result != null ) {
                                          view.showBusyIndicator( Constants.INSTANCE.CreatingRepositoryStructure() );
                                          repositoryStructureService.call( new RemoteCallback<Repository>() {
                                                                               @Override
                                                                               public void callback( Repository repository ) {
                                                                                   view.hideBusyIndicator();
                                                                                   RepositoryStructurePresenter.this.repository = repository;
                                                                                   init();
                                                                               }
                                                                           },
                                                                           new HasBusyIndicatorDefaultErrorCallback( view ) ).initRepository( repository,
                                                                                                                                              true );

                                      }
                                  }
                              },
                              false );

            } else if ( !model.isManaged() ) {
                view.showBusyIndicator( Constants.INSTANCE.CreatingRepositoryStructure() );
                repositoryStructureService.call( new RemoteCallback<Repository>() {
                                                     @Override
                                                     public void callback( Repository repository ) {
                                                         view.hideBusyIndicator();
                                                         RepositoryStructurePresenter.this.repository = repository;
                                                         init();
                                                     }
                                                 },
                                                 new HasBusyIndicatorDefaultErrorCallback( view ) ).initRepository( repository,
                                                                                                                    false );
            }
        }
    }

    private void doRepositoryInitialization( final DeploymentMode mode ) {
        view.showBusyIndicator( Constants.INSTANCE.CreatingRepositoryStructure() );
        repositoryStructureService.call( new RemoteCallback<Path>() {

                                             @Override
                                             public void callback( Path response ) {
                                                 view.hideBusyIndicator();
                                                 init();
                                             }

                                         },
                                         new HasBusyIndicatorDefaultErrorCallback( view ) {
                                             @Override
                                             public boolean error( final Message message,
                                                                   final Throwable throwable ) {
                                                 // The *real* Throwable is wrapped in an InvocationTargetException when ran as a Unit Test and invoked with Reflection.
                                                 final Throwable _throwable = ( throwable.getCause() == null ? throwable : throwable.getCause() );
                                                 if ( _throwable instanceof GAVAlreadyExistsException ) {
                                                     final GAVAlreadyExistsException gae = (GAVAlreadyExistsException) _throwable;
                                                     conflictingRepositoriesPopup.setContent( gae.getGAV(),
                                                                                              gae.getRepositories(),
                                                                                              new Command() {
                                                                                                  @Override
                                                                                                  public void execute() {
                                                                                                      conflictingRepositoriesPopup.hide();
                                                                                                      doRepositoryInitialization( DeploymentMode.FORCED );
                                                                                                  }
                                                                                              } );
                                                     conflictingRepositoriesPopup.show();
                                                     return true;

                                                 } else {
                                                     return super.error( message,
                                                                         _throwable );
                                                 }
                                             }
                                         } ).initRepositoryStructure( new GAV( view.getDataView().getGroupId(),
                                                                               view.getDataView().getArtifactId(),
                                                                               view.getDataView().getVersion() ),
                                                                      repository,
                                                                      mode );
    }

    private void updateEditorTitle( final boolean initialized ) {
        if ( repository == null ) {
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                                     Constants.INSTANCE.RepositoryNotSelected() ) );

        } else if ( !initialized ) {
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                                     Constants.INSTANCE.UnInitializedStructure( getRepositoryLabel( repository ) ) ) );

        } else if ( model.isMultiModule() ) {
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                                     Constants.INSTANCE.RepositoryStructureWithName( getRepositoryLabel( repository ) + "→ "
                                                                                                                             + model.getPOM().getGav().getArtifactId() + ":"
                                                                                                                             + model.getPOM().getGav().getGroupId() + ":"
                                                                                                                             + model.getPOM().getGav().getVersion() ) ) );

        } else if ( model.isSingleProject() ) {
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                                     Constants.INSTANCE.RepositoryStructureWithName( getRepositoryLabel( repository ) + "→ " + model.getOrphanProjects().get( 0 ).getProjectName() ) ) );

        } else {
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                                                                     Constants.INSTANCE.UnmanagedRepository( getRepositoryLabel( repository ) ) ) );
        }
    }

    private String getRepositoryLabel( final Repository repository ) {
        return repository != null ? ( repository.getAlias() + " (" + branch + ") " ) : "";
    }

    private void addStructureChangeListeners() {
        if ( pathToRepositoryStructure != null ) {

            pathToRepositoryStructure.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
                @Override
                public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                    concurrentUpdateSessionInfo = eventInfo;
                }
            } );

            pathToRepositoryStructure.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
                @Override
                public void execute( final ObservablePath.OnConcurrentRenameEvent info ) {
                    newConcurrentRename( info.getSource(),
                                         info.getTarget(),
                                         info.getIdentity(),
                                         new Command() {
                                             @Override
                                             public void execute() {
                                                 enableActions( false );
                                             }
                                         },
                                         new Command() {
                                             @Override
                                             public void execute() {
                                                 reload();
                                             }
                                         }
                                       ).show();
                }
            } );

            pathToRepositoryStructure.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
                @Override
                public void execute( final ObservablePath.OnConcurrentDelete info ) {
                    newConcurrentDelete( info.getPath(),
                                         info.getIdentity(),
                                         new Command() {
                                             @Override
                                             public void execute() {
                                                 enableActions( false );
                                             }
                                         },
                                         new Command() {
                                             @Override
                                             public void execute() {
                                                 placeManager.closePlace( "repositoryStructureScreen" );
                                             }
                                         }
                                       ).show();
                }
            } );
        }
    }

    private void updateModulesList( final List<String> modules ) {
        if ( modules != null ) {
            for ( String module : model.getModules() ) {
                dataProvider.getList().add( new ProjectModuleRow( module ) );
            }
        }
    }

    private void updateProjectsList( final List<Project> projects ) {
        if ( projects != null ) {
            for ( Project project : projects ) {
                dataProvider.getList().add( new ProjectModuleRow( project.getProjectName() ) );
            }
        }
    }

    private void enableActions( final boolean value ) {
        view.getModulesView().enableActions( value );
    }

    private void clearView() {
        view.getDataView().clear();
        dataProvider.getList().clear();
        enableActions( true );
    }

    /**
     * *** Presenter interfaces *******
     */
    @Override
    public void onAddModule() {
        final POM pom = new POM();
        if ( model.isMultiModule() ) {
            final GAV parentGAV = new GAV();
            parentGAV.setGroupId( view.getDataView().getGroupId() );
            parentGAV.setArtifactId( view.getDataView().getArtifactId() );
            parentGAV.setVersion( view.getDataView().getVersion() );
            pom.setParent( parentGAV );

            pom.getGav().setGroupId( parentGAV.getGroupId() );
            pom.getGav().setVersion( parentGAV.getVersion() );
        } else {
            pom.getGav().setGroupId( workbenchContext.getActiveOrganizationalUnit().getDefaultGroupId() );
        }

        wizard.initialise( pom );

        wizard.start( getModuleAddedSuccessCallback(),
                      false );
    }

    private Callback<Project> getModuleAddedSuccessCallback() {
        //optimization to avoid reloading the complete model when a module is added.
        return new Callback<Project>() {
            @Override
            public void callback( final Project _project ) {
                lastAddedModule = _project;
                if ( _project != null ) {
                    //A new module was added.
                    if ( model.isMultiModule() ) {
                        view.showBusyIndicator( Constants.INSTANCE.Loading() );
                        repositoryStructureService.call( new RemoteCallback<RepositoryStructureModel>() {
                                                             @Override
                                                             public void callback( RepositoryStructureModel _model ) {
                                                                 view.hideBusyIndicator();
                                                                 if ( _model != null ) {
                                                                     model.setPOM( _model.getPOM() );
                                                                     model.setPOMMetaData( _model.getPOMMetaData() );
                                                                     model.setModules( _model.getModules() );
                                                                     model.getModulesProject().put( _project.getProjectName(),
                                                                                                    _project );
                                                                     addToModulesList( _project );
                                                                 }
                                                             }
                                                         },
                                                         new HasBusyIndicatorDefaultErrorCallback( view ) ).load( repository,
                                                                                                                  false );

                    } else {
                        view.showBusyIndicator( Constants.INSTANCE.Loading() );
                        pomService.call( new RemoteCallback<POM>() {
                                             @Override
                                             public void callback( POM _pom ) {
                                                 view.hideBusyIndicator();
                                                 model.getOrphanProjects().add( _project );
                                                 model.getOrphanProjectsPOM().put( _project.getSignatureId(),
                                                                                   _pom );
                                                 addToModulesList( _project );
                                             }
                                         },
                                         new HasBusyIndicatorDefaultErrorCallback( view ) ).load( _project.getPomXMLPath() );
                    }
                }
            }
        };
    }

    @Override
    public void addDataDisplay( final HasData<ProjectModuleRow> display ) {
        dataProvider.addDataDisplay( display );
    }

    @Override
    public void onDeleteModule( final ProjectModuleRow moduleRow ) {
        final Project project = getSelectedModule( moduleRow.getName() );
        String message = null;

        if ( project != null ) {

            if ( model.isMultiModule() ) {
                message = Constants.INSTANCE.ConfirmModuleDeletion( moduleRow.getName() );
            } else {
                message = Constants.INSTANCE.ConfirmProjectDeletion( moduleRow.getName() );
            }

            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                                                                                      message,
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              deleteSelectedModule( project );
                                                                                          }
                                                                                      },
                                                                                      CommonConstants.INSTANCE.YES(),
                                                                                      ButtonType.DANGER,
                                                                                      IconType.TRASH,
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              //do nothing
                                                                                          }
                                                                                      },
                                                                                      null,
                                                                                      ButtonType.DEFAULT,
                                                                                      null,
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              //do nothing.
                                                                                          }
                                                                                      },
                                                                                      null,
                                                                                      ButtonType.DEFAULT,
                                                                                      null
                                                                                    );

            yesNoCancelPopup.setClosable( false );
            yesNoCancelPopup.show();
        }
    }

    private void deleteSelectedModule( final Project project ) {
        view.showBusyIndicator( Constants.INSTANCE.Deleting() );
        lastDeletedModule = project;
        repositoryStructureService.call( getModuleDeletedSuccessCallback( project ),
                                         new HasBusyIndicatorDefaultErrorCallback( view ) ).delete( project.getPomXMLPath(),
                                                                                                    "Module removed" );
    }

    private RemoteCallback<Void> getModuleDeletedSuccessCallback( final Project _project ) {
        //optimization to avoid reloading the complete model when a module is added.
        return new RemoteCallback<Void>() {
            @Override
            public void callback( Void response ) {
                if ( _project != null ) {
                    //A project was deleted
                    if ( model.isMultiModule() ) {
                        view.showBusyIndicator( Constants.INSTANCE.Loading() );
                        repositoryStructureService.call( new RemoteCallback<RepositoryStructureModel>() {
                                                             @Override
                                                             public void callback( RepositoryStructureModel _model ) {
                                                                 view.hideBusyIndicator();
                                                                 if ( _model != null ) {
                                                                     model.setPOM( _model.getPOM() );
                                                                     model.setPOMMetaData( _model.getPOMMetaData() );
                                                                     model.setModules( _model.getModules() );
                                                                     model.getModulesProject().remove( _project.getProjectName() );
                                                                     removeFromModulesList( _project.getProjectName() );
                                                                 }
                                                             }
                                                         },
                                                         new HasBusyIndicatorDefaultErrorCallback( view ) ).load( repository, false );

                    } else {
                        model.getOrphanProjects().remove( _project );
                        model.getOrphanProjectsPOM().remove( _project.getSignatureId() );
                        removeFromModulesList( _project.getProjectName() );
                    }
                }
            }
        };
    }

    @Override
    public void onEditModule( final ProjectModuleRow moduleRow ) {
        Project project = getSelectedModule( moduleRow.getName() );
        if ( project != null ) {
            contextChangeEvent.fire( new ProjectContextChangeEvent( workbenchContext.getActiveOrganizationalUnit(),
                                                                    repository,
                                                                    branch,
                                                                    project ) );
            placeManager.goTo( "projectScreen" );
        }
    }

    @Override
    public void onInitRepositoryStructure() {
        initRepositoryStructure();
    }

    @Override
    public void onSaveRepositoryStructure() {
        if ( model.getPOM() != null ) {
            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                                                                                      Constants.INSTANCE.ConfirmSaveRepositoryStructure(),
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              saveRepositoryStructure();
                                                                                          }
                                                                                      },
                                                                                      CommonConstants.INSTANCE.YES(),
                                                                                      ButtonType.PRIMARY,
                                                                                      IconType.SAVE,
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              //do nothing
                                                                                          }
                                                                                      },
                                                                                      null,
                                                                                      ButtonType.DEFAULT,
                                                                                      null,
                                                                                      new Command() {
                                                                                          @Override
                                                                                          public void execute() {
                                                                                              //do nothing.
                                                                                          }
                                                                                      },
                                                                                      null,
                                                                                      ButtonType.DEFAULT,
                                                                                      null
                                                                                    );

            yesNoCancelPopup.setClosable( false );
            yesNoCancelPopup.show();
        }
    }

    private void saveRepositoryStructure() {
        if ( model.getPOM() != null ) {
            model.getPOM().getGav().setGroupId( view.getDataView().getGroupId() );
            model.getPOM().getGav().setArtifactId( view.getDataView().getArtifactId() );
            model.getPOM().getGav().setVersion( view.getDataView().getVersion() );

            view.showBusyIndicator( Constants.INSTANCE.Saving() );
            repositoryStructureService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                    view.hideBusyIndicator();
                    init();
                }
            } ).save( model.getPathToPOM(),
                      model,
                      "" );
        }
    }

    @Override
    public void onConvertToMultiModule() {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                                                                                  Constants.INSTANCE.ConfirmConvertToMultiModuleStructure(),
                                                                                  new Command() {
                                                                                      @Override
                                                                                      public void execute() {
                                                                                          convertToMultiModule();
                                                                                      }
                                                                                  },
                                                                                  CommonConstants.INSTANCE.YES(),
                                                                                  ButtonType.PRIMARY,
                                                                                  IconType.SAVE,
                                                                                  new Command() {
                                                                                      @Override
                                                                                      public void execute() {
                                                                                          //do nothing
                                                                                      }
                                                                                  },
                                                                                  null,
                                                                                  ButtonType.DEFAULT,
                                                                                  null,
                                                                                  new Command() {
                                                                                      @Override
                                                                                      public void execute() {
                                                                                          //do nothing.
                                                                                      }
                                                                                  },
                                                                                  null,
                                                                                  ButtonType.DEFAULT,
                                                                                  null
                                                                                );

        yesNoCancelPopup.setClosable( false );
        yesNoCancelPopup.show();
    }

    private void convertToMultiModule() {
        Project project = model.getOrphanProjects().get( 0 );
        POM pom = model.getOrphanProjectsPOM().get( project.getSignatureId() );
        GAV gav = new GAV( view.getDataView().getGroupId(),
                           view.getDataView().getArtifactId(),
                           view.getDataView().getVersion() );

        view.showBusyIndicator( Constants.INSTANCE.ConvertingToMultiModuleProject() );
        repositoryStructureService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.hideBusyIndicator();
                init();
            }
        } ).convertToMultiProjectStructure( model.getOrphanProjects(),
                                            gav,
                                            repository,
                                            true,
                                            null );
    }

    @Override
    public void onOpenSingleProject() {
        if ( model != null && model.isSingleProject() ) {
            placeManager.goTo( "projectScreen" );
        }
    }

    private Project getSelectedModule( final String name ) {
        Project project = null;
        if ( model != null && name != null ) {
            if ( model.isMultiModule() ) {
                project = model.getModulesProject() != null ? model.getModulesProject().get( name ) : null;
            } else if ( model.getOrphanProjects() != null ) {
                for ( Project _project : model.getOrphanProjects() ) {
                    if ( name.equals( _project.getProjectName() ) ) {
                        project = _project;
                        break;
                    }
                }
            }
        }
        return project;
    }

    private void removeFromModulesList( final String module ) {
        if ( module != null ) {
            int index = -1;
            for ( ProjectModuleRow row : dataProvider.getList() ) {
                index++;
                if ( module.equals( row.getName() ) ) {
                    break;
                }
            }
            if ( index >= 0 && ( index == 0 || index < dataProvider.getList().size() ) ) {
                dataProvider.getList().remove( index );
            }
        }
    }

    private void addToModulesList( final Project project ) {
        ProjectModuleRow row = new ProjectModuleRow( project.getProjectName() );
        if ( !dataProvider.getList().contains( row ) ) {
            dataProvider.getList().add( row );
        }
    }

    private boolean repositoryOrBranchChanged( final Repository selectedRepository,
                                               final String branch) {
        return selectedRepository != null
                && ( !selectedRepository.equals( this.repository )
                || !branch.equals( this.branch ) );
    }

    private void makeMenuBar() {
        final List<MenuItem> items = new ArrayList<MenuItem>();

        configure = MenuFactory
                .newTopLevelMenu( Constants.INSTANCE.Configure() )
                .withRoles( kieACL.getGrantedRoles( CONFIGURE_REPOSITORY ) )
                .respondsWith( getConfigureCommand() )
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
                .respondsWith( getReleaseCommand() )
                .endMenu()
                .build().getItems().get( 0 );

        items.add( configure );
        items.add( promote );
        items.add( release );

        menus = new Menus() {
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
        };

        MenuItem item;
        item = getItem( MenuItems.CONFIGURE_MENU_ITEM );
        configureIsGranted = item != null && item.isEnabled();

        item = getItem( MenuItems.PROMOTE_MENU_ITEM );
        promoteIsGranted = item != null && item.isEnabled();

        item = getItem( MenuItems.RELEASE_MENU_ITEM );
        releaseIsGranted = item != null && item.isEnabled();
    }

    private void enableAssetsManagementMenu( final boolean enable ) {
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

    private MenuItem getItem( final MenuItems itemKey ) {
        return menus != null ? menus.getItemsMap().get( itemKey ) : null;
    }

    private Command getConfigureCommand() {
        return new Command() {
            @Override
            public void execute() {
                POM pom = null;
                if ( model != null && ( model.isSingleProject() || model.isMultiModule() ) ) {
                    pom = model.isMultiModule() ? model.getPOM() : model.getSingleProjectPOM();
                    view.getConfigureScreenPopupView().configure( repository.getAlias(),
                                                                  branch,
                                                                  pom.getGav().getVersion(),
                                                                  new com.google.gwt.user.client.Command() {
                                                                      @Override
                                                                      public void execute() {
                                                                          String devBranch = view.getConfigureScreenPopupView().getDevBranch();
                                                                          String releaseBranch = view.getConfigureScreenPopupView().getReleaseBranch();

                                                                          String version = view.getConfigureScreenPopupView().getVersion();

                                                                          configureRepository( repository.getAlias(),
                                                                                               branch,
                                                                                               devBranch,
                                                                                               releaseBranch,
                                                                                               version );
                                                                          view.getConfigureScreenPopupView().hide();
                                                                      }
                                                                  } );
                    view.getConfigureScreenPopupView().show();
                }
            }
        };
    }

    private Command getPromoteCommand() {
        return new Command() {
            @Override
            public void execute() {
                view.getPromoteScreenPopupView().configure( repository.getAlias(),
                                                            branch,
                                                            repository.getBranches(),
                                                            new com.google.gwt.user.client.Command() {
                                                                @Override
                                                                public void execute() {
                                                                    String targetBranch = view.getPromoteScreenPopupView().getTargetBranch();
                                                                    promoteChanges( repository.getAlias(),
                                                                                    branch,
                                                                                    targetBranch );
                                                                    view.getPromoteScreenPopupView().hide();
                                                                }
                                                            } );
                view.getPromoteScreenPopupView().show();
            }
        };
    }

    private Command getReleaseCommand() {
        return new Command() {
            @Override
            public void execute() {
                POM pom = null;
                if ( model != null && ( model.isSingleProject() || model.isMultiModule() ) ) {
                    pom = model.isMultiModule() ? model.getPOM() : model.getSingleProjectPOM();

                    view.getReleaseScreenPopupView().configure( repository.getAlias(),
                                                                branch,
                                                                trimSnapshotFromVersion( pom.getGav().getVersion() ),
                                                                pom.getGav().getVersion(),
                                                                new com.google.gwt.user.client.Command() {
                                                                    @Override
                                                                    public void execute() {
                                                                        String username = view.getReleaseScreenPopupView().getUsername();
                                                                        String password = view.getReleaseScreenPopupView().getPassword();
                                                                        String serverURL = view.getReleaseScreenPopupView().getServerURL();
                                                                        String version = view.getReleaseScreenPopupView().getVersion();
                                                                        Boolean deployToRuntime = view.getReleaseScreenPopupView().getDeployToRuntime();
                                                                        releaseProject( repository.getAlias(),
                                                                                        branch,
                                                                                        username,
                                                                                        password,
                                                                                        serverURL,
                                                                                        deployToRuntime,
                                                                                        version );
                                                                        view.getReleaseScreenPopupView().hide();
                                                                    }
                                                                } );
                    view.getReleaseScreenPopupView().show();
                }
            }
        };
    }

    private String trimSnapshotFromVersion( String version ) {
        if ( version != null && version.endsWith( "-SNAPSHOT" ) ) {
            return version.replace( "-SNAPSHOT", "" );
        }
        return version;
    }

    public void configureRepository( final String repository,
                                     final String sourceBranch,
                                     final String devBranch,
                                     final String releaseBranch,
                                     final String version ) {
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
                                              errorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      } ).configureRepository( repository,
                                                               sourceBranch,
                                                               devBranch,
                                                               releaseBranch,
                                                               version );
    }

    public void promoteChanges( final String repository,
                                final String sourceBranch,
                                final String destinationBranch ) {
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

                                              errorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                                              return true;
                                          }
                                      } ).promoteChanges( repository,
                                                          sourceBranch,
                                                          destinationBranch );

    }

    public void releaseProject( final String repository,
                                final String branch,
                                final String userName,
                                final String password,
                                final String serverURL,
                                final Boolean deployToRuntime,
                                final String version ) {
        String _serverURL = serverURL;
        if ( _serverURL != null && !_serverURL.isEmpty() && _serverURL.endsWith( "/" ) ) {
            _serverURL = _serverURL.substring( 0,
                                               _serverURL.length() - 1 );
        }

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
                                      } ).releaseProject( repository,
                                                          branch,
                                                          userName,
                                                          password,
                                                          _serverURL,
                                                          deployToRuntime,
                                                          version );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
