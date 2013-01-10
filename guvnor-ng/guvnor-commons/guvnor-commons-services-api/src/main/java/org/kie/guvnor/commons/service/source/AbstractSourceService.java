package org.kie.guvnor.commons.service.source;

import org.kie.commons.java.nio.file.Path;

/**
 * Base implementation of all SourceServices
 */
public abstract class AbstractSourceService implements SourceService {

    private static final String PREFIX = "/src/main/resources";

    @Override
    public boolean accepts( final Path path ) {
        final String pattern = getPattern();
        final String uri = path.toUri().toString();
        return uri.substring( uri.length() - pattern.length() ).equals( pattern );
    }

    protected String stripProjectPrefix( final Path path ) {
        final String uri = path.toUri().toString();
        final int prefixIndex = uri.indexOf( PREFIX );
        return uri.substring( prefixIndex );
    }

    protected String correctFileName( final String path,
                                      String requiredFileExtension ) {
        if ( !requiredFileExtension.startsWith( "." ) ) {
            requiredFileExtension = "." + requiredFileExtension;
        }
        return path.replace( getPattern(),
                             requiredFileExtension );
    }

}
