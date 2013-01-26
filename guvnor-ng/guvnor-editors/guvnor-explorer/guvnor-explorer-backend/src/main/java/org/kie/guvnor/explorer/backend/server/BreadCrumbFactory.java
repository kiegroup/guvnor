package org.kie.guvnor.explorer.backend.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileStore;
import org.kie.commons.java.nio.file.Path;
import org.kie.guvnor.explorer.model.BreadCrumb;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;

/**
 * A Factory to make Bread Crumbs!
 */
@ApplicationScoped
public class BreadCrumbFactory {

    private IOService ioService;
    private ProjectService projectService;
    private Paths paths;

    public BreadCrumbFactory() {
        //Required by WELD
    }

    @Inject
    public BreadCrumbFactory( final @Named("ioStrategy") IOService ioService,
                              final ProjectService projectService,
                              final Paths paths ) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.paths = paths;
    }

    public List<BreadCrumb> makeBreadCrumbs( final org.uberfire.backend.vfs.Path path ) {
        return makeBreadCrumbs( path,
                                new ArrayList<org.kie.commons.java.nio.file.Path>() );
    }

    public List<BreadCrumb> makeBreadCrumbs( final org.uberfire.backend.vfs.Path path,
                                             final List<org.kie.commons.java.nio.file.Path> exclusions ) {
        final List<BreadCrumb> breadCrumbs = new ArrayList<BreadCrumb>();

        org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        org.kie.commons.java.nio.file.Path nioFileName = nioPath.getFileName();
        while ( nioFileName != null ) {
            if ( includePath( nioPath,
                              exclusions ) ) {
                final BreadCrumb breadCrumb = new BreadCrumb( paths.convert( nioPath ),
                                                              nioFileName.toString() );
                breadCrumbs.add( 0,
                                 breadCrumb );
            }
            nioPath = nioPath.getParent();
            nioFileName = nioPath.getFileName();
        }
        breadCrumbs.add( 0, new BreadCrumb( paths.convert( nioPath ),
                                            getRootDirectory( nioPath ) ) );

        return breadCrumbs;
    }

    public List<Path> makeBreadCrumbExclusions( final org.uberfire.backend.vfs.Path path ) {
        final List<org.kie.commons.java.nio.file.Path> exclusions = new ArrayList<Path>();
        final org.uberfire.backend.vfs.Path projectRoot = projectService.resolveProject( path );
        if ( projectRoot == null ) {
            return exclusions;
        }
        final org.kie.commons.java.nio.file.Path e0 = paths.convert( projectRoot );
        final org.kie.commons.java.nio.file.Path e1 = e0.resolve( "src" );
        final org.kie.commons.java.nio.file.Path e2 = e1.resolve( "main" );
        final org.kie.commons.java.nio.file.Path e3 = e2.resolve( "java" );
        final org.kie.commons.java.nio.file.Path e4 = e2.resolve( "resources" );
        exclusions.add( e1 );
        exclusions.add( e2 );
        exclusions.add( e3 );
        exclusions.add( e4 );
        return exclusions;
    }

    private boolean includePath( final org.kie.commons.java.nio.file.Path path,
                                 final List<org.kie.commons.java.nio.file.Path> exclusions ) {
        for ( final org.kie.commons.java.nio.file.Path p : exclusions ) {
            if ( path.endsWith( p ) ) {
                return false;
            }
        }
        return true;
    }

    private String getRootDirectory( final org.kie.commons.java.nio.file.Path path ) {
        final Iterator<FileStore> fileStoreIterator = path.getFileSystem().getFileStores().iterator();
        if ( fileStoreIterator.hasNext() ) {
            return fileStoreIterator.next().name();
        }
        return "";
    }

}
