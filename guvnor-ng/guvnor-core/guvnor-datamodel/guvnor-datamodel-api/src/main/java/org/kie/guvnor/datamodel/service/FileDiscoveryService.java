package org.kie.guvnor.datamodel.service;

import java.util.Collection;

import org.kie.commons.java.nio.file.Path;

/**
 * Service to discover files in a given Path
 */
public interface FileDiscoveryService {

    /**
     * Discover files
     * @param pathToSearch The root Path to search. Sub-folders are not included.
     * @param fileExtension The file extension for which to search
     * @return
     */
    Collection<Path> discoverFiles( final Path pathToSearch,
                                    final String fileExtension );

}
