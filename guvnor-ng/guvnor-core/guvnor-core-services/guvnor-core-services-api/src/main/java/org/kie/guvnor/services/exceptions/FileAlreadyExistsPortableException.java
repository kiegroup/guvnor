package org.kie.guvnor.services.exceptions;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable Exception for when a File already exists
 */
@Portable
public class FileAlreadyExistsPortableException extends GenericPortableException {

    //Cannot use org.uberfire.backend.vfs.Path as it is not Serializable
    private String path;

    public FileAlreadyExistsPortableException() {
    }

    public FileAlreadyExistsPortableException( final String path ) {
        this.path = path;
    }

}
