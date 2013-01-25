package org.kie.guvnor.explorer.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.guvnor.explorer.model.FileItem;
import org.kie.guvnor.explorer.model.FolderItem;
import org.kie.guvnor.explorer.model.Item;
import org.kie.guvnor.explorer.model.ParentFolderItem;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Loader to add Projects structure being pom.xml and immediate packages under src/main/resources
 */
@Dependent
@Named("projectRootList")
public class ProjectRootLoader implements ItemsLoader {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public List<Item> load( final Path path ) {

        final List<Item> items = new ArrayList<Item>();

        //Ensure Path represents a Folder
        org.kie.commons.java.nio.file.Path rPath = paths.convert( path );
        if ( !Files.isDirectory( rPath ) ) {
            rPath = rPath.getParent();
        }

        //Get list of immediate children
        final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = ioService.newDirectoryStream( rPath );
        for ( final org.kie.commons.java.nio.file.Path p : directoryStream ) {

            if ( Files.isRegularFile( p ) ) {
                items.add( new FileItem( paths.convert( p ) ) );
            } else if ( Files.isDirectory( p ) ) {
                items.add( new FolderItem( paths.convert( p ) ) );
            }
        }

        //Add ability to move up one level in the hierarchy
        items.add( new ParentFolderItem( paths.convert( rPath.getParent() ),
                                         ".." ) );

        return items;
    }

}
