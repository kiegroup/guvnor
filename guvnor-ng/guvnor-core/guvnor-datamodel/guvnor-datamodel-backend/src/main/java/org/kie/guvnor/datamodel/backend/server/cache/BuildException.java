package org.kie.guvnor.datamodel.backend.server.cache;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.commons.service.builder.model.BuildResults;

/**
 * An exception to wrap errors creating a DataModelOracle
 */
public class BuildException extends Exception {

    private final BuildResults results;

    public BuildException( final BuildResults results ) {
        this.results = PortablePreconditions.checkNotNull( "results",
                                                           results );
    }

    public BuildResults getResults() {
        return this.results;
    }

}
