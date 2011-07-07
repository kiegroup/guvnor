package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class GlobalAreaTreeItem implements IsWidget {

    private GlobalAreaTreeItemView view;

    public GlobalAreaTreeItem( GlobalAreaTreeItemView view ) {
        this.view = view;
    }

    public Widget asWidget() {
        return view.asWidget();
    }
}
