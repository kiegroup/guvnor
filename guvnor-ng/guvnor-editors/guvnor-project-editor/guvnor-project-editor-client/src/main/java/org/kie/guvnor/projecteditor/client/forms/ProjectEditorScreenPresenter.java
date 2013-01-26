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

package org.kie.guvnor.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.ui.client.save.SaveCommand;
import org.kie.guvnor.commons.ui.client.save.SaveOpWrapper;
import org.kie.guvnor.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.project.service.KModuleService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;

@WorkbenchEditor(identifier = "projectEditorScreen", fileTypes = "pom.xml")
public class ProjectEditorScreenPresenter
        implements ProjectEditorScreenView.Presenter {

    private final ProjectEditorScreenView view;
    private final POMEditorPanel pomPanel;
    private MetadataWidget pomMetaDataPanel = new MetadataWidget();
    private final KModuleEditorPanel kModuleEditorPanel;
    private MetadataWidget kModuleMetaDataPanel = new MetadataWidget();
    private final Caller<KModuleService> projectEditorServiceCaller;
    private final Caller<BuildService> buildServiceCaller;

    private Path pathToPomXML;
    private Path pathToKModuleXML;
    private final Caller<MetadataService> metadataService;
    private Metadata kmoduleMetadata;
    private Metadata pomMetadata;

    @Inject
    public ProjectEditorScreenPresenter(
            ProjectEditorScreenView view,
            POMEditorPanel pomPanel,
            KModuleEditorPanel kModuleEditorPanel,
            Caller<KModuleService> projectEditorServiceCaller,
            Caller<BuildService> buildServiceCaller,
            Caller<MetadataService> metadataService) {
        this.view = view;
        this.pomPanel = pomPanel;
        this.kModuleEditorPanel = kModuleEditorPanel;
        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.buildServiceCaller = buildServiceCaller;
        this.metadataService = metadataService;

        view.setPresenter(this);
        view.setPOMEditorPanel(pomPanel);
        view.setPOMMetadataPanel(pomMetaDataPanel);
    }

    @OnStart
    public void init(Path path) {

        pathToPomXML = path;
        pomPanel.init(path);
        projectEditorServiceCaller.call(
                new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path pathToKModuleXML) {
                        ProjectEditorScreenPresenter.this.pathToKModuleXML = pathToKModuleXML;
                        if (pathToKModuleXML != null) {
                            setUpKProject();
                        }
                    }
                }
        ).pathToRelatedKModuleFileIfAny(path);
    }

    private void setUpKProject() {
        view.setKModuleEditorPanel(kModuleEditorPanel);
        view.setKModuleMetadataPanel(kModuleMetaDataPanel);
    }

    @WorkbenchPartTitle
    public IsWidget getTitle() {
        return pomPanel.getTitle();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        MenuBar menuBar = new DefaultMenuBar();

        menuBar.addItem(new DefaultMenuItemCommand(
                view.getSaveMenuItemText(),
                new Command() {
                    @Override
                    public void execute() {
                        new SaveOpWrapper(pathToPomXML, new SaveCommand() {
                            @Override
                            public void execute(final String comment) {
                                // We need to use callback here or jgit will break when we save two files at the same time.
                                pomPanel.save(
                                        comment,
                                        new com.google.gwt.user.client.Command() {
                                            @Override
                                            public void execute() {
                                                if (kModuleEditorPanel.hasBeenInitialized()) {
                                                    kModuleEditorPanel.save(comment, kmoduleMetadata);
                                                }
                                                // TODO: Save the metadata, use callback (check the comment above) -Rikkola-
                                            }
                                        },
                                        pomMetadata);
                            }
                        }).save();


                    }
                }
        ));
        menuBar.addItem(new DefaultMenuItemCommand(
                view.getBuildMenuItemText(),
                new Command() {
                    @Override
                    public void execute() {
                        buildServiceCaller.call(
                                new RemoteCallback<Void>() {
                                    @Override
                                    public void callback(Void v) {

                                    }
                                }
                        ).build(pathToPomXML);
                    }
                }
        ));
        // For now every module is a kie project.
//        if (pathToKModuleXML == null) {
//            menuBar.addItem(new DefaultMenuItemCommand(
//                    view.getEnableKieProjectMenuItemText(),
//                    new Command() {
//                        @Override
//                        public void execute() {
//                            projectEditorServiceCaller.call(
//                                    new RemoteCallback<Path>() {
//                                        @Override
//                                        public void callback(Path pathToKProject) {
//                                            pathToKModuleXML = pathToKProject;
//                                            setUpKProject(pathToKProject);
//                                        }
//                                    }
//                            ).setUpKModuleStructure(pathToPomXML);
//                        }
//                    }
//            ));
//        }

        return menuBar;
    }

    @Override
    public void onPOMMetadataTabSelected() {
        if (pomMetadata == null) {
            metadataService.call(new RemoteCallback<Metadata>() {
                @Override
                public void callback(Metadata metadata) {
                    pomMetadata = metadata;
                    pomMetaDataPanel.setContent(metadata, false);
                }
            }).getMetadata(pathToPomXML);
        }
    }

    @Override
    public void onKModuleTabSelected() {
        if (!kModuleEditorPanel.hasBeenInitialized()) {
            kModuleEditorPanel.init(pathToKModuleXML);
        }
    }

    @Override
    public void onKModuleMetadataTabSelected() {
        if (kmoduleMetadata == null) {
            metadataService.call(new RemoteCallback<Metadata>() {
                @Override
                public void callback(Metadata metadata) {
                    kmoduleMetadata = metadata;
                    kModuleMetaDataPanel.setContent(metadata, false);
                }
            }).getMetadata(pathToKModuleXML);
        }
    }
}
