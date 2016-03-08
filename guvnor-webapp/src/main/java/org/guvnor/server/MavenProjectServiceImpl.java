/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.server;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.AbstractProjectService;
import org.guvnor.common.services.project.backend.server.utils.IdentifierUtils;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.guvnor.common.services.project.backend.server.ProjectResourcePaths.*;

@Service
@ApplicationScoped
public class MavenProjectServiceImpl
        extends AbstractProjectService<Project>
        implements ProjectService<Project> {

    public MavenProjectServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public MavenProjectServiceImpl( final @Named("ioStrategy") IOService ioService,
                                    final POMService pomService,
                                    final ConfigurationService configurationService,
                                    final ConfigurationFactory configurationFactory,
                                    final Event<NewProjectEvent> newProjectEvent,
                                    final Event<NewPackageEvent> newPackageEvent,
                                    final Event<RenameProjectEvent> renameProjectEvent,
                                    final Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache,
                                    final SessionInfo sessionInfo,
                                    final AuthorizationManager authorizationManager,
                                    final BackwardCompatibleUtil backward,
                                    final CommentedOptionFactory commentedOptionFactory,
                                    final MavenResourceResolver resourceResolver ) {
        super( ioService,
               pomService,
               configurationService,
               configurationFactory,
               newProjectEvent,
               newPackageEvent,
               renameProjectEvent,
               invalidateDMOCache,
               sessionInfo,
               authorizationManager,
               backward,
               commentedOptionFactory,
               resourceResolver );
    }

    @Override
    public Project newProject( final Path fsRoot,
                               final POM pom,
                               final String baseUrl ) {
        final FileSystem fs = Paths.convert( fsRoot ).getFileSystem();
        try {
            //Projects are always created in the FS root
            final Path projectRootPath = Paths.convert( Paths.convert( fsRoot ).resolve( pom.getName() ) );

            ioService.startBatch( new FileSystem[]{ fs },
                                  commentedOptionFactory.makeCommentedOption( "New project [" + pom.getName() + "]" ) );

            //Create POM.xml
            pomService.create( projectRootPath,
                               baseUrl,
                               pom );

            //Raise an event for the new project
            final Project project = resolveProject( projectRootPath );
            newProjectEvent.fire( new NewProjectEvent( project,
                                                       commentedOptionFactory.getSafeSessionId(),
                                                       commentedOptionFactory.getSafeIdentityName() ) );

            //Create a default workspace based on the GAV
            final String legalJavaGroupId[] = IdentifierUtils.convertMavenIdentifierToJavaIdentifier( pom.getGav().getGroupId().split( "\\.",
                                                                                                                                       -1 ) );
            final String legalJavaArtifactId[] = IdentifierUtils.convertMavenIdentifierToJavaIdentifier( pom.getGav().getArtifactId().split( "\\.",
                                                                                                                                             -1 ) );
            final String defaultWorkspacePath = StringUtils.join( legalJavaGroupId,
                                                                  "/" ) + "/" + StringUtils.join( legalJavaArtifactId,
                                                                                                  "/" );
            final Path defaultPackagePath = Paths.convert( Paths.convert( projectRootPath ).resolve( MAIN_RESOURCES_PATH ) );
            final org.guvnor.common.services.project.model.Package defaultPackage = resolvePackage( defaultPackagePath );
            final Package defaultWorkspacePackage = resourceResolver.newPackage( defaultPackage,
                                                                                 defaultWorkspacePath,
                                                                                 false );

            //Raise an event for the new project's default workspace
            newPackageEvent.fire( new NewPackageEvent( defaultWorkspacePackage ) );

            //Return new project
            return project;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public Project newProject( final Path repositoryRoot,
                               final POM pom,
                               final String baseURL,
                               final DeploymentMode mode ) {
        return newProject( repositoryRoot,
                           pom,
                           baseURL );
    }

    @Override
    public Project resolveProject( final Path resource ) {
        return resourceResolver.resolveProject( resource );
    }

    @Override
    public Project resolveParentProject( final Path resource ) {
        return resourceResolver.resolveParentProject( resource );
    }

    @Override
    public Project resolveToParentProject( final Path resource ) {
        return resourceResolver.resolveParentProject( resource );
    }

    @Override
    public Set<Package> resolvePackages( final Project project ) {
        return resourceResolver.resolvePackages( project );
    }

    @Override
    public Set<Package> resolvePackages( final Package pkg ) {
        return resourceResolver.resolvePackages( pkg );
    }

    @Override
    public Package resolveDefaultPackage( final Project project ) {
        return resourceResolver.resolveDefaultPackage( project );
    }

    @Override
    public Package resolveParentPackage( final Package pkg ) {
        return resourceResolver.resolveParentPackage( pkg );
    }

    @Override
    public boolean isPom( final Path resource ) {
        return resourceResolver.isPom( resource );
    }

    @Override
    public org.guvnor.common.services.project.model.Package resolvePackage( final Path resource ) {
        return resourceResolver.resolvePackage( resource );
    }

    @Override
    public Project simpleProjectInstance( final org.uberfire.java.nio.file.Path nioProjectRootPath ) {
        return resourceResolver.simpleProjectInstance( nioProjectRootPath );
    }

}