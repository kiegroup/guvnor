package org.guvnor.common.services.backend.file;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.file.RenameService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceRenamedEvent;

@Service
public class RenameServiceImpl implements RenameService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        try {
            System.out.println( "USER:" + identity.getName() + " RENAMING asset [" + path.getFileName() + "] to [" + newName + "]" );

            final org.kie.commons.java.nio.file.Path _path = paths.convert( path );

            String originalFileName = _path.getFileName().toString();
            final String extension = originalFileName.substring( originalFileName.indexOf( "." ) );
            final org.kie.commons.java.nio.file.Path _target = _path.resolveSibling( newName + extension );

            ioService.move( _path,
                            _target,
                            new CommentedOption( identity.getName(), comment ) );

            final Path target = paths.convert( _target );
            resourceRenamedEvent.fire( new ResourceRenamedEvent( path,
                                                                 target,
                                                                 sessionInfo ) );

            return target;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
