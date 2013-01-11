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

import java.io.ByteArrayInputStream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.builder.impl.InternalKieModule;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.service.builder.model.Results;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.m2repo.model.GAV;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    private Paths paths;
    private SourceServices sourceServices;
    private Event<Results> messagesEvent;
    private ProjectService projectService;
    private M2RepoService m2RepoService;

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl( final Paths paths,
                             final SourceServices sourceServices,
                             final ProjectService projectService,
                             final M2RepoService m2RepoService,
                             final Event<Results> messagesEvent ) {
        this.paths = paths;
        this.sourceServices = sourceServices;
        this.messagesEvent = messagesEvent;
        this.projectService = projectService;
        this.m2RepoService = m2RepoService;
    }

    @Override
    public void build( final Path pathToPom ) {
        final GroupArtifactVersionModel gav = projectService.loadGav( pathToPom );

        final Builder builder = new Builder( paths.convert( pathToPom ).getParent(),
                                             gav.getArtifactId(),
                                             paths,
                                             sourceServices );

        final Results results = builder.build();
        if ( results.isEmpty() ) {

            final InternalKieModule kieModule = (InternalKieModule) builder.getKieModule();
            final ByteArrayInputStream input = new ByteArrayInputStream( kieModule.getBytes() );

            //Refactor GAV later
            final GAV anotherGav = new GAV( gav.getArtifactId(),
                                            gav.getGroupId(),
                                            gav.getVersion() );
            m2RepoService.deployJar( input,
                                     anotherGav );
        }
        messagesEvent.fire( results );
    }
}
