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

package org.kie.guvnor.packageeditor.client.forms;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

import javax.enterprise.inject.New;

@WorkbenchEditor(identifier = "packageConfigScreen", fileTypes = "package.config")
public class PackageConfigScreenPresenter
        implements PackageConfigScreenView.Presenter {

    private final PackageConfigScreenView view;
    private final Caller<ProjectService> projectEditorServiceCaller;
    private final Caller<MetadataService> metadataService;
    private Path path;

    @Inject
    public PackageConfigScreenPresenter(@New PackageConfigScreenView view,
                                        Caller<ProjectService> projectEditorServiceCaller,
                                        Caller<MetadataService> metadataService) {
        this.view = view;
        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.metadataService = metadataService;
        view.setPresenter(this);
    }

    @OnStart
    public void init(Path path) {

        this.path = path;

        projectEditorServiceCaller.call(new RemoteCallback<PackageConfiguration>() {

            @Override
            public void callback(PackageConfiguration packageConfiguration) {
                view.setImports(packageConfiguration.getImports());
            }
        }).loadPackageConfiguration(path);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Package Configurations";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onShowMetadata() {
        metadataService.call(new RemoteCallback<Metadata>() {
            @Override
            public void callback(Metadata metadata) {
                view.setMetadata(metadata);
            }
        }).getMetadata(path);
    }

    // TODO: Save -Rikkola-

}
