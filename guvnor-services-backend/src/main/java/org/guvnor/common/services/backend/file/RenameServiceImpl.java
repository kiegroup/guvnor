package org.guvnor.common.services.backend.file;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.file.RenameService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

@Service
public class RenameServiceImpl implements RenameService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        try {
            System.out.println( "USER:" + identity.getName() + " RENAMING asset [" + path.getFileName() + "] to [" + newName + "]" );

            final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

            String originalFileName = _path.getFileName().toString();
            final String extension = originalFileName.substring( originalFileName.indexOf( "." ) );
            final org.uberfire.java.nio.file.Path _target = _path.resolveSibling( newName + extension );

            ioService.move( _path,
                            _target,
                            new CommentedOption( sessionInfo.getId(), identity.getName(), null, comment ) );

            return Paths.convert( _target );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
