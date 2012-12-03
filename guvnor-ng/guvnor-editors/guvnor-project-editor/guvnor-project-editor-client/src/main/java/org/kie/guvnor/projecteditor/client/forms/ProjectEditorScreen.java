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

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

@WorkbenchEditor(identifier = "projectEditorScreen", fileTypes = "xml")
public class ProjectEditorScreen
        implements ProjectEditorScreenView.Presenter {

    private final ProjectEditorScreenView view;
    private final GroupArtifactVersionEditorPanel gavPanel;
    private final KProjectEditorPanel kProjectEditorPanel;
    private final Caller<ProjectEditorService> projectEditorServiceCaller;
    private Path pathToPomXML;

    @Inject
    public ProjectEditorScreen(ProjectEditorScreenView view,
                               GroupArtifactVersionEditorPanel gavPanel,
                               KProjectEditorPanel kProjectEditorPanel,
                               Caller<ProjectEditorService> projectEditorServiceCaller) {
        this.view = view;
        this.gavPanel = gavPanel;
        this.kProjectEditorPanel = kProjectEditorPanel;
        this.projectEditorServiceCaller = projectEditorServiceCaller;

        view.setPresenter(this);
        view.setGroupArtifactVersionEditorPanel(gavPanel);
        view.setKProjectEditorPanel(kProjectEditorPanel);
    }

    @OnStart
    public void init(Path path) {
        pathToPomXML = path;
        gavPanel.init(path);
        projectEditorServiceCaller.call(
                new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path path) {
                        if (path != null) {
                            setUpKProject(path);
                        } else {
                            view.setKProjectToggleOff();
                        }
                    }
                }
        ).pathToRelatedKProjectFileIfAny();
    }

    private void setUpKProject(Path path) {
        kProjectEditorPanel.init(path);
        view.setKProjectToggleOn();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ""; // TODO needs to be set later, to what ever the artifact name is -Rikkola-
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onKProjectToggleOn() {
        projectEditorServiceCaller.call(
                new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path pathToKProject) {
                        kProjectEditorPanel.init(pathToKProject);
                    }
                }
        ).setUpProjectStructure(pathToPomXML);
    }
}
