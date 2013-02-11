package org.kie.guvnor.explorer.backend.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Utilities for BreadCrumbs
 */
@ApplicationScoped
public class BreadCrumbUtilities {

    private Paths paths;
    private ProjectService projectService;

    public BreadCrumbUtilities() {
        //Required by WELD
    }

    @Inject
    public BreadCrumbUtilities( final Paths paths,
                                final ProjectService projectService ) {
        this.paths = paths;
        this.projectService = projectService;
    }

    public List<org.kie.commons.java.nio.file.Path> makeBreadCrumbExclusions( final Path path ) {
        final List<org.kie.commons.java.nio.file.Path> exclusions = new ArrayList<org.kie.commons.java.nio.file.Path>();
        final org.uberfire.backend.vfs.Path projectRoot = projectService.resolveProject( path );
        if ( projectRoot == null ) {
            return exclusions;
        }
        final org.kie.commons.java.nio.file.Path e0 = paths.convert( projectRoot );
        final org.kie.commons.java.nio.file.Path e1 = e0.resolve( "src" );
        final org.kie.commons.java.nio.file.Path e2 = e1.resolve( "main" );
        exclusions.add( e1 );
        exclusions.add( e2 );
        return exclusions;
    }

    public Map<org.kie.commons.java.nio.file.Path, String> makeBreadCrumbCaptionSubstitutionsForDefaultPackage( final Path path ) {
        final Map<org.kie.commons.java.nio.file.Path, String> substitutions = new HashMap<org.kie.commons.java.nio.file.Path, String>();
        final org.uberfire.backend.vfs.Path projectRoot = projectService.resolveProject( path );
        if ( projectRoot == null ) {
            return substitutions;
        }
        final org.kie.commons.java.nio.file.Path e0 = paths.convert( projectRoot );
        final org.kie.commons.java.nio.file.Path e1 = e0.resolve( "src/main/java" );
        final org.kie.commons.java.nio.file.Path e2 = e0.resolve( "src/main/resources" );
        substitutions.put( e1,
                           "java" );
        substitutions.put( e2,
                           "resources" );
        return substitutions;
    }

}
