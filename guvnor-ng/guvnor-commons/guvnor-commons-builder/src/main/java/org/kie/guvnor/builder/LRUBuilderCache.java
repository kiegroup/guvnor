package org.kie.guvnor.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.commons.io.IOService;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.services.cache.LRUCache;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<Path, Builder> {

    @Inject
    private Paths paths;

    @Inject
    private POMService pomService;

    @Inject
    private IOService ioService;

    public synchronized Builder assertBuilder( final Path pathToPom ) {
        Builder builder = getEntry( pathToPom );
        if ( builder == null ) {
            final POM gav = pomService.load( pathToPom );
            builder = new Builder( paths.convert( pathToPom ).getParent(),
                                   gav.getGav().getArtifactId(),
                                   paths,
                                   ioService );
            setEntry( pathToPom,
                      builder );
        }
        return builder;
    }

}
