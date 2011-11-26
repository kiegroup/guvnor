package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;

import org.drools.guvnor.client.asseteditor.MultiViewRow;

public interface MultiAssetView extends IsWidget {

    void init(MultiViewRow[] rows, ClientFactory clientFactory, EventBus eventBus);

}
