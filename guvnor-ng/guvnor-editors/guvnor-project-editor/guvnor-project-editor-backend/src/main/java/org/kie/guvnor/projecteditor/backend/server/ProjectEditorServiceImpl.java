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

package org.kie.guvnor.projecteditor.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.projecteditor.model.KProjectModel;
import org.kie.guvnor.projecteditor.service.ProjectEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Service
@ApplicationScoped
public class ProjectEditorServiceImpl
        implements ProjectEditorService {

    @Inject
    private VFSService vfsService;

    @Override
    public void save(Path path, KProjectModel model) {
       vfsService.write(path,ProjectEditorContentHandler.toString(model));
    }

    @Override
    public KProjectModel load(Path path) {
        return ProjectEditorContentHandler.toModel(vfsService.readAllString(path));
    }

}
