package org.kie.guvnor.services.inbox;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

@Portable
public class AssetOpenedEvent {

    private Path resourcePath;

    public AssetOpenedEvent() {
    }

    public AssetOpenedEvent( final Path resourcePath ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        this.resourcePath = resourcePath;
    }

    public Path getResourcePath() {
        return this.resourcePath;
    }

}
