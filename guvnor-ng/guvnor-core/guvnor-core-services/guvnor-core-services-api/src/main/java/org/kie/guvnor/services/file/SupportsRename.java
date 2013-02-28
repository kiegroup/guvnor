package org.kie.guvnor.services.file;

import org.uberfire.backend.vfs.Path;

public interface SupportsRename {

    Path rename( final Path path,
                 final String newName,
                 final String comment );

}
