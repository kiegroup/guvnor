package org.guvnor.common.services.builder;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.service.ProjectService;

public interface AsyncIncrementalBuilder {

    public void execute( final ProjectService projectService,
                         final BuildService buildService,
                         final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                         final Event<BuildResults> buildResultsEvent );

    String getDescription();
}
