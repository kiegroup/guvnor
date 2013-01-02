package org.kie.guvnor.datamodel.backend.server;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.datamodel.events.InvalidateDataModelOracleCacheEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.uberfire.backend.vfs.Path;

/**
 * A simple cache (no expiry) of DataModelOracles
 */
@ApplicationScoped
public class DefaultDataModelCache implements DataModelOracleCache {

    private Map<Path, DataModelOracle> oracleCache = new HashMap<Path, DataModelOracle>();

    @Override
    public synchronized DataModelOracle getDataModelOracle( final Path project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        return oracleCache.get( project );
    }

    @Override
    public synchronized void setDataModelOracle( final Path project,
                                                 final DataModelOracle oracle ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        PortablePreconditions.checkNotNull( "oracle",
                                            oracle );
        oracleCache.put( project,
                         oracle );
    }

    @Override
    public synchronized void invalidateCache() {
        this.oracleCache.clear();
    }

    @Override
    public synchronized void invalidateCache( final Path project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        this.oracleCache.remove( project );
    }

    public void invalidateCache( @Observes final InvalidateDataModelOracleCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path project = event.getProject();
        invalidateCache( project );
    }

}
