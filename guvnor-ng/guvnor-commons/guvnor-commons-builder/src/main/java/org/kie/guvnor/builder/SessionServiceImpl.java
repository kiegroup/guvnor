package org.kie.guvnor.builder;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSession;
import org.kie.guvnor.commons.service.session.SessionService;
import org.uberfire.backend.vfs.Path;

import javax.inject.Inject;

public class SessionServiceImpl
        implements SessionService {

    private LRUBuilderCache cache;

    public SessionServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public SessionServiceImpl(final LRUBuilderCache cache) {
        this.cache = cache;
    }

    @Override
    public KieSession newKieSession(Path pathToPom) {

        final Builder builder = cache.assertBuilder(pathToPom);
        ReleaseId releaseId = builder.getKieModule().getReleaseId();

        return KieServices.Factory.get().newKieContainer(releaseId).newKieSession();
    }

}
