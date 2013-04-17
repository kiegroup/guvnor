package org.kie.guvnor.workitems.model;

import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;

@Portable
public class WorkItemsMetaContent {

    private Map<String, String> workItemElementDefinitions;
    private List<String> workItemImages;

    public WorkItemsMetaContent() {
    }

    public WorkItemsMetaContent( final Map<String, String> workItemElementDefinitions,
                                 final List<String> workItemImages ) {
        this.workItemElementDefinitions = PortablePreconditions.checkNotNull( "workItemElementDefinitions",
                                                                              workItemElementDefinitions );
        this.workItemImages = PortablePreconditions.checkNotNull( "workItemImages",
                                                                  workItemImages );
    }

    public Map<String, String> getWorkItemElementDefinitions() {
        return this.workItemElementDefinitions;
    }

    public List<String> getWorkItemImages() {
        return this.workItemImages;
    }

}
