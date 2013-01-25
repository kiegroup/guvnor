package org.kie.guvnor.explorer.backend.server.util;

import org.kie.commons.java.nio.file.Path;

/**
 * A Filter to exclude "dot Files" that can be hidden from users
 */
public class DotFileFilter implements Filter {

    private Filter next = null;

    @Override
    public boolean accept( final Path path ) {
        if ( path.getFileName().toString().startsWith( "." ) ) {
            return false;
        }
        if ( next != null ) {
            return next.accept( path );
        }
        return true;
    }

    @Override
    public void setNextFilter( final Filter filter ) {
        this.next = filter;
    }

}
