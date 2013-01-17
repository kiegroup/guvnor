package org.kie.guvnor.datamodel.backend.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache of DataModelOracles
 */
@ApplicationScoped
public class LRUDataModelOracleCache implements DataModelOracleCache {

    private static final int MAX_ENTRIES = 20;

    private Map<Path, DataModelOracle> oracleCache;

    @Inject
    private ProjectService projectService;

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
    public DataModelOracle getDataModelOracle( final Path packagePath ) {
        PortablePreconditions.checkNotNull( "packagePath",
                                            packagePath );
        return oracleCache.get( packagePath );
    }

    @Override
    public void setDataModelOracle( final Path packagePath,
                                    final DataModelOracle oracle ) {
        PortablePreconditions.checkNotNull( "packagePath",
                                            packagePath );
        PortablePreconditions.checkNotNull( "oracle",
                                            oracle );
        oracleCache.put( packagePath,
                         oracle );
    }

    @Override
    public void invalidateCache() {
        this.oracleCache.clear();
    }

    @Override
    public void invalidateCache( final Path packagePath ) {
        PortablePreconditions.checkNotNull( "packagePath",
                                            packagePath );
        this.oracleCache.remove( packagePath );
    }

    public void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final Path projectPath = projectService.resolveProject( resourcePath );
        final String projectUri = projectPath.toURI();

        final List<Path> cacheEntriesToInvalidate = new ArrayList<Path>();

        for ( final Path packagePath : oracleCache.keySet() ) {
            final String packageUri = packagePath.toURI();
            if ( packageUri.startsWith( projectUri ) ) {
                cacheEntriesToInvalidate.add( packagePath );
            }
        }

        for ( final Path packagePath : cacheEntriesToInvalidate ) {
            invalidateCache( packagePath );
        }
    }

    public void invalidatePackageCache( @Observes final InvalidateDMOPackageCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final Path packagePath = projectService.resolvePackage( resourcePath );
        invalidateCache( packagePath );
    }

}
