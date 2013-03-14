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

package org.kie.guvnor.project.backend.server;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.InvalidPathException;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.model.Repository;
import org.kie.guvnor.project.service.KModuleService;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.exceptions.FileAlreadyExistsPortableException;
import org.kie.guvnor.services.exceptions.GenericPortableException;
import org.kie.guvnor.services.exceptions.InvalidPathPortableException;
import org.kie.guvnor.services.exceptions.NoSuchFilePortableException;
import org.kie.guvnor.services.exceptions.SecurityPortableException;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class ProjectServiceImpl
        implements ProjectService {

    private static final String SOURCE_FILENAME = "src";
    private static final String POM_FILENAME = "pom.xml";
    private static final String KMODULE_FILENAME = "src/main/resources/META-INF/kmodule.xml";

    private static final String SOURCE_JAVA_PATH = "src/main/java";
    private static final String SOURCE_RESOURCES_PATH = "src/main/resources";
    private static final String TEST_JAVA_PATH = "src/test/java";
    private static final String TEST_RESOURCES_PATH = "src/test/resources";

    private IOService ioService;
    private Paths paths;

    private POMService pomService;
    private M2RepoService m2RepoService;
    private KModuleService kModuleService;
    private MetadataService metadataService;
    private PackageConfigurationContentHandler packageConfigurationContentHandler;

    private Event<ResourceAddedEvent> resourceAddedEvent;
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    private Identity identity;

    public ProjectServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public ProjectServiceImpl( final @Named("ioStrategy") IOService ioService,
                               final Paths paths,
                               final M2RepoService m2RepoService,
                               final POMService pomService,
                               final KModuleService kModuleService,
                               final MetadataService metadataService,
                               final PackageConfigurationContentHandler packageConfigurationContentHandler,
                               final Event<ResourceAddedEvent> resourceAddedEvent,
                               final Event<ResourceUpdatedEvent> resourceUpdatedEvent,
                               final Identity identity ) {
        this.ioService = ioService;
        this.paths = paths;
        this.m2RepoService = m2RepoService;
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.metadataService = metadataService;
        this.packageConfigurationContentHandler = packageConfigurationContentHandler;
        this.resourceAddedEvent = resourceAddedEvent;
        this.resourceUpdatedEvent = resourceUpdatedEvent;
        this.identity = identity;
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig( final Path project ) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public Path resolveProject( final Path resource ) {

        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //Check if resource is the project root
        org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();

        //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
        if ( Files.isRegularFile( path ) ) {
            path = path.getParent();
        }
        if ( hasPom( path ) && hasKModule( path ) ) {
            return resource;
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
        if ( !hasKModule( path ) ) {
            return null;
        }
        return paths.convert( path );
    }

    @Override
    public Path resolvePackage( final Path resource ) {

        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //If Path is not within a Project we cannot resolve a package
        final Path projectRoot = resolveProject( resource );
        if ( projectRoot == null ) {
            return null;
        }

        //The Path must be within a Project's src/main/resources or src/test/resources path
        boolean resolved = false;
        org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        final org.kie.commons.java.nio.file.Path srcJavaPath = paths.convert( projectRoot ).resolve( SOURCE_JAVA_PATH );
        final org.kie.commons.java.nio.file.Path srcResourcesPath = paths.convert( projectRoot ).resolve( SOURCE_RESOURCES_PATH );
        final org.kie.commons.java.nio.file.Path testJavaPath = paths.convert( projectRoot ).resolve( TEST_JAVA_PATH );
        final org.kie.commons.java.nio.file.Path testResourcesPath = paths.convert( projectRoot ).resolve( TEST_RESOURCES_PATH );
        if ( path.startsWith( srcJavaPath ) ) {
            resolved = true;
        } else if ( path.startsWith( srcResourcesPath ) ) {
            resolved = true;
        } else if ( path.startsWith( testJavaPath ) ) {
            resolved = true;
        } else if ( path.startsWith( testResourcesPath ) ) {
            resolved = true;
        }
        if ( !resolved ) {
            return null;
        }

        //If the Path is already a folder simply return it
        if ( Files.isDirectory( path ) ) {
            return resource;
        }

        path = path.getParent();

        return paths.convert( path );
    }

    @Override
    public String resolvePackageName( final Path path ) {

        //Check path is actually within a Package within a Project
        final Path packagePath = resolvePackage( path );
        if ( packagePath == null ) {
            return null;
        }
        final Path projectPath = resolveProject( packagePath );
        if ( projectPath == null ) {
            return null;
        }

        //Use the relative path between Project root and Package path to build the package name
        final org.kie.commons.java.nio.file.Path nioProjectPath = paths.convert( projectPath );
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        final org.kie.commons.java.nio.file.Path nioDelta = nioProjectPath.relativize( nioPackagePath );

        //Build package name
        String packageName = nioDelta.toString();
        if ( packageName.startsWith( SOURCE_JAVA_PATH ) ) {
            packageName = packageName.replace( SOURCE_JAVA_PATH,
                                               "" );
        } else if ( packageName.startsWith( SOURCE_RESOURCES_PATH ) ) {
            packageName = packageName.replace( SOURCE_RESOURCES_PATH,
                                               "" );
        } else if ( packageName.startsWith( TEST_JAVA_PATH ) ) {
            packageName = packageName.replace( TEST_JAVA_PATH,
                                               "" );
        } else if ( packageName.startsWith( TEST_RESOURCES_PATH ) ) {
            packageName = packageName.replace( TEST_RESOURCES_PATH,
                                               "" );
        }
        if ( packageName.length() == 0 ) {
            return "defaultpkg";
        }
        if ( packageName.startsWith( "/" ) ) {
            packageName = packageName.substring( 1 );
        }
        return packageName.replaceAll( "/",
                                       "." );
    }

    @Override
    public Path newProject( final Path activePath,
                            final String projectName ) {

        Path projectRootPath = null;
        try {

            final POM pomModel = new POM();
            final Repository repository = new Repository();
            repository.setId( "guvnor-m2-repo" );
            repository.setName( "Guvnor M2 Repo" );
            repository.setUrl( m2RepoService.getRepositoryURL() );
            pomModel.addRepository( repository );

            //Projects are always created in the FS root
            final Path fsRoot = getFileSystemRoot( activePath );
            projectRootPath = getProjectRootPath( fsRoot,
                                                  projectName );

            //Set-up project structure and KModule.xml
            kModuleService.setUpKModuleStructure( projectRootPath );

            //Create POM.xml
            pomService.create( projectRootPath );

            //Create Project configuration
            final Path projectConfigPath = paths.convert( paths.convert( projectRootPath ).resolve( "project.imports" ),
                                                          false );
            ioService.createFile( paths.convert( projectConfigPath ) );
            ioService.write( paths.convert( projectConfigPath ),
                             packageConfigurationContentHandler.toString( new PackageConfiguration() ) );

            //Signal creation to interested parties
            resourceAddedEvent.fire( new ResourceAddedEvent( projectRootPath ) );

            return paths.convert( paths.convert( projectRootPath ).resolve( "pom.xml" ) );

        } catch ( InvalidPathException e ) {
            throw new InvalidPathPortableException( projectRootPath.toURI() );

        } catch ( SecurityException e ) {
            throw new SecurityPortableException( projectRootPath.toURI() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( FileAlreadyExistsException e ) {
            throw new FileAlreadyExistsPortableException( projectRootPath.toURI() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( UnsupportedOperationException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    private Path getFileSystemRoot( final Path activePath ) {
        return PathFactory.newPath( activePath.getFileSystem(),
                                    "/",
                                    activePath.toURI() );
    }

    private Path getProjectRootPath( final Path fsRoot,
                                     final String projectName ) {
        return paths.convert( paths.convert( fsRoot ).resolve( projectName ),
                              false );
    }

    @Override
    public Path newPackage( final Path contextPath,
                            final String packageName ) {
        return newDirectory( contextPath,
                             packageName );
    }

    @Override
    public Path newDirectory( final Path contextPath,
                              final String dirName ) {
        Path directoryPath = null;
        try {

            directoryPath = paths.convert( ioService.createDirectory( paths.convert( contextPath ).resolve( dirName ) ) );

            //Signal creation to interested parties
            resourceAddedEvent.fire( new ResourceAddedEvent( directoryPath ) );

            return directoryPath;

        } catch ( InvalidPathException e ) {
            throw new InvalidPathPortableException( directoryPath.toURI() );

        } catch ( SecurityException e ) {
            throw new SecurityPortableException( directoryPath.toURI() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( FileAlreadyExistsException e ) {
            throw new FileAlreadyExistsPortableException( directoryPath.toURI() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( UnsupportedOperationException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    private boolean hasPom( final org.kie.commons.java.nio.file.Path path ) {
        final org.kie.commons.java.nio.file.Path pomPath = path.resolve( POM_FILENAME );
        return Files.exists( pomPath );
    }

    private boolean hasKModule( final org.kie.commons.java.nio.file.Path path ) {
        final org.kie.commons.java.nio.file.Path kmodulePath = path.resolve( KMODULE_FILENAME );
        return Files.exists( kmodulePath );
    }

    @Override
    public PackageConfiguration load( final Path path ) {
        try {
            final String content = ioService.readAllString( paths.convert( path ) );
            return packageConfigurationContentHandler.toModel( content );

        } catch ( NoSuchFileException e ) {
            throw new NoSuchFilePortableException( path.toURI() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    @Override
    public Path save( final Path resource,
                      final PackageConfiguration packageConfiguration,
                      final Metadata metadata,
                      final String comment ) {
        try {
            ioService.write( paths.convert( resource ),
                             packageConfigurationContentHandler.toString( packageConfiguration ),
                             metadataService.setUpAttributes( resource,
                                                              metadata ),
                             makeCommentedOption( comment ) );

            //Signal update to interested parties
            resourceUpdatedEvent.fire( new ResourceUpdatedEvent( resource ) );

            return resource;

        } catch ( InvalidPathException e ) {
            throw new InvalidPathPortableException( resource.toURI() );

        } catch ( SecurityException e ) {
            throw new SecurityPortableException( resource.toURI() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( FileAlreadyExistsException e ) {
            throw new FileAlreadyExistsPortableException( resource.toURI() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( UnsupportedOperationException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

}