package org.kie.guvnor.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a folder
 */
@Portable
public class FileItem implements Item {

    private Path path;
    private String caption;

    public FileItem() {
        //For Errai-marshalling
    }

    public FileItem( final Path path ) {
        this( path,
              path.getFileName() );
    }

    public FileItem( final Path path,
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
        return ItemType.FILE;
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
