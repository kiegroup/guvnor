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
import org.kie.commons.io.IOService;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.commons.service.builder.model.BuildResults;
import org.kie.guvnor.commons.service.builder.model.IncrementalBuildResults;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    private Paths paths;
    private SourceServices sourceServices;
    private Event<BuildResults> buildResultsEvent;
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;
    private POMService pomService;
    private M2RepoService m2RepoService;
    private IOService ioService;

    private LRUBuilderCache cache = new LRUBuilderCache();

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl( final Paths paths,
                             final SourceServices sourceServices,
                             final POMService pomService,
                             final M2RepoService m2RepoService,
                             final Event<BuildResults> buildResultsEvent,
                             final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                             final IOService ioService ) {
        this.paths = paths;
        this.sourceServices = sourceServices;
        this.pomService = pomService;
        this.m2RepoService = m2RepoService;
        this.buildResultsEvent = buildResultsEvent;
        this.incrementalBuildResultsEvent = incrementalBuildResultsEvent;
        this.ioService = ioService;
    }

    @Override
    public void build( final Path pathToPom ) {
        assertBuilderCache( pathToPom );
        final Builder builder = cache.getEntry( pathToPom );
        final BuildResults results = builder.build();

        if ( results.getMessages().isEmpty() ) {
            deploy( pathToPom );
        }
        buildResultsEvent.fire( results );
    }

    private void assertBuilderCache( final Path pathToPom ) {
        if ( cache.getKeys().contains( pathToPom ) ) {
            return;
        }
        final POM gav = pomService.loadPOM( pathToPom );
        final Builder builder = new Builder( paths.convert( pathToPom ).getParent(),
                                             gav.getGav().getArtifactId(),
                                             paths,
                                             sourceServices,
                                             ioService );
        builder.build();
        cache.setEntry( pathToPom,
                        builder );
    }

    private void deploy( final Path pathToPom ) {
        assertBuilderCache( pathToPom );
        final POM gav = pomService.loadPOM( pathToPom );
        final Builder builder = cache.getEntry( pathToPom );
        final InternalKieModule kieModule = (InternalKieModule) builder.getKieModule();
        final ByteArrayInputStream input = new ByteArrayInputStream( kieModule.getBytes() );
        m2RepoService.deployJar( input,
                                 gav.getGav() );
    }

    @Override
    public void addResource( final Path pathToPom,
                             final Path resource ) {
        assertBuilderCache( pathToPom );
        final Builder builder = cache.getEntry( pathToPom );
        final IncrementalBuildResults results = builder.addResource( paths.convert( resource ) );
        incrementalBuildResultsEvent.fire( results );
    }

    @Override
    public void deleteResource( final Path pathToPom,
                                final Path resource ) {
        assertBuilderCache( pathToPom );
        final Builder builder = cache.getEntry( pathToPom );
        final IncrementalBuildResults results = builder.deleteResource( paths.convert( resource ) );
        incrementalBuildResultsEvent.fire( results );
    }

    @Override
    public void updateResource( final Path pathToPom,
                                final Path resource ) {
        assertBuilderCache( pathToPom );
        final Builder builder = cache.getEntry( pathToPom );
        final IncrementalBuildResults results = builder.updateResource( paths.convert( resource ) );
        incrementalBuildResultsEvent.fire( results );
    }

}
