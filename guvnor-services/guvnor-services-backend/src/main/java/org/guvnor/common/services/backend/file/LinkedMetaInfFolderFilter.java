package org.guvnor.common.services.backend.file;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.file.Path;

/**
 * A Filter to exclude "META-INF" folder from users
 */
public class LinkedMetaInfFolderFilter implements LinkedFilter {

    private LinkedFilter next = null;

    public LinkedMetaInfFolderFilter() {
    }

    /**
     * Constructor that automatically chains the next filter
     * @param filter
     */
    public LinkedMetaInfFolderFilter( final LinkedFilter filter ) {
        setNextFilter( PortablePreconditions.checkNotNull( "filter",
                                                           filter ) );
    }

    @Override
    public boolean accept( final Path path ) {
        if ( path.getFileName().toString().equalsIgnoreCase( "META-INF" ) ) {
            return false;
        }
        if ( next != null ) {
            return next.accept( path );
        }
        return true;
    }

    @Override
    public void setNextFilter( final LinkedFilter filter ) {
        this.next = filter;
    }

}
