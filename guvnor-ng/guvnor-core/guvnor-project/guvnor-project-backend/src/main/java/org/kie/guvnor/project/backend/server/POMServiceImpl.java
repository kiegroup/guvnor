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
import org.kie.guvnor.commons.service.source.SourceServices;
import org.kie.guvnor.commons.service.source.ViewSourceService;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class POMServiceImpl
        implements POMService,
        ViewSourceService<POM>{

    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;
    private IOService ioService;
    private Paths paths;
    private POMContentHandler pomContentHandler;
    private MetadataService metadataService;
    private SourceServices sourceServices;

    @Inject
    private Identity identity;

    public POMServiceImpl() {
        // For Weld
    }

    @Inject
    public POMServiceImpl( Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache,
                           @Named("ioStrategy") IOService ioService,
                           MetadataService metadataService,
                           SourceServices sourceServices,
                           Paths paths,
                           POMContentHandler pomContentHandler ) {
        this.invalidateDMOProjectCache = invalidateDMOProjectCache;
        this.ioService = ioService;
        this.metadataService = metadataService;
        this.sourceServices = sourceServices;
        this.paths = paths;
        this.pomContentHandler = pomContentHandler;
    }

    @Override
    public POM loadPOM( final Path path ) {
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
    public Path savePOM( final String commitMessage,
                         final Path pathToPOM,
                         final POM pomModel,
                         Metadata metadata ) {
        try {
            Path result;
            if ( metadata == null ) {
                result = paths.convert(
                        ioService.write(
                                paths.convert( pathToPOM ),
                                pomContentHandler.toString( pomModel ),
                                makeCommentedOption( commitMessage ) ) );
            } else {
                result = paths.convert(
                        ioService.write(
                                paths.convert( pathToPOM ),
                                pomContentHandler.toString( pomModel ),
                                metadataService.setUpAttributes( pathToPOM, metadata ),
                                makeCommentedOption( commitMessage ) ) );
            }

            invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( result ) );

            return result;

        } catch ( IOException e ) {
            e.printStackTrace();  //TODO Notify this in the Problems screen -Rikkola-
        }
        return null;
    }

    @Override
    public Path savePOM( final Path pathToPOM,
                         final POM pomModel ) {
        try {

            Path result = paths.convert(
                    ioService.write(
                            paths.convert( pathToPOM ),
                            pomContentHandler.toString( pomModel ) ) );

            invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( result ) );

            return result;

        } catch ( IOException e ) {
            e.printStackTrace();  //TODO Notify this in the Problems screen -Rikkola-
        }
        return null;
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
    public String toSource(Path path, POM model) {

        return sourceServices.getServiceFor(paths.convert(path)).getSource(paths.convert(path),model);
    }
}
