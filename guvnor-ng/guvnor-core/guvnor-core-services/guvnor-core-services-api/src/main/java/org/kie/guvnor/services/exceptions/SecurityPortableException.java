package org.kie.guvnor.services.exceptions;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable SecurityException
 */
@Portable
public class SecurityPortableException extends GenericPortableException {

    //Cannot use org.uberfire.backend.vfs.Path as it is not Serializable
    private String path;

    public SecurityPortableException() {
    }

    public SecurityPortableException( final String path ) {
        this.path = path;
    }

}
