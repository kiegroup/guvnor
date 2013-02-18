package org.kie.guvnor.project.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;

@Remote
public interface POMService {

    POM loadPOM(final Path path);

    public Path savePOM(final String commitMessage,
                        final Path pathToPOM,
                        final POM pomModel,
                        final Metadata metadata);

    Path savePOM(Path pathToPom, POM pomModel);
}
