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

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.kie.guvnor.project.service.ProjectService;
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
    private IOService ioService;
    private Paths paths;

    @Inject
    GroupArtifactVersionModelContentHandler groupArtifactVersionModelContentHandler;

    public ProjectServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public ProjectServiceImpl( final @Named("ioStrategy") IOService ioService,
                               final Paths paths ) {
        this.ioService = ioService;
        this.paths = paths;
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig( final Path project ) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public GroupArtifactVersionModel loadGav( final Path path ) {
        try {
            org.kie.commons.java.nio.file.Path convert = paths.convert( path );
            String propertiesString = ioService.readAllString( convert );
            return groupArtifactVersionModelContentHandler.toModel( propertiesString );
        } catch ( IOException e ) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        }
        return null;

    }

    @Override
    public Path resolveProject( final Path resource ) {

        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //Check if resource is the project root
        org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        if ( hasPom( path ) && hasKModule( path ) ) {
            return resource;
        }

        //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
        if ( Files.isRegularFile( path ) ) {
            path = path.getParent();
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
        return PathFactory.newPath( paths.convert( path.getFileSystem() ),
                                    path.getFileName().toString(),
                                    path.toUri().toString() );
    }

    @Override
    public Path resolvePackage( final Path resource ) {

        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //A resource is already a folder simply return it
        org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        if ( Files.isDirectory( path ) ) {
            return resource;
        }

        path = path.getParent();

        return PathFactory.newPath( paths.convert( path.getFileSystem() ),
                                    path.getFileName().toString(),
                                    path.toUri().toString() );
    }

    private boolean hasPom( final org.kie.commons.java.nio.file.Path path ) {
        final org.kie.commons.java.nio.file.Path pomPath = path.resolve( POM_FILENAME );
        return Files.exists( pomPath );
    }

    private boolean hasKModule( final org.kie.commons.java.nio.file.Path path ) {
        final org.kie.commons.java.nio.file.Path kmodulePath = path.resolve( KMODULE_FILENAME );
        return Files.exists( kmodulePath );
    }

}
