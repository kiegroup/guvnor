package org.kie.guvnor.services.config.events;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.services.config.model.imports.Import;
import org.uberfire.backend.vfs.Path;

/**
 * An event signalling adding an import
 */
public class ImportAddedEvent {

    private final Import item;
    private final Path resourcePath;

    public ImportAddedEvent( final Path resourcePath,
                             final Import item ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        PortablePreconditions.checkNotNull( "item",
                                            item );
        this.resourcePath = resourcePath;
        this.item = item;
    }

    public Import getImport() {
        return this.item;
    }

    public Path getResourcePath() {
        return this.resourcePath;
    }

}
