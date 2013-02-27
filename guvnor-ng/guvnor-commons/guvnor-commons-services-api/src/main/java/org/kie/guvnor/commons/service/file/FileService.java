package org.kie.guvnor.commons.service.file;

import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
public interface FileService<T> {

    Path create( final Path context,
                 final String fileName,
                 final T content,
                 final String comment );

    Path save( final Path path,
               final T content,
               final Metadata metadata,
               final String comment );

    Path save( final Path context,
               final String fileName,
               final T content,
               final String comment );

    void delete( final Path path,
                 final String comment );

    Path rename( final Path path,
                 final String newName,
                 final String comment );

    Path copy( final Path path,
               final String newName,
               final String comment );

}
