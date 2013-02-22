package org.kie.guvnor.datamodel.backend.server;

import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.datamodel.service.FileDiscoveryService;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Default implementation of FileDiscoveryService
 */
@ApplicationScoped
public class FileDiscoveryServiceImpl implements FileDiscoveryService {

    @Override
    public Collection<Path> discoverFiles( final Path pathToSearch,
                                           final String fileExtension ) {
        PortablePreconditions.checkNotNull( "pathToSearch",
                                            pathToSearch );
        PortablePreconditions.checkNotNull( "fileExtension",
                                            fileExtension );

        final List<Path> discoveredFiles = new ArrayList<Path>();

        //The pathToSearch could be a file, and of the type we need
        if ( Files.isRegularFile( pathToSearch ) ) {
            if ( pathToSearch.getFileName().endsWith( fileExtension ) ) {
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
                final String uri = entry.toUri().toString();
                return uri.endsWith( fileExtension );
            }
        } );
        for ( final Path file : files ) {
            discoveredFiles.add( file );
        }

        return discoveredFiles;
    }
}
