package org.guvnor.common.services.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.service.ProjectService;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.DescriptiveThreadFactory;

import static javax.ejb.TransactionAttributeType.*;

@Singleton
@Startup
@TransactionAttribute(NOT_SUPPORTED)
public class IncrementalBuilderExecutorManager {

    @Inject
    private ProjectService projectService;

    @Inject
    private BuildService buildService;

    @Inject
    private Event<BuildResults> buildResultsEvent;

    @Inject
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;

    private AtomicBoolean useExecService = new AtomicBoolean( false );
    private ExecutorService executorService = null;

    @Asynchronous
    public void execute( final AsyncIncrementalBuilder incrementalBuilder ) {
        if ( useExecService.get() ) {
            getExecutorService().execute( new DescriptiveRunnable() {
                @Override
                public void run() {
                    incrementalBuilder.execute( projectService, buildService, incrementalBuildResultsEvent, buildResultsEvent );
                }

                @Override
                public String getDescription() {
                    return incrementalBuilder.getDescription();
                }
            } );
        } else {
            incrementalBuilder.execute( projectService, buildService, incrementalBuildResultsEvent, buildResultsEvent );
        }
    }

    void setExecutorService( final ExecutorService executorService ) {
        this.executorService = executorService;
        this.useExecService.set( true );
    }

    private ExecutorService getExecutorService() {
        if ( executorService == null ) {
            executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );
        }
        return executorService;
    }

    public void setServices( final ProjectService projectService,
                             final BuildService buildService,
                             final Event<BuildResults> buildResultsEvent,
                             final Event<IncrementalBuildResults> incrementalBuildResultsEvent ) {
        this.projectService = projectService;
        this.buildService = buildService;
        this.buildResultsEvent = buildResultsEvent;
        this.incrementalBuildResultsEvent = incrementalBuildResultsEvent;
        this.useExecService.set( true );
    }

    public void shutdown() {
        if ( useExecService.get() ) {
            executorService.shutdownNow();
        }
    }
}
