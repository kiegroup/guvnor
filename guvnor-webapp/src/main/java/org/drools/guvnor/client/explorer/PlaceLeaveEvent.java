package org.drools.guvnor.client.explorer;

public class PlaceLeaveEvent {

    interface Handler {
        void onPlaceLeave( PlaceLeaveEvent event );
    }

    public PlaceLeaveEvent( String key ) {
    }
}
