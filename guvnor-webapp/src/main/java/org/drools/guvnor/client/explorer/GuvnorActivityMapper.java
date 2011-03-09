package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class GuvnorActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;

    public GuvnorActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    public Activity getActivity(Place place) {
        Activity activity = null;

        if (place instanceof AuthorPerspectivePlace) {
            activity = new AuthorPerspectiveActivity(clientFactory);
        } else if (place instanceof IFramePerspectivePlace) {
            return new IFramePerspectiveActivity(clientFactory);
        }

        return activity;
    }

}
