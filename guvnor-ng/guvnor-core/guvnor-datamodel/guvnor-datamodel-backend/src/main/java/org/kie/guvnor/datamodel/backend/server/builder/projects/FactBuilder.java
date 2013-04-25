package org.kie.guvnor.datamodel.backend.server.builder.projects;

import org.kie.guvnor.datamodel.oracle.ProjectDataModelOracleImpl;

/**
 * Builder for Fact Types
 */
public interface FactBuilder {

    public ProjectDataModelOracleBuilder end();

    public void build( final ProjectDataModelOracleImpl oracle );

}
