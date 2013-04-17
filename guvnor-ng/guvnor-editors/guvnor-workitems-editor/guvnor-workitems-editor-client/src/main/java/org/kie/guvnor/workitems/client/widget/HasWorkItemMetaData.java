package org.kie.guvnor.workitems.client.widget;

import org.kie.guvnor.workitems.model.WorkItemsMetaContent;

/**
 * Marker interface for widgets that need Work Item Meta Data
 */
public interface HasWorkItemMetaData {

    void setMetaData(final WorkItemsMetaContent metaData);

}
