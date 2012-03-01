package org.drools.guvnor.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.explorer.ClientFactory;

public class AppControllerHelloWorldImpl implements AppController {

    private final EventBus eventBus;
    private final WorkbenchPageManager workbenchPageManager;

    public AppControllerHelloWorldImpl(ClientFactory clientFactory, EventBus eventBus) {
        this.eventBus = eventBus;
        workbenchPageManager = new WorkbenchPageManager(eventBus);
        workbenchPageManager.setWorkbenchPage(new WorkbenchPage(eventBus));

    }

    @Override
    public void setUserName(String userName) {
        //TODO: Generated code -Rikkola-
    }

    @Override
    public IsWidget getMainPanel() {
        return workbenchPageManager.getWorkbenchPage();
    }
}
