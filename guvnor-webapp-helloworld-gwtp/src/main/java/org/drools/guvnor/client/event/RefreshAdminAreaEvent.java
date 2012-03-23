package org.drools.guvnor.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class RefreshAdminAreaEvent extends
        GwtEvent<RefreshAdminAreaEvent.RefreshAdminAreaHandler> {

    public RefreshAdminAreaEvent() {
        // Possibly for serialization.
    }

    public static void fire(HasHandlers source) {
        RefreshAdminAreaEvent eventInstance = new RefreshAdminAreaEvent();
        source.fireEvent(eventInstance);
    }

    public static void fire(HasHandlers source, RefreshAdminAreaEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    public interface HasRefreshAdminAreaHandler extends HasHandlers {
        HandlerRegistration addRefreshAdminAreaHandler(
                RefreshAdminAreaHandler handler);
    }

    public interface RefreshAdminAreaHandler extends EventHandler {
        public void onRefreshAdminArea(RefreshAdminAreaEvent event);
    }

    private static final Type<RefreshAdminAreaHandler> TYPE = new Type<RefreshAdminAreaHandler>();

    public static Type<RefreshAdminAreaHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<RefreshAdminAreaHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RefreshAdminAreaHandler handler) {
        handler.onRefreshAdminArea(this);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "RefreshAdminAreaEvent[" + "]";
    }
}
