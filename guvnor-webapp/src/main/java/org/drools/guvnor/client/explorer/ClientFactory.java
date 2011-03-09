package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {

    AuthorPerspectiveView getAuthorPerspectiveView();

    PlaceController getPlaceController();

    EventBus getEventBus();

    PerspectivesPanelView getPerspectivesPanelView(boolean showTitle);

    IFramePerspectiveView getIFramePerspectiveView();
}
