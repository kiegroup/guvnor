package org.kie.guvnor.datamodel.backend.server.builder.projects;

import org.kie.guvnor.datamodel.oracle.ProjectDefinition;

/**
 * Builder for Fact Types
 */
public interface FactBuilder {

    public ProjectDefinitionBuilder end();

    public void build( final ProjectDefinition oracle );

}
