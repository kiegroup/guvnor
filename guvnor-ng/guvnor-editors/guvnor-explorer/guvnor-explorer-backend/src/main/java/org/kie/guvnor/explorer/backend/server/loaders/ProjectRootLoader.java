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
import org.kie.guvnor.explorer.model.ParentFolderItem;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Loader to add Projects structure being pom.xml and immediate packages under src/main/resources
 */
@Dependent
@Named("projectRootList")
public class ProjectRootLoader implements ItemsLoader {

    private static final String POM_PATH = "pom.xml";
    private static final String RESOURCES_PATH = "src/main/resources";

    private final Filter filter;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    public ProjectRootLoader() {
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

        //Add pom.xml file
        final org.kie.commons.java.nio.file.Path pRoot = paths.convert( projectRoot );
        final org.kie.commons.java.nio.file.Path pomPath = pRoot.resolve( POM_PATH );
        items.add( new FileItem( paths.convert( pomPath ) ) );

        //Add Items within Project's Resources path
        final org.kie.commons.java.nio.file.Path resourcesPath = pRoot.resolve( RESOURCES_PATH );
        items.addAll( loadPackages( resourcesPath ) );

        //Add ability to move up one level in the hierarchy
        items.add( new ParentFolderItem( paths.convert( pRoot.getParent() ),
                                         ".." ) );

        return items;
    }

    private List<Item> loadPackages( final org.kie.commons.java.nio.file.Path path ) {
        //Check Path exists
        final List<Item> items = new ArrayList<Item>();
        if ( !Files.exists( path ) ) {
            return items;
        }

        //Default package
        items.add( new PackageItem( paths.convert( path ),
                                    "default" ) );

        //Other packages
        final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = ioService.newDirectoryStream( path );
        for ( final org.kie.commons.java.nio.file.Path p : directoryStream ) {
            if ( filter.accept( p ) ) {
                if ( Files.isDirectory( p ) ) {
                    items.add( new PackageItem( paths.convert( p ) ) );
                }
            }
        }
        return items;
    }

}
