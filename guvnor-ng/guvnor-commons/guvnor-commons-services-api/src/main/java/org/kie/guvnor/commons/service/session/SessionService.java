package org.kie.guvnor.commons.service.session;

import org.kie.runtime.KieSession;
import org.uberfire.backend.vfs.Path;

public interface SessionService {

    KieSession newKieSession( final Path pathToPom );

}
