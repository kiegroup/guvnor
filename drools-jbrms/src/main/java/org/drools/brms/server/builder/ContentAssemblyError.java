package org.drools.brms.server.builder;

import org.drools.repository.VersionableItem;

/**
 * This class is used to accumulate error reports for asset.
 * This can then be used to feed back to the user where the problems are.
 * 
 * @author Michael Neale
 */
public class ContentAssemblyError {

    public ContentAssemblyError(VersionableItem it, String message) {
        this.itemInError = it;
        this.errorReport = message;
    }
    /**
     * This may be null, if its not associated to any particular asset.
     */
    public VersionableItem itemInError;
    public String errorReport;
    
}
