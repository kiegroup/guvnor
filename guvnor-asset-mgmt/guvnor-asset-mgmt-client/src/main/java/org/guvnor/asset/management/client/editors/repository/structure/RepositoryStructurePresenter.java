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
package org.guvnor.asset.management.client.editors.repository.structure;

import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
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
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
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
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchScreen(identifier = "repositoryStructureScreen")
public class RepositoryStructurePresenter
        implements RepositoryStructureView.Presenter,
        RepositoryStructureDataView.Presenter,
        ProjectModulesView.Presenter {

    private RepositoryStructureView view;

    @Inject
    private Caller<RepositoryStructureService> repositoryStructureService;

    @Inject
    private Caller<POMService> pomService;

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

    @Inject
    private ProjectWizard wizzard;

    @Inject
    protected Event<ProjectContextChangeEvent> contextChangedEvent;

    private Menus menus;

    @Inject
    protected Caller<AssetManagementService> assetManagementServices;

    @Inject
    public RepositoryStructurePresenter(RepositoryStructureView view) {
        this.view = view;
        view.setPresenter(this);
        view.getDataView().setPresenter(this);
        view.getModulesView().setPresenter(this);
        makeMenuBar();
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        processContextChange(workbenchContext.getActiveRepository(), workbenchContext.getActiveProject());
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
        if (pathToRepositoryStructure != null) {
            pathToRepositoryStructure.dispose();
        }
    }

    @OnFocus
    public void onFocus() {
        //workaround.
        dataProvider.flush();
        dataProvider.refresh();
    }

    private void onContextChange(@Observes final ProjectContextChangeEvent event) {
        processContextChange(event.getRepository(), event.getProject());
    }

    private void processContextChange(final Repository repository, final Project project) {
        boolean repoOrBranchChanged = false;

        if (repository == null) {
            clearView();
            view.setReadonly(true);
            view.setModulesViewVisible(false);
            enableActions(false);
        } else if ((repoOrBranchChanged = repositoryOrBranchChanged(repository)) || (project != null && !project.equals(this.project))) {

            this.repository = repository;
            this.branch = repository != null ? repository.getCurrentBranch() : null;
            this.project = project;

            view.setReadonly(false);
            if (repoOrBranchChanged || (lastAddedModule == null || !lastAddedModule.equals(project)) && lastDeletedModule == null) {
                init();
            }
            lastAddedModule = null;
            lastDeletedModule = null;
        }
    }

    private void init() {

        view.showBusyIndicator(Constants.INSTANCE.Loading());
        clearView();
        repositoryStructureService.call(getLoadModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).load(repository);

    }

    private RemoteCallback<RepositoryStructureModel> getLoadModelSuccessCallback() {

        return new RemoteCallback<RepositoryStructureModel>() {
            @Override
            public void callback(final RepositoryStructureModel model) {

                view.hideBusyIndicator();

                RepositoryStructurePresenter.this.model = model;
                boolean initialized = false;
                dataProvider.getList().clear();
                if (pathToRepositoryStructure != null) {
                    pathToRepositoryStructure.dispose();
                }
                concurrentUpdateSessionInfo = null;

                if (model == null) {

                    RepositoryStructurePresenter.this.model = new RepositoryStructureModel();
                    view.getDataView().setMode(RepositoryStructureDataView.ViewMode.CREATE_STRUCTURE);
                    view.getModulesView().setMode(ProjectModulesView.ViewMode.MODULES_VIEW);
                    view.setModel(RepositoryStructurePresenter.this.model);
                    view.setModulesViewVisible(false);
                    pathToRepositoryStructure = null;

                } else if (model.isMultiModule()) {

                    view.getDataView().setMode(RepositoryStructureDataView.ViewMode.EDIT_MULTI_MODULE_PROJECT);
                    view.getModulesView().setMode(ProjectModulesView.ViewMode.MODULES_VIEW);
                    view.setModel(model);
                    view.setModulesViewVisible(true);
                    initialized = true;

                    pathToRepositoryStructure = IOC.getBeanManager().lookupBean(ObservablePath.class).getInstance().wrap(model.getPathToPOM());

                    updateModulesList(model.getModules());

                } else if (model.isSingleProject()) {

                    view.getDataView().setMode(RepositoryStructureDataView.ViewMode.EDIT_SINGLE_MODULE_PROJECT);
                    view.getModulesView().setMode(ProjectModulesView.ViewMode.PROJECTS_VIEW);
                    view.setModel(model);
                    view.setModulesViewVisible(false);
                    initialized = true;

                    pathToRepositoryStructure = IOC.getBeanManager().lookupBean(ObservablePath.class).getInstance().wrap(model.getOrphanProjects().get(0).getPomXMLPath());

                } else {

                    view.getDataView().setMode(RepositoryStructureDataView.ViewMode.EDIT_UNMANAGED_REPOSITORY);
                    view.getModulesView().setMode(ProjectModulesView.ViewMode.PROJECTS_VIEW);
                    view.setModulesViewVisible(true);
                    initialized = true;

                    view.setModel(model);
                    updateProjectsList(model.getOrphanProjects());
                }

                addStructureChangeListeners();
                updateEditorTitle(initialized);
                updateMenus(initialized);
            }

            
        };
    }

    private void updateMenus(boolean initialized) {
        List<MenuItem> items = menus.getItems();
        for(MenuItem mi : items){
            mi.setEnabled(initialized);
        }
    }
    
    private void reload() {
        concurrentUpdateSessionInfo = null;
        init();
    }

    private void initRepositoryStructure() {
        //TODO add parameters validation

        if (view.getDataView().isMultiModule()) {
            view.showBusyIndicator(Constants.INSTANCE.CreatingRepositoryStructure());
            repositoryStructureService.call(new RemoteCallback<Path>() {

                @Override
                public void callback(Path response) {
                    view.hideBusyIndicator();
                    init();
                }

            }, new HasBusyIndicatorDefaultErrorCallback(view)).initRepositoryStructure(
                    new GAV(view.getDataView().getGroupId(),
                            view.getDataView().getArtifactId(),
                            view.getDataView().getVersionId()),
                    repository);

        } else if (view.getDataView().isSingleModule()) {
            wizzard.setContent(null, null, null);
            wizzard.start(new Callback<Project>() {
                @Override
                public void callback(Project result) {
                    lastAddedModule = result;
                    if (result != null) {
                        view.showBusyIndicator(Constants.INSTANCE.CreatingRepositoryStructure());
                        repositoryStructureService.call(new RemoteCallback<Repository>() {
                            @Override
                            public void callback(Repository repository) {
                                view.hideBusyIndicator();
                                RepositoryStructurePresenter.this.repository = repository;
                                init();

                            }
                        }, new HasBusyIndicatorDefaultErrorCallback(view)).initRepository(repository, true);

                    }
                }
            }, false);
        } else if (view.getDataView().isUnmanagedRepository()) {
            view.showBusyIndicator(Constants.INSTANCE.CreatingRepositoryStructure());
            repositoryStructureService.call(new RemoteCallback<Repository>() {
                @Override
                public void callback(Repository repository) {
                    view.hideBusyIndicator();
                    RepositoryStructurePresenter.this.repository = repository;
                    init();
                }
            }, new HasBusyIndicatorDefaultErrorCallback(view)).initRepository(repository, false);
        }
    }

    private void updateEditorTitle(boolean initialized) {

        if (repository == null) {

            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                    Constants.INSTANCE.RepositoryNotSelected()));

        } else if (!initialized) {

            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.UnInitializedStructure(getRepositoryLabel(repository))));

        } else if (model.isMultiModule()) {

            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.RepositoryStructureWithName(getRepositoryLabel(repository) + "- > "
                            + model.getPOM().getGav().getArtifactId() + ":"
                            + model.getPOM().getGav().getGroupId() + ":"
                            + model.getPOM().getGav().getVersion())));

        } else if (model.isSingleProject()) {
            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.RepositoryStructureWithName(getRepositoryLabel(repository) + "- > " + model.getOrphanProjects().get(0).getProjectName())));

        } else {
            changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(
                    placeRequest,
                    Constants.INSTANCE.UnmanagedRepository(getRepositoryLabel(repository))));
        }
    }

    private String getRepositoryLabel(Repository repository) {
        return repository != null ? (repository.getAlias() + " (" + repository.getCurrentBranch() + ") ") : "";
    }

    private void addStructureChangeListeners() {

        if (pathToRepositoryStructure != null) {

            pathToRepositoryStructure.onConcurrentUpdate(new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
                @Override
                public void execute(final ObservablePath.OnConcurrentUpdateEvent eventInfo) {
                    concurrentUpdateSessionInfo = eventInfo;
                }
            });

            pathToRepositoryStructure.onConcurrentRename(new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
                @Override
                public void execute(final ObservablePath.OnConcurrentRenameEvent info) {
                    newConcurrentRename(info.getSource(),
                            info.getTarget(),
                            info.getIdentity(),
                            new Command() {
                                @Override
                                public void execute() {
                                    enableActions(false);
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
            });

            pathToRepositoryStructure.onConcurrentDelete(new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
                @Override
                public void execute(final ObservablePath.OnConcurrentDelete info) {
                    newConcurrentDelete(info.getPath(),
                            info.getIdentity(),
                            new Command() {
                                @Override
                                public void execute() {
                                    enableActions(false);
                                }
                            },
                            new Command() {
                                @Override
                                public void execute() {
                                    placeManager.closePlace("repositoryStructureScreen");
                                }
                            }
                    ).show();
                }
            });
        }
    }

    private void updateModulesList(List<String> modules) {
        if (modules != null) {
            for (String module : model.getModules()) {
                dataProvider.getList().add(new ProjectModuleRow(module));
            }
        }
    }

    private void updateProjectsList(List<Project> projects) {
        if (projects != null) {
            for (Project project : projects) {
                dataProvider.getList().add(new ProjectModuleRow(project.getProjectName()));
            }
        }
    }

    private void enableActions(boolean value) {
        view.getDataView().enableActions(value);
        view.getModulesView().enableActions(value);
    }

    private void clearView() {
        view.getDataView().clear();
        dataProvider.getList().clear();
        enableActions(true);
    }

    /**
     * *** Presenter interfaces *******
     */
    @Override
    public void onAddModule() {
        if (model.isMultiModule()) {
            wizzard.setContent(null,
                    view.getDataView().getGroupId(),
                    view.getDataView().getVersionId());
        } else {
            wizzard.setContent(null,
                    null,
                    null);

        }

        wizzard.start(getModuleAddedSuccessCallback(), false);
    }

    private Callback<Project> getModuleAddedSuccessCallback() {
        //optimization to avoid reloading the complete model when a module is added.
        return new Callback<Project>() {
            @Override
            public void callback(final Project _project) {
                lastAddedModule = _project;
                if (_project != null) {
                    //A new module was added.
                    if (model.isMultiModule()) {
                        view.showBusyIndicator(Constants.INSTANCE.Loading());
                        repositoryStructureService.call(new RemoteCallback<RepositoryStructureModel>() {
                            @Override
                            public void callback(RepositoryStructureModel _model) {
                                view.hideBusyIndicator();
                                if (_model != null) {
                                    model.setPOM(_model.getPOM());
                                    model.setPOMMetaData(_model.getPOMMetaData());
                                    model.setModules(_model.getModules());
                                    model.getModulesProject().put(_project.getProjectName(), _project);
                                    addToModulesList(_project);
                                }
                            }
                        }, new HasBusyIndicatorDefaultErrorCallback(view)).load(repository, false);

                    } else {
                        view.showBusyIndicator(Constants.INSTANCE.Loading());
                        pomService.call(new RemoteCallback<POM>() {
                            @Override
                            public void callback(POM _pom) {
                                view.hideBusyIndicator();
                                model.getOrphanProjects().add(_project);
                                model.getOrphanProjectsPOM().put(_project.getSignatureId(), _pom);
                                addToModulesList(_project);
                            }
                        }, new HasBusyIndicatorDefaultErrorCallback(view)).load(_project.getPomXMLPath());
                    }
                }
            }
        };
    }

    @Override
    public void addDataDisplay(HasData<ProjectModuleRow> display) {
        dataProvider.addDataDisplay(display);
    }

    @Override
    public void onDeleteModule(ProjectModuleRow moduleRow) {

        final Project project = getSelectedModule(moduleRow.getName());
        String message = null;

        if (project != null) {

            if (model.isMultiModule()) {
                message = Constants.INSTANCE.ConfirmModuleDeletion(moduleRow.getName());
            } else {
                message = Constants.INSTANCE.ConfirmProjectDeletion(moduleRow.getName());
            }

            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Information(),
                    message,
                    new Command() {
                        @Override
                        public void execute() {
                            deleteSelectedModule(project);
                        }
                    },
                    CommonConstants.INSTANCE.YES(),
                    ButtonType.DANGER,
                    IconType.MINUS_SIGN,
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

            yesNoCancelPopup.setCloseVisible(false);
            yesNoCancelPopup.show();
        }
    }

    private void deleteSelectedModule(final Project project) {

        view.showBusyIndicator(Constants.INSTANCE.Deleting());
        lastDeletedModule = project;
        repositoryStructureService.call(getModuleDeletedSuccessCallback(project), new HasBusyIndicatorDefaultErrorCallback(view)).delete(project.getPomXMLPath(), "Module removed");
    }

    private RemoteCallback<Void> getModuleDeletedSuccessCallback(final Project _project) {
        //optimization to avoid reloading the complete model when a module is added.
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                if (_project != null) {
                    //A project was deleted
                    if (model.isMultiModule()) {
                        view.showBusyIndicator(Constants.INSTANCE.Loading());
                        repositoryStructureService.call(new RemoteCallback<RepositoryStructureModel>() {
                            @Override
                            public void callback(RepositoryStructureModel _model) {
                                view.hideBusyIndicator();
                                if (_model != null) {
                                    model.setPOM(_model.getPOM());
                                    model.setPOMMetaData(_model.getPOMMetaData());
                                    model.setModules(_model.getModules());
                                    model.getModulesProject().remove(_project.getProjectName());
                                    removeFromModulesList(_project.getProjectName());
                                }
                            }
                        }, new HasBusyIndicatorDefaultErrorCallback(view)).load(repository, false);

                    } else {
                        model.getOrphanProjects().remove(_project);
                        model.getOrphanProjectsPOM().remove(_project.getSignatureId());
                        removeFromModulesList(_project.getProjectName());
                    }
                }
            }
        };
    }

    @Override
    public void onEditModule(ProjectModuleRow moduleRow) {

        Project project = getSelectedModule(moduleRow.getName());
        if (project != null) {
            //TODO check if there's a better implementation for this projectScreen opening.
            contextChangeEvent.fire(new ProjectContextChangeEvent(workbenchContext.getActiveOrganizationalUnit(), repository, project));
            placeManager.goTo("projectScreen");
        }
    }

    @Override
    public void onArtifactIdChange(String artifactId) {
        //Window.alert( "onArtifactIdChange: " + artifactId );
    }

    @Override
    public void onGroupIdChange(String groupId) {
        //Window.alert( "onGroupIdChange: " + groupId );
    }

    @Override
    public void onVersionChange(String version) {
        //Window.alert( "onVersionChange: " + version );
    }

    @Override
    public void onProjectModeChange() {

    }

    @Override
    public void onInitRepositoryStructure() {
        initRepositoryStructure();
    }

    @Override
    public void onSaveRepositoryStructure() {

        if (model.getPOM() != null) {

            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Information(),
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

            yesNoCancelPopup.setCloseVisible(false);
            yesNoCancelPopup.show();
        }
    }

    private void saveRepositoryStructure() {

        if (model.getPOM() != null) {

            model.getPOM().getGav().setGroupId(view.getDataView().getGroupId());
            model.getPOM().getGav().setArtifactId(view.getDataView().getArtifactId());
            model.getPOM().getGav().setVersion(view.getDataView().getVersionId());

            view.showBusyIndicator(Constants.INSTANCE.Saving());
            repositoryStructureService.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void response) {
                    view.hideBusyIndicator();
                    init();
                }
            }).save(model.getPathToPOM(), model, "");
        }
    }

    @Override
    public void onConvertToMultiModule() {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Information(),
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

        yesNoCancelPopup.setCloseVisible(false);
        yesNoCancelPopup.show();

    }

    private void convertToMultiModule() {
        Project project = model.getOrphanProjects().get(0);
        POM pom = model.getOrphanProjectsPOM().get(project.getSignatureId());
        GAV gav = new GAV(view.getDataView().getGroupId(), view.getDataView().getArtifactId(), view.getDataView().getVersionId());

        view.showBusyIndicator(Constants.INSTANCE.ConvertingToMultiModuleProject());
        repositoryStructureService.call(new RemoteCallback<Path>() {
            @Override
            public void callback(Path response) {
                view.hideBusyIndicator();
                init();
            }
        }).convertToMultiProjectStructure(model.getOrphanProjects(), gav, repository, true, null);
    }

    @Override
    public void onOpenSingleProject() {
        if (model != null && model.isSingleProject()) {
            placeManager.goTo("projectScreen");
        }
    }

    private Project getSelectedModule(String name) {
        Project project = null;
        if (model != null && name != null) {
            if (model.isMultiModule()) {
                project = model.getModulesProject() != null ? model.getModulesProject().get(name) : null;
            } else if (model.getOrphanProjects() != null) {
                for (Project _project : model.getOrphanProjects()) {
                    if (name.equals(_project.getProjectName())) {
                        project = _project;
                        break;
                    }
                }
            }
        }
        return project;
    }

    private void removeFromModulesList(String module) {
        if (module != null) {
            int index = -1;
            for (ProjectModuleRow row : dataProvider.getList()) {
                index++;
                if (module.equals(row.getName())) {
                    break;
                }
            }
            if (index >= 0 && (index == 0 || index < dataProvider.getList().size())) {
                dataProvider.getList().remove(index);
            }
        }
    }

    private void addToModulesList(final Project project) {
        dataProvider.getList().add(new ProjectModuleRow(project.getProjectName()));
    }

    private boolean repositoryOrBranchChanged(Repository selectedRepository) {
        return selectedRepository != null
                && (!selectedRepository.equals(this.repository)
                || !selectedRepository.getCurrentBranch().equals(this.branch));
    }

    private void makeMenuBar() {
        MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> configure = MenuFactory
                            .newTopLevelMenu(Constants.INSTANCE.Configure())
                            //.withRoles(kieACL.getGrantedRoles(F_PROJECT_AUTHORING_SAVE))
                            .respondsWith(getConfigureCommand())
                            .endMenu();
                    MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> promote = configure.newTopLevelMenu(Constants.INSTANCE.Promote())
                            // .withRoles(kieACL.getGrantedRoles(F_PROJECT_AUTHORING_DELETE))
                            .respondsWith(getPromoteCommand())
                            .endMenu();

                    MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> release = configure.newTopLevelMenu(Constants.INSTANCE.Release())
                            //  .withRoles(kieACL.getGrantedRoles(F_PROJECT_AUTHORING_RENAME))
                            .respondsWith(getReleaseCommand())
                            .endMenu();

                    menus = configure.build();
        

    }

    private Command getConfigureCommand() {
        return new Command() {
            @Override
            public void execute() {
                view.getConfigureScreenPopupView().configure(repository.getAlias(), branch, model.getPOM().getGav().getVersion(), new com.google.gwt.user.client.Command() {
                    @Override
                    public void execute() {
                        String devBranch = view.getConfigureScreenPopupView().getDevBranch();
                        String releaseBranch = view.getConfigureScreenPopupView().getReleaseBranch();

                        String version = view.getConfigureScreenPopupView().getVersion();

                        configureRepository(repository.getAlias(), branch, devBranch, releaseBranch, version);
                        view.getConfigureScreenPopupView().hide();
                    }
                });
                view.getConfigureScreenPopupView().show();
            }
        };

    }

    private Command getPromoteCommand() {
        return new Command() {
            @Override
            public void execute() {
                view.getPromoteScreenPopupView().configure(repository.getAlias(), branch, repository.getBranches(), new com.google.gwt.user.client.Command() {
                    @Override
                    public void execute() {
                        String targetBranch = view.getPromoteScreenPopupView().getTargetBranch();
                        promoteChanges(repository.getAlias(), branch, targetBranch);
                        view.getPromoteScreenPopupView().hide();
                    }
                });
                view.getPromoteScreenPopupView().show();
            }
        };

    }

    private Command getReleaseCommand() {
        return new Command() {
            @Override
            public void execute() {
                view.getReleaseScreenPopupView().configure(repository.getAlias(), branch, model.getPOM().getGav().getVersion(), new com.google.gwt.user.client.Command() {
                    @Override
                    public void execute() {
                        String username = view.getReleaseScreenPopupView().getUsername();
                        String password = view.getReleaseScreenPopupView().getPassword();
                        String serverURL = view.getReleaseScreenPopupView().getServerURL();
                        String version = view.getReleaseScreenPopupView().getVersion();
                        Boolean deployToRuntime = view.getReleaseScreenPopupView().getDeployToRuntime();
                        releaseProject(repository.getAlias(), branch, username, password, serverURL, deployToRuntime, version);
                        view.getReleaseScreenPopupView().hide();
                    }
                });
                view.getReleaseScreenPopupView().show();
            }
        };

    }

    public void configureRepository(String repository, String sourceBranch, String devBranch, String releaseBranch, String version) {
        assetManagementServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                //view.displayNotification( "Repository Configuration Started!" );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                org.uberfire.client.workbench.widgets.common.ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }
        ).configureRepository(repository, sourceBranch, devBranch, releaseBranch, version);

    }

    public void promoteChanges(String repository, String sourceBranch, String destinationBranch) {
        assetManagementServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                // view.displayNotification( "Promote Changes Process Started!" );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                org.uberfire.client.workbench.widgets.common.ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }
        ).promoteChanges(repository, sourceBranch, destinationBranch);

    }

    public void releaseProject(String repository, String branch,
            String userName, String password, String serverURL, Boolean deployToRuntime, String version) {

        if (serverURL != null && !serverURL.isEmpty() && serverURL.endsWith("/")) {
            serverURL = serverURL.substring(0, serverURL.length() - 1);
        }

        assetManagementServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
//                                              view.displayNotification( "Release project process started" );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                return true;
            }
        }
        ).releaseProject(repository, branch, userName, password, serverURL, deployToRuntime, version);

    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
}
