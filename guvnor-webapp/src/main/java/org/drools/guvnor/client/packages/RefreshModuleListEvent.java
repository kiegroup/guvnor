package org.drools.guvnor.client.packages;

import com.google.gwt.event.shared.GwtEvent;

public class RefreshModuleListEvent extends GwtEvent<RefreshModuleListEventHandler> {

    public static Type<RefreshModuleListEventHandler> TYPE = new Type<RefreshModuleListEventHandler>();

    @Override
    public Type<RefreshModuleListEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( RefreshModuleListEventHandler eventHandler ) {
        eventHandler.onRefreshList( this );
    }
}
