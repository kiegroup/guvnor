/*
 * Copyright 2013 JBoss Inc
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

import org.junit.Before;
import org.junit.Test;
import org.kie.commons.io.IOService;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.event.Event;

import static org.mockito.Mockito.mock;

public class BuildServiceImplTest {


    private BuildService service;

    @Before
    public void setUp() throws Exception {
        IOService ioService = mock(IOService.class);
        Paths paths = mock(Paths.class);
        SourceServices sourceServices = mock(SourceServices.class);
        ProjectService projectService = mock(ProjectService.class);
        M2RepoService m2RepoService = mock(M2RepoService.class);
        Event messagesEvent = mock(Event.class);

        service = new BuildServiceImpl(ioService, paths, sourceServices, projectService, m2RepoService, messagesEvent);
    }
}
