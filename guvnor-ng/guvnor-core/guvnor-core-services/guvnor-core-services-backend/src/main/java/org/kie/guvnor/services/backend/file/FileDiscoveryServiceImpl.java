package org.kie.guvnor.services.backend.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.services.file.Filter;
import org.kie.guvnor.services.file.FileDiscoveryService;

/**
 * Default implementation of FileDiscoveryService
 */
@ApplicationScoped
public class FileDiscoveryServiceImpl implements FileDiscoveryService {

    @Override
    public Collection<Path> discoverFiles( final Path pathToSearch,
                                           final Filter filter ) {
        PortablePreconditions.checkNotNull( "pathToSearch",
                                            pathToSearch );
        PortablePreconditions.checkNotNull( "filter",
                                            filter );

        final List<Path> discoveredFiles = new ArrayList<Path>();

        //The pathToSearch could be a file, and of the type we need
        if ( Files.isRegularFile( pathToSearch ) ) {
            if ( filter.accept( pathToSearch ) ) {
                discoveredFiles.add( pathToSearch );
                return discoveredFiles;
            }
        }

        //This check should never match, but it's included as a safe-guard
        if ( !Files.isDirectory( pathToSearch ) ) {
            return discoveredFiles;
        }

        //Path represents a Folder, so check its content
        final DirectoryStream<Path> files = Files.newDirectoryStream( pathToSearch, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                return filter.accept( entry );
            }
        } );
        for ( final Path file : files ) {
            discoveredFiles.add( file );
        }

        return discoveredFiles;
    }
}
