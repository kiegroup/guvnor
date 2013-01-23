package org.kie.guvnor.project.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.project.model.POM;
import org.uberfire.backend.vfs.Path;

@Remote
public interface POMService {

    POM loadPOM(final Path path);

    public Path savePOM(final Path pathToPOM,
                        final POM pomModel);
}
