package org.kie.guvnor.datamodel.backend.server;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.datamodel.events.InvalidateDataModelOracleCacheEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache of DataModelOracles
 */
@ApplicationScoped
public class LRUDataModelOracleCache implements DataModelOracleCache {

    private static final int MAX_ENTRIES = 20;

    private Map<Path, DataModelOracle> oracleCache;

    public LRUDataModelOracleCache() {
        oracleCache = new LinkedHashMap<Path, DataModelOracle>( MAX_ENTRIES + 1,
                                                                0.75f,
                                                                true ) {
            public boolean removeEldestEntry( Map.Entry eldest ) {
                return size() > MAX_ENTRIES;
            }
        };
        oracleCache = (Map) Collections.synchronizedMap( oracleCache );
    }

    @Override
    public DataModelOracle getDataModelOracle( final Path project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        return oracleCache.get( project );
    }

    @Override
    public void setDataModelOracle( final Path project,
                                    final DataModelOracle oracle ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        PortablePreconditions.checkNotNull( "oracle",
                                            oracle );
        oracleCache.put( project,
                         oracle );
    }

    @Override
    public void invalidateCache() {
        this.oracleCache.clear();
    }

    @Override
    public void invalidateCache( final Path project ) {
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
