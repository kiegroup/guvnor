package org.kie.guvnor.services.file;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface CopyService {

    Path copy( final Path path,
               final String newName,
               final String comment );

}
