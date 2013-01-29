package org.kie.guvnor.explorer.backend.server.loaders;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.guvnor.explorer.backend.server.util.DotFileFilter;
import org.kie.guvnor.explorer.backend.server.util.Filter;
import org.kie.guvnor.explorer.backend.server.util.MetaInfFolderFilter;
import org.kie.guvnor.explorer.model.FileItem;
import org.kie.guvnor.explorer.model.Item;
import org.kie.guvnor.explorer.model.PackageItem;
import org.kie.guvnor.explorer.model.ParentPackageItem;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Loader to add Projects, Folders and Files
 */
@Dependent
@Named("projectPackageList")
public class ProjectPackageLoader implements ItemsLoader {

    private static final String RESOURCES_PATH = "src/main/resources";

    private final Filter filter;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    public ProjectPackageLoader() {
        filter = new DotFileFilter();
        filter.setNextFilter( new MetaInfFolderFilter() );
    }

    @Override
    public List<Item> load( final Path path,
                            final Path projectRoot ) {

        //Check Path exists
        final List<Item> items = new ArrayList<Item>();
        if ( !Files.exists( paths.convert( path ) ) ) {
            return items;
        }

        //Ensure Path represents a Folder
        org.kie.commons.java.nio.file.Path pPath = paths.convert( path );
        if ( !Files.isDirectory( pPath ) ) {
            pPath = pPath.getParent();
        }

        //Get list of immediate children
        final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = ioService.newDirectoryStream( pPath );
        for ( final org.kie.commons.java.nio.file.Path p : directoryStream ) {
            if ( filter.accept( p ) ) {
                if ( Files.isRegularFile( p ) ) {
                    items.add( new FileItem( paths.convert( p ) ) );
                } else if ( Files.isDirectory( p ) ) {
                    items.add( new PackageItem( paths.convert( p ) ) );
                }
            }
        }

        //Add ability to move up one level in the hierarchy
        final org.kie.commons.java.nio.file.Path pRoot = paths.convert( projectRoot );
        items.add( new ParentPackageItem( paths.convert( getParent( pPath,
                                                                    pRoot ) ),
                                          ".." ) );

        return items;
    }

    //Explorer flattens the folder hierarchy from /src/main/resources/p1 to /resources and /p1
    //where /resources represents the default package and /p1 a user-defined package. Therefore
    //when navigating up the hierarchy we need to translate /p1's parent into /src/main.
    private org.kie.commons.java.nio.file.Path getParent( final org.kie.commons.java.nio.file.Path pPath,
                                                          final org.kie.commons.java.nio.file.Path pRoot ) {
        org.kie.commons.java.nio.file.Path pParent = pPath.getParent();
        final org.kie.commons.java.nio.file.Path pResources = pRoot.resolve( RESOURCES_PATH );
        if ( Files.isSameFile( pParent,
                               pResources ) ) {
            pParent = pParent.getParent();
        }
        return pParent;
    }

}
