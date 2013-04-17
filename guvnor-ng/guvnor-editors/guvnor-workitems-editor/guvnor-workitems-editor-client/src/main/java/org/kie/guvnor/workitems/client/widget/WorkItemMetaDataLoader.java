package org.kie.guvnor.workitems.client.widget;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.DefaultErrorCallback;
import org.kie.guvnor.workitems.client.widget.HasWorkItemMetaData;
import org.kie.guvnor.workitems.model.WorkItemsMetaContent;
import org.kie.guvnor.workitems.service.WorkItemsEditorService;

/**
 * Loader for Work Items Editors meta-data.
 */
@ApplicationScoped
public class WorkItemMetaDataLoader {

    @Inject
    private Caller<WorkItemsEditorService> workItemsService;

    private WorkItemsMetaContent metaContent = null;

    public void loadMetaContent( final HasWorkItemMetaData handler ) {
        if ( metaContent == null ) {
            workItemsService.call( getMetaContentSuccessCallback( handler ),
                                   new DefaultErrorCallback() ).loadMetaContent();
        } else {
            handler.setMetaData( metaContent );
        }
    }

    private RemoteCallback<WorkItemsMetaContent> getMetaContentSuccessCallback( final HasWorkItemMetaData handler ) {
        return new RemoteCallback<WorkItemsMetaContent>() {

            @Override
            public void callback( final WorkItemsMetaContent content ) {
                metaContent = content;
                handler.setMetaData( content );
            }
        };
    }

}
