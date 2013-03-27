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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.models.commons.shared.imports.Import;
import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.kie.api.KieServices;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.builder.model.BuildMessage;
import org.kie.guvnor.commons.service.builder.model.BuildResults;
import org.kie.guvnor.commons.service.builder.model.IncrementalBuildResults;
import org.kie.guvnor.services.backend.file.DotFileFilter;
import org.uberfire.backend.server.util.Paths;

public class Builder {

    private final static String RESOURCE_PATH = "src/main/resources";

    private final static String ERROR_CLASS_NOT_FOUND = "Class not found";

    private KieBuilder kieBuilder;
    private final KieServices kieServices;
    private final KieFileSystem kieFileSystem;
    private final Path moduleDirectory;
    private final Paths paths;
    private final String artifactId;
    private final IOService ioService;
    private final ProjectService projectService;
    private final DirectoryStream.Filter<Path> filter;

    private final String projectPrefix;

    private Map<String, Path> handles = new HashMap<String, Path>();

    public Builder( final Path moduleDirectory,
                    final String artifactId,
                    final Paths paths,
                    final IOService ioService,
                    final ProjectService projectService ) {
        this( moduleDirectory,
              artifactId,
              paths,
              ioService,
              projectService,
              new DotFileFilter() );
    }

    public Builder( final Path moduleDirectory,
                    final String artifactId,
                    final Paths paths,
                    final IOService ioService,
                    final ProjectService projectService,
                    final DirectoryStream.Filter<Path> filter ) {
        this.moduleDirectory = moduleDirectory;
        this.artifactId = artifactId;
        this.paths = paths;
        this.ioService = ioService;
        this.projectService = projectService;
        this.filter = filter;

        projectPrefix = moduleDirectory.toUri().toString();
        kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();

        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream( moduleDirectory );
        visitPaths( directoryStream );
    }

    public BuildResults build() {
        //KieBuilder is not re-usable for successive "full" builds
        kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        final Results kieResults = kieBuilder.buildAll().getResults();
        final BuildResults results = convertMessages( kieResults );

        //Check external imports are available. These are loaded when a DMO is requested, but it's better to report them early
        final org.kie.commons.java.nio.file.Path nioExternalImportsPath = moduleDirectory.resolve( "project.imports" );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final org.uberfire.backend.vfs.Path externalImportsPath = paths.convert( nioExternalImportsPath );
            final PackageConfiguration packageConfiguration = projectService.load( externalImportsPath );
            final Imports imports = packageConfiguration.getImports();
            for ( final Import item : imports.getImports() ) {
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                } catch ( ClassNotFoundException cnfe ) {
                    results.addBuildMessage( makeMessage( ERROR_CLASS_NOT_FOUND,
                                                          cnfe ) );
                }
            }
        }

        return results;
    }

    public IncrementalBuildResults addResource( final Path resource ) {
        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }
        //Add new resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        final InputStream is = ioService.newInputStream( resource );
        final BufferedInputStream bis = new BufferedInputStream( is );
        kieFileSystem.write( destinationPath,
                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
        handles.put( destinationPath,
                     resource );

        //Incremental build
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();

        //Messages from incremental build
        final IncrementalBuildResults results = convertMessages( incrementalResults );

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        return results;
    }

    public IncrementalBuildResults deleteResource( final Path resource ) {
        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }
        //Delete resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        kieFileSystem.delete( destinationPath );

        //Incremental build
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();

        //Messages from incremental build
        final IncrementalBuildResults results = convertMessages( incrementalResults );

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        return results;
    }

    public IncrementalBuildResults updateResource( final Path resource ) {
        return addResource( resource );
    }

    public KieModule getKieModule() {
        return kieBuilder.getKieModule();
    }

    public boolean isBuilt() {
        return kieBuilder != null;
    }

    private void visitPaths( final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream ) {
        for ( final org.kie.commons.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                visitPaths( Files.newDirectoryStream( path ) );

            } else if ( filter.accept( path ) ) {
                final String destinationPath = path.toUri().toString().substring( projectPrefix.length() + 1 );
                final InputStream is = ioService.newInputStream( path );
                final BufferedInputStream bis = new BufferedInputStream( is );
                kieFileSystem.write( destinationPath,
                                     KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                handles.put( destinationPath,
                             path );
            }
        }
    }

    private BuildResults convertMessages( final Results kieBuildResults ) {
        final BuildResults results = new BuildResults();
        results.setArtifactID( artifactId );

        for ( final Message message : kieBuildResults.getMessages() ) {
            results.addBuildMessage( convertMessage( message ) );
        }

        return results;
    }

    private IncrementalBuildResults convertMessages( final IncrementalResults kieIncrementalResults ) {
        final IncrementalBuildResults results = new IncrementalBuildResults();
        results.setArtifactID( artifactId );

        for ( final Message message : kieIncrementalResults.getAddedMessages() ) {
            results.addAddedMessage( convertMessage( message ) );
        }
        for ( final Message message : kieIncrementalResults.getRemovedMessages() ) {
            results.addRemovedMessage( convertMessage( message ) );
        }

        return results;
    }

    private BuildMessage convertMessage( final Message message ) {
        final BuildMessage m = new BuildMessage();
        switch ( message.getLevel() ) {
            case ERROR:
                m.setLevel( BuildMessage.Level.ERROR );
                break;
            case WARNING:
                m.setLevel( BuildMessage.Level.WARNING );
                break;
            case INFO:
                m.setLevel( BuildMessage.Level.INFO );
                break;
        }

        m.setId( message.getId() );
        m.setArtifactID( artifactId );
        m.setLine( message.getLine() );
        if ( message.getPath() != null && !message.getPath().isEmpty() ) {
            m.setPath( paths.convert( handles.get( RESOURCE_PATH + "/" + message.getPath() ) ) );
        }
        m.setColumn( message.getColumn() );
        m.setText( message.getText() );
        return m;
    }

    private BuildMessage makeMessage( final String prefix,
                                      final Exception e ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( BuildMessage.Level.ERROR );
        buildMessage.setText( prefix + ": " + e.getMessage() );
        return buildMessage;
    }

}
