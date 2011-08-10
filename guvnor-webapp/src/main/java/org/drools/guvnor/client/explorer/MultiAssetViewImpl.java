package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.ruleeditor.MultiViewEditor;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;

public class MultiAssetViewImpl implements MultiAssetView {

    private MultiViewEditor multiview;


    public void init(MultiViewRow[] rows, ClientFactory clientFactory) {
        multiview = new MultiViewEditor(
                rows,
                clientFactory );
    }

    public Widget asWidget() {
        return multiview;
    }
}
