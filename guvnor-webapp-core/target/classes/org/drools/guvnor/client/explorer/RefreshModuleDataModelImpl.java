package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;

/**
 * This is the default impl to handle RefreshModuleDataModelEvent. Do nothing, call back immediately.  
 */
public class RefreshModuleDataModelImpl implements RefreshModuleDataModelEvent.Handler {
    private static RefreshModuleDataModelImpl INSTANCE = null;

    public static RefreshModuleDataModelImpl getInstance() {
        if (INSTANCE == null){
            INSTANCE = new RefreshModuleDataModelImpl();
        }
        return INSTANCE;
    }

    private RefreshModuleDataModelImpl() {
    }

    public void setEventBus(final EventBus eventBus) {   
        eventBus.addHandler(RefreshModuleDataModelEvent.TYPE, this);
    }

    public void onRefreshModuleDataModel(RefreshModuleDataModelEvent refreshModuleDataModelEvent) {
        refreshModuleDataModelEvent.getCallbackCommand().execute();
    }
}