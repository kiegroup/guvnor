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

package org.kie.guvnor.projectconfigscreen.client.forms;

import javax.enterprise.inject.New;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.menu.ResourceMenuBuilderImpl;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.projectconfigscreen.client.ProjectConfigResourceType;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;

@WorkbenchEditor(identifier = "projectConfigScreen", supportedTypes = { ProjectConfigResourceType.class })
public class ProjectConfigScreenPresenter
        implements ProjectConfigScreenView.Presenter {

    private ProjectConfigScreenView view;
    private Caller<ProjectService>  projectEditorServiceCaller;
    private Caller<MetadataService> metadataService;
    private Path                    path;
    private PackageConfiguration    packageConfiguration;

    private ResourceMenuBuilderImpl menuBuilder;

    private MenuBar menuBar;

    public ProjectConfigScreenPresenter() {
    }

    @Inject
    public ProjectConfigScreenPresenter( @New ProjectConfigScreenView view,
                                         @New ResourceMenuBuilderImpl menuBuilder,
                                         Caller<ProjectService> projectEditorServiceCaller,
                                         Caller<MetadataService> metadataService ) {
        this.view = view;
        this.menuBuilder = menuBuilder;
        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.metadataService = metadataService;
        view.setPresenter( this );
    }

    @OnStart
    public void init( final Path path ) {

        this.path = path;
        makeMenuBar();

        projectEditorServiceCaller.call( new RemoteCallback<PackageConfiguration>() {

            @Override
            public void callback( PackageConfiguration packageConfiguration ) {
                ProjectConfigScreenPresenter.this.packageConfiguration = packageConfiguration;
                view.setImports( path, packageConfiguration.getImports() );
            }
        } ).loadPackageConfiguration( path );
    }

    private void makeMenuBar() {
        menuBar = menuBuilder.addFileMenu().addSave( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Project Import Suggestions";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onShowMetadata() {
        metadataService.call( new RemoteCallback<Metadata>() {
            @Override
            public void callback( Metadata metadata ) {
                view.setMetadata( metadata );
            }
        } ).getMetadata( path );
    }

    @OnSave
    public void onSave() {
        projectEditorServiceCaller.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {

            }
        } ).save( path, packageConfiguration );
    }

    @WorkbenchMenu
    public MenuBar getMenuBar() {
        return menuBar;
    }

}
