package org.guvnor.common.services.builder;

import org.guvnor.common.services.project.model.Project;
import org.kie.api.runtime.KieSession;
import org.guvnor.common.services.project.model.Project;

public interface SessionService {

    KieSession newKieSession( final Project project );

}
