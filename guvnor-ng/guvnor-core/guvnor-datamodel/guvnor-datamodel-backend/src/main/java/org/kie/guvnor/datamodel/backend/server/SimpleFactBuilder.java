package org.kie.guvnor.datamodel.backend.server;

import org.kie.guvnor.datamodel.model.ModelAnnotation;
import org.kie.guvnor.datamodel.model.ModelField;

/**
 * Simple builder for Fact Types
 */
public class SimpleFactBuilder extends BaseFactBuilder {

    public SimpleFactBuilder( final DataModelBuilder builder,
                              final String factType ) {
        super( builder,
               factType );
    }

    public SimpleFactBuilder addField( final ModelField field ) {
        super.addField( field );
        return this;
    }

    public SimpleFactBuilder addAnnotation( final ModelAnnotation annotation ) {
        super.addAnnotation( annotation );
        return this;
    }

}
