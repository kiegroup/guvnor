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

package org.guvnor.common.services.builder;

import java.io.ByteArrayInputStream;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.DeployResult;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    private Paths paths;
    private POMService pomService;
    private ExtendedM2RepoService m2RepoService;
    private ProjectService projectService;
    private LRUBuilderCache cache;
    private Event<DeployResult> deployResultEvent;

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl( final Paths paths,
                             final POMService pomService,
                             final ExtendedM2RepoService m2RepoService,
                             final ProjectService projectService,
                             final LRUBuilderCache cache,
                             final Event<DeployResult> deployResultEvent ) {
        this.paths = paths;
        this.pomService = pomService;
        this.m2RepoService = m2RepoService;
        this.projectService = projectService;
        this.cache = cache;
        this.deployResultEvent = deployResultEvent;
    }

    @Override
    public BuildResults build( final Project project ) {
        try {
            final BuildResults results = doBuild( project );
            return results;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public DeployResult buildAndDeploy( final Project project ) {
        try {
            //Build
            final BuildResults results = doBuild( project );

            //Deploy, if no errors
            final POM pom = pomService.load( project.getPomXMLPath() );
            if ( results.getMessages().isEmpty() ) {
                final Builder builder = cache.assertBuilder( project );
                final InternalKieModule kieModule = (InternalKieModule) builder.getKieModule();
                final ByteArrayInputStream input = new ByteArrayInputStream( kieModule.getBytes() );
                m2RepoService.deployJar( input,
                                         pom.getGav() );

                DeployResult deployResult = new DeployResult( pom.getGav() );
                deployResult.setBuildMessages( results.getMessages() );
                deployResultEvent.fire( deployResult );

                return deployResult;
            } else {
                DeployResult deployResult = new DeployResult( pom.getGav() );
                deployResult.setBuildMessages( results.getMessages() );
                deployResultEvent.fire( deployResult );
                return deployResult;
            }

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private BuildResults doBuild( final Project project ) {
        final Builder builder = cache.assertBuilder( project );
        final BuildResults results = builder.build();
        return results;
    }

    @Override
    public boolean isBuilt( final Project project ) {
        final Builder builder = cache.assertBuilder( project );
        return builder.isBuilt();
    }

    @Override
    public IncrementalBuildResults addPackageResource( final Path resource ) {
        try {
            IncrementalBuildResults results = new IncrementalBuildResults();
            final Project project = projectService.resolveProject( resource );
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.addResource( paths.convert( resource ) );
            }

            return results;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public IncrementalBuildResults deletePackageResource( final Path resource ) {
        try {
            IncrementalBuildResults results = new IncrementalBuildResults();
            final Project project = projectService.resolveProject( resource );
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.deleteResource( paths.convert( resource ) );
            }

            return results;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public IncrementalBuildResults updatePackageResource( final Path resource ) {
        try {
            IncrementalBuildResults results = new IncrementalBuildResults();
            final Project project = projectService.resolveProject( resource );
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.updateResource( paths.convert( resource ) );
            }

            return results;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges( final Project project,
                                                              final Set<ResourceChange> changes ) {
        IncrementalBuildResults results = new IncrementalBuildResults();
        try {
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.applyBatchResourceChanges( changes );
            }

            return results;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
