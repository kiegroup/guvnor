package org.drools.guvnor.client.packages;

import com.google.gwt.event.shared.GwtEvent;

public class RefreshPackageListEvent extends GwtEvent<RefreshPackageListEventHandler> {

    public static Type<RefreshPackageListEventHandler> TYPE = new Type<RefreshPackageListEventHandler>();


    public RefreshPackageListEvent( String uuid ) {

    }

    @Override
    public Type<RefreshPackageListEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( RefreshPackageListEventHandler eventHandler ) {
        eventHandler.onRefreshList( this );
    }
}
