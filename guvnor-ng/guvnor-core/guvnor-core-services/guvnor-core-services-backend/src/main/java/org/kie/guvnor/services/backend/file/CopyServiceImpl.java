package org.kie.guvnor.services.backend.file;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.guvnor.services.file.CopyService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.workbench.widgets.events.ResourceCopiedEvent;
import org.uberfire.security.Identity;

@Service
public class CopyServiceImpl implements CopyService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private Event<ResourceCopiedEvent> resourceCopiedEvent;

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        System.out.println( "USER:" + identity.getName() + " COPYING asset [" + path.getFileName() + "] to [" + newName + "]" );
        final String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName;
        final String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName;
        final Path targetPath = PathFactory.newPath( path.getFileSystem(), targetName, targetURI );
        ioService.copy( paths.convert( path ),
                        paths.convert( targetPath ),
                        new CommentedOption( identity.getName(), comment ) );

        resourceCopiedEvent.fire( new ResourceCopiedEvent( path,
                                                           targetPath ) );
        return targetPath;
    }

}
