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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.kie.KieServices;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModule;
import org.kie.builder.Message;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.commons.service.builder.model.Results;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.services.backend.file.DotFileFilter;
import org.kie.guvnor.services.file.Filter;
import org.uberfire.backend.server.util.Paths;

public class Builder {

    private final static String RESOURCE_PATH = "/src/main/resources";

    private final KieBuilder kieBuilder;
    private final KieFileSystem kieFileSystem;
    private final Path moduleDirectory;
    private final Paths paths;
    private final String artifactId;
    private final SourceServices sourceServices;
    private final IOService ioService;
    private final Filter filter;

    private final String projectPrefix;

    private Map<String, Path> handles = new HashMap<String, Path>();

    public Builder( final Path moduleDirectory,
                    final String artifactId,
                    final Paths paths,
                    final SourceServices sourceServices,
                    final IOService ioService ) {
        this( moduleDirectory,
              artifactId,
              paths,
              sourceServices,
              ioService,
              new DotFileFilter() );
    }

    public Builder( final Path moduleDirectory,
                    final String artifactId,
                    final Paths paths,
                    final SourceServices sourceServices,
                    final IOService ioService,
                    final Filter filter ) {
        this.moduleDirectory = moduleDirectory;
        this.artifactId = artifactId;
        this.paths = paths;
        this.sourceServices = sourceServices;
        this.ioService = ioService;
        this.filter = filter;

        projectPrefix = moduleDirectory.toUri().toString();

        KieServices kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();

        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream( moduleDirectory );

        visitPaths( directoryStream );

        kieBuilder = kieServices.newKieBuilder( kieFileSystem );
    }

    public Results build() {
        kieBuilder.buildAll();
        return getResults();
    }

    public KieModule getKieModule() {
        return kieBuilder.getKieModule();
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
            }
        }
    }

    private Results getResults() {
        final Results results = new Results();
        results.setArtifactID( artifactId );

        for ( final Message message : kieBuilder.getResults().getMessages() ) {
            final org.kie.guvnor.commons.service.builder.model.Message m = new org.kie.guvnor.commons.service.builder.model.Message();
            switch ( message.getLevel() ) {
                case ERROR:
                    m.setLevel( org.kie.guvnor.commons.service.builder.model.Message.Level.ERROR );
                    break;
                case WARNING:
                    m.setLevel( org.kie.guvnor.commons.service.builder.model.Message.Level.WARNING );
                    break;
                case INFO:
                    m.setLevel( org.kie.guvnor.commons.service.builder.model.Message.Level.INFO );
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

            results.getMessages().add( m );
        }

        return results;
    }
}
