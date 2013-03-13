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

package org.kie.guvnor.testscenario.backend.server;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.drools.guvnor.models.testscenarios.shared.SingleScenarioResult;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.InvalidPathException;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.services.exceptions.FileAlreadyExistsPortableException;
import org.kie.guvnor.services.exceptions.GenericPortableException;
import org.kie.guvnor.services.exceptions.InvalidPathPortableException;
import org.kie.guvnor.services.exceptions.SecurityPortableException;
import org.kie.guvnor.services.file.CopyService;
import org.kie.guvnor.services.file.DeleteService;
import org.kie.guvnor.services.file.RenameService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.drools.guvnor.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.kie.guvnor.testscenario.service.ScenarioTestEditorService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceOpenedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class ScenarioTestEditorServiceImpl implements ScenarioTestEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<InvalidateDMOPackageCacheEvent> invalidatePackageDMOEvent;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final Scenario content,
                        final String comment ) {
        Path newPath = null;
        try {
            final org.kie.commons.java.nio.file.Path nioPath = paths.convert( context ).resolve( fileName );
            newPath = paths.convert( nioPath,
                                     false );

            ioService.createFile( nioPath );
            ioService.write( nioPath,
                             ScenarioXMLPersistence.getInstance().marshal( content ),
                             makeCommentedOption( comment ) );

            //Signal creation to interested parties
            resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

            return newPath;

        } catch ( InvalidPathException e ) {
            throw new InvalidPathPortableException( newPath.toURI() );

        } catch ( SecurityException e ) {
            throw new SecurityPortableException( newPath.toURI() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( FileAlreadyExistsException e ) {
            throw new FileAlreadyExistsPortableException( newPath.toURI() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( UnsupportedOperationException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    @Override
    public Scenario load( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );

        //Signal opening to interested parties
        resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

        return ScenarioXMLPersistence.getInstance().unmarshal( content );
    }

    @Override
    public Path save( final Path resource,
                      final Scenario content,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         ScenarioXMLPersistence.getInstance().marshal( content ),
                         metadataService.setUpAttributes( resource,
                                                          metadata ),
                         makeCommentedOption( comment ) );

        //Invalidate Package-level DMO cache as Globals have changed.
        invalidatePackageDMOEvent.fire( new InvalidateDMOPackageCacheEvent( resource ) );

        //Signal update to interested parties
        resourceUpdatedEvent.fire( new ResourceUpdatedEvent( resource ) );

        return resource;
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        deleteService.delete( path,
                              comment );
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        return renameService.rename( path,
                                     newName,
                                     comment );
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        return copyService.copy( path,
                                 newName,
                                 comment );
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
    public SingleScenarioResult runScenario( String packageName,
                                             Scenario scenario ) {
        return null;  //TODO: -Rikkola-
    }
}
