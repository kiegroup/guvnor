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

package org.guvnor.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.backend.server.AbstractProjectService;
import org.guvnor.common.services.project.backend.server.ProjectConfigurationContentHandler;
import org.guvnor.common.services.project.backend.server.utils.IdentifierUtils;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.rpc.SessionInfo;

import org.jboss.errai.security.shared.api.identity.User;


@Service
@ApplicationScoped
public class ProjectServiceImpl
        extends AbstractProjectService<Project>
        implements ProjectService<Project> {

    public ProjectServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public ProjectServiceImpl( @Named("ioStrategy") IOService ioService,
                               POMService pomService,
                               ProjectConfigurationContentHandler projectConfigurationContentHandler,
                               ConfigurationService configurationService,
                               ConfigurationFactory configurationFactory,
                               Event<NewProjectEvent> newProjectEvent,
                               Event<NewPackageEvent> newPackageEvent,
                               Event<RenameProjectEvent> renameProjectEvent,
                               Event<DeleteProjectEvent> deleteProjectEvent,
                               Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache,
                               User identity,
                               SessionInfo sessionInfo ) {
        super( ioService, pomService, projectConfigurationContentHandler, configurationService,
               configurationFactory, newProjectEvent, newPackageEvent, renameProjectEvent, deleteProjectEvent,
               invalidateDMOCache, identity, sessionInfo );
    }

    @Override
    public Project resolveProject( final Path resource ) {
        try {
            //Null resource paths cannot resolve to a Project
            if ( resource == null ) {
                return null;
            }

            //Check if resource is the project root
            org.uberfire.java.nio.file.Path path = Paths.convert( resource ).normalize();

            //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
            if ( Files.isRegularFile( path ) ) {
                path = path.getParent();
            }
            if ( hasPom( path ) ) {
                return makeProject( path );
            }
            while ( path.getNameCount() > 0 && !path.getFileName().toString().equals( SOURCE_FILENAME ) ) {
                path = path.getParent();
            }
            if ( path.getNameCount() == 0 ) {
                return null;
            }
            path = path.getParent();
            if ( path.getNameCount() == 0 || path == null ) {
                return null;
            }
            if ( !hasPom( path ) ) {
                return null;
            }
            return makeProject( path );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Project newProject( final org.guvnor.structure.repositories.Repository repository,
                               final POM pom,
                               final String baseUrl ) {
        final FileSystem fs = Paths.convert( repository.getRoot() ).getFileSystem();
        try {
            //Projects are always created in the FS root
            final Path fsRoot = repository.getRoot();
            final Path projectRootPath = Paths.convert( Paths.convert( fsRoot ).resolve( pom.getName() ) );

            ioService.startBatch( new FileSystem[]{fs}, makeCommentedOption( "New project [" + pom.getName() + "]" ) );

            //Create POM.xml
            pomService.create( projectRootPath,
                               baseUrl,
                               pom );

            //Raise an event for the new project
            final Project project = resolveProject( projectRootPath );
            newProjectEvent.fire( new NewProjectEvent( project, getSessionId(), getIdentityName() ) );

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
            final Package defaultWorkspacePackage = doNewPackage( defaultPackage,
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
    public Project simpleProjectInstance( final org.uberfire.java.nio.file.Path nioProjectRootPath ) {
        final Path projectRootPath = Paths.convert( nioProjectRootPath );

        return new Project( projectRootPath,
                            Paths.convert( nioProjectRootPath.resolve( POM_PATH ) ),
                            projectRootPath.getFileName() );

    }

  @Override
  public Project resolveParentProject(Path resource) {
    return null;
  }

  @Override
  public Project resolveToParentProject(Path resource) {
    return null;
  }
}