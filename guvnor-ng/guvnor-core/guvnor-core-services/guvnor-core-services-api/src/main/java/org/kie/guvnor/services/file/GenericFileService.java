package org.kie.guvnor.services.file;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface GenericFileService {

    void delete(final Path path,
                final String comment);

    Path rename(final Path path,
                final String newName,
                final String comment);

    Path copy(final Path path,
              final String newName,
              final String comment);
}
