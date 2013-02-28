package org.kie.guvnor.services.file;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;

@Remote
public interface RenameService {

    Path rename( final Path path,
                 final String newName,
                 final String comment );

}
