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
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.kie.guvnor.projecteditor.model.KModuleModel;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;

public class MockProjectEditorServiceCaller
        implements Caller<ProjectEditorService> {

    private final ProjectEditorService service;

    private KModuleModel savedModel;
    private KModuleModel modelForLoading;

    private RemoteCallback callback;
    private GroupArtifactVersionModel savedGav;
    private Path pathToRelatedKModuleFileIfAny;

    MockProjectEditorServiceCaller() {

        service = new ProjectEditorService() {


            @Override
            public Path setUpKModuleStructure(Path pathToPom) {
                callback.callback(pathToRelatedKModuleFileIfAny);
                return pathToRelatedKModuleFileIfAny;
            }

            @Override
            public void saveKModule(Path path, KModuleModel model) {
                callback.callback(null);
                savedModel = model;
            }

            @Override
            public Path saveGav(Path path, GroupArtifactVersionModel gav) {
                callback.callback(null);
                savedGav = gav;
                return null;
            }

            @Override
            public KModuleModel loadKModule(Path path) {
                callback.callback(modelForLoading);
                return modelForLoading;
            }

            @Override
            public Path pathToRelatedKModuleFileIfAny(Path pathToPomXML) {
                callback.callback(pathToRelatedKModuleFileIfAny);
                return pathToRelatedKModuleFileIfAny;
            }

            @Override
            public Path newProject(String name) {
                return null;  //TODO -Rikkola-
            }
        };
    }

    public KModuleModel getSavedModel() {
        return savedModel;
    }

    @Override
    public ProjectEditorService call(RemoteCallback<?> callback) {
        this.callback = callback;
        return service;
    }

    @Override
    public ProjectEditorService call(RemoteCallback<?> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        return service;
    }

    public void setUpModelForLoading(KModuleModel upModelForLoading) {
        this.modelForLoading = upModelForLoading;
    }

    public GroupArtifactVersionModel getSavedGav() {
        return savedGav;
    }

    public void setPathToRelatedKModuleFileIfAny(Path pathToRelatedKModuleFileIfAny) {
        this.pathToRelatedKModuleFileIfAny = pathToRelatedKModuleFileIfAny;
    }

}
