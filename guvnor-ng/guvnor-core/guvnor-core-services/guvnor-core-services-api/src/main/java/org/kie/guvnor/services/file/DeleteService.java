package org.kie.guvnor.services.file;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface DeleteService {

    void delete( final Path path,
                 final String comment );

}
