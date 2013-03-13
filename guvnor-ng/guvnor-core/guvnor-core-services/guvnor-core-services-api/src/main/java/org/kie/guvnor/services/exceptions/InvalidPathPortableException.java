package org.kie.guvnor.services.exceptions;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable Exception for invalid paths
 */
@Portable
public class InvalidPathPortableException extends GenericPortableException {

    //Cannot use org.uberfire.backend.vfs.Path as it is not Serializable
    private String path;

    public InvalidPathPortableException() {
    }

    public InvalidPathPortableException( final String path ) {
        this.path = path;
    }

}
