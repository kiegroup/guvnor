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
import org.kie.guvnor.projecteditor.model.GroupArtifactVersionModel;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.model.builder.Messages;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;

public class MockProjectEditorServiceCaller
        implements Caller<ProjectEditorService> {

    private final ProjectEditorService service;

    private KProjectModel savedModel;
    private KProjectModel modelForLoading;

    private RemoteCallback callback;
    private Messages messages;
    private GroupArtifactVersionModel gavModel;
    private GroupArtifactVersionModel savedGav;
    private Path pathToRelatedKProjectFileIfAny;

    MockProjectEditorServiceCaller() {

        service = new ProjectEditorService() {


            @Override
            public Path setUpProjectStructure(Path pathToPom) {
                callback.callback(pathToRelatedKProjectFileIfAny);
                return pathToRelatedKProjectFileIfAny;
            }

            @Override
            public void saveKProject(Path path, KProjectModel model) {
                callback.callback(null);
                savedModel = model;
            }

            @Override
            public void saveGav(Path path, GroupArtifactVersionModel gav) {
                callback.callback(null);
                savedGav = gav;
            }

            @Override
            public KProjectModel loadKProject(Path path) {
                callback.callback(modelForLoading);
                return modelForLoading;
            }

            @Override
            public Messages build(Path path) {
                callback.callback(messages);
                return messages;
            }

            @Override
            public GroupArtifactVersionModel loadGav(Path path) {
                callback.callback(gavModel);
                return gavModel;
            }

            @Override
            public Path pathToRelatedKProjectFileIfAny(Path pathToPomXML) {
                callback.callback(pathToRelatedKProjectFileIfAny);
                return pathToRelatedKProjectFileIfAny;
            }
        };
    }

    public KProjectModel getSavedModel() {
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

    public void setUpModelForLoading(KProjectModel upModelForLoading) {
        this.modelForLoading = upModelForLoading;
    }

    public void setUpMessages(Messages messages) {
        this.messages = messages;
    }

    public void setGav(GroupArtifactVersionModel gavModel) {
        this.gavModel = gavModel;
    }

    public GroupArtifactVersionModel getSavedGav() {
        return savedGav;
    }

    public void setPathToRelatedKProjectFileIfAny(Path pathToRelatedKProjectFileIfAny) {
        this.pathToRelatedKProjectFileIfAny = pathToRelatedKProjectFileIfAny;
    }
}
