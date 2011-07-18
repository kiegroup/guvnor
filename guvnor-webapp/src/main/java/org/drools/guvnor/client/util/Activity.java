package org.drools.guvnor.client.util;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.explorer.AcceptTabItem;

public abstract class Activity {

    public abstract void start( AcceptTabItem tabbedPanel, EventBus eventBus );

    public boolean mayStop() {
        return true;
    }

    public void onStop() {

    }
}
