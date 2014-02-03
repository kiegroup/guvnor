package org.guvnor.common.services.backend.file;

import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Path;

/**
 * Filter to match Java source files
 */
public class JavaFileFilter implements DirectoryStream.Filter<Path> {

    @Override
    public boolean accept( final Path path ) {
        final String fileName = path.getFileName().toString();
        return fileName.toLowerCase().endsWith( ".java" );
    }

}
