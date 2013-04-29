package org.kie.guvnor.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.kie.guvnor.commons.service.builder.model.BuildResults;
import org.kie.guvnor.commons.service.builder.model.IncrementalBuildResults;

/**
 * Test Observer for Build events
 */
@ApplicationScoped
public class BuildResultsObserver {

    private BuildResults buildResults;
    private IncrementalBuildResults incrementalBuildResults;

    public void onBuildResults( final @Observes BuildResults results ) {
        this.buildResults = results;
    }

    public void onIncrementalBuildResults( final @Observes IncrementalBuildResults results ) {
        this.incrementalBuildResults = results;
    }

    public BuildResults getBuildResults() {
        return buildResults;
    }

    public IncrementalBuildResults getIncrementalBuildResults() {
        return incrementalBuildResults;
    }
}
