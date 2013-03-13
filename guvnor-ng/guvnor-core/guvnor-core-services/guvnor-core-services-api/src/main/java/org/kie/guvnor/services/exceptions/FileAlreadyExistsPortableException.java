package org.kie.guvnor.services.exceptions;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Portable FileAlreadyExistsException
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
