package org.kie.guvnor.services.exceptions;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable NoSuchFileException
 */
@Portable
public class NoSuchFilePortableException extends GenericPortableException {

    //Cannot use org.uberfire.backend.vfs.Path as it is not Serializable
    private String path;

    public NoSuchFilePortableException() {
    }

    public NoSuchFilePortableException( final String path ) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
