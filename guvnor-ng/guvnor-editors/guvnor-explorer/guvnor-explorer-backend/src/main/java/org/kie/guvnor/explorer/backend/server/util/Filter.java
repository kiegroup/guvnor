package org.kie.guvnor.explorer.backend.server.util;

import org.kie.commons.java.nio.file.Path;

/**
 * Filter that can be chained together
 */
public interface Filter {

    /**
     * Should the provided Path be processed
     * @param path The Path to process
     * @return true if the Path should be processed
     */
    boolean accept( final Path path );

    /**
     * Set the next Filter in the chain
     * @param filter
     */
    void setNextFilter( final Filter filter );

}
