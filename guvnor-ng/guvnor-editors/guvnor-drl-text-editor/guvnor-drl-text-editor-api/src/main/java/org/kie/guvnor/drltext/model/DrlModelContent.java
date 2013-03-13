package org.kie.guvnor.drltext.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

@Portable
public class DrlModelContent {

    private String drl;
    private DataModelOracle oracle;

    public DrlModelContent() {
    }

    public DrlModelContent( final String drl,
                            final DataModelOracle oracle ) {
        this.drl = PortablePreconditions.checkNotNull( "drl",
                                                       drl );
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
    }

    public String getDrl() {
        return this.drl;
    }

    public DataModelOracle getDataModel() {
        return this.oracle;
    }

}
