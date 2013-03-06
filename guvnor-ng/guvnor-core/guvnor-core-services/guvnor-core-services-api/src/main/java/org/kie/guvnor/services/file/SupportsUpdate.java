package org.kie.guvnor.services.file;

import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;

public interface SupportsUpdate<T> {

    Path save( final Path path,
               final T content,
               final Metadata metadata,
               final String comment );

}
