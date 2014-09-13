/*
 * Copyright 2012 JBoss Inc
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
package org.guvnor.asset.management.client.editors.project.structure;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModuleRow;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectStructureDataView;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.ProjectStructureModel;
import org.guvnor.asset.management.service.ProjectStructureService;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.popups.YesNoCancelPopup;
import org.kie.uberfire.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.uberfire.client.common.ConcurrentChangePopup.*;

@WorkbenchScreen( identifier = "projectStructureScreen" )
public class ProjectStructurePresenter
        implements ProjectStructureView.Presenter,
        ProjectStructureDataView.Presenter,
        ProjectModulesView.Presenter {

    private ProjectStructureView view;

    @Inject
    private Caller<ProjectStructureService> projectStructureService;

    @Inject
    private Caller<BuildService> buildServiceCaller;

    @Inject
    private Event<BuildResults> buildResultsEvent;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private Event<BeforeClosePlaceEvent> beforeCloseEvent;

    @Inject
    private Event<ProjectContextChangeEvent> contextChangeEvent;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ProjectContext workbenchContext;

    private ProjectStructureModel model;

    private Project project;

    private Project lastAddedModule;

    private Repository repository;

    private ObservablePath pathToProjectStructure;

    private PlaceRequest placeRequest;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private ListDataProvider<ProjectModuleRow> dataProvider = new ListDataProvider<ProjectModuleRow>();

    @Inject
    private ProjectWizard wizzard;

    @Inject
    public ProjectStructurePresenter( ProjectStructureView view ) {
        this.view = view;
        view.setPresenter( this );
        view.getDataView().setPresenter( this );
        view.getModulesView().setPresenter( this );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        processContextChange( workbenchContext.getActiveRepository(), workbenchContext.getActiveProject() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.ProjectStructure();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view.asWidget();
    }

    @OnClose
    public void onClose() {
        concurrentUpdateSessionInfo = null;
        if ( pathToProjectStructure != null ) {
            pathToProjectStructure.dispose();
        }
    }

    @OnFocus
    public void onFocus() {
        dataProvider.flush();
        dataProvider.refresh();
    }

    private void onContextChange( @Observes final ProjectContextChangeEvent event ) {
        processContextChange( event.getRepository(), event.getProject() );
    }

    private void processContextChange( final Repository repository, final Project project ) {

        if ( ( repository != null && !repository.equals( this.repository ) )  ||
                (project != null && !project.equals( this.project ) ) ) {

            this.repository = repository;
            this.project = project;

            if ( lastAddedModule == null || !lastAddedModule.equals( project ) ) {
                init();
            } else {
                lastAddedModule = null;
            }
        }
    }

    private void init() {

        view.showBusyIndicator( Constants.INSTANCE.Loading() );
        clearView();
        projectStructureService.call( getLoadModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback( view ) ).load( repository );

    }

    private RemoteCallback<ProjectStructureModel> getLoadModelSuccessCallback() {

        return new RemoteCallback<ProjectStructureModel>() {
            @Override
            public void callback( final ProjectStructureModel model ) {

                view.hideBusyIndicator();

                ProjectStructurePresenter.this.model = model;
                dataProvider.getList().clear();
                if ( pathToProjectStructure != null ) {
                    pathToProjectStructure.dispose();
                }
                concurrentUpdateSessionInfo = null;

                if ( model == null ) {

                    ProjectStructurePresenter.this.model = new ProjectStructureModel();
                    view.getDataView().setMode( ProjectStructureDataView.ViewMode.CREATE_STRUCTURE );
                    view.setModel( ProjectStructurePresenter.this.model );
                    view.setModulesViewVisible( false );
                    pathToProjectStructure = null;

                } else if ( model.isMultiModule() ) {

                    view.getDataView().setMode( ProjectStructureDataView.ViewMode.EDIT_MULTI_MODULE_PROJECT );
                    view.setModel( model );
                    view.setModulesViewVisible( true );

                    pathToProjectStructure = IOC.getBeanManager().lookupBean( ObservablePath.class ).getInstance().wrap( model.getPathToPOM() );

                    //TODO refactor this modules loading.
                    if ( model.getModules() != null ) {
                        for ( String module : model.getModules() ) {
                            dataProvider.getList().add( new ProjectModuleRow( module ) );
                        }
                        //dataProvider.refresh();
                    }

                } else if ( model.isSingleProject() ) {

                    view.getDataView().setMode( ProjectStructureDataView.ViewMode.EDIT_SINGLE_MODULE_PROJECT );
                    view.setModel( model );
                    view.setModulesViewVisible( false );

                } else {
                    //TODO, define backward compatibility
                    //we are opening the project structure for a repository with N > 1 orphan projects.
                    //likely this repository is from a previous version.
                    String message = "Current repository seems to be a Repository created with a KIE workbench previous version.\n";
                    message += "The following projects where found.\n\n:";
                    for ( Project project : model.getOrphanProjects() ) {
                        message += ( project.getProjectName() + "\n" );
                    }
                    Window.alert( message );
                }

                addStructureChangeListeners();
                updateEditorTitle();
            }
        };
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        init();
    }

    private void initProjectStructure() {
        //TODO add parameters validation, and a callback or event observer in order to
        //know when a project was created.

        if ( view.getDataView().isMultiModule() ) {
            view.showBusyIndicator( "Creating project structure" );
            projectStructureService.call( new RemoteCallback<Path>() {

                @Override
                public void callback( Path response ) {
                    view.hideBusyIndicator();
                    init();
                }

            } ).initProjectStructure( new GAV( view.getDataView().getGroupId(),
                    view.getDataView().getArtifactId(),
                    view.getDataView().getVersionId() ),
                    this.repository );
        } else {
            //TODO, in order to know the project creation status a callback could be added to the wizzard.
            //this will let us know if the wizzard was canceled or if the project was successfully created.
            //at the moment if the single project was created the ProjectCreation event will be raized anyway,
            //and the ProjectContext will be automatically updated.
            //So we can still reload the ProjectStructure data when a project change is detected.
            wizzard.setContent( null, null, null);
            wizzard.start();
        }
    }

    private void updateEditorTitle() {
        //TODO create missing constants.
        if ( repository == null ) {

            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( placeRequest,
                    "A repository has not been selected." ) );

        } else if ( model == null ) {

            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.ProjectStructureWithName( this.repository.getAlias() ) ) );

        } else if ( model.isMultiModule() ) {

            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.ProjectStructureWithName( this.repository.getAlias() + "- > " +
                            model.getPOM().getGav().getArtifactId() + ":"
                            + model.getPOM().getGav().getGroupId() + ":"
                            + model.getPOM().getGav().getVersion() ) ) );

        } else if ( model.isSingleProject() ) {
            //TODO, review screen naming for this case.
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.ProjectStructureWithName( this.repository.getAlias() + "- > " + model.getOrphanProjects().get( 0 ).getProjectName() ) ) );

        } else {
            //TODO, review screen naming for this case.
            //it's a repository in the old format, just print the repository name.
            changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.ProjectStructureWithName( this.repository.getAlias() ) ) );
        }
    }

    private void addStructureChangeListeners() {

        if ( pathToProjectStructure != null ) {
            pathToProjectStructure.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
                @Override
                public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                    concurrentUpdateSessionInfo = eventInfo;
                }
            } );

            pathToProjectStructure.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
                @Override
                public void execute( final ObservablePath.OnConcurrentRenameEvent info ) {
                    newConcurrentRename( info.getSource(),
                            info.getTarget(),
                            info.getIdentity(),
                            new Command() {
                                @Override
                                public void execute() {
                                    disableMenus();
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

            pathToProjectStructure.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
                @Override
                public void execute( final ObservablePath.OnConcurrentDelete info ) {
                    newConcurrentDelete( info.getPath(),
                            info.getIdentity(),
                            new Command() {
                                @Override
                                public void execute() {
                                    disableMenus();
                                }
                            },
                            new Command() {
                                @Override
                                public void execute() {
                                    placeManager.closePlace( "projectStructureScreen" );
                                }
                            }
                    ).show();
                }
            } );
        }
    }

    private void disableMenus() {

    }

    private void clearView() {
        view.getDataView().clear();
        dataProvider.getList().clear();
    }

    /**
     * *** Presenter interfaces *******
     */

    @Override
    public void onAddModule() {
        wizzard.setContent( null,
                view.getDataView().getGroupId(),
                view.getDataView().getVersionId() );
        wizzard.start( new Callback<Project>() {
            @Override public void callback( Project result ) {
                lastAddedModule = result;
                init();
            }
        }, false );
    }

    @Override
    public void addDataDisplay( HasData<ProjectModuleRow> display ) {
        dataProvider.addDataDisplay( display );
    }

    @Override
    public void onDeleteModule( ProjectModuleRow moduleRow ) {
        deleteSelectedModule( moduleRow.getName() );
    }

    @Override
    public void onEditModule( ProjectModuleRow moduleRow ) {

        Project module;
        if ( model != null &&
                model.getModulesProject() != null &&
                (( module = model.getModulesProject().get( moduleRow.getName() )) != null ) ) {
                //TODO check if there's a better implementation for this projectScreen opening.
                contextChangeEvent.fire( new ProjectContextChangeEvent( workbenchContext.getActiveOrganizationalUnit(), repository, module ) );
                placeManager.goTo( "projectScreen" );
        }
    }

    @Override
    public void onArtifactIdChange( String artifactId ) {
        Window.alert( "onArtifactIdChange: " + artifactId );
    }

    @Override
    public void onGroupIdChange( String groupId ) {
        Window.alert( "onGroupIdChange: " + groupId );
    }

    @Override
    public void onVersionChange( String version ) {
        Window.alert( "onVersionChange: " + version );
    }

    @Override
    public void onProjectModeChange( boolean isSingle ) {

    }

    @Override
    public void onInitProjectStructure() {
        initProjectStructure();
    }

    @Override
    public void onSaveProjectStructure() {
        if ( model.getPOM() != null ) {

            model.getPOM().getGav().setGroupId( view.getDataView().getGroupId() );
            model.getPOM().getGav().setArtifactId( view.getDataView().getArtifactId() );
            model.getPOM().getGav().setVersion( view.getDataView().getVersionId() );

            view.showBusyIndicator( "Saving" );
            projectStructureService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                    view.hideBusyIndicator();
                    init();
                }
            } ).save( model.getPathToPOM(), model, "" );
        }
    }

    @Override
    public void onConvertToMultiModule() {
        Project project = model.getOrphanProjects().get( 0 );
        POM pom = model.getOrphanProjectsPOM().get( project.getSignatureId() );
        GAV gav = new GAV( view.getDataView().getGroupId(), view.getDataView().getArtifactId(), view.getDataView().getVersionId() );

        view.showBusyIndicator( "Converting to multi module project" );
        projectStructureService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.hideBusyIndicator();
                init();
            }
        } ).convertToMultiProjectStructure( model.getOrphanProjects(), gav, repository, true, null );
    }

    @Override
    public void onOpenSingleProject() {
        if ( model != null && model.isSingleProject() ) {
            placeManager.goTo( "projectScreen" );
        }
    }

    private void deleteSelectedModule( String module ) {
        final Project project = model.getModulesProject() != null ? model.getModulesProject().get( module ) : null;
        if ( project != null ) {

            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Information(),
                    "Are you sure that you want to remove module: " + module + " from project?",
                    new Command() {
                        @Override
                        public void execute() {

                            view.showBusyIndicator( "Deleting" );
                            projectStructureService.call( new RemoteCallback<Void>() {
                                @Override
                                public void callback( Void response ) {
                                    view.hideBusyIndicator();
                                    //TODO avoid reloading every time.
                                    init();
                                }
                            }, new HasBusyIndicatorDefaultErrorCallback( view ) ).delete( project.getPomXMLPath(), "Module removed" );
                        }
                    },
                    CommonConstants.INSTANCE.YES(),
                    ButtonType.DANGER,
                    IconType.MINUS_SIGN,
                    new Command() {
                        @Override public void execute() {
                            //do nothing
                        }
                    },
                    null,
                    ButtonType.DEFAULT,
                    null,
                    new Command() {
                        @Override public void execute() {
                            //do nothing.
                        }
                    },
                    null,
                    ButtonType.DEFAULT,
                    null
            );

            yesNoCancelPopup.setCloseVisible( false );
            yesNoCancelPopup.show();

        }
    }
}