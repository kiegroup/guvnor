package org.kie.guvnor.commons.service.session;

import org.kie.api.runtime.KieSession;
import org.uberfire.backend.vfs.Path;

public interface SessionService {

    KieSession newKieSession(final Path pathToPom, String sessionName);

}
