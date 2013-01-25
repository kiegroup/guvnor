package org.kie.guvnor.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a project
 */
@Portable
public class ProjectItem implements Item {

    private Path path;
    private String caption;

    public ProjectItem() {
        //For Errai-marshalling
    }

    public ProjectItem( final Path path ) {
        this( path,
              path.getFileName() );
    }

    public ProjectItem( final Path path,
                        final String caption ) {
        PortablePreconditions.checkNotNull( "path",
                                            path );
        PortablePreconditions.checkNotNull( "caption",
                                            caption );
        this.path = path;
        this.caption = caption;
    }

    @Override
    public ItemType getType() {
        return ItemType.PROJECT;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public String getCaption() {
        return this.caption;
    }

}
