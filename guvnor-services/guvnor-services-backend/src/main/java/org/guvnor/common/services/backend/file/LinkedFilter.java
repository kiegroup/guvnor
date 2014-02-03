package org.guvnor.common.services.backend.file;

import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Path;

/**
 * Filter that can be chained together
 */
public interface LinkedFilter extends DirectoryStream.Filter<Path> {

    /**
     * Set the next Filter in the chain
     * @param filter
     */
    void setNextFilter( final LinkedFilter filter );

}
