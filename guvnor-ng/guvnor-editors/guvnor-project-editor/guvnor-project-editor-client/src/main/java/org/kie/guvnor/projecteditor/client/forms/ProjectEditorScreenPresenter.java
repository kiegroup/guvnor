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

import javax.enterprise.inject.New;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.ui.client.save.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.save.SaveOperationService;
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
public class
        ProjectEditorScreenPresenter
        implements ProjectEditorScreenView.Presenter {

    private ProjectEditorScreenView view;
    private POMEditorPanel          pomPanel;
    private KModuleEditorPanel      kModuleEditorPanel;
    private Caller<KModuleService>  projectEditorServiceCaller;
    private Caller<BuildService>    buildServiceCaller;

    private Path                    pathToPomXML;
    private Path                    pathToKModuleXML;
    private Caller<MetadataService> metadataService;
    private Metadata                kmoduleMetadata;
    private Metadata                pomMetadata;
    private SaveOperationService    saveOperationService;

    public ProjectEditorScreenPresenter() {
    }

    @Inject
    public ProjectEditorScreenPresenter(
            @New ProjectEditorScreenView view,
            @New POMEditorPanel pomPanel,
            @New KModuleEditorPanel kModuleEditorPanel,
            Caller<KModuleService> projectEditorServiceCaller,
            Caller<BuildService> buildServiceCaller,
            Caller<MetadataService> metadataService,
            SaveOperationService saveOperationService ) {
        this.view = view;
        this.pomPanel = pomPanel;
        this.kModuleEditorPanel = kModuleEditorPanel;
        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.buildServiceCaller = buildServiceCaller;
        this.metadataService = metadataService;
        this.saveOperationService = saveOperationService;

        view.setPresenter( this );
        view.setPOMEditorPanel( pomPanel );
    }

    @OnStart
    public void init( Path path ) {

        pathToPomXML = path;
        pomPanel.init( path );
        projectEditorServiceCaller.call(
                new RemoteCallback<Path>() {
                    @Override
                    public void callback( Path pathToKModuleXML ) {
                        ProjectEditorScreenPresenter.this.pathToKModuleXML = pathToKModuleXML;
                        if ( pathToKModuleXML != null ) {
                            setUpKProject();
                        }
                    }
                }
                                       ).pathToRelatedKModuleFileIfAny( path );
    }

    private void setUpKProject() {
        view.setKModuleEditorPanel( kModuleEditorPanel );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        //TODO {porcelli} Needs a way to notify title updates -> return pomPanel.getTitle();
        return "Project Editor [" + pathToPomXML.getFileName() + "]";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        MenuBar menuBar = new DefaultMenuBar();

        menuBar.addItem( new DefaultMenuItemCommand(
                view.getSaveMenuItemText(),
                new Command() {
                    @Override
                    public void execute() {
                        saveOperationService.save( pathToPomXML, new CommandWithCommitMessage() {
                            @Override
                            public void execute( final String comment ) {
                                // We need to use callback here or jgit will break when we save two files at the same time.
                                pomPanel.save(
                                        comment,
                                        new com.google.gwt.user.client.Command() {
                                            @Override
                                            public void execute() {
                                                if ( kModuleEditorPanel.hasBeenInitialized() ) {
                                                    kModuleEditorPanel.save( comment, kmoduleMetadata );
                                                }
                                                // TODO: Save the metadata, use callback (check the comment above) -Rikkola-
                                            }
                                        },
                                        pomMetadata );
                            }
                        } );
                    }
                }
        ) );
        menuBar.addItem( new DefaultMenuItemCommand(
                view.getBuildMenuItemText(),
                new Command() {
                    @Override
                    public void execute() {
                        buildServiceCaller.call(
                                new RemoteCallback<Void>() {
                                    @Override
                                    public void callback( Void v ) {

                                    }
                                }
                                               ).build( pathToPomXML );
                    }
                }
        ) );
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
        if ( pomMetadata == null ) {
            metadataService.call( new RemoteCallback<Metadata>() {
                @Override
                public void callback( Metadata metadata ) {
                    pomMetadata = metadata;
                    view.setPOMMetadata( metadata );
                }
            } ).getMetadata( pathToPomXML );
        }
    }

    @Override
    public void onKModuleTabSelected() {
        if ( !kModuleEditorPanel.hasBeenInitialized() ) {
            kModuleEditorPanel.init( pathToKModuleXML );
        }
    }

    @Override
    public void onKModuleMetadataTabSelected() {
        if ( kmoduleMetadata == null ) {
            metadataService.call( new RemoteCallback<Metadata>() {
                @Override
                public void callback( Metadata metadata ) {
                    kmoduleMetadata = metadata;
                    view.setKModuleMetadata( metadata );
                }
            } ).getMetadata( pathToKModuleXML );
        }
    }
}
