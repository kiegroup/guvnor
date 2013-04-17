package org.kie.guvnor.workitems.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;

@Portable
public class WorkItemsModelContent {

    private String definition;

    public WorkItemsModelContent() {
    }

    public WorkItemsModelContent( final String definition ) {
        this.definition = PortablePreconditions.checkNotNull( "definition",
                                                              definition );
    }

    public String getDefinition() {
        return this.definition;
    }

}
