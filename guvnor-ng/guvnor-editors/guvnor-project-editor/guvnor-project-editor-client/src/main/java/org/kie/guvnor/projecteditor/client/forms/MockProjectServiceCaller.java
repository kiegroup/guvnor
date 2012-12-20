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

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;

import java.util.Collection;

public class MockProjectServiceCaller
        implements Caller<ProjectService> {


    private GroupArtifactVersionModel gavModel;
    private final ProjectService service;
    private RemoteCallback callback;

    public MockProjectServiceCaller() {
        service = new ProjectService() {

            @Override
            public Collection<Path> listProjectResources(Path project) {
                return null;  //TODO -Rikkola-
            }

            @Override
            public WorkingSetSettings loadWorkingSetConfig(Path project) {
                return null;  //TODO -Rikkola-
            }

            @Override
            public GroupArtifactVersionModel loadGav(Path path) {
                callback.callback(gavModel);
                return gavModel;
            }

            @Override
            public Path resolveProject(Path resource) {
                return null;  //TODO -Rikkola-
            }
        };
    }

    @Override
    public ProjectService call(RemoteCallback<?> remoteCallback) {
        callback = remoteCallback;
        return service;
    }

    @Override
    public ProjectService call(RemoteCallback<?> remoteCallback, ErrorCallback errorCallback) {
        callback = remoteCallback;
        return service;
    }

    public void setGav(GroupArtifactVersionModel gavModel) {
        this.gavModel = gavModel;
    }
}
