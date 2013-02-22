package org.kie.guvnor.datamodel.backend.server.cache;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.datamodel.oracle.ProjectDefinition;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * A simple LRU cache for Project DataModelOracles
 */
@ApplicationScoped
@Named("ProjectDataModelOracleCache")
public class LRUProjectDataModelOracleCache extends LRUCache<Path, ProjectDefinition> {

    @Inject
    private ProjectService projectService;

    @Inject
    private LRUDataModelOracleCache packageCache;

    public void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final Path projectPath = projectService.resolveProject( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( projectPath != null ) {
            invalidateCache( projectPath );
        }
    }

}
