/*
 * Copyright 2013 JBoss Inc
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
package org.guvnor.common.services.backend.validation;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

import org.guvnor.common.services.backend.file.KModuleFileFilter;
import org.guvnor.common.services.backend.file.PomFileFilter;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.validation.GenericValidator;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Validator capable of validating generic Kie assets (i.e those that are handled by KieBuilder)
 */
public class DefaultGenericKieValidator implements GenericValidator {

    private final static String RESOURCE_PATH = "src/main/resources";

    @Inject
    private Paths paths;

    @Inject
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    private final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> pomFileFilter = new PomFileFilter();
    private final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> kmoduleFileFilter = new KModuleFileFilter();

    public DefaultGenericKieValidator() {
    }

    public List<ValidationMessage> validate( final Path resourcePath,
                                             final InputStream resource,
                                             final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path>... supportingFileFilters ) {

        final Project project = projectService.resolveProject( resourcePath );
        if ( project == null ) {
            return Collections.emptyList();
        }

        final KieServices kieServices = KieServices.Factory.get();
        final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        final String projectPrefix = project.getRootPath().toURI().toString();

        //Add Java Model files
        final org.kie.commons.java.nio.file.Path nioProjectRoot = paths.convert( project.getRootPath() );
        final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream( nioProjectRoot );
        visitPaths( projectPrefix,
                    kieFileSystem,
                    directoryStream,
                    supportingFileFilters );

        //Add resource to be validated
        final String destinationPath = resourcePath.toURI().toString().substring( projectPrefix.length() + 1 );
        final BufferedInputStream bis = new BufferedInputStream( resource );

        kieFileSystem.write( destinationPath,
                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );

        //Validate
        final KieBuilder kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        final Results kieResults = kieBuilder.buildAll().getResults();
        final List<ValidationMessage> results = convertMessages( kieResults );

        return results;
    }

    private void visitPaths( final String projectPrefix,
                             final KieFileSystem kieFileSystem,
                             final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream,
                             final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path>... supportingFileFilters ) {
        for ( final org.kie.commons.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                visitPaths( projectPrefix,
                            kieFileSystem,
                            Files.newDirectoryStream( path ) );

            } else {
                if ( acceptPath( path,
                                 supportingFileFilters ) ) {
                    final String destinationPath = path.toUri().toString().substring( projectPrefix.length() + 1 );
                    final InputStream is = ioService.newInputStream( path );
                    final BufferedInputStream bis = new BufferedInputStream( is );

                    kieFileSystem.write( destinationPath,
                                         KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                }
            }
        }
    }

    private boolean acceptPath( final org.kie.commons.java.nio.file.Path path,
                                final DirectoryStream.Filter<org.kie.commons.java.nio.file.Path>... supportingFileFilters ) {
        if ( pomFileFilter.accept( path ) ) {
            return true;
        } else if ( kmoduleFileFilter.accept( path ) ) {
            return true;
        }
        for ( DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> filter : supportingFileFilters ) {
            if ( filter.accept( path ) ) {
                return true;
            }
        }
        return false;
    }

    private List<ValidationMessage> convertMessages( final Results kieBuildResults ) {
        final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        for ( final Message message : kieBuildResults.getMessages() ) {
            validationMessages.add( convertMessage( message ) );
        }

        return validationMessages;
    }

    private ValidationMessage convertMessage( final Message message ) {
        final ValidationMessage msg = new ValidationMessage();
        switch ( message.getLevel() ) {
            case ERROR:
                msg.setLevel( ValidationMessage.Level.ERROR );
                break;
            case WARNING:
                msg.setLevel( ValidationMessage.Level.WARNING );
                break;
            case INFO:
                msg.setLevel( ValidationMessage.Level.INFO );
                break;
        }

        msg.setId( message.getId() );
        msg.setLine( message.getLine() );
        msg.setColumn( message.getColumn() );
        msg.setText( message.getText() );
        return msg;
    }

}
