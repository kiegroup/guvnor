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
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.commons.io.IOService;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.service.builder.model.BuildResults;
import org.kie.guvnor.commons.service.builder.model.IncrementalBuildResults;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    private Paths paths;
    private Event<BuildResults> buildResultsEvent;
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;
    private POMService pomService;
    private M2RepoService m2RepoService;
    private ProjectService projectService;
    private LRUBuilderCache cache;

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl( final Paths paths,
                             final POMService pomService,
                             final M2RepoService m2RepoService,
                             final Event<BuildResults> buildResultsEvent,
                             final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                             final ProjectService projectService,
                             final LRUBuilderCache cache) {
        this.paths = paths;
        this.pomService = pomService;
        this.m2RepoService = m2RepoService;
        this.buildResultsEvent = buildResultsEvent;
        this.incrementalBuildResultsEvent = incrementalBuildResultsEvent;
        this.projectService = projectService;
        this.cache = cache;
    }

    @Override
    public void build( final Path pathToPom ) {
        final BuildResults results = doBuild( pathToPom );
        buildResultsEvent.fire( results );
    }

    @Override
    public void buildAndDeploy( final Path pathToPom ) {
        //Build
        final BuildResults results = doBuild( pathToPom );
        buildResultsEvent.fire( results );

        //Deploy, if no errors
        if ( results.getMessages().isEmpty() ) {
            final POM gav = pomService.load( pathToPom );
            final Builder builder = cache.assertBuilder( pathToPom );
            final InternalKieModule kieModule = (InternalKieModule) builder.getKieModule();
            final ByteArrayInputStream input = new ByteArrayInputStream( kieModule.getBytes() );
            m2RepoService.deployJar( input,
                                     gav.getGav() );
        }
    }

    private BuildResults doBuild( final Path pathToPom ) {
        final Builder builder = cache.assertBuilder( pathToPom );
        final BuildResults results = builder.build();
        return results;
    }

    @Override
    public void addResource( final Path resource ) {
        final Path pathToPom = projectService.resolvePathToPom( resource );
        if ( pathToPom == null ) {
            return;
        }
        final Builder builder = cache.assertBuilder( pathToPom );
        if ( !builder.isBuilt() ) {
            build( pathToPom );
        }
        final IncrementalBuildResults results = builder.addResource( paths.convert( resource ) );
        incrementalBuildResultsEvent.fire( results );
    }

    @Override
    public void deleteResource( final Path resource ) {
        final Path pathToPom = projectService.resolvePathToPom( resource );
        if ( pathToPom == null ) {
            return;
        }
        final Builder builder = cache.assertBuilder( pathToPom );
        if ( !builder.isBuilt() ) {
            build( pathToPom );
        }
        final IncrementalBuildResults results = builder.deleteResource( paths.convert( resource ) );
        incrementalBuildResultsEvent.fire( results );
    }

    @Override
    public void updateResource( final Path resource ) {
        final Path pathToPom = projectService.resolvePathToPom( resource );
        if ( pathToPom == null ) {
            return;
        }
        final Builder builder = cache.assertBuilder( pathToPom );
        if ( !builder.isBuilt() ) {
            build( pathToPom );
        }
        final IncrementalBuildResults results = builder.updateResource( paths.convert( resource ) );
        incrementalBuildResultsEvent.fire( results );
    }

}
