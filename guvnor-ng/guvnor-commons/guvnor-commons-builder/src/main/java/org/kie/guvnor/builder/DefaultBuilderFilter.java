package org.kie.guvnor.builder;

import org.kie.commons.java.nio.file.Path;

/**
 * Default filter that excludes only Meta Data resources
 */
public class DefaultBuilderFilter implements BuilderFilter {

    @Override
    //Don't process MetaData files
    public boolean accept( final Path path ) {
        final String fileName = path.getFileName().toString();
        return !fileName.startsWith( "." );
    }

}
