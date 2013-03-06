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

package org.kie.guvnor.projecteditor.backend.server;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.commons.service.source.ViewSourceService;
import org.kie.guvnor.project.backend.server.KModuleContentHandler;
import org.kie.guvnor.project.model.KModuleModel;
import org.kie.guvnor.project.service.KModuleService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class KModuleServiceImpl
        implements KModuleService,
                   ViewSourceService<KModuleModel> {

    private IOService ioService;
    private MetadataService metadataService;
    private SourceServices sourceServices;
    private Paths paths;
    private KModuleContentHandler moduleContentHandler;
    private Identity identity;
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    public KModuleServiceImpl() {
        // Weld needs this for proxying.
    }

    @Inject
    public KModuleServiceImpl( final @Named("ioStrategy") IOService ioService,
                               final MetadataService metadataService,
                               final SourceServices sourceServices,
                               final Paths paths,
                               final KModuleContentHandler moduleContentHandler,
                               final Identity identity,
                               final Event<ResourceUpdatedEvent> resourceUpdatedEvent ) {
        this.ioService = ioService;
        this.metadataService = metadataService;
        this.sourceServices = sourceServices;
        this.paths = paths;
        this.moduleContentHandler = moduleContentHandler;
        this.identity = identity;
        this.resourceUpdatedEvent = resourceUpdatedEvent;
    }

    @Override
    public Path setUpKModuleStructure( final Path projectRoot ) {
        try {
            // Create project structure
            final org.kie.commons.java.nio.file.Path nioRoot = paths.convert( projectRoot );

            ioService.createDirectory( nioRoot.resolve( "src/main/java" ) );
            ioService.createDirectory( nioRoot.resolve( "src/main/resources" ) );
            ioService.createDirectory( nioRoot.resolve( "src/test/java" ) );
            ioService.createDirectory( nioRoot.resolve( "src/test/resources" ) );

            final org.kie.commons.java.nio.file.Path pathToKModuleXML = nioRoot.resolve( "src/main/resources/META-INF/kmodule.xml" );
            ioService.write( pathToKModuleXML,
                             moduleContentHandler.toString( new KModuleModel() ) );

            //Don't raise a NewResourceAdded event as this is handled at the Project level in ProjectServices

            return paths.convert( pathToKModuleXML );

        } catch ( Exception e ) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        }
        return null;
    }

    @Override
    public KModuleModel load( final Path path ) {
        return moduleContentHandler.toModel( ioService.readAllString( paths.convert( path ) ) );
    }

    @Override
    public Path save( final Path path,
                      final KModuleModel content,
                      final Metadata metadata,
                      final String comment ) {
        if ( metadata == null ) {
            ioService.write(
                    paths.convert( path ),
                    moduleContentHandler.toString( content ),
                    makeCommentedOption( comment ) );
        } else {
            ioService.write(
                    paths.convert( path ),
                    moduleContentHandler.toString( content ),
                    metadataService.setUpAttributes( path,
                                                     metadata ),
                    makeCommentedOption( comment ) );
        }
        //Signal update to interested parties
        resourceUpdatedEvent.fire( new ResourceUpdatedEvent( path ) );

        return path;
    }

    @Override
    public Path pathToRelatedKModuleFileIfAny( final Path pathToPomXML ) {
        final org.kie.commons.java.nio.file.Path directory = paths.convert( pathToPomXML ).getParent();
        final org.kie.commons.java.nio.file.Path pathToKModuleXML = directory.resolve( "src/main/resources/META-INF/kmodule.xml" );
        if ( ioService.exists( pathToKModuleXML ) ) {
            return paths.convert( pathToKModuleXML );
        } else {
            return null;
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

    @Override
    public String toSource( Path path,
                            KModuleModel model ) {
        return sourceServices.getServiceFor( paths.convert( path ) ).getSource( paths.convert( path ), model );
    }
}