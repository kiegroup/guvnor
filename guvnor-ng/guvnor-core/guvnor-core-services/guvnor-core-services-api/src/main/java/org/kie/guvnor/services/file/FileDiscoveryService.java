package org.kie.guvnor.services.file;

import java.util.Collection;

import org.kie.commons.java.nio.file.Path;

/**
 * Service to discover files in a given Path
 */
public interface FileDiscoveryService {

    /**
     * Discover files
     * @param pathToSearch The root Path to search. Sub-folders are not included.
     * @param filter A filter to restrict the matched files.
     * @return
     */
    Collection<Path> discoverFiles( final Path pathToSearch,
                                    final Filter filter );

}
