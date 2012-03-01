package org.drools.guvnor.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class AddViewPartEvent extends GwtEvent<AddViewPartEvent.Handler> {

    interface Handler extends EventHandler {
        public void onAddViewPart(AddViewPartEvent event);
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private final String viewPartId;

    public AddViewPartEvent(String viewPartId) {
        this.viewPartId = viewPartId;
    }

    public String getViewPartId() {
        return viewPartId;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAddViewPart(this);
    }
}

