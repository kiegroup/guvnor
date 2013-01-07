package org.kie.guvnor.datamodel.backend.server;

import org.kie.guvnor.datamodel.model.DefaultDataModel;

/**
 * Builder for Fact Types
 */
public interface FactBuilder {

    public DataModelBuilder end();

    public void build( final DefaultDataModel oracle );

}
