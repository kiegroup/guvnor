package org.guvnor.common.services.backend.file;

import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Path;

/**
 * Default filter that excludes only Meta Data resources
 */
public class DotFileFilter implements DirectoryStream.Filter<Path> {

    @Override
    //Don't process MetaData files
    public boolean accept( final Path path ) {
        final String fileName = path.getFileName().toString();
        return fileName.startsWith( "." );
    }

}
