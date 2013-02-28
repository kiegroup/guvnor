package org.kie.guvnor.services.backend.file;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.services.file.Filter;

/**
 * Default filter that excludes only Meta Data resources
 */
public class DotFileFilter implements Filter {

    @Override
    //Don't process MetaData files
    public boolean accept( final Path path ) {
        final String fileName = path.getFileName().toString();
        return !fileName.startsWith( "." );
    }

}
