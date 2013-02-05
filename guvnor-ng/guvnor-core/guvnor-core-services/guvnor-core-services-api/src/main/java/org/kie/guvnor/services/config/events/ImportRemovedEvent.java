package org.kie.guvnor.services.config.events;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.services.config.model.imports.Import;
import org.uberfire.backend.vfs.Path;

/**
 * An event signalling removal of an import
 */
public class ImportRemovedEvent {

    private final Import item;
    private final Path resourcePath;

    public ImportRemovedEvent( final Path resourcePath,
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
