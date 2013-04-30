package org.kie.guvnor.datamodel.backend.server.builder.projects;

import org.kie.guvnor.datamodel.model.ModelField;

/**
 * Simple builder for Fact Types
 */
public class SimpleFactBuilder extends BaseFactBuilder {

    public SimpleFactBuilder( final ProjectDataModelOracleBuilder builder,
                              final String factType,
                              final boolean isEvent,
                              final boolean isDeclaredType ) {
        super( builder,
               factType,
               false,
               isEvent,
               isDeclaredType );
    }

    public SimpleFactBuilder addField( final ModelField field ) {
        super.addField( field );
        return this;
    }

}
