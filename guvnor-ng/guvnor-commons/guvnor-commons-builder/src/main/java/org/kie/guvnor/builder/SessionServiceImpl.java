package org.kie.guvnor.builder;

import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.guvnor.commons.service.session.SessionService;
import org.kie.runtime.KieSession;
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
