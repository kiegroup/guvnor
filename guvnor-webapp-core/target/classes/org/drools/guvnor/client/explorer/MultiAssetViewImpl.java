package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.asseteditor.MultiViewRow;

public class MultiAssetViewImpl implements MultiAssetView {

    private MultiViewEditor multiview;


    public void init(MultiViewRow[] rows, ClientFactory clientFactory, EventBus eventBus) {
        multiview = new MultiViewEditor(
                rows,
                clientFactory,
                eventBus);
    }

    public Widget asWidget() {
        return multiview;
    }
}
