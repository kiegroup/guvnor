package org.guvnor.common.services.backend.file;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.file.CopyService;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceCopiedEvent;

@Service
public class CopyServiceImpl implements CopyService {

    private static final Logger LOGGER = LoggerFactory.getLogger( CopyServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Instance<CopyHelper> helpers;

    @Inject
    private Event<ResourceCopiedEvent> resourceCopiedEvent;

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        try {
            LOGGER.info( "User:" + identity.getName() + " copying file [" + path.getFileName() + "] to [" + newName + "]" );

            String originalFileName = path.getFileName().substring( path.getFileName().lastIndexOf( "/" ) + 1 );
            final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
            final String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName + extension;
            final String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName + extension;
            final Path targetPath = PathFactory.newPath( path.getFileSystem(),
                                                         targetName,
                                                         targetURI );

            ioService.startBatch();

            ioService.copy( Paths.convert( path ),
                            Paths.convert( targetPath ),
                            new CommentedOption( sessionInfo.getId(), identity.getName(), null, comment ) );

            //Delegate additional changes required for a copy to applicable Helpers
            for ( CopyHelper helper : helpers ) {
                if ( helper.supports( targetPath ) ) {
                    helper.postProcess( path,
                                        targetPath );
                }
            }

            ioService.endBatch();

            resourceCopiedEvent.fire( new ResourceCopiedEvent( path,
                                                               targetPath,
                                                               sessionInfo ) );

            return targetPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
