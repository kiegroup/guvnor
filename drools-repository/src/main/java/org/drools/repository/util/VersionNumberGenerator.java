package org.drools.repository.util;

import org.drools.repository.VersionableItem;

/**
 * This is a pluggable version label generator, people can override it 
 * if needed.
 * Version labels are really "version numbers". 
 * 
 * This will be consulted when an asset is checked in.
 * 
 */
public interface VersionNumberGenerator {

    /**
     * @param currentVersionNumber The current version number.
     * @param asset The current asset that is being checked in.
     * @return The version label for the thing being checked in.
     */
    public String calculateNextVersion(String currentVersionNumber, VersionableItem asset);
    
}
