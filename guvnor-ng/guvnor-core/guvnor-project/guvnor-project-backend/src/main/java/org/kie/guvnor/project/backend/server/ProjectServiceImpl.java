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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.model.Repository;
import org.kie.guvnor.project.service.KModuleService;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

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
    private PackageConfigurationContentHandler packageConfigurationContentHandler;

    public ProjectServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public ProjectServiceImpl( final M2RepoService m2RepoService,
                               final @Named("ioStrategy") IOService ioService,
                               final Paths paths,
                               final KModuleService kModuleService,
                               final POMService pomService,
                               final PackageConfigurationContentHandler packageConfigurationContentHandler ) {
        this.m2RepoService = m2RepoService;
        this.ioService = ioService;
        this.paths = paths;
        this.kModuleService = kModuleService;
        this.pomService = pomService;
        this.packageConfigurationContentHandler = packageConfigurationContentHandler;
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
                            final String name ) {
        POM pomModel = new POM();
        Repository repository = new Repository();
        repository.setId( "guvnor-m2-repo" );
        repository.setName( "Guvnor M2 Repo" );
        repository.setUrl( m2RepoService.getRepositoryURL() );
        pomModel.addRepository( repository );

        Path pathToPom = createPOMFile( activePath, name );
        kModuleService.setUpKModuleStructure( pathToPom );

        saveImportSuggestions( paths.convert( pathToPom ).getParent() );

        return pomService.savePOM( pathToPom, pomModel );
    }

    @Override
    public Path newPackage( final Path path ) {
        return paths.convert( ioService.createDirectory( paths.convert( path ) ) );
    }

    private void saveImportSuggestions( final org.kie.commons.java.nio.file.Path folderPath ) {
        Path path = paths.convert( folderPath );
        org.kie.commons.java.nio.file.Path pathToFile = ioService.createFile( paths.convert( PathFactory.newPath( path.getFileSystem(), "project.imports", path.toURI() + "/project.imports" ) ) );
        ioService.write( pathToFile, packageConfigurationContentHandler.toString( new PackageConfiguration( new Imports() ) ) );
    }

    private Path createPOMFile( final Path activePath,
                                final String name ) {
        return paths.convert( ioService.createFile( paths.convert( createPOMPath( activePath, name ) ) ) );
    }

    private Path createPOMPath( final Path activePath,
                                final String name ) {
        return PathFactory.newPath( activePath.getFileSystem(), "pom.xml", activePath.toURI() + "/" + name + "/pom.xml" );
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
    public PackageConfiguration loadPackageConfiguration( final Path path ) {
        return packageConfigurationContentHandler.toModel( ioService.readAllString( paths.convert( path ) ) );
    }

    @Override
    public void save( final Path path,
                      final PackageConfiguration packageConfiguration ) {
        ioService.write( paths.convert( path ), packageConfigurationContentHandler.toString( packageConfiguration ) );
    }
}
