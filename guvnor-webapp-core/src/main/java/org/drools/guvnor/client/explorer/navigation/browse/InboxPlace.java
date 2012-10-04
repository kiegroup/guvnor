package org.drools.guvnor.client.explorer.navigation.browse;

import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

public class InboxPlace extends DefaultPlaceRequest {

    public InboxPlace(String inboxType) {
        super("inbox");
        addParameter("inboxType", inboxType);
    }

}
