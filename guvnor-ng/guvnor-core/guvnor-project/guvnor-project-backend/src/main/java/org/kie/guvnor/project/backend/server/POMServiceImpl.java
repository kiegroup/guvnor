package org.kie.guvnor.project.backend.server;

import java.io.IOException;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.InvalidPathException;
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.commons.service.source.ViewSourceService;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.services.exceptions.FileAlreadyExistsPortableException;
import org.kie.guvnor.services.exceptions.GenericPortableException;
import org.kie.guvnor.services.exceptions.InvalidPathPortableException;
import org.kie.guvnor.services.exceptions.SecurityPortableException;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class POMServiceImpl
        implements POMService,
                   ViewSourceService<POM> {

    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;
    private IOService ioService;
    private Paths paths;
    private POMContentHandler pomContentHandler;
    private MetadataService metadataService;
    private SourceServices sourceServices;
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Identity identity;

    public POMServiceImpl() {
        // For Weld
    }

    @Inject
    public POMServiceImpl( final Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache,
                           final @Named("ioStrategy") IOService ioService,
                           final MetadataService metadataService,
                           final SourceServices sourceServices,
                           final Paths paths,
                           final POMContentHandler pomContentHandler,
                           final Event<ResourceUpdatedEvent> resourceUpdatedEvent ) {
        this.invalidateDMOProjectCache = invalidateDMOProjectCache;
        this.ioService = ioService;
        this.metadataService = metadataService;
        this.sourceServices = sourceServices;
        this.paths = paths;
        this.pomContentHandler = pomContentHandler;
        this.resourceUpdatedEvent = resourceUpdatedEvent;
    }

    @Override
    public Path create( final Path projectRoot ) {
        org.kie.commons.java.nio.file.Path pathToPOMXML = null;
        try {
            final org.kie.commons.java.nio.file.Path nioRoot = paths.convert( projectRoot );
            pathToPOMXML = nioRoot.resolve( "pom.xml" );

            ioService.createFile( pathToPOMXML );
            ioService.write( pathToPOMXML,
                             pomContentHandler.toString( new POM() ) );

            //Don't raise a NewResourceAdded event as this is handled at the Project level in ProjectServices

            return paths.convert( pathToPOMXML );

        } catch ( InvalidPathException e ) {
            throw new InvalidPathPortableException( pathToPOMXML.toUri().toString() );

        } catch ( SecurityException e ) {
            throw new SecurityPortableException( pathToPOMXML.toUri().toString() );

        } catch ( IllegalArgumentException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( FileAlreadyExistsException e ) {
            throw new FileAlreadyExistsPortableException( pathToPOMXML.toUri().toString() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( UnsupportedOperationException e ) {
            throw new GenericPortableException( e.getMessage() );

        }
    }

    @Override
    public POM load( final Path path ) {
        try {
            org.kie.commons.java.nio.file.Path convert = paths.convert( path );
            String propertiesString = ioService.readAllString( convert );
            return pomContentHandler.toModel( propertiesString );

        } catch ( IOException e ) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        }
        return null;
    }

    @Override
    public Path save( final Path path,
                      final POM content,
                      final Metadata metadata,
                      final String comment ) {
        try {
            if ( metadata == null ) {
                ioService.write( paths.convert( path ),
                                 pomContentHandler.toString( content ),
                                 makeCommentedOption( comment ) );
            } else {
                ioService.write( paths.convert( path ),
                                 pomContentHandler.toString( content ),
                                 metadataService.setUpAttributes( path,
                                                                  metadata ),
                                 makeCommentedOption( comment ) );
            }

            //Invalidate Project-level DMO cache as POM has changed.
            invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( path ) );

            //Signal update to interested parties
            resourceUpdatedEvent.fire( new ResourceUpdatedEvent( path ) );

        } catch ( IOException e ) {
            e.printStackTrace();  //TODO Notify this in the Problems screen -Rikkola-
        }
        return path;
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
    public String toSource( final Path path,
                            final POM model ) {
        return sourceServices.getServiceFor( paths.convert( path ) ).getSource( paths.convert( path ), model );
    }
}
