package org.guvnor.common.services.project.service;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.shared.file.SupportsRead;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface POMService extends SupportsRead<POM>,
                                    SupportsUpdate<POM> {

    Path create( final Path projectRoot,
                 final String baseURL,
                 final POM pom );

}
