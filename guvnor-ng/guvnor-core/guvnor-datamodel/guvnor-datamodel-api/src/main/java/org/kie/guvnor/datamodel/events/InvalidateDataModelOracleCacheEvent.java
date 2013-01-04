package org.kie.guvnor.datamodel.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * Event to invalidate an entry in a DataModelOracleCache
 */
@Portable
public class InvalidateDataModelOracleCacheEvent {

    private Path project;

    public InvalidateDataModelOracleCacheEvent() {
    }

    public InvalidateDataModelOracleCacheEvent( final Path project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        this.project = project;
    }

    public Path getProject() {
        return this.project;
    }

}
