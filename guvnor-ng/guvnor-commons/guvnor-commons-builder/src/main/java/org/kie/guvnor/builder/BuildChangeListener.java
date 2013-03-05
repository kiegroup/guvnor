package org.kie.guvnor.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.commons.service.builder.BuildService;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.config.AppConfigService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;

/**
 * Listener for changes to project resources to handle incremental builds
 */
@ApplicationScoped
public class BuildChangeListener {

    private static final String POM_FILE = "pom.xml";

    private static final String INCREMENTAL_BUILD_PROPERTY_NAME = "build.enable-incremental";

    @Inject
    private Paths paths;

    @Inject
    private ProjectService projectService;

    @Inject
    private BuildService buildService;

    @Inject
    private AppConfigService appConfigService;

    private ExecutorService executor;

    private boolean isIncrementalEnabled = false;

    @PostConstruct
    private void setupExecutorService() {
        final int cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool( cores );
        isIncrementalEnabled = isIncrementalBuildEnabled();
    }

    private boolean isIncrementalBuildEnabled() {
        final String value = appConfigService.loadPreferences().get( INCREMENTAL_BUILD_PROPERTY_NAME );
        return Boolean.parseBoolean( value );
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
        //Do nothing if incremental builds are disabled
        if ( !isIncrementalEnabled ) {
            return;
        }

        //Perform incremental build
        PortablePreconditions.checkNotNull( "resourceAddedEvent",
                                            resourceAddedEvent );
        final Path resource = resourceAddedEvent.getPath();

        //If resource is not within a Package it cannot be used for an incremental build
        final Path packagePath = projectService.resolvePackage( resource );
        if ( packagePath == null ) {
            return;
        }

        //Schedule an incremental build
        executor.execute( new Runnable() {

            @Override
            public void run() {
                try {
                    buildService.addResource( resource );
                } catch ( Exception e ) {
                    //Swallow for now...
                    System.out.println( e.fillInStackTrace() );
                }
            }
        } );
    }

    public void deleteResource( @Observes final ResourceDeletedEvent resourceDeletedEvent ) {
        //Do nothing if incremental builds are disabled
        if ( !isIncrementalEnabled ) {
            return;
        }

        //Perform incremental build
        PortablePreconditions.checkNotNull( "resourceDeletedEvent",
                                            resourceDeletedEvent );
        final Path resource = resourceDeletedEvent.getPath();

        //If resource is not within a Package it cannot be used for an incremental build
        final Path packagePath = projectService.resolvePackage( resource );
        if ( packagePath == null ) {
            return;
        }

        //Schedule an incremental build
        executor.execute( new Runnable() {

            @Override
            public void run() {
                try {
                    buildService.deleteResource( resource );
                } catch ( Exception e ) {
                    //Swallow for now...
                    System.out.println( e.fillInStackTrace() );
                }
            }
        } );
    }

    public void updateResource( @Observes final ResourceUpdatedEvent resourceUpdatedEvent ) {
        //Do nothing if incremental builds are disabled
        if ( !isIncrementalEnabled ) {
            return;
        }

        //Perform incremental build
        PortablePreconditions.checkNotNull( "resourceUpdatedEvent",
                                            resourceUpdatedEvent );
        final Path resource = resourceUpdatedEvent.getPath();

        //If resource is not within a Package it cannot be used for an incremental build
        final Path packagePath = projectService.resolvePackage( resource );
        if ( packagePath == null ) {
            return;
        }

        //Schedule an incremental build
        executor.execute( new Runnable() {

            @Override
            public void run() {
                try {
                    buildService.updateResource( resource );
                } catch ( Exception e ) {
                    //Swallow for now...
                    System.out.println( e.fillInStackTrace() );
                }
            }
        } );
    }

}
