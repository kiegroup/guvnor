package org.kie.guvnor.datamodel.events;

import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * Event to invalidate an entry in a DataModelOracleCache
 */
public class InvalidateDataModelOracleCacheEvent {

    private final Path project;

    public InvalidateDataModelOracleCacheEvent( final Path project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        this.project = project;
    }

    public Path getProject() {
        return this.project;
    }

}
