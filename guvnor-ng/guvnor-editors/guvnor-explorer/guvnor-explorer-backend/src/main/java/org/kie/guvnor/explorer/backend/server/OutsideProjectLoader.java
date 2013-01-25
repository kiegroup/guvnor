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
import org.kie.guvnor.explorer.model.ProjectItem;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Loader to add Projects, Folders and Files
 */
@Dependent
@Named("outsideProjectList")
public class OutsideProjectLoader implements ItemsLoader {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private Paths paths;

    @Override
    public List<Item> load( final Path path ) {

        final List<Item> items = new ArrayList<Item>();

        //Ensure Path represents a Folder
        org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        if ( !Files.isDirectory( nioPath ) ) {
            nioPath = nioPath.getParent();
        }

        //Get list of immediate children
        final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = ioService.newDirectoryStream( nioPath );
        for ( final org.kie.commons.java.nio.file.Path p : directoryStream ) {

            if ( Files.isRegularFile( p ) ) {
                items.add( new FileItem( paths.convert( p ) ) );
            } else if ( Files.isDirectory( p ) ) {
                final Path childPath = paths.convert( p );
                final Path resolvedProjectPath = projectService.resolveProject( childPath );
                if ( resolvedProjectPath != null && resolvedProjectPath.toURI().equals( childPath.toURI() ) ) {
                    items.add( new ProjectItem( paths.convert( p ) ) );
                } else {
                    items.add( new FolderItem( paths.convert( p ) ) );
                }
            }
        }

        //Add ability to move up one level in the hierarchy
        items.add( new ParentFolderItem( paths.convert( nioPath.getParent() ),
                                         ".." ) );

        return items;
    }

}
