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

package org.kie.guvnor.builder;

import org.kie.commons.io.IOService;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.service.builder.model.Messages;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@RequestScoped
public class BuilderServiceImpl
        implements BuildService {

    private IOService ioService;
    private Paths paths;
    private SourceServicesImpl sourceServices;
    private Event<Messages> messagesEvent;
    private ProjectService projectService;

    public BuilderServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuilderServiceImpl(IOService ioService,
                              Paths paths,
                              SourceServicesImpl sourceServices,
                              ProjectService projectService,
                              Event<Messages> messagesEvent) {
        this.ioService = ioService;
        this.paths = paths;
        this.sourceServices = sourceServices;
        this.messagesEvent = messagesEvent;
        this.projectService = projectService;
    }

    @Override
    public void build(Path pathToPom) {
        GroupArtifactVersionModel gav = projectService.loadGav(pathToPom);

        Builder builder = new Builder(paths.convert(pathToPom), gav.getArtifactId(), ioService, paths, sourceServices, messagesEvent);

        builder.build();
    }
}
