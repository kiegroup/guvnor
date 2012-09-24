package org.drools.guvnor.client.explorer.navigation.browse;

import org.uberfire.shared.mvp.PlaceRequest;

public class InboxPlace extends PlaceRequest {

    public InboxPlace(String inboxType) {
        super("inbox");
        addParameter("inboxType", inboxType);
    }

}
