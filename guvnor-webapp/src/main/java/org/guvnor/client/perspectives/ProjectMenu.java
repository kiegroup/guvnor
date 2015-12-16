/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.client.perspectives;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.client.editors.fileexplorer.PathSelectedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProjectMenu {

    @Inject
    private Caller<ProjectService> projectService;

    @Inject
    private Caller<BuildService> buildService;

    @Inject
    private Event<BuildResults> buildResultsEvent;

    @Inject
    protected Event<NotificationEvent> notification;

    private Project activeProject;

    @Inject
    private PlaceManager placeManager;

    private MenuItem build = MenuFactory
            .newSimpleItem("Build and Deploy")
            .respondsWith(new Command() {
                @Override
                public void execute() {
                    notification.fire(new NotificationEvent("Build started for project " + activeProject.getProjectName()));
                    buildService.call(
                            new RemoteCallback<BuildResults>() {
                                @Override
                                public void callback(BuildResults buildResults) {
                                    if (buildResults.getErrorMessages().isEmpty()) {
                                        notification.fire(new NotificationEvent("Build Successful",
                                                NotificationEvent.NotificationType.SUCCESS));
                                    } else {
                                        notification.fire(new NotificationEvent("Build Failed",
                                                NotificationEvent.NotificationType.ERROR));
                                    }
                                    
                                    buildResultsEvent.fire(buildResults);
                                }
                            }
                    ).buildAndDeploy(activeProject);
                }
            }).endMenu()
            .build().getItems().get(0);

    public List<MenuItem> getMenuItems() {
        List<MenuItem> menuItems = new ArrayList<MenuItem>();

        build.setEnabled(false);
        menuItems.add(build);

        return menuItems;
    }

    public void onPathSelectedEvent(@Observes PathSelectedEvent pathSelectedEvent) {
        projectService.call(
                new RemoteCallback<Project>() {
                    @Override
                    public void callback(Project project) {
                        activeProject = project;
                        build.setEnabled(project != null);
                    }
                }
        ).resolveProject(pathSelectedEvent.getPath());
    }

}
