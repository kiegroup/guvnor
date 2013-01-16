package org.kie.guvnor.datamodel.backend.server;

import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.builder.DefaultBuilderFilter;

/**
 * A filter to ensure only Model related resources are included
 */
public class ModelBuilderFilter extends DefaultBuilderFilter {

    private static final String PATTERN = ".model.drl";

    @Override
    public boolean accept( final Path path ) {
        boolean accept = super.accept( path );
        if ( !accept ) {
            return accept;
        }

        final String uri = path.toUri().toString();
        return uri.substring( uri.length() - PATTERN.length() ).equals( PATTERN );
    }
}
