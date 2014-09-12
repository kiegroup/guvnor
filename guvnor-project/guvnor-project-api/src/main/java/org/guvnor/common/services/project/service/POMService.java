package org.guvnor.common.services.project.service;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.shared.file.SupportsRead;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface POMService extends SupportsRead<POM>,
                                    SupportsUpdate<POM> {

    Path create( final Path projectRoot,
                 final String baseURL,
                 final POM pom );

    Path save( final Path path,
               final POM content,
               final Metadata metadata,
               final String comment,
               final boolean updateModules );

}
