package org.kie.guvnor.project.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.services.file.SupportsRead;
import org.kie.guvnor.services.file.SupportsUpdate;
import org.uberfire.backend.vfs.Path;

@Remote
public interface POMService extends SupportsRead<POM>,
                                    SupportsUpdate<POM> {

    Path create( final Path projectRoot );

}
