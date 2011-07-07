package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.ui.IsWidget;

public interface TabContentWidget extends IsWidget {

    public String getTabTitle();

    public String getID();
}
