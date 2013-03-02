package org.kie.guvnor.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;

/**
 * Listener for changes to project resources to handle incremental builds
 */
public class BuildChangeListener {

    private static final String POM_FILE = "pom.xml";

    @Inject
    private Paths paths;

    @Inject
    private ProjectService projectService;

    @Inject
    private BuildService buildService;

    private ExecutorService executor;

    @PostConstruct
    private void setupExecutorService() {
        executor = Executors.newFixedThreadPool( 2 );
    }

    @PreDestroy
    private void destroyExecutorService() {
        try {
            executor.shutdown();
            if ( !executor.awaitTermination( 10,
                                             TimeUnit.SECONDS ) ) {
                executor.shutdownNow();
                if ( !executor.awaitTermination( 10,
                                                 TimeUnit.SECONDS ) ) {
                    System.err.println( "executor did not terminate" );
                }
            }
        } catch ( InterruptedException e ) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void addResource( @Observes final ResourceAddedEvent resourceAddedEvent ) {
        try {
            PortablePreconditions.checkNotNull( "resourceAddedEvent",
                                                resourceAddedEvent );
            final Path resource = resourceAddedEvent.getPath();
            final Path pathToPom = getPathToPom( resource );

            //If resource is not within a Project or the Project lacks a pom.xml file exit
            if ( pathToPom == null ) {
                return;
            }

            //Schedule an incremental build
            executor.submit( new Runnable() {

                @Override
                public void run() {
                    buildService.addResource( pathToPom,
                                              resource );
                }
            } );

        } catch ( Exception e ) {
            //Swallow for now...
            System.out.println( e.fillInStackTrace() );
        }
    }

    public void deleteResource( @Observes final ResourceDeletedEvent resourceDeletedEvent ) {
        try {
            PortablePreconditions.checkNotNull( "resourceDeletedEvent",
                                                resourceDeletedEvent );
            final Path resource = resourceDeletedEvent.getPath();
            final Path pathToPom = getPathToPom( resource );

            //If resource is not within a Project or the Project lacks a pom.xml file exit
            if ( pathToPom == null ) {
                return;
            }

            //Schedule an incremental build
            executor.submit( new Runnable() {

                @Override
                public void run() {
                    buildService.deleteResource( pathToPom,
                                                 resource );
                }
            } );

        } catch ( Exception e ) {
            //Swallow for now...
            System.out.println( e.fillInStackTrace() );
        }
    }

    public void updateResource( @Observes final ResourceUpdatedEvent resourceUpdatedEvent ) {
        try {
            PortablePreconditions.checkNotNull( "resourceUpdatedEvent",
                                                resourceUpdatedEvent );
            final Path resource = resourceUpdatedEvent.getPath();
            final Path pathToPom = getPathToPom( resource );

            //If resource is not within a Project or the Project lacks a pom.xml file exit
            if ( pathToPom == null ) {
                return;
            }

            //Schedule an incremental build
            executor.submit( new Runnable() {

                @Override
                public void run() {
                    buildService.updateResource( pathToPom,
                                                 resource );
                }
            } );

        } catch ( Exception e ) {
            //Swallow for now...
            System.out.println( e.fillInStackTrace() );
        }
    }

    private Path getPathToPom( final Path resource ) {
        final Path projectPath = projectService.resolveProject( resource );
        //Check resource is within a Project
        if ( projectPath == null ) {
            return null;
        }
        final Path pathToPom = makePathToPom( projectPath );
        //Don't assume there's a pom file
        if ( pathToPom == null ) {
            return null;
        }
        return pathToPom;
    }

    private Path makePathToPom( final Path projectPath ) {
        final org.kie.commons.java.nio.file.Path pom = paths.convert( projectPath ).resolve( POM_FILE );
        if ( pom == null ) {
            return null;
        }
        return paths.convert( pom );
    }

}
