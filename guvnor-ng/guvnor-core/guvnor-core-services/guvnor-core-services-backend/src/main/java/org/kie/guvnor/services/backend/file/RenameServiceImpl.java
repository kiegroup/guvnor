package org.kie.guvnor.services.backend.file;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.AtomicMoveNotSupportedException;
import org.kie.commons.java.nio.file.DirectoryNotEmptyException;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.guvnor.services.exceptions.FileAlreadyExistsPortableException;
import org.kie.guvnor.services.exceptions.GenericPortableException;
import org.kie.guvnor.services.file.RenameService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.workbench.widgets.events.ResourceRenamedEvent;
import org.uberfire.security.Identity;

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
    private Event<ResourceRenamedEvent> resourceRenamedEvent;

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        System.out.println( "USER:" + identity.getName() + " RENAMING asset [" + path.getFileName() + "] to [" + newName + "]" );

        String originalFileName = path.getFileName().substring(path.getFileName().lastIndexOf( "/" )+1);
        final String extension = originalFileName.substring( originalFileName.indexOf("."));
        final String targetName = path.getFileName().substring( 0, path.getFileName().lastIndexOf( "/" ) + 1 ) + newName + extension;
        final String targetURI = path.toURI().substring( 0, path.toURI().lastIndexOf( "/" ) + 1 ) + newName + extension;
        final Path targetPath = PathFactory.newPath( path.getFileSystem(),
                                                     targetName,
                                                     targetURI );

        try {
            ioService.move( paths.convert( path ),
                            paths.convert( targetPath ),
                            new CommentedOption( identity.getName(), comment ) );

            resourceRenamedEvent.fire( new ResourceRenamedEvent( path,
                                                                 targetPath ) );

        } catch ( AtomicMoveNotSupportedException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( DirectoryNotEmptyException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( SecurityException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( FileAlreadyExistsException e ) {
            throw new FileAlreadyExistsPortableException( targetPath.toURI() );

        } catch ( IOException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( UnsupportedOperationException e ) {
            throw new GenericPortableException( e.getMessage() );

        } catch ( Exception e ) {
            throw new GenericPortableException( e.getMessage() );

        } finally {
            return targetPath;
        }
    }
}
