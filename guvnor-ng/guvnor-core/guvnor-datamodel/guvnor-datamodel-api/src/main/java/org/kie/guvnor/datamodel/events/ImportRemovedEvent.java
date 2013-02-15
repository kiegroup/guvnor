package org.kie.guvnor.datamodel.events;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.services.config.model.imports.Import;

/**
 * An event signalling removal of an import
 */
public class ImportRemovedEvent {

    private final Import item;
    private final DataModelOracle oracle;

    public ImportRemovedEvent( final DataModelOracle oracle,
                               final Import item ) {
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
        this.item = PortablePreconditions.checkNotNull( "item",
                                                        item );
    }

    public Import getImport() {
        return this.item;
    }

    public DataModelOracle getDataModelOracle() {
        return this.oracle;
    }

}
