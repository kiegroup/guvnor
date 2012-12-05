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
import org.kie.guvnor.projecteditor.service.FileService;
import org.uberfire.backend.vfs.Path;

public class MockFileServiceCaller
        implements Caller<FileService> {

    private Path newPathToReturn;

    private FileService fileService;
    private RemoteCallback callback;

    public MockFileServiceCaller() {
        this.fileService = new FileService() {
            @Override
            public Path newProject(String folderName) {
                callback.callback(newPathToReturn);
                return newPathToReturn;
            }
        };
    }

    public void setNewPathToReturn(Path newPathToReturn) {
        this.newPathToReturn = newPathToReturn;
    }

    @Override
    public FileService call(RemoteCallback<?> remoteCallback) {
        callback = remoteCallback;
        return fileService;
    }

    @Override
    public FileService call(RemoteCallback<?> remoteCallback, ErrorCallback errorCallback) {
        callback = remoteCallback;
        return fileService;
    }
}
