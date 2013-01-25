package org.kie.guvnor.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a folder
 */
@Portable
public class ParentFolderItem implements Item {

    private Path path;
    private String caption;

    public ParentFolderItem() {
        //For Errai-marshalling
    }

    public ParentFolderItem( final Path path ) {
        this( path,
              path.getFileName() );
    }

    public ParentFolderItem( final Path path,
                             final String caption ) {
        PortablePreconditions.checkNotNull( "caption",
                                            caption );
        this.path = path;
        this.caption = caption;
    }

    @Override
    public ItemType getType() {
        return ItemType.PARENT_FOLDER;
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
