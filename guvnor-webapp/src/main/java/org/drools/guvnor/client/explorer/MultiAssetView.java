package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;

public interface MultiAssetView extends IsWidget {

    void init(MultiViewRow[] rows, ClientFactory clientFactory);

}
