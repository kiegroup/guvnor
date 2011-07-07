package org.drools.guvnor.client.packages;

import com.google.gwt.event.shared.GwtEvent;

public class CloseTabContentWidgetEvent extends GwtEvent<CloseTabContentWidgetEventHandler> {

    public static Type<CloseTabContentWidgetEventHandler> TYPE = new Type<CloseTabContentWidgetEventHandler>();

    private final String id;

    public CloseTabContentWidgetEvent( String id ) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public Type<CloseTabContentWidgetEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( CloseTabContentWidgetEventHandler eventHandler ) {
        eventHandler.onCloseTabContentWidget( this );
    }
}
